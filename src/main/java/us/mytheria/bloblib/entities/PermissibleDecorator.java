package us.mytheria.bloblib.entities;

import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;

public record PermissibleDecorator(
        @NotNull Address<Permissible> address) implements AddressDecorator<Permissible>, Permissible {

    public static PermissibleDecorator of(@NotNull Address<Permissible> address) {
        Objects.requireNonNull(address, "'address' is null");
        return new PermissibleDecorator(address);
    }

    @Nullable
    public String getStartsWithPermission(@NotNull String prefix) {
        PermissionAttachmentInfo lookup = getEffectivePermissions().stream().filter(info -> info.getPermission().startsWith(prefix)).findFirst().orElse(null);
        if (lookup == null)
            return null;
        return lookup.getPermission().substring(prefix.length() - 1);
    }

    @Override
    public boolean isPermissionSet(@NotNull String name) {
        return address.look().isPermissionSet(name);
    }

    @Override
    public boolean isPermissionSet(@NotNull Permission permission) {
        return address.look().isPermissionSet(permission);
    }

    @Override
    public boolean hasPermission(@NotNull String name) {
        return address.look().hasPermission(name);
    }

    @Override
    public boolean hasPermission(@NotNull Permission permission) {
        return address.look().hasPermission(permission);
    }

    @NotNull
    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String name, boolean value) {
        return address.look().addAttachment(plugin, name, value);
    }

    @NotNull
    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin) {
        return address.look().addAttachment(plugin);
    }

    @Nullable
    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String name, boolean value, int ticks) {
        return address.look().addAttachment(plugin, name, value, ticks);
    }

    @Nullable
    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, int ticks) {
        return address.look().addAttachment(plugin, ticks);
    }

    @Override
    public void removeAttachment(@NotNull PermissionAttachment permissionAttachment) {
        address.look().removeAttachment(permissionAttachment);
    }

    @Override
    public void recalculatePermissions() {
        address.look().recalculatePermissions();
    }

    @NotNull
    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return address.look().getEffectivePermissions();
    }

    @Override
    public boolean isOp() {
        return address.look().isOp();
    }

    @Override
    public void setOp(boolean value) {
        address.look().setOp(value);
    }
}
