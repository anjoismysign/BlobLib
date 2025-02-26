package us.mytheria.bloblib.entities;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record PlayerDecorator(@NotNull PlayerAddress address) implements AddressDecorator<Player> {

    public static PlayerDecorator address(@NotNull PlayerAddress address) {
        Objects.requireNonNull(address, "'address' is null");
        return new PlayerDecorator(address);
    }

    @NotNull
    public static PlayerDecorator address(@NotNull String address) {
        return address(PlayerAddress.of(address));
    }

    @NotNull
    public PermissibleDecorator getPermissible() {
        Player lookup = address.look();
        if (lookup == null)
            throw new NullPointerException("'lookup' is null");
        return PermissibleDecorator.of(AddressFactory.getInstance().of(lookup));
    }

}
