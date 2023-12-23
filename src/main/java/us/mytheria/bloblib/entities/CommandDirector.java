package us.mytheria.bloblib.entities;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class CommandDirector {
    private final BlobExecutor executor;
    private final List<Function<ExecutorData, Boolean>> nonAdminChildCommands;
    private final List<Function<ExecutorData, Boolean>> adminChildCommands;
    private final List<Function<ExecutorData, List<String>>> nonAdminChildTabCompleter;
    private final List<Function<ExecutorData, List<String>>> adminChildTabCompleter;

    public CommandDirector(JavaPlugin plugin, String commandName) {
        this.executor = new BlobExecutor(plugin, commandName);
        this.nonAdminChildCommands = new ArrayList<>();
        this.adminChildCommands = new ArrayList<>();
        this.nonAdminChildTabCompleter = new ArrayList<>();
        this.adminChildTabCompleter = new ArrayList<>();
        setDefaultCommands();
        setDefaultTabCompleter();
    }

    public BlobExecutor getExecutor() {
        return executor;
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

    protected void setDefaultCommands() {
        executor.setCommand((sender, args) -> {
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
        });
    }

    protected void setDefaultTabCompleter() {
        executor.setTabCompleter((sender, args) -> {
            List<String> suggestions = new ArrayList<>();
            for (Function<ExecutorData, List<String>> childTabCompleter : nonAdminChildTabCompleter) {
                List<String> childTabCompletion = childTabCompleter.apply(new ExecutorData(executor, args, sender));
                if (childTabCompletion != null && !childTabCompletion.isEmpty())
                    suggestions.addAll(childTabCompletion);
            }
            if (!executor.hasAdminPermission(sender, null))
                return suggestions;
            for (Function<ExecutorData, List<String>> childTabCompleter : adminChildTabCompleter) {
                List<String> childTabCompletion = childTabCompleter.apply(new ExecutorData(executor, args, sender));
                if (childTabCompletion != null && !childTabCompletion.isEmpty())
                    suggestions.addAll(childTabCompletion);
            }
            return suggestions;
        });
    }
}