package io.github.anjoismysign.bloblib.api;

import io.github.anjoismysign.bloblib.middleman.AlternativeSavingMiddleman;
import io.github.anjoismysign.bloblib.middleman.BlobTycoonMiddleman;
import io.github.anjoismysign.bloblib.middleman.profile.AbsentProfileProvider;
import io.github.anjoismysign.bloblib.middleman.profile.ProfileProvider;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;

public class BlobLibProfileAPI {

    private static BlobLibProfileAPI INSTANCE;

    public static BlobLibProfileAPI getInstance(){
        if (INSTANCE == null){
            INSTANCE = new BlobLibProfileAPI();
        }
        return INSTANCE;
    }

    private ProfileProvider provider;

    private BlobLibProfileAPI(){
    }

    private void setupProvider(){
        PluginManager pluginManager = Bukkit.getPluginManager();
        boolean blobTycoonEnabled = pluginManager.isPluginEnabled("BlobTycoon");
        if (blobTycoonEnabled){
            provider = new BlobTycoonMiddleman();
            return;
        }
        boolean alternativeSavingEnabled = pluginManager.isPluginEnabled("AlternativeSaving");
        if (alternativeSavingEnabled){
            provider = new AlternativeSavingMiddleman();
            return;
        }
        provider = new AbsentProfileProvider();
    }

    /**
     * Gets the ProfileProvider
     * @return the ProfileProvider that has been detected.
     */
    @NotNull
    public ProfileProvider getProvider() {
        if (provider == null){
            setupProvider();
        }
        return provider;
    }

}
