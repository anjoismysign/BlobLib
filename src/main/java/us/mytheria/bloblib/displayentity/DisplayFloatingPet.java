package us.mytheria.bloblib.displayentity;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * TODO: NOT TESTED YET
 */
public abstract class DisplayFloatingPet<T extends Display, R extends Cloneable>
        implements DisplayPet<Display, R> {
    private Particle particle;
    protected T entity;
    private Location location;
    private UUID owner;
    private boolean activated, pauseLogic;
    private String customName;
    private DisplayEntityAnimations animations;
    private BukkitTask logicTask;
    protected R display;

    /**
     * Creates a pet
     *
     * @param owner      - the FloatingPet owner
     * @param display    - the display (like BlockData/ItemStack)
     *                   (must be an item or a block)
     * @param particle   - the Particle to itemStack
     * @param customName - the CustomName of the pet
     *                   (if null will be used 'owner's Pet')
     */
    public DisplayFloatingPet(Player owner, R display, @Nullable Particle particle,
                              @Nullable String customName) {
        this.pauseLogic = false;
        setOwner(owner.getUniqueId());
        setDisplay(display);
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
        loc.setX(loc.getX() - 1);
        loc.setY(loc.getY() + 0.85);
        setLocation(loc);
        spawnEntity(loc);
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

//    private void spawnEntity(Location loc) {
//        BlobLib plugin = BlobLib.getInstance();
//        entity = (BlockDisplay) loc.getWorld().spawnEntity(loc,
//                EntityType.BLOCK_DISPLAY);
//        setCustomName(getCustomName());
//        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> entity.setCustomNameVisible(true), 1);
//        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> entity.setGravity(false), 3);
//        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> entity.setInvulnerable(true), 5);
//        entity.setBlock(getDisplay());
//        initAnimations(plugin);
//    }

    /**
     * Will set pet's custom name.
     * If passing null, will be used 'owner's Pet'
     *
     * @param customName - the custom name
     */
    public void setCustomName(String customName) {
        this.customName = customName;
        if (entity == null)
            return;
        entity.setCustomNameVisible(true);
        entity.setCustomName(customName);
    }

    protected void initAnimations(JavaPlugin plugin) {
        animations = new DisplayEntityAnimations(this, 0.5, 0.55,
                0.025, 0.2, -0.5);
        initLogic(plugin);
    }

    private void initLogic(JavaPlugin plugin) {
        logicTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            Player owner = findOwner();
            if (owner == null || !owner.isOnline()) {
                destroy();
                return;
            }
            spawnParticles();
            if (!isPauseLogic()) {
                double distance = Math.sqrt(Math.pow(getLocation().getX() - owner.getLocation().getX(), 2) + Math.pow(getLocation().getZ() - owner.getLocation().getZ(), 2));
                if (distance >= 2.5D || Math.abs(owner.getLocation().getY() - getLocation().getY()) > 1D)
                    move();
                else if (distance <= 1.0D) {
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

    private void move() {
        this.location = animations.move(findOwnerOrFail(), location);
    }

    private void idle() {
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
        this.owner = owner;
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
        String name = findOwnerOrFail().getName() + "'s Pet";
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
        if (entity != null) {
            loadChunks(location);
            loadChunks(loc);
            entity.teleport(loc);
        }
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

    private void setLocation(Location location) {
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

    private void setActive(boolean activated) {
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