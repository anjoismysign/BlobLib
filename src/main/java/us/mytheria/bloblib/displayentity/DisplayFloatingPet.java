package us.mytheria.bloblib.displayentity;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.NumberConversions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Random;
import java.util.UUID;

/**
 * Represents a FloatingPet.
 * Uses 1.20.2+ Bukkit API
 *
 * @param <T> - the Display entity
 * @param <R> - (BlockData/ItemStack)
 */
public abstract class DisplayFloatingPet<T extends Display, R extends Cloneable>
        implements DisplayPet<Display, R> {

    private Particle particle;
    protected T entity;
    private Location location;
    private UUID owner;
    private boolean activated, pauseLogic;
    private String customName;
    protected SyncDisplayEntityAnimations animations;
    protected BukkitTask logicTask;
    protected R display;
    protected final DisplayFloatingPetSettings settings;

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
     */
    public DisplayFloatingPet(@NotNull Player owner,
                              @NotNull R display,
                              @Nullable Particle particle,
                              @Nullable String customName,
                              @NotNull DisplayFloatingPetSettings settings
    ) {
        this.pauseLogic = false;
        this.settings = Objects.requireNonNull(settings);
        setOwner(owner.getUniqueId());
        setDisplay(Objects.requireNonNull(display));
        setCustomName(customName);
        setParticle(particle);
    }

    /**
     * Spawns the pet.
     * NEEDS TO BE CALLED SYNCHRONOUSLY!
     */
    public void spawn() {
        setActive(true);
        Location loc = findOwnerOrFail().getLocation().clone();
        Random random = new Random();
        loc.setX(loc.getX() + random.nextInt(3) - 1);
        loc.setZ(loc.getZ() + random.nextInt(3) - 1);
        loc.setY(loc.getY() + 0.85);
        loc.setPitch(1);
        setLocation(loc);
        spawnEntity(loc);
        entity.setTransformation(settings.displayMeasurements().toTransformation());
    }

    /**
     * Spawns the entity in world and initializes animations
     *
     * @param location - the location to spawn
     */
    abstract void spawnEntity(Location location);

    /**
     * Sets the pet display
     *
     * @param display - the new display
     */
    public abstract void setDisplay(R display);

    /**
     * Will set pet's custom name.
     *
     * @param customName - the custom name
     */
    public void setCustomName(String customName) {
        this.customName = customName;
        if (entity == null)
            return;
        entity.setCustomNameVisible(customName != null);
        entity.setCustomName(customName);
    }

    protected void initAnimations(JavaPlugin plugin) {
        animations = new SyncDisplayEntityAnimations(this,
                settings.animationsCarrier());
        initLogic(plugin);
    }

    protected void initLogic(JavaPlugin plugin) {
        EntityAnimationsCarrier animationsCarrier = settings.animationsCarrier();
        logicTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            Player owner = findOwner();
            if (owner == null) {
                destroy();
                return;
            }
            spawnParticles(animationsCarrier.particlesOffset(), 0);
            if (!isPauseLogic()) {
                double distance = Math.sqrt(Math.pow(getLocation().getX() - owner.getLocation().getX(), 2) + Math.pow(getLocation().getZ() - owner.getLocation().getZ(), 2));
                if (distance >= animationsCarrier.teleportDistanceThreshold()) {
                    Location loc = owner.getLocation().clone();
                    loc.setPitch(getLocation().getPitch());
                    teleport(loc);
                } else if (distance >= animationsCarrier.approachDistanceThreshold() || Math.abs(owner.getLocation().getY() + animationsCarrier.yOffset() - getLocation().getY()) > 1D)
                    move();
                else if (distance <= animationsCarrier.minimumDistance()) {
                    moveAway();
                } else {
                    idle();
                }
            }
        }, 0, 1);
    }

    private void moveAway() {
        this.location = animations.moveAway(findOwnerOrFail(), location);
    }

    protected void move() {
        this.location = animations.move(findOwnerOrFail(), location);
    }

    protected void idle() {
        this.location = animations.idle(findOwnerOrFail(), location);
    }

    /**
     * Similar to Entity#remove.
     * Will mark the pet as deactivated and will remove the block display.
     */
    public void destroy() {
        setActive(false);
        Bukkit.getScheduler().cancelTask(logicTask.getTaskId());
        if (entity != null) {
            entity.remove();
            entity = null;
        }
    }

    /**
     * Returns the pet display
     *
     * @return the display
     */
    public R getDisplay() {
        return display;
    }

    /**
     * Returns the owner of the pet
     *
     * @return the owner
     */
    public UUID getOwner() {
        return owner;
    }

    /**
     * Sets the owner of the pet
     *
     * @param owner - the new owner
     */
    public void setOwner(UUID owner) {
        this.owner = Objects.requireNonNull(owner);
    }

    /**
     * Returns the display entity of the pet
     *
     * @return the display entity
     */
    @Override
    public T getEntity() {
        return entity;
    }

    /**
     * Retrieves pet's name.
     *
     * @return if customName is null, returns 'owner's Pet', else returns customName
     */
    public String getCustomName() {
        String name = null;
        if (this.customName != null)
            name = this.customName;
        return name;
    }

    /**
     * Teleports the pet
     *
     * @param loc the new location
     */
    public void teleport(Location loc) {
        loadChunks(location);
        loadChunks(loc);
        if (!NumberConversions.isFinite(loc.getYaw()))
            loc.setYaw(1.0f);
        if (!NumberConversions.isFinite(loc.getPitch()))
            loc.setPitch(1.0f);
        entity.teleport(loc);
        setLocation(loc);
    }

    private void loadChunks(Location loc) {
        if (!loc.getChunk().isLoaded())
            loc.getChunk().load();

        if (!loc.getWorld().getChunkAt(loc.getChunk().getX() + 1, loc.getChunk().getZ()).isLoaded())
            loc.getWorld().getChunkAt(loc.getChunk().getX() + 1, loc.getChunk().getZ()).load();

        if (!loc.getWorld().getChunkAt(loc.getChunk().getX() - 1, loc.getChunk().getZ()).isLoaded())
            loc.getWorld().getChunkAt(loc.getChunk().getX() - 1, loc.getChunk().getZ()).load();

        if (!loc.getWorld().getChunkAt(loc.getChunk().getX(), loc.getChunk().getZ() + 1).isLoaded())
            loc.getWorld().getChunkAt(loc.getChunk().getX(), loc.getChunk().getZ() + 1).load();

        if (!loc.getWorld().getChunkAt(loc.getChunk().getX(), loc.getChunk().getZ() - 1).isLoaded())
            loc.getWorld().getChunkAt(loc.getChunk().getX(), loc.getChunk().getZ() - 1).load();

        if (!loc.getWorld().getChunkAt(loc.getChunk().getX() + 1, loc.getChunk().getZ() + 1).isLoaded())
            loc.getWorld().getChunkAt(loc.getChunk().getX() + 1, loc.getChunk().getZ() + 1).load();

        if (!loc.getWorld().getChunkAt(loc.getChunk().getX() - 1, loc.getChunk().getZ() - 1).isLoaded())
            loc.getWorld().getChunkAt(loc.getChunk().getX() - 1, loc.getChunk().getZ() - 1).load();

        if (!loc.getWorld().getChunkAt(loc.getChunk().getX() - 1, loc.getChunk().getZ() + 1).isLoaded())
            loc.getWorld().getChunkAt(loc.getChunk().getX() - 1, loc.getChunk().getZ() + 1).load();

        if (!loc.getWorld().getChunkAt(loc.getChunk().getX() + 1, loc.getChunk().getZ() - 1).isLoaded())
            loc.getWorld().getChunkAt(loc.getChunk().getX() + 1, loc.getChunk().getZ() - 1).load();

    }

    /**
     * Returns the location of the pet
     *
     * @return the location
     */
    public Location getLocation() {
        return location;
    }

    protected void setLocation(Location location) {
        this.location = location;
    }

    /**
     * Returns if the pet is activated.
     * If true, it means that it is spawned.
     * If false, it means that it was called
     * to be removed.
     *
     * @return if the pet is activated
     */
    public boolean isActive() {
        return activated;
    }

    protected void setActive(boolean activated) {
        this.activated = activated;
    }

    /**
     * Returns if the pet logic is paused
     *
     * @return if the logic is paused
     */
    public boolean isPauseLogic() {
        return pauseLogic;
    }

    /**
     * Sets if the pet logic should be paused
     *
     * @param pauseLogic - if the logic should be paused
     *                   if true, will make pet static/paused
     */
    public void setPauseLogic(boolean pauseLogic) {
        this.pauseLogic = pauseLogic;
    }

    /**
     * Returns the particle of the pet
     *
     * @return the particle
     */
    @Nullable
    public Particle getParticle() {
        return particle;
    }

    /**
     * Sets the particle of the pet
     *
     * @param particle - the new particle
     *                 if null, the pet will not have a particle
     */
    public void setParticle(Particle particle) {
        this.particle = particle;
    }
}