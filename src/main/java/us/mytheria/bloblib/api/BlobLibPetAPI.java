package us.mytheria.bloblib.api;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.BlobLib;

public class BlobLibPetAPI {
    private static BlobLibPetAPI instance;
    private final BlobLib plugin;
    private final NamespacedKey petTypeKey;

    private BlobLibPetAPI(BlobLib plugin) {
        this.plugin = plugin;
        this.petTypeKey = new NamespacedKey(plugin, "pet-type");
    }

    public static BlobLibPetAPI getInstance(BlobLib plugin) {
        if (instance == null) {
            if (plugin == null)
                throw new NullPointerException("injected dependency is null");
            BlobLibPetAPI.instance = new BlobLibPetAPI(plugin);
        }
        return instance;
    }

    public static BlobLibPetAPI getInstance() {
        return getInstance(null);
    }


    public NamespacedKey getPetTypeKey() {
        return petTypeKey;
    }

    /**
     * Checks whether the entity is a pet.
     *
     * @param entity The entity to check.
     * @return True if the entity is a pet. False otherwise.
     */
    public boolean isPet(Entity entity) {
        return getPetType(entity) != null;
    }

    /**
     * Gets the pet type of the entity.
     *
     * @param entity The entity to get the pet type of.
     * @return The pet type of the entity. Null if not set.
     */
    @Nullable
    public String getPetType(Entity entity) {
        if (!entity.getPersistentDataContainer().has(petTypeKey, PersistentDataType.STRING))
            return null;
        return entity.getPersistentDataContainer().get(petTypeKey, PersistentDataType.STRING);
    }

    /**
     * Sets the pet type of the entity.
     *
     * @param entity The entity to set the pet type of.
     * @param type   The type to set.
     */
    public void setPetType(@NotNull Entity entity,
                           @NotNull String type) {
        entity.getPersistentDataContainer().set(petTypeKey, PersistentDataType.STRING, type);
    }
}
