package us.mytheria.bloblib.displayentity;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;

public abstract class DisplayFloatingPet<T extends Display, R extends Cloneable>
        implements DisplayPet<Display, R> {
    private Particle particle;
    protected T entity, vehicle;
    private Location location;
    private UUID owner;
    private boolean activated, pauseLogic;
    private String customName;
    private SyncDisplayEntityAnimations animations;
    private BukkitTask logicTask;
    protected R display;
    private final Method[] methods = ((Supplier<Method[]>) () -> {
        try {
            Method getHandle = Class.forName(Bukkit.getServer().getClass().getPackage().getName() + ".entity.CraftEntity").getDeclaredMethod("getHandle");
            return new Method[]{
                    getHandle, getHandle.getReturnType().getDeclaredMethod("b", double.class, double.class, double.class, float.class, float.class)
            };
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }).get();
    private final DisplayFloatingPetSettings settings;

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
                              @Nullable String customName,
                              DisplayFloatingPetSettings settings
    ) {
        this.pauseLogic = false;
        this.settings = settings;
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
        Random random = new Random();
        loc.setX(loc.getX() + random.nextInt(3) - 1);
        loc.setZ(loc.getZ() + random.nextInt(3) - 1);
        loc.setY(loc.getY() + 0.85);
        setLocation(loc);
        spawnEntity(loc);
        entity.setTransformation(settings.displayMeasures().toTransformation());
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
        EntityAnimationsCarrier animationsCarrier = settings.animationsCarrier();
        animations = new SyncDisplayEntityAnimations(this, 0.45, 0.2,
                animationsCarrier.hoverSpeed(),
                animationsCarrier.hoverHeightCeiling(),
                animationsCarrier.hoverHeightFloor(),
                animationsCarrier.yOffset());
        initLogic(plugin);
    }

    private void initLogic(JavaPlugin plugin) {
        EntityAnimationsCarrier animationsCarrier = settings.animationsCarrier();
        logicTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            Player owner = findOwner();
            if (owner == null || !owner.isOnline()) {
                destroy();
                return;
            }
            spawnParticles(animationsCarrier.particlesOffset(), 0);
            if (!isPauseLogic()) {
                double distance = Math.sqrt(Math.pow(getLocation().getX() - owner.getLocation().getX(), 2) + Math.pow(getLocation().getZ() - owner.getLocation().getZ(), 2));
                if (distance >= 2.5D || Math.abs(owner.getLocation().getY() + animationsCarrier.yOffset() - getLocation().getY()) > 1D)
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
        if (vehicle != null) {
            loadChunks(location);
            loadChunks(loc);
            /*
             * Reflection required. Might not work on future versions due
             * to obfuscation.
             * Known to work in 1.20.1
             */
            try {
                methods[1].invoke(methods[0].invoke(vehicle), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else
            throw new NullPointerException("Expected vehicle is null");
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