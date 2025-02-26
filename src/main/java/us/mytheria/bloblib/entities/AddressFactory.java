package us.mytheria.bloblib.entities;

import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;

public class AddressFactory {

    private static AddressFactory INSTANCE;

    public static AddressFactory getInstance(){
        if (INSTANCE == null)
            INSTANCE = new AddressFactory();
        return INSTANCE;
    }

    public Address<Permissible> of(@NotNull Permissible permissible){
        Objects.requireNonNull(permissible, "'permissible' is null");
        return new Address<>() {@Override
            public@NotNull Permissible look() {
                return permissible;
            }};
    }
}
