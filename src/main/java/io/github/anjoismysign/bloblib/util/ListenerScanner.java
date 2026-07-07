package io.github.anjoismysign.bloblib.util;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Supplier;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;

public final class ListenerScanner {

    private ListenerScanner() {}

    /**
     * Scans the specified package for concrete {@link Listener} implementations,
     * instantiates each one using its public no-argument constructor, and registers
     * the resulting listeners with Bukkit's {@link org.bukkit.plugin.PluginManager}.
     * <p>
     * Only listener classes located directly in the specified package are scanned.
     * Sub-packages are ignored. Classes that are abstract, interfaces, or do not
     * provide a public no-argument constructor are skipped.
     * <p>
     * If the supplied debug flag evaluates to {@code true}, the simple name of each
     * successfully registered listener is logged to the plugin logger.
     *
     * @param plugin the plugin whose JAR will be scanned and with which listeners
     *               will be registered
     * @param packageName the package containing listener classes to scan
     * @param debugSupplier supplies whether debug logging is enabled
     */
    public static void scanAndRegister(JavaPlugin plugin, String packageName, Supplier<Boolean> debugSupplier){
        List<Listener> listeners = ListenerScanner.scan(
                plugin,
                packageName
        );

        for (Listener listener : listeners) {
            Bukkit.getPluginManager().registerEvents(listener, plugin);
            if (debugSupplier.get()) {
                plugin.getLogger().info("Registered listener: " + listener.getClass().getSimpleName());
            }
        }
    }

    private static List<Listener> scan(JavaPlugin plugin, String packageName) {
        List<Listener> result = new ArrayList<>();
        String packagePath = packageName.replace('.', '/');

        File jarFile = getJarFile(plugin);
        if (jarFile == null) {
            plugin.getLogger().warning("[ListenerScanner] Could not locate plugin JAR; falling back to empty listener list.");
            return result;
        }

        ClassLoader classLoader = plugin.getClass().getClassLoader();

        try (JarFile jar = new JarFile(jarFile)) {
            Enumeration<JarEntry> entries = jar.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();

                // Keep only .class files directly inside the target package (non-recursive).
                // Swap the startsWith+!contains check for a plain startsWith if you want
                // recursive scanning of sub-packages.
                if (!name.startsWith(packagePath + "/")
                        || !name.endsWith(".class")
                        || name.substring(packagePath.length() + 1).contains("/")) {
                    continue;
                }

                String className = name
                        .replace('/', '.')
                        .replace(".class", "");

                try {
                    Class<?> clazz = classLoader.loadClass(className);

                    if (!Listener.class.isAssignableFrom(clazz)) continue;
                    if (clazz.isInterface()) continue;
                    if (Modifier.isAbstract(clazz.getModifiers())) continue;

                    // Require a public no-arg constructor (mirrors Spring's default behaviour)
                    Constructor<?> constructor = clazz.getConstructor();  // throws NoSuchMethodException if not public no-arg
                    Listener instance = (Listener) constructor.newInstance();
                    result.add(instance);

                } catch (NoSuchMethodException exception) {
                    plugin.getLogger().warning("[ListenerScanner] Skipping " + className
                            + ": no public no-arg constructor found.");
                } catch (Exception exception) {
                    plugin.getLogger().log(Level.SEVERE,
                            "[ListenerScanner] Failed to instantiate " + className, exception);
                }
            }
        } catch (Exception exception) {
            plugin.getLogger().log(Level.SEVERE, "[ListenerScanner] JAR scan failed", exception);
        }

        return result;
    }

    /**
     * Resolves the physical JAR file for the given plugin via its ClassLoader's
     * URL, which Paper/Bukkit always sets to the plugin's JAR path.
     */
    private static File getJarFile(JavaPlugin plugin) {
        try {
            URL location = plugin.getClass()
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation();
            return new File(location.toURI());
        } catch (Exception exception) {
            plugin.getLogger().log(Level.WARNING,
                    "[ListenerScanner] Could not resolve JAR via ProtectionDomain, trying ClassLoader URL", exception);
        }

        // Fallback: ask the ClassLoader directly
        try {
            URL resource = plugin.getClass().getClassLoader()
                    .getResource(plugin.getClass().getName().replace('.', '/') + ".class");
            if (resource != null) {
                // URL is jar:file:/path/to/plugin.jar!/com/example/...
                String path = resource.getPath();
                String jarPath = path.substring("file:".length(), path.indexOf('!'));
                return new File(jarPath);
            }
        } catch (Exception ignored) {}

        return null;
    }
}
