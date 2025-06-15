package io.github.anjoismysign.bloblib.entities;

import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;

public interface PermissionDecorator {

    @NotNull
    default PermissionDecorator proxyPermissionDecorator() {
        Permission permission = getPermission();
        return new PermissionDecorator() {
            @Override
            public @NotNull Permission getPermission() {
                return permission;
            }
        };
    }

    @NotNull
    Permission getPermission();

    @NotNull
    default String getChildPermission(@NotNull String name) {
        return getPermission().getName() + "." + name;
    }


}
