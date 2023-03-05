package us.mytheria.bloblib.entities.currency;

import me.anjoismysign.anjo.entities.Result;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mytheria.bloblib.BlobLibAssetAPI;
import us.mytheria.bloblib.entities.BlobChildCommand;
import us.mytheria.bloblib.entities.BlobExecutor;
import us.mytheria.bloblib.entities.ExecutorData;
import us.mytheria.bloblib.entities.ObjectManager;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class BlobEconomyCommand<T extends WalletOwner> {
    private final BlobExecutor executor;
    protected final WalletOwnerManager<T> walletOwnerManager;
    protected final ObjectManager<Currency> currencyManager;
    private final List<Function<ExecutorData, Boolean>> nonAdminChildCommands;
    private final List<Function<ExecutorData, Boolean>> adminChildCommands;
    private final List<Function<ExecutorData, List<String>>> nonAdminChildTabCompleter;
    private final List<Function<ExecutorData, List<String>>> adminChildTabCompleter;
    private final String depositArgumentName;
    private final String withdrawArgumentName;
    private final String setArgumentName;
    private final String resetArgumentName;

    protected BlobEconomyCommand(WalletOwnerManager<T> manager,
                                 ObjectManager<Currency> currencyManager,
                                 String commandName,
                                 String depositArgumentName,
                                 String withdrawArgumentName,
                                 String setArgumentName,
                                 String resetArgumentName) {
        nonAdminChildCommands = new ArrayList<>();
        adminChildCommands = new ArrayList<>();
        nonAdminChildTabCompleter = new ArrayList<>();
        adminChildTabCompleter = new ArrayList<>();
        this.depositArgumentName = depositArgumentName;
        this.withdrawArgumentName = withdrawArgumentName;
        this.setArgumentName = setArgumentName;
        this.resetArgumentName = resetArgumentName;
        this.executor = new BlobExecutor(manager.getPlugin(), commandName);
        this.walletOwnerManager = manager;
        this.currencyManager = currencyManager;
        setDefaultCommands().setDefaultTabCompleter();
        addAdminChildCommand(this::command);
        addAdminChildTabCompleter(this::tabCompleter);
    }

    public void addNonAdminChildCommand(Function<ExecutorData, Boolean> nonAdminChildCommand) {
        this.nonAdminChildCommands.add(nonAdminChildCommand);
    }

    public void addAdminChildCommand(Function<ExecutorData, Boolean> adminChildCommand) {
        this.adminChildCommands.add(adminChildCommand);
    }

    public void addNonAdminChildTabCompleter(Function<ExecutorData, List<String>> nonAdminChildTabCompleter) {
        this.nonAdminChildTabCompleter.add(nonAdminChildTabCompleter);
    }

    public void addAdminChildTabCompleter(Function<ExecutorData, List<String>> adminChildTabCompleter) {
        this.adminChildTabCompleter.add(adminChildTabCompleter);
    }

    public List<Function<ExecutorData, Boolean>> getAdminChildCommands() {
        return adminChildCommands;
    }

    public List<Function<ExecutorData, Boolean>> getNonAdminChildCommands() {
        return nonAdminChildCommands;
    }

    public List<Function<ExecutorData, List<String>>> getAdminChildTabCompleter() {
        return adminChildTabCompleter;
    }

    public List<Function<ExecutorData, List<String>>> getNonAdminChildTabCompleter() {
        return nonAdminChildTabCompleter;
    }

    private BlobEconomyCommand<T> setDefaultCommands() {
        executor.setCommand((sender, args) -> {
                    if (executor.hasNoArguments(sender, args))
                        return true;
                    for (Function<ExecutorData, Boolean> childCommand : nonAdminChildCommands) {
                        if (childCommand.apply(new ExecutorData(executor, args, sender)))
                            return true;
                    }
                    if (!executor.hasAdminPermission(sender))
                        return true;
                    for (Function<ExecutorData, Boolean> childCommand : adminChildCommands) {
                        if (childCommand.apply(new ExecutorData(executor, args, sender)))
                            return true;
                    }
                    return false;
                }
        );
        return this;
    }

    private void setDefaultTabCompleter() {
        executor.setTabCompleter((sender, args) -> {
            List<String> suggestions = new ArrayList<>();
            for (Function<ExecutorData, List<String>> childTabCompleter : nonAdminChildTabCompleter) {
                List<String> childTabCompletion = childTabCompleter.apply(new ExecutorData(executor, args, sender));
                if (childTabCompletion != null && !childTabCompletion.isEmpty())
                    suggestions.addAll(childTabCompletion);
            }
            if (!executor.hasAdminPermission(sender))
                return suggestions;
            for (Function<ExecutorData, List<String>> childTabCompleter : adminChildTabCompleter) {
                List<String> childTabCompletion = childTabCompleter.apply(new ExecutorData(executor, args, sender));
                if (childTabCompletion != null && !childTabCompletion.isEmpty())
                    suggestions.addAll(childTabCompletion);
            }
            return suggestions;
        });
    }

    public boolean command(ExecutorData data) {
        String[] args = data.args();
        BlobExecutor executor = data.executor();
        CommandSender sender = data.sender();
        if (!executor.hasAdminPermission(sender))
            return true;
        Result<BlobChildCommand> depositResult = executor
                .isChildCommand(depositArgumentName, args);
        if (depositResult.isValid()) {
            BlobEconomyCommandContext<T> context = BlobEconomyCommandContext.WITH_AMOUNT(this, args, sender);
            if (context == null)
                return true;
            Currency currency = context.currency();
            double amount = context.amount();
            T walletOwner = context.walletOwner();
            Player player = context.player();
            walletOwner.deposit(currency, amount);
            BlobLibAssetAPI.getMessage("Economy.Deposit").modify(s -> s.replace("%display%", currency.display(amount))
                    .replace("%currency%", currency.getKey())
                    .replace("%player%", player.getName())).toCommandSender(sender);
            return true;
        }

        Result<BlobChildCommand> withdrawResult = executor
                .isChildCommand(withdrawArgumentName, args);
        if (withdrawResult.isValid()) {
            BlobEconomyCommandContext<T> context = BlobEconomyCommandContext.WITH_AMOUNT(this, args, sender);
            if (context == null)
                return true;
            Currency currency = context.currency();
            double amount = context.amount();
            T walletOwner = context.walletOwner();
            Player player = context.player();
            if (!walletOwner.has(currency, amount)) {
                double missing = amount - walletOwner.getBalance(currency);
                BlobLibAssetAPI.getMessage("Economy.Cannot-Bankrupt-Others").modify(s -> s.replace("%display%", currency.display(missing))
                        .replace("%currency%", currency.getKey())
                        .replace("%player%", player.getName())).toCommandSender(sender);
                return true;
            }
            walletOwner.withdraw(currency, amount);
            BlobLibAssetAPI.getMessage("Economy.Withdraw").modify(s -> s.replace("%display%", currency.display(amount))
                    .replace("%currency%", currency.getKey())
                    .replace("%player%", player.getName())).toCommandSender(sender);
            return true;
        }
        Result<BlobChildCommand> setResult = executor
                .isChildCommand(setArgumentName, args);
        if (setResult.isValid()) {
            BlobEconomyCommandContext<T> context = BlobEconomyCommandContext.WITH_AMOUNT(this, args, sender);
            if (context == null)
                return true;
            Currency currency = context.currency();
            double amount = context.amount();
            Player player = context.player();
            context.walletOwner().setBalance(currency, amount);
            BlobLibAssetAPI.getMessage("Economy.Set").modify(s -> s.replace("%display%", currency.display(amount))
                    .replace("%currency%", currency.getKey())
                    .replace("%player%", player.getName())).toCommandSender(sender);
            return true;
        }
        Result<BlobChildCommand> resetResult = executor
                .isChildCommand(resetArgumentName, args);
        if (resetResult.isValid()) {
            BlobEconomyCommandContext<T> context = BlobEconomyCommandContext.WITHOUT_AMOUNT(this, args, sender);
            if (context == null)
                return true;
            Currency currency = context.currency();
            Player player = context.player();
            context.walletOwner().reset(currency);
            BlobLibAssetAPI.getMessage("Economy.Reset").modify(s -> s
                    .replace("%currency%", currency.getKey())
                    .replace("%player%", player.getName())).toCommandSender(sender);
            return true;
        }
        return false;
    }

    public List<String> tabCompleter(ExecutorData data) {
        String[] args = data.args();
        BlobExecutor executor = data.executor();
        List<String> suggestions = new ArrayList<>();
        if (!executor.hasAdminPermission(data.sender()))
            return suggestions;
        switch (args.length) {
            case 1 -> {
                suggestions.add(depositArgumentName);
                suggestions.add(withdrawArgumentName);
                suggestions.add(setArgumentName);
                suggestions.add(resetArgumentName);
                return suggestions;
            }
            case 2 -> {
                if (executor.isChildCommand(withdrawArgumentName, args).isValid())
                    suggestions.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
                if (executor.isChildCommand(depositArgumentName, args).isValid())
                    suggestions.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
                if (executor.isChildCommand(setArgumentName, args).isValid())
                    suggestions.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
                if (executor.isChildCommand(resetArgumentName, args).isValid())
                    suggestions.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
                return suggestions;
            }
            case 3 -> {
                if (executor.isChildCommand(resetArgumentName, args).isValid())
                    suggestions.addAll(currencyManager.keys());
                return suggestions;
            }
            case 4 -> {
                if (executor.isChildCommand(withdrawArgumentName, args).isValid())
                    suggestions.addAll(currencyManager.keys());
                if (executor.isChildCommand(depositArgumentName, args).isValid())
                    suggestions.addAll(currencyManager.keys());
                if (executor.isChildCommand(setArgumentName, args).isValid())
                    suggestions.addAll(currencyManager.keys());
                return suggestions;
            }
            default -> {
                return suggestions;
            }
        }
    }
}
