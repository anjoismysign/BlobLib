package io.github.anjoismysign.bloblib.managers.cruder;

import com.google.common.collect.Maps;
import io.github.anjoismysign.bloblib.api.BlobLibProfileAPI;
import io.github.anjoismysign.bloblib.entities.AccountCrudable;
import io.github.anjoismysign.bloblib.entities.Cleanable;
import io.github.anjoismysign.bloblib.entities.PlayerDecoratorAware;
import io.github.anjoismysign.bloblib.entities.ProfileView;
import io.github.anjoismysign.bloblib.events.ProfileManagementQuitEvent;
import io.github.anjoismysign.bloblib.middleman.profile.Profile;
import io.github.anjoismysign.bloblib.utilities.ClassHandler;
import io.github.anjoismysign.psa.PostLoadable;
import io.github.anjoismysign.psa.crud.Crudable;
import io.papermc.paper.connection.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public final class AccountCruder<R extends AccountCrudable<T>, T extends Crudable> extends ProfiledCruder<T> implements Listener {

    private final @NotNull Cruder<R> accountCruder;
    private final @NotNull Function<String, T> profileCreateFunction;
    private final @NotNull Map<UUID, Data<R,T>> accounts = Maps.newConcurrentMap();

    private static final class Data<R extends AccountCrudable<T> ,T extends Crudable> {
        private final R account;
        private @Nullable ProfileView create;

        private Data(R account) {
            this.account = account;
            this.create = null;
        }
    }

    public AccountCruder(@NotNull JavaPlugin plugin,
                         @NotNull Class<R> accountClass,
                         @NotNull Class<T> profileClass) {
        super(plugin);
        var profileAPI = BlobLibProfileAPI.getInstance();
        var provider = profileAPI.getProvider();
        var providerName = provider.getProviderName();
        @NotNull var directory = providerName.equals("AbsentProfileProvider") ?
                plugin.getDataFolder()
                :
                new File(plugin.getDataFolder(), providerName);
        if (!directory.isDirectory()) {
            directory.mkdirs();
        }
        Function<String, R> accountCreateFunction = identification -> new ClassHandler<>(accountClass).constructCrudable(identification);
        var profileClassHandler = new ClassHandler<>(profileClass);
        profileCreateFunction = profileClassHandler::constructCrudable;
        accountCruder = Cruder.of(plugin, accountClass, accountCreateFunction, directory);
        loadAll();
    }

    @EventHandler
    public void onQuit(ProfileManagementQuitEvent event) {
        Player player = event.getPlayer();
        UUID uniqueId = player.getUniqueId();
        @Nullable Data<R,T> data = this.accounts.remove(uniqueId);
        @Nullable R account = data == null ? null : data.account;
        BukkitTask task = autoSave.remove(uniqueId);
        if (task != null) {
            task.cancel();
        }
        playerDecorators.remove(uniqueId.toString());
        if (loading.contains(uniqueId)) {
            return;
        }
        saving.add(uniqueId);
        Runnable asyncRunnable = () -> {
            if (account != null) {
                accountCruder.update(account);
                @Nullable var currentProfile = account.getCurrentProfile();
                if (currentProfile instanceof Cleanable cleanable){
                    cleanable.cleanup();
                }
            }
            saving.remove(uniqueId);
        };
        if (Bukkit.isPrimaryThread()) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, asyncRunnable);
        } else {
            asyncRunnable.run();
        }
    }

    @Override
    @Nullable T getAccount(@NotNull UUID uniqueIdentifier) {
        @Nullable var data = this.accounts.get(uniqueIdentifier);
        @Nullable var account = data == null ? null : data.account;
        return account == null ? null : account.getCurrentProfile();
    }

    private void postLoadData(Data<R,T> data) {
        var account = data.account;
        var identification = account.getIdentification();
        account.setPlayerDecorator(assignPlayerDecorator(identification));
        var profiles = account.getProfiles();
        if (!profiles.isEmpty()) {
            int currentProfileIndex = account.getCurrentProfileIndex();
            var create = data.create;
            if (create != null){
                createProfile(data);
                return;
            }
            if (currentProfileIndex < 0 || currentProfileIndex >= profiles.size()) {
                account.setCurrentProfileIndex(0);
            }
            T profile = profiles.get(account.getCurrentProfileIndex());
            switchToProfile(account, profile);
        } else {
            var profileAPI = BlobLibProfileAPI.getInstance();
            var provider = profileAPI.getProvider();
            var profileManagement = provider.getProfileManagement(UUID.fromString(identification));
            if (profileManagement == null) {
                return;
            }
            var profile = profileManagement.getProfiles().get(profileManagement.getCurrentProfileIndex());
            data.create = profile.toView();
            createProfile(data);
        }
    }

    private void switchToProfile(R account, T profile) {
        var profiles = account.getProfiles();
        int index = profiles.indexOf(profile);
        var currentProfile = account.getCurrentProfile();
        Runnable asyncRunnable = () -> {
            if (currentProfile != null && !profile.getIdentification().equals(currentProfile.getIdentification()) && currentProfile instanceof Cleanable cleanable) {
                cleanable.cleanup();
            }
            if (currentProfile == null || !profile.getIdentification().equals(currentProfile.getIdentification())){
                if (profile instanceof PostLoadable postLoadable) {
                    postLoadable.onPostLoad();
                }
            }
            if (profile instanceof PlayerDecoratorAware aware){
                aware.setPlayerDecorator(account.getPlayerDecorator());
            }
            account.setCurrentProfileIndex(index);
        };
        if (Bukkit.isPrimaryThread()) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, asyncRunnable);
        } else {
            asyncRunnable.run();
        }
    }

    private void createProfile(Data<R,T> data) {
        var account = data.account;
        var view = data.create;
        T profile = profileCreateFunction.apply(view.identification());
        if (profile instanceof PostLoadable postLoadable){
            postLoadable.onPostLoad();
        }
        if (profile instanceof PlayerDecoratorAware aware){
            aware.setPlayerDecorator(account.getPlayerDecorator());
        }
        var profiles = account.getProfiles();
        profiles.add(profile);
        data.create = null;
        switchToProfile(account, profile);
    }

    private BukkitTask saveTask(@NotNull R account,
                                @NotNull PlayerConnection connection) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                if (!connection.isConnected()) {
                    cancel();
                    return;
                }
                accountCruder.update(account);
            }
        }.runTaskTimerAsynchronously(plugin, 20 * 60 * 5,
                20 * 60 * 5);
    }

    @Override
    Runnable loadRunnable(PlayerConnection connection,
                          UUID uniqueId,
                          Profile profile){
        String idToString = uniqueId.toString();
        return () -> {
            if (!connection.isConnected()) {
                playerDecorators.remove(idToString);
                loading.remove(uniqueId);
                return;
            }
            @Nullable var data = this.accounts.get(uniqueId);
            if (data == null) {
                var account = accountCruder.readOrGenerate(idToString);
                accountCruder.update(account);
                data = new Data<>(account);
                @Nullable R finalAccount = account;
                @Nullable Data<R, T> finalData = data;
                Bukkit.getScheduler().runTask(plugin, () -> {
                    loading.remove(uniqueId);
                    if (!connection.isConnected()) {
                        return;
                    }
                    this.accounts.put(uniqueId, finalData);
                    autoSave.put(uniqueId, saveTask(finalAccount, connection));
                });
            }
            var account = data.account;
            var profiles = account.getProfiles();
            @Nullable var accountProfile = profiles
                    .stream()
                    .filter(view -> view.getIdentification().equals(profile.getIdentification()))
                    .findFirst()
                    .orElse(null);
            int index = profiles.indexOf(accountProfile);
            if (accountProfile == null){
                data.create = profile.toView();
            }
            account.setCurrentProfileIndex(index);
            postLoadData(data);
        };
    }

    @Override
    public void shutdown() {
        autoSave.values().forEach(BukkitTask::cancel);
        autoSave.clear();
        for (var data : accounts.values()) {
            var account = data.account;
            accountCruder.update(account);
        }
        accounts.clear();
    }

}
