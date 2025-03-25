package us.mytheria.bloblib.displayentity;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.BlobLib;

import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public class ArmorStandFloatingPet implements DisplayPet<ArmorStand, ItemStack> {
    private Particle particle;
    private ArmorStand entity;
    private Location location;
    private UUID owner;
    private boolean activated, pauseLogic;
    private ItemStack display;
    private String customName;
    private DisplayEntityAnimations animations;
    private BukkitTask logicTask;
    private final EntityAnimationsCarrier animationsCarrier;

    /**
     * Creates a pet
     *
     * @param owner      - the FloatingPet owner
     * @param itemStack  - the ItemStack to itemStack
     *                   (must be an item or a block)
     * @param particle   - the Particle to itemStack
     * @param customName - the CustomName of the pet
     *                   (if null will be used 'owner's Pet')
     */
    public ArmorStandFloatingPet(@NotNull Player owner,
                                 @NotNull ItemStack itemStack,
                                 @Nullable Particle particle,
                                 @Nullable String customName,
                                 @NotNull EntityAnimationsCarrier animationsCarrier) {
        this.animationsCarrier = Objects.requireNonNull(animationsCarrier);
        Material type = itemStack.getType();
        if (!type.isItem() && type.isBlock())
            throw new IllegalArgumentException("ItemStack must be an item or a block");
        this.pauseLogic = false;
        setOwner(owner.getUniqueId());
        setDisplay(itemStack);
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
        spawnArmorStand(loc);
    }

    private void spawnArmorStand(Location loc) {
        BlobLib plugin = BlobLib.getInstance();
        entity = createArmorStand(loc);
        setCustomName(getCustomName());
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> entity.setCustomNameVisible(true), 1);
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> entity.setGravity(false), 3);
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> entity.setSmall(true), 4);
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> entity.setInvulnerable(true), 5);
        entity.getEquipment().setHelmet(getDisplay());
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> entity.setVisible(false), 6);
        initAnimations(plugin);

    }

    /**
     * Will set pet's custom name.
     * If passing null, will be used 'owner's Pet'
     *
     * @param customName - the custom name
     */
    public void setCustomName(@Nullable String customName) {
        this.customName = customName;
        if (entity == null)
            return;
        entity.setCustomNameVisible(true);
        entity.setCustomName(customName);
    }

    private void initAnimations(JavaPlugin plugin) {
        animations = new DisplayEntityAnimations(this, animationsCarrier);
        initLogic(plugin);
    }

    private void initLogic(JavaPlugin plugin) {
        logicTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            Player owner = findOwner();
            if (owner == null) {
                destroy();
                return;
            }
            spawnParticles();
            if (!isPauseLogic()) {
                double distance = Math.sqrt(Math.pow(getLocation().getX() - owner.getLocation().getX(), 2) + Math.pow(getLocation().getZ() - owner.getLocation().getZ(), 2));
                if (distance >= animationsCarrier.teleportDistanceThreshold())
                    teleport(owner.getLocation());
                else if (distance >= animationsCarrier.approachDistanceThreshold() || Math.abs(owner.getLocation().getY() + animationsCarrier.yOffset() - getLocation().getY()) > 1D)
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

    private void move() {
        this.location = animations.move(findOwnerOrFail(), location);
    }

    private void idle() {
        this.location = animations.idle(findOwnerOrFail(), location);
    }

    /**
     * Similar to Entity#remove.
     * Will mark the pet as deactivated and will remove the armorstand.
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
     * Returns the pet head
     *
     * @return the head
     */
    public ItemStack getDisplay() {
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
     * Returns the armorstand of the pet
     *
     * @return the armorstand
     */
    public ArmorStand getEntity() {
        return entity;
    }

    /**
     * Retrieves pet's name.
     *
     * @return if customName is null, returns 'owner's Pet', else returns customName
     */
    @NotNull
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

    private ArmorStand createArmorStand(Location loc) {
        return (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);

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

    /**
     * Sets the pet head
     *
     * @param itemStack - the new head
     */
    public void setDisplay(ItemStack itemStack) {
        this.display = Objects.requireNonNull(itemStack);
    }

    /**
     * Will set pet's custom name.
     * If passing null, will be used 'owner's Pet'
     *
     * @param customName - the custom name
     */
    public void customName(@Nullable Component customName) {
        if (customName == null)
            setCustomName(null);
        else
            LegacyComponentSerializer.legacyAmpersand().serialize(customName);
    }

    /**
     * Retrieves pet's name.
     *
     * @return if customName is null, returns 'owner's Pet', else returns customName
     */
    public @NotNull Component customName() {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(getCustomName());
    }
}