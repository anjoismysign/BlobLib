package us.mytheria.bloblib.reflection.nonlivingentity;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.lang.reflect.Method;
import java.util.function.Supplier;

public class NLEWrapper1_20_R1 implements NonLivingEntityWrapper {
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

    public void vehicleTeleport(Entity vehicle, Location location) {
        try {
            methods[1].invoke(methods[0].invoke(vehicle),
                    location.getX(),
                    location.getY(),
                    location.getZ(),
                    location.getYaw(),
                    location.getPitch());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
