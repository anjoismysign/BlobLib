package io.github.anjoismysign.bloblib.entities;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Supplier;

public record PlayerDecorator(@NotNull PlayerAddress address,
                              @NotNull Supplier<Boolean> validSupplier) implements AddressDecorator<Player> {

    public static PlayerDecorator of(@NotNull Player player){
        return new PlayerDecorator(PlayerAddress.of(player.getUniqueId().toString()), player::isConnected);
    }

    public static PlayerDecorator address(@NotNull PlayerAddress address) {
        Objects.requireNonNull(address, "'address' is null");
        return new PlayerDecorator(address, () -> address.look() != null);
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

    @Override
    public boolean isValid(){
        return validSupplier.get();
    }

}
