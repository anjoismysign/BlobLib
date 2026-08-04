package io.github.anjoismysign.bloblib.disguise;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class Absent implements Disguiser {
    private final DisguiseEngine engine;

    public Absent() {
        engine = DisguiseEngine.NONE;
    }


    @Override
    public DisguiseEngine getEngine() {
        return engine;
    }

    @Override
    public void disguiseEntity(String disguise, Entity entity) {
    }

    @Override
    public void disguiseEntityForTarget(String disguise, Entity entity,
                                        Player... target) {
    }
}
