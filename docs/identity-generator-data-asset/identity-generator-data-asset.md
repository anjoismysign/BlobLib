# IdentityGenerator Data Asset (BukkitIdentityManager)

The IdentityGenerator data-asset pattern bridges the [holoworld](https://github.com/anjoismysign/holoworld) `IdentityGenerator<T>` / `IdentityManager<T>` system with Bukkit, SkeramidCommands, and `BlobPlugin`. Each YAML file defines an `IdentityGenerator` whose fields configure how to produce a `DataAsset` instance. The file **name** (without the `.yml` extension) serves as the identifier that is passed into `generate(identifier)`. This is ideal when your asset carries fields that are not known until registration time (e.g., a permission string or cooldown) but still needs to produce a fully materialized `DataAsset` at runtime.

---

## How It Works

```
Plugin data folder
  └─ <snake_case_name>/
       ├─ some_identifier.yml   ──► deserializes into IdentityGenerator<T>
       └─ another_id.yml        ──► deserializes into IdentityGenerator<T>

IdentityManager (holoworld)
  └─ For each file:
       1. Reads the file name → "some_identifier"
       2. Deserializes the YAML into IdentityGenerator<T> via SnakeYAML
       3. Calls generator.generate("some_identifier") → T (DataAsset)
       4. Caches the resulting T for lookup
```

Use `IdentityGenerator` + `BukkitIdentityManager` when:

- Each asset instance needs a **unique identifier** that is derived from its file name rather than embedded in the YAML body.
- The `IdentityGenerator` class carries **configuration fields** (e.g., `permission`, `experienceCost`) that are used to build the final `DataAsset`, and the identifier from the file name is a core part of the constructed data.
- You want a clean separation between *generator configuration* and *generated asset*. The generator lives in YAML; the produced asset is the runtime object.

---

## Directory & Discovery

- **Base directory:** `<plugin-data-folder>/<snake_case_name>/` — the `name` argument passed to the factory or `PluginManager.addIdentityManager()` is converted via `NamingConventions.toSnakeCase()`.
- **File format:** `.yml` (YAML, parsed via SnakeYAML)
- **Identifier:** the file name without the `.yml` extension (e.g., `warps/spawn.yml` → identifier `"spawn"`)
- **Subdirectories:** supported — all `.yml` files in subfolders are loaded recursively by `SingletonManagerFactory`.
- **Loading:** each file is deserialised into the registered `IdentityGenerator<T>` class; `T` is produced by calling `generator.generate(identifier)`.
- **Duplicate identifiers:** logged and skipped (first loaded wins).
- **Verbose logging:** controlled by `BlobLibConfigManager.isVerbose()` — when enabled, the plugin logger is passed to `SingletonManagerFactory`.

---

## YAML Configuration

### File Scope

Each `.yml` file is deserialised directly into your `IdentityGenerator<T>` implementation via SnakeYAML. There is no wrapping root section — the YAML keys **are** the fields of your generator class.

```yaml
# <plugin-data-folder>/my_warp_data/spawn.yml
permission: "myplugin.warp.spawn"
experienceCost: 50
```

The file name `spawn` becomes the identifier passed to `generate("spawn")`.

### Field Conventions

| YAML Key | Java Field | Type | Required | Description |
|---|---|---|---|---|
| Any snake_case key | Matching camelCase field via SnakeYAML | As declared in generator class | Controlled by `failOnNullField` | Generator field used to produce the final `DataAsset` |

**Naming:** SnakeYAML automatically maps YAML keys (typically `snake_case`) to Java fields (typically `camelCase`). Use `setter` methods or public fields as expected by SnakeYAML's `Constructor`.

---

## API Reference

### `BukkitIdentityManager<T extends DataAsset>`

**Package:** `io.github.anjoismysign.bloblib.managers.asset`

An interface that extends both `IdentityManager<T>` (holoworld) and `CommandTarget<T>` (SkeramidCommands). This gives every manager automatic tab-completion and string-parsing for commands.

```java
public interface BukkitIdentityManager<T extends DataAsset>
        extends IdentityManager<T>, CommandTarget<T> {

    @NotNull Plugin plugin();

    @Override
    default @NotNull Logger logger();

    @Override
    default List<String> get();            // CommandTarget: all identifiers

    @Override
    default @Nullable T parse(String s);   // CommandTarget: map().get(s)
}
```

### `SimpleBukkitIdentityManager<T extends DataAsset>`

A `record` implementation that wraps a `Plugin` and a holoworld `IdentityManager<T>`. All methods delegate to the wrapped manager.

#### Delegated Methods

| Method | Returns | Description |
|---|---|---|
| `generatorClass()` | `Class<? extends IdentityGenerator<T>>` | The registered generator class |
| `directory()` | `File` | The YAML directory for this manager |
| `getIdentifiers()` | `Set<String>` | All loaded identifiers (file names without `.yml`) |
| `reload()` | `void` | Re-scan the directory and reload all assets |
| `fetchGeneration(id)` | `@Nullable DataAssetEntry<T>` | Fetch a single entry by identifier, or `null` |
| `getGeneration(id)` | `DataAssetEntry<T>` | Like `fetchGeneration` but throws `GenerationNotFoundException` on miss |
| `add(IdentityGeneration<T>)` | `boolean` | Add a new generation (writes the generator to a `.yml` file) |
| `size()` | `int` | Number of loaded identifiers |
| `isEmpty()` | `boolean` | Whether the manager has any loaded entries |
| `map()` | `Map<String, T>` | All loaded assets as identifier → asset |
| `stream()` | `Stream<T>` | Sequential stream over all loaded assets |
| `parallelStream()` | `Stream<T>` | Parallel stream over all loaded assets |

### `PluginManager` — Required Instantiation

`PluginManager` (singleton) is the **only** way to instantiate a `BukkitIdentityManager`. You must **never** call `SimpleBukkitIdentityManager.of()` directly. Using `PluginManager.addIdentityManager()` is mandatory because it:

1. **Tracks lifecycle** — the manager is registered in the central registry keyed by generator class, so it is automatically reloaded when `/bloblib reload` runs.
2. **Cleans up on disable** — when your `BlobPlugin` is disabled, `PluginManager.unregisterPlugin()` removes the manager from all internal maps, preventing stale references and memory leaks.
3. **Provides command resolution** — the central registry enables SkeramidCommands and other BlobLib systems to discover your manager by class for command tab-completion and parsing.

Attempting to construct a `SimpleBukkitIdentityManager` directly (e.g. via `SimpleBukkitIdentityManager.of(...)`) will create an orphan manager that will not be reloaded, not be discoverable by other BlobLib components, and will **not** be cleaned up on plugin disable.

#### `addIdentityManager`

```java
public <T extends DataAsset> BukkitIdentityManager<T> addIdentityManager(
    @NotNull Class<? extends IdentityGenerator<T>> generatorClass,
    @NotNull BlobPlugin plugin,
    @NotNull String name,
    boolean failOnNullField
)
```

**Parameters:**

| Parameter | Type | Description |
|---|---|---|
| `generatorClass` | `Class<? extends IdentityGenerator<T>>` | The generator class |
| `plugin` | `BlobPlugin` | The owning plugin (must extend `BlobPlugin`) |
| `name` | `String` | Logical name → snake_case directory |
| `failOnNullField` | `boolean` | Passed through to SnakeYAML |

**How it works (internally):**

`addIdentityManager` delegates to `SimpleBukkitIdentityManager.of()` and then registers the resulting manager in the central registry:

1. Constructs the directory: `new File(plugin.getDataFolder(), NamingConventions.toSnakeCase(name))`
2. Checks `BlobLibConfigManager.getInstance().isVerbose()` — if verbose, passes `plugin.getLogger()` to `SingletonManagerFactory`
3. Calls `SingletonManagerFactory.INSTANCE.identityManager(clazz, parentDirectory, logger, failOnNullField)`
4. Wraps the result in `new SimpleBukkitIdentityManager<>(plugin, identityManager)` (already `reload()`-ed)
5. Stores the manager in the registry, keyed by `generatorClass`
6. Tracks the owning `BlobPlugin` for automatic cleanup

**Returns:** the newly created `BukkitIdentityManager<T>` (already loaded).

#### `getIdentityManager`

```java
@Nullable
public <T extends DataAsset> BukkitIdentityManager<T> getIdentityManager(
    Class<? extends IdentityGenerator<T>> generatorClass
)
```

Returns the previously registered manager, or `null`. Useful when another part of your plugin or another plugin needs to access the same manager.

---

## Referencing Non-IdentityGenerator DataAssets by Identifier

Your `DataAsset` implementation may need to reference built-in BlobLib assets such as `BlobMessage`, `TranslatableItem`, `TranslatablePositionable`, `BlobSound`, or `Action`. These are *not* managed via `IdentityGenerator` — they are loaded through their respective `DataAssetManager<T>` or `LocalizableDataAssetManager<T>`.

The convention is:

1. **Store only the identifier** (`String`) in your `DataAsset` record.
2. **Resolve the full asset** through the appropriate `BlobLibAPI` method at runtime, typically in a getter method on your record.

### Common BlobLib API Accessors

| Asset Type | API Singleton | Lookup Method |
|---|---|---|
| `TranslatablePositionable` | `BlobLibTranslatableAPI.getInstance()` | `getTranslatablePositionable(identifier)` |
| `TranslatableItem` | `BlobLibTranslatableAPI.getInstance()` | `getTranslatableItem(identifier)` |
| `TranslatableBlock` | `BlobLibTranslatableAPI.getInstance()` | `getTranslatableBlock(identifier)` |
| `TranslatableSnippet` | `BlobLibTranslatableAPI.getInstance()` | `getTranslatableSnippet(identifier)` |
| `TranslatableArea` | `BlobLibTranslatableAPI.getInstance()` | `getTranslatableArea(identifier)` |
| `BlobMessage` | `BlobLibMessageAPI.getInstance()` | Access via `getMessageManager().getDefault()` |
| `BlobSound` | `BlobLibSoundAPI.getInstance()` | Access via `getSoundManager().mapAssets()` |
| `Action<Entity>` | `BlobLibActionAPI.getInstance()` | Access via `getActionManager().mapAssets()` |

### Example: Referencing TranslatablePositionable by Identifier

```java
public record WarpData(
    String identifier,
    String permission,
    int experienceCost,
    String positionableIdentifier  // ← store only the String
) implements DataAsset {

    @NotNull
    public TranslatablePositionable positionable() {
        return Objects.requireNonNull(
            BlobLibTranslatableAPI.getInstance()
                .getTranslatablePositionable(positionableIdentifier),
            "'" + positionableIdentifier + "' doesn't point to a TranslatablePositionable"
        );
    }
}
```

---

## Complete Example: WarpData

Below is a full, copy-paste-ready example of an external plugin that defines a `WarpData` asset, an `IdentityGenerator` for it, registers it with BlobLib, and gets automatic `CommandTarget` support.

### 1. Define the `DataAsset` Record

```java
package com.example.myplugin.asset;

import io.github.anjoismysign.bloblib.api.BlobLibTranslatableAPI;
import io.github.anjoismysign.bloblib.entities.translatable.TranslatablePositionable;
import io.github.anjoismysign.holoworld.asset.DataAsset;
import io.github.anjoismysign.holoworld.asset.IdentityGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record WarpData(
    @NotNull String identifier,
    @NotNull String permission,
    int experienceCost,
    @NotNull String positionableIdentifier
) implements DataAsset {

    /**
     * Resolves the TranslatablePositionable referenced by positionableIdentifier.
     * The identifier points to a TranslatablePositionable registered in BlobLib's
     * translatable system — the full object is looked up at runtime.
     */
    @NotNull
    public TranslatablePositionable positionable() {
        return Objects.requireNonNull(
            BlobLibTranslatableAPI.getInstance()
                .getTranslatablePositionable(positionableIdentifier),
            "'" + positionableIdentifier + "' doesn't point to a TranslatablePositionable"
        );
    }

    /**
     * Inner IdentityGenerator class.
     * SnakeYAML deserialises each .yml file into this class.
     * The fields here become the YAML keys.
     */
    public static class Info implements IdentityGenerator<WarpData> {
        private String permission;
        private int experienceCost;
        private String positionableIdentifier;

        @Override
        public @NotNull WarpData generate(@NotNull String identifier) {
            Objects.requireNonNull(
                BlobLibTranslatableAPI.getInstance()
                    .getTranslatablePositionable(positionableIdentifier),
                "'" + positionableIdentifier + "' doesn't point to a TranslatablePositionable"
            );
            return new WarpData(identifier, permission, experienceCost, positionableIdentifier);
        }

        // ---- Getters / Setters (required by SnakeYAML) ----

        public String getPermission() {
            return permission;
        }

        public void setPermission(String permission) {
            this.permission = permission;
        }

        public int getExperienceCost() {
            return experienceCost;
        }

        public void setExperienceCost(int experienceCost) {
            this.experienceCost = experienceCost;
        }

        public String getPositionableIdentifier() {
            return positionableIdentifier;
        }

        public void setPositionableIdentifier(String positionableIdentifier) {
            this.positionableIdentifier = positionableIdentifier;
        }
    }
}
```

### 2. YAML Files

Each warp is a separate `.yml` file under the configured directory. The file name becomes the identifier.

**`plugins/MyWarpPlugin/warp_data/spawn.yml`**

```yaml
permission: "mywarpplugin.warp.spawn"
experienceCost: 50
positionableIdentifier: "warps.spawn_location"
```

**`plugins/MyWarpPlugin/warp_data/shop.yml`**

```yaml
permission: "mywarpplugin.warp.shop"
experienceCost: 25
positionableIdentifier: "warps.shop_location"
```

### 3. Extend `BlobPlugin` and Register (Mandatory: Use `PluginManager`)

Instantiation of a `BukkitIdentityManager` must go through `PluginManager.addIdentityManager()`. This ensures lifecycle tracking, reload support, and automatic cleanup. Directly calling `SimpleBukkitIdentityManager.of()` is **not supported** and will create an orphan manager.

```java
package com.example.myplugin;

import com.example.myplugin.asset.WarpData;
import io.github.anjoismysign.bloblib.managers.BlobPlugin;
import io.github.anjoismysign.bloblib.managers.PluginManager;
import io.github.anjoismysign.bloblib.managers.asset.BukkitIdentityManager;
import io.github.anjoismysign.bloblib.entities.IManagerDirector;
import org.jetbrains.annotations.NotNull;

public class MyWarpPlugin extends BlobPlugin {

    private BukkitIdentityManager<WarpData> warpManager;

    @Override
    public void onEnable() {
        // ManagerDirector constructor calls registerToBlobLib() automatically.
        // (Not shown: implement getManagerDirector() and a ManagerDirector class)
        // director = new MyManagerDirector(this);

        // MUST use PluginManager.addIdentityManager() — the only supported way.
        // The name "WarpData" is converted to "warp_data/" via NamingConventions.toSnakeCase().
        warpManager = PluginManager.getInstance().addIdentityManager(
            WarpData.Info.class,
            this,        // your BlobPlugin instance
            "WarpData",
            true         // failOnNullField = true → null fields throw at load time
        );
        // warpManager is now loaded and registered in the central registry.
    }

    @Override
    public @NotNull IManagerDirector getManagerDirector() {
        return director;
    }

    public BukkitIdentityManager<WarpData> getWarpManager() {
        return warpManager;
    }
}
```

### 4. Directory Structure at Runtime

```
plugins/MyWarpPlugin/
  ├─ warp_data/                     ← snake_case of "WarpData"
  │    ├─ spawn.yml
  │    ├─ shop.yml
  │    └─ nether_ fortress.yml
  ├─ config.yml
  └─ ... (other plugin files)
```

### 5. Using the Manager in Commands

Because `BukkitIdentityManager<T>` implements `CommandTarget<T>`, it can be used directly as a command parameter:

```java
import io.github.anjoismysign.skeramidcommands.command.Command;

// In your command setup:
Command warpCmd = ...;
warpCmd.setParameters(warpManager);  // tab-completion + parsing built in

warpCmd.onExecute((pm, args) -> {
    WarpData warp = warpManager.parse(args[0]);
    if (warp != null) {
        // warp.identifier()    → "spawn"
        // warp.permission()    → "mywarpplugin.warp.spawn"
        // warp.experienceCost() → 50
        // warp.positionable()  → resolves TranslatablePositionable at runtime
    }
});
```

---

## Lifecycle & Registration Order

```
Plugin enables (MyWarpPlugin extends BlobPlugin)
  └─ ManagerDirector constructor
       ├─ Creates BlobFileManager
       ├─ Calls plugin.registerToBlobLib(this)
       │    └─ PluginManager.registerPlugin(plugin, director)
       │         ├─ Registers plugin
       │         └─ loadAssets(plugin, director)
       │              └─ Loads all BlobLib-native assets (messages, sounds, etc.)
       └─ Scans @BManager / @BListener annotations

  └─ PluginManager.addIdentityManager(WarpData.Info.class, plugin, "WarpData", true)
       └─ (internally: SingletonManagerFactory.INSTANCE.identityManager(...)
            wrapped in SimpleBukkitIdentityManager,
            registered in central registry)
            ├─ Scans <data-folder>/warp_data/ for *.yml files
            ├─ For each file:
            │    ├─ Reads file name → identifier (e.g. "spawn")
            │    ├─ Deserialises YAML → WarpData.Info (IdentityGenerator)
            │    └─ Calls info.generate("spawn") → WarpData
            └─ Stores all IdentityGeneration<T> + resulting T in memory

Runtime command
  └─ warpManager.parse("spawn") → WarpData (from cache)
  └─ warp.positionable() → BlobLibTranslatableAPI lookup at call-time

Reload (/bloblib reload)
  └─ PluginManager.reload()
       └─ For each registered manager: manager.reload()
            └─ Re-scans directory, re-reads all YAML, re-generates assets

Plugin disables
  └─ BlobPlugin.onDisable()
       ├─ ManagerDirector.realUnload()
       └─ unregisterFromBlobLib()
            └─ PluginManager.unregisterPlugin(plugin)
                 └─ Removes warpManager from identityManagers map
```

---

## `failOnNullField` Behaviour

| Value | Effect |
|---|---|
| `true` | SnakeYAML's `Constructor` does **not** skip missing properties. Any field that is absent from the YAML (or `null`) causes an exception to be thrown at load time. Use during development to catch incomplete configurations. |
| `false` | Missing fields are silently set to their Java defaults (`null` for objects, `0` for primitives, `false` for booleans). Use in production when partial configurations are acceptable. |

Null checks for *optional* fields should be handled inside your `IdentityGenerator.generate()` implementation regardless of this flag.

---

## See Also

- [BlobMessage](../data-asset/blob-message.md) — `DataAsset` for chat/actionbar/title messages
- [Translatable Item](../data-asset/translatable-item.md) — `DataAsset` for locale-aware item stacks
- [Translatable Positionable](../data-asset/translatablepositionable.md) — `DataAsset` for location-based displays
- [BlobSound](../data-asset/blob-sound.md) — `DataAsset` for sound definitions
- [Actions](../data-asset/action.md) — `DataAsset` for executable actions
- `io.github.anjoismysign.bloblib.api.BlobLibTranslatableAPI` — runtime lookup of translatable assets
- `io.github.anjoismysign.holoworld.asset.IdentityGenerator` — the holoworld interface
- `io.github.anjoismysign.holoworld.asset.IdentityGeneration` — record pairing an identifier + generator
- `io.github.anjoismysign.holoworld.manager.IdentityManager` — the core manager interface
- `io.github.anjoismysign.bloblib.managers.PluginManager` — central registry for all manager types
