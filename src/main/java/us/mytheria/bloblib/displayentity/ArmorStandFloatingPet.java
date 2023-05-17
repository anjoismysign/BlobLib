package us.mytheria.bloblib.displayentity;

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
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.BlobLib;

import java.util.UUID;

public class ArmorStandFloatingPet implements DisplayPet<ArmorStand> {
    private Particle particle;
    private ArmorStand armorStand;
    private Location location;
    private UUID owner;
    private boolean activated, pauseLogic;
    private ItemStack display;
    private String customName;
    private DisplayEntityAnimations animations;
    private BukkitTask logicTask;

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
    public ArmorStandFloatingPet(Player owner, ItemStack itemStack, @Nullable Particle particle,
                                 @Nullable String customName) {
        Material type = itemStack.getType();
        if (!type.isItem() && type.isBlock())
            throw new IllegalArgumentException("ItemStack must be an item or a block");
        this.pauseLogic = false;
        setOwner(owner);
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
        Location loc = getOwner().getLocation().clone();
        loc.setX(loc.getX() - 1);
        loc.setY(loc.getY() + 0.85);
        setLocation(loc);
        spawnArmorStand(loc);
    }

    private void spawnArmorStand(Location loc) {
        BlobLib plugin = BlobLib.getInstance();
        armorStand = createArmorStand(loc);
        setCustomName(getCustomName());
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> armorStand.setCustomNameVisible(true), 1);
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> armorStand.setGravity(false), 3);
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> armorStand.setSmall(true), 4);
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> armorStand.setInvulnerable(true), 5);
        armorStand.getEquipment().setHelmet(getDisplay());
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> armorStand.setVisible(false), 6);
        initAnimations(plugin);

    }

    /**
     * Will set pet's custom name.
     * If passing null, will be used 'owner's Pet'
     *
     * @param customName - the custom name
     */
    public void setCustomName(String customName) {
        this.customName = customName;
        if (armorStand == null)
            return;
        armorStand.setCustomNameVisible(true);
        armorStand.setCustomName(customName);
    }

    private void initAnimations(JavaPlugin plugin) {
        animations = new DisplayEntityAnimations(this, 0.5, 0.55,
                0.025, 0.2, -0.5);
        initLogic(plugin);
    }

    private void initLogic(JavaPlugin plugin) {
        logicTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            Player owner = getOwner();
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
        this.location = animations.moveAway(getOwner(), location);
    }

    private void move() {
        this.location = animations.move(getOwner(), location);
    }

    private void idle() {
        this.location = animations.idle(getOwner(), location);
    }

    /**
     * Similar to Entity#remove.
     * Will mark the pet as deactivated and will remove the armorstand.
     */
    public void destroy() {
        setActive(false);
        Bukkit.getScheduler().cancelTask(logicTask.getTaskId());
        if (armorStand != null) {
            armorStand.remove();
            armorStand = null;
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
    public Player getOwner() {
        return Bukkit.getPlayer(owner);
    }

    /**
     * Sets the owner of the pet
     *
     * @param owner - the new owner
     */
    public void setOwner(Player owner) {
        this.owner = owner.getUniqueId();
    }

    /**
     * Returns the armorstand of the pet
     *
     * @return the armorstand
     */
    public ArmorStand getEntity() {
        return armorStand;
    }

    /**
     * Retrieves pet's name.
     *
     * @return if customName is null, returns 'owner's Pet', else returns customName
     */
    public String getCustomName() {
        String name = getOwner().getName() + "'s Pet";
        if (this.customName != null)
            name = this.customName;
        return name;
    }

    /**
     * Teleports the pet
     *
     * @param loc - the new location
     */
    public void teleport(Location loc) {
        if (armorStand != null) {
            loadChunks(location);
            loadChunks(loc);
            armorStand.teleport(loc);
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
     * @param display - the new head
     */
    public void setDisplay(ItemStack display) {
        this.display = display;
    }
}