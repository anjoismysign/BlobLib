package us.mytheria.bloblib.displayentity;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a FloatingPet.
 * Uses 1.20.2+ Bukkit API
 *
 * @param <T> - the Display entity
 * @param <R> - (BlockData/ItemStack)
 */
public abstract class DisplayPackFloatingPet<T extends Display, R extends Cloneable>
        extends DisplayFloatingPet<T, R> {
    protected final PackMaster<DisplayPackFloatingPet<T, R>> packMaster;
    private final int index, holdIndex;

    /**
     * Creates a pet
     *
     * @param owner      - the FloatingPet owner
     * @param display    - the display (like BlockData/ItemStack)
     *                   (must be an item or a block)
     * @param particle   - the Particle to itemStack
     * @param customName - the CustomName of the pet
     *                   (if null will be used 'owner's Pet')
     * @param settings   - the settings of the pet
     * @param packMaster - the pack master
     * @param realIndex  - the real index of the pet
     */
    public DisplayPackFloatingPet(@NotNull Player owner,
                                  @NotNull R display,
                                  @Nullable Particle particle,
                                  @Nullable String customName,
                                  @NotNull DisplayFloatingPetSettings settings,
                                  @NotNull PackMaster<DisplayPackFloatingPet<T, R>> packMaster,
                                  int realIndex) {
        super(owner, display, particle, customName, settings);
        this.packMaster = packMaster;
        this.index = realIndex;
        this.holdIndex = packMaster.addComponent(this, realIndex);
    }

    @Override
    protected void initLogic(JavaPlugin plugin) {
        EntityAnimationsCarrier animationsCarrier = settings.animationsCarrier();
        logicTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            Player owner = findOwner();
            if (owner == null) {
                destroy();
                return;
            }
            spawnParticles(animationsCarrier.particlesOffset(), 0);
            if (!isPauseLogic())
                move();
        }, 0, 1);
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    protected void initAnimations(JavaPlugin plugin) {
        animations = new SyncDisplayPackEntityAnimations<>(this,
                settings.animationsCarrier(),
                packMaster,
                holdIndex);
        initLogic(plugin);
    }

    public int getIndex() {
        return index;
    }
}