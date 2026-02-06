package io.github.anjoismysign.bloblib.managers.cruder;

import com.google.common.collect.Maps;
import io.github.anjoismysign.bloblib.api.BlobLibProfileAPI;
import io.github.anjoismysign.bloblib.entities.ChunkedAccountCrudable;
import io.github.anjoismysign.bloblib.entities.Cleanable;
import io.github.anjoismysign.bloblib.entities.PlayerDecoratorAware;
import io.github.anjoismysign.bloblib.entities.ProfileView;
import io.github.anjoismysign.bloblib.events.ProfileManagementQuitEvent;
import io.github.anjoismysign.bloblib.middleman.profile.Profile;
import io.github.anjoismysign.bloblib.utilities.ClassHandler;
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

public final class ChunkedAccountCruder<T extends Crudable> extends ProfiledCruder<T> implements Listener {

    private final @NotNull Cruder<ChunkedAccountCrudable> accountCruder;
    private final @NotNull Cruder<T> profileCruder;
    private final @NotNull Map<UUID, Data<T>> data = Maps.newConcurrentMap();

    private static final class Data<T extends Crudable> {
        private final ChunkedAccountCrudable account;
        private @Nullable T currentProfile;
        private @Nullable ProfileView create;

        private Data(ChunkedAccountCrudable account) {
            this.account = account;
            this.currentProfile = null;
            this.create = null;
        }
    }

    public ChunkedAccountCruder(@NotNull JavaPlugin plugin,
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
        accountCruder = Cruder.of(plugin, ChunkedAccountCrudable.class, ChunkedAccountCrudable::new, directory);
        profileCruder = Cruder.of(plugin, profileClass, identification -> new ClassHandler<>(profileClass).constructCrudable(identification), directory);
        loadAll();
    }

    @EventHandler
    public void onQuit(ProfileManagementQuitEvent event) {
        Player player = event.getPlayer();
        UUID uniqueId = player.getUniqueId();
        @Nullable Data<T> data = this.data.remove(uniqueId);
        @Nullable ChunkedAccountCrudable account = data == null ? null : data.account;
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
                @Nullable var currentProfile = data.currentProfile;
                if (currentProfile instanceof Cleanable cleanable){
                    cleanable.cleanup();
                }
                profileCruder.update(currentProfile);
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
        var data = this.data.get(uniqueIdentifier);
        return data == null ? null : data.currentProfile;
    }

    private void postLoadData(Data<T> data) {
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
            ProfileView view = profiles.get(account.getCurrentProfileIndex());
            switchToProfile(data, view);
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

    private void switchToProfile(Data<T> data, ProfileView view) {
        var account = data.account;
        var profiles = account.getProfiles();
        if (view == null) {
            return;
        }
        int index = profiles.indexOf(view);
        @Nullable var currentProfile = data.currentProfile;
        Runnable asyncRunnable = () -> {
            if (currentProfile != null) {
                profileCruder.update(currentProfile);
                if (currentProfile instanceof Cleanable cleanable) {
                    cleanable.cleanup();
                }
            }
            data.currentProfile = profileCruder.readOrGenerate(view.identification());
            if (data.currentProfile instanceof PlayerDecoratorAware aware){
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

    private void createProfile(Data<T> data) {
        var account = data.account;
        var view = data.create;
        @Nullable var currentProfile = data.currentProfile;
        if (currentProfile != null) {
            profileCruder.update(currentProfile);
        }
        T profile = profileCruder.createAndUpdate(view.identification());
        if (profile instanceof PlayerDecoratorAware aware){
            aware.setPlayerDecorator(account.getPlayerDecorator());
        }
        var profiles = account.getProfiles();
        profiles.add(view);
        data.create = null;
        switchToProfile(data, view);
    }

    private BukkitTask saveTask(@NotNull Data<T> data,
                                @NotNull PlayerConnection connection) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                if (!connection.isConnected()) {
                    cancel();
                    return;
                }
                accountCruder.update(data.account);
                @Nullable var currentProfile = data.currentProfile;
                if (currentProfile != null) {
                    profileCruder.update(currentProfile);
                }
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
            @Nullable Data<T> data = this.data.get(uniqueId);
            if (data == null) {
                data = new Data<>(accountCruder.readOrGenerate(idToString));
                accountCruder.update(data.account);
                @Nullable Data<T> finalData = data;
                Bukkit.getScheduler().runTask(plugin, () -> {
                    loading.remove(uniqueId);
                    if (!connection.isConnected()) {
                        return;
                    }
                    this.data.put(uniqueId, finalData);
                    autoSave.put(uniqueId, saveTask(finalData, connection));
                });
            }
            var account = data.account;
            var profiles = account.getProfiles();
            @Nullable var profileView = profiles
                    .stream()
                    .filter(view -> view.identification().equals(profile.getIdentification()))
                    .findFirst()
                    .orElse(null);
            int index = profiles.indexOf(profileView);
            if (profileView == null){
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
        for (var data : data.values()) {
            accountCruder.update(data.account);
            @Nullable var currentProfile = data.currentProfile;
            if (currentProfile != null) {
                profileCruder.update(currentProfile);
            }
        }
        data.clear();
    }

}
