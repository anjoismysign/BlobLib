package us.mytheria.bloblib.floatingpet;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import us.mytheria.bloblib.managers.Manager;
import us.mytheria.bloblib.managers.ManagerDirector;

import java.util.*;

public class FloatingPetManager<T extends FloatingPet> extends Manager implements Listener {
    private Map<UUID, List<T>> ownerMap;
    private Map<UUID, T> petMap;

    public FloatingPetManager(ManagerDirector managerDirector) {
        super(managerDirector);
        ownerMap = new HashMap<>();
        petMap = new HashMap<>();
        Bukkit.getPluginManager().registerEvents(this, managerDirector.getPlugin());
    }

    @Override
    public void unload() {
        for (List<T> t : ownerMap.values()) {
            for (T floatingPet : t) {
                floatingPet.destroy();
            }
        }
    }

    @Override
    public void reload() {
        ownerMap = new HashMap<>();
        petMap = new HashMap<>();
    }

    /**
     * Will point the pet to the owner and the owner to the pet
     * inside the maps.
     *
     * @param floatingPet - the pet to add
     */
    public void addPet(T floatingPet) {
        petMap.put(floatingPet.getArmorStand().getUniqueId(), floatingPet);
        ownerMap.computeIfAbsent(floatingPet.getOwner().getUniqueId(), k -> new ArrayList<>()).add(floatingPet);
    }

    /**
     * Will retrieve an Optional containing the list of pets
     * for the specified player.
     *
     * @param player - the player to check
     * @return an Optional containing the list of pets
     */
    public Optional<List<T>> hasPets(Player player) {
        return hasPets(player.getUniqueId());
    }

    /**
     * Will retrieve an Optional containing the list of pets
     * for the specified UUID.
     *
     * @param uuid - the UUID to check
     * @return an Optional containing the list of pets
     */
    public Optional<List<T>> hasPets(UUID uuid) {
        return Optional.ofNullable(ownerMap.get(uuid));
    }

    /**
     * Will retrieve an Optional containing the pet for the
     * specified UUID.
     *
     * @param uuid - the UUID to check
     * @return an Optional containing the pet
     */
    public Optional<T> isPet(UUID uuid) {
        return Optional.ofNullable(petMap.get(uuid));
    }

    /**
     * Will retrieve an Optional containing the pet for the
     * specified Entity.
     *
     * @param entity - the Entity to check
     * @return an Optional containing the pet
     */
    public Optional<T> isPet(Entity entity) {
        return isPet(entity.getUniqueId());
    }

    @EventHandler
    public void onPetInteract(PlayerArmorStandManipulateEvent e) {
        ArmorStand armorStand = e.getRightClicked();
        if (isPet(armorStand).isEmpty())
            return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onOwnerRespawn(PlayerRespawnEvent e) {
        Player player = e.getPlayer();
        hasPets(player).ifPresent(pets -> pets.forEach(pet -> {
            Location location = player.getLocation().clone();
            location.setX(location.getX() - 1);
            location.setY(location.getY() + 0.75);
            pet.setPauseLogic(true);
            pet.teleport(location);
            pet.setPauseLogic(false);
        }));
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent e) {
        Player player = e.getPlayer();
        hasPets(player).ifPresent(pets -> pets.forEach(pet -> {
            Location location = player.getLocation().clone();
            location.setX(location.getX() - 1);
            location.setY(location.getY() + 0.75);
            pet.setPauseLogic(true);
            pet.teleport(location);
            pet.setPauseLogic(false);
        }));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        hasPets(player).ifPresent(pets -> pets.forEach(pet -> {
            petMap.remove(pet.getArmorStand().getUniqueId());
            pet.destroy();
        }));
        ownerMap.remove(player.getUniqueId());
    }

    public Map<UUID, List<T>> getOwnerMap() {
        return ownerMap;
    }

    public Map<UUID, T> getPetMap() {
        return petMap;
    }
}
