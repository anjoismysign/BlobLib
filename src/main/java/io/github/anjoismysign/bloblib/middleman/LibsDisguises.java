package io.github.anjoismysign.bloblib.middleman;

import io.github.anjoismysign.bloblib.disguises.DisguiseEngine;
import io.github.anjoismysign.bloblib.disguises.Disguiser;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.utilities.parser.DisguiseParser;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class LibsDisguises implements Disguiser {
    private final DisguiseEngine engine;

    public LibsDisguises() {
        engine = DisguiseEngine.NONE;
    }


    @Override
    public DisguiseEngine getEngine() {
        return engine;
    }

    @Override
    public void disguiseEntity(String raw, Entity entity) {
        Disguise disguise;
        try {
            disguise = DisguiseParser.parseDisguise(raw);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return;
        }
        DisguiseAPI.disguiseToAll(entity, disguise);
    }

    @Override
    public void disguiseEntityForTarget(String raw, Entity entity,
                                        Player... target) {
        Disguise disguise;
        try {
            disguise = DisguiseParser.parseDisguise(raw);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return;
        }
        DisguiseAPI.disguiseToPlayers(entity, disguise, target);
    }
}
