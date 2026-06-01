# External Plugin API Integration (SkeramidCommands)

BlobLib provides three Bukkit-aware manager interfaces that bridge [holoworld](https://github.com/anjoismysign/holoworld)'s `DataAsset` system with [SkeramidCommands](https://github.com/anjoismysign/skeramidcommands)' `CommandTarget`. This allows external plugins to define custom data carrier classes backed by YAML files, register them via BlobLib's `PluginManager`, and get **automatic command tab-completion and string parsing** from SkeramidCommands — all without writing command plumbing.

---

## How It Works

The integration is built on three layers:

| Layer | Library | Role |
|---|---|---|
| **DataAsset** | holoworld | Interface that marks a class as a loadable data carrier from YAML/JSON |
| **Manager interfaces** (`IdentityManager`, `AssetManager`, `GeneratorManager`) | holoworld | Read, cache, and reload `DataAsset` instances from a file directory |
| **CommandTarget\<T\>** | SkeramidCommands | Provides `List<String> get()` for tab-completion and `T parse(String)` for argument resolution |

BlobLib combines each holoworld manager interface with SkeramidCommands' `CommandTarget<T>` into a single Bukkit-specific interface. Plugins then register concrete implementations through `PluginManager`, which stores them centrally and ties them to the owning `BlobPlugin` for automatic lifecycle management.

---

## The Three Bukkit Manager Interfaces

Each interface lives in the `io.github.anjoismysign.bloblib.managers.asset` package and follows the same pattern: extend a holoworld manager + `CommandTarget<T>`, expose a `Plugin plugin()`, and provide default implementations of `CommandTarget.get()` and `CommandTarget.parse()` that delegate to the inherited `map()`.

### `BukkitIdentityManager<T extends DataAsset>`

```java
public interface BukkitIdentityManager<T extends DataAsset>
        extends IdentityManager<T>, CommandTarget<T> {

    @NotNull Plugin plugin();

    @Override default @NotNull Logger logger();
    @Override default List<String> get();       // map().keySet() as a list
    @Override default @Nullable T parse(String s);  // map().get(s)
}
```

For assets where each identifier maps to an `IdentityGenerator<T>` that produces a `T`. The YAML directory is treated as a collection of *generator definitions* rather than raw instances.

### `BukkitAssetManager<T extends DataAsset>`

```java
public interface BukkitAssetManager<T extends DataAsset>
        extends AssetManager<T>, CommandTarget<T> {

    @NotNull Plugin plugin();

    @Override default @NotNull Logger logger();
    @Override default List<String> get();
    @Override default @Nullable T parse(String s);
}
```

For assets where each YAML file deserialises directly into a `T` instance. The identifiers are the filenames (or section keys within files), and the values are the deserialised objects.

### `BukkitGeneratorManager<T extends DataAsset>`

```java
public interface BukkitGeneratorManager<T extends DataAsset>
        extends GeneratorManager<T>, CommandTarget<T> {

    @NotNull Plugin plugin();

    @Override default @NotNull Logger logger();
    @Override default List<String> get();
    @Override default @Nullable T parse(String s);
}
```

For generated assets where each YAML file defines an `AssetGenerator<T>` that produces one or more `T` instances.

---

## Convenience Record Implementations

Each interface has a Java `record` implementation in the same package that delegates to `SingletonManagerFactory` from holoworld and wraps the result:

| Interface | Record Implementation | Factory Method |
|---|---|---|
| `BukkitIdentityManager<T>` | `SimpleBukkitIdentityManager<T>` | `SimpleBukkitIdentityManager.of(clazz, plugin, name, failOnNullField)` |
| `BukkitAssetManager<T>` | `SimpleBukkitAssetManager<T>` | `SimpleBukkitAssetManager.of(clazz, plugin, name, failOnNullField)` |
| `BukkitGeneratorManager<T>` | `SimpleBukkitGeneratorManager<T>` | `SimpleBukkitGeneratorManager.of(clazz, plugin, name, failOnNullField)` |

All three factory methods:

1. Derive the YAML directory from `plugin.getDataFolder() / snake_case(name)` via `NamingConventions.toSnakeCase`
2. Check `BlobLibConfigManager.getInstance().isVerbose()` for logging
3. Call `SingletonManagerFactory.INSTANCE.<managerType>(...)` to create the holoworld manager
4. Return the Bukkit record that wraps both the `Plugin` reference and the holoworld manager

### `SimpleBukkitIdentityManager<T>.of()`

| Parameter | Type | Description |
|---|---|---|
| `clazz` | `Class<? extends IdentityGenerator<T>>` | The generator class that reads YAML and produces `T` instances |
| `plugin` | `Plugin` | The owning Bukkit plugin |
| `name` | `String` | Logical name (converted to snake_case for the directory) |
| `failOnNullField` | `boolean` | Whether to throw on null configuration fields |

### `SimpleBukkitAssetManager<T>.of()`

| Parameter | Type | Description |
|---|---|---|
| `clazz` | `Class<T>` | The `DataAsset` class to deserialise YAML into |
| `plugin` | `Plugin` | The owning Bukkit plugin |
| `name` | `String` | Logical name (converted to snake_case for the directory) |
| `failOnNullField` | `boolean` | Whether to throw on null configuration fields |

### `SimpleBukkitGeneratorManager<T>.of()`

| Parameter | Type | Description |
|---|---|---|
| `clazz` | `Class<? extends AssetGenerator<T>>` | The generator class that reads YAML and produces `T` instances |
| `plugin` | `Plugin` | The owning Bukkit plugin |
| `name` | `String` | Logical name (converted to snake_case for the directory) |
| `failOnNullField` | `boolean` | Whether to throw on null configuration fields |

### Delegated Methods (all three records)

Each record delegates these methods to the underlying holoworld manager:

| Method | Returns | Description |
|---|---|---|
| `directory()` | `File` | The YAML directory for this manager |
| `getIdentifiers()` | `Set<String>` | All loaded identifiers |
| `reload()` | `void` | Re-scan the directory and reload all assets |
| `generatorClass()` / `assetClass()` | `Class<?>` | The registered class (where applicable) |
| `fetchGeneration(id)` / `fetchAsset(id)` | `DataAssetEntry<T>` | Fetch a single entry by identifier |
| `add(...)` | `boolean` | Add a new entry to the manager |

---

## `PluginManager` — Central Registry

`PluginManager` (singleton, `io.github.anjoismysign.bloblib.managers.PluginManager`) is the central registry where external plugins register their managers. It stores three maps:

```java
Map<Class<?>, BukkitAssetManager<?>> assetManagers
Map<Class<?>, BukkitGeneratorManager<?>> generatorManagers
Map<Class<?>, BukkitIdentityManager<?>> identityManagers
```

### Registration Methods

#### `addAssetManager`

```java
public <T extends DataAsset> BukkitAssetManager<T> addAssetManager(
    @NotNull Class<T> assetClass,
    @NotNull BlobPlugin plugin,
    boolean failOnNullField
)

public <T extends DataAsset> BukkitAssetManager<T> addAssetManager(
    @NotNull Class<T> assetClass,
    @NotNull BlobPlugin plugin,
    @NotNull String name,
    boolean failOnNullField
)
```

Creates a `SimpleBukkitAssetManager` via `SimpleBukkitAssetManager.of(assetClass, plugin, name, failOnNullField)`, stores it in the registry, and tracks the association so the manager is cleaned up when the plugin is unregistered.

#### `addGeneratorManager`

```java
public <T extends DataAsset> BukkitGeneratorManager<T> addGeneratorManager(
    @NotNull Class<? extends AssetGenerator<T>> generatorClass,
    @NotNull BlobPlugin plugin,
    boolean failOnNullField
)

public <T extends DataAsset> BukkitGeneratorManager<T> addGeneratorManager(
    @NotNull Class<? extends AssetGenerator<T>> generatorClass,
    @NotNull BlobPlugin plugin,
    @NotNull String name,
    boolean failOnNullField
)
```

Same pattern — creates a `SimpleBukkitGeneratorManager` and registers it.

#### `addIdentityManager`

```java
public <T extends DataAsset> BukkitIdentityManager<T> addIdentityManager(
    @NotNull Class<? extends IdentityGenerator<T>> generatorClass,
    @NotNull BlobPlugin plugin,
    @NotNull String name,
    boolean failOnNullField
)
```

Creates a `SimpleBukkitIdentityManager` and registers it.

### Lookup Methods

```java
@Nullable
public <T extends DataAsset> BukkitAssetManager<T> getAssetManager(Class<?> assetClass)

@Nullable
public <T extends DataAsset> BukkitGeneratorManager<T> getGeneratorManager(
    Class<? extends AssetGenerator<T>> generatorClass)

@Nullable
public <T extends DataAsset> BukkitIdentityManager<T> getIdentityManager(
    Class<? extends IdentityGenerator<T>> generatorClass)
```

Each returns the registered manager or `null` if not found.

---

## `BlobPlugin` — Base Class for External Plugins

External plugins that want to use BlobLib's asset system **must** extend `BlobPlugin` (instead of `JavaPlugin` directly). `BlobPlugin` is declared as:

```java
public abstract class BlobPlugin extends JavaPlugin
        implements PermissionDecorator, ComponentConsumer
```

### Required Override

```java
@NotNull
public abstract IManagerDirector getManagerDirector();
```

Every `BlobPlugin` must provide a `ManagerDirector` (or a custom implementation of `IManagerDirector`).

### Registration Lifecycle

1. **Construction**: `ManagerDirector`'s constructor calls `plugin.registerToBlobLib(this)`, which triggers `PluginManager.registerPlugin(plugin, director)`.
2. **Auto-loading**: `PluginManager.registerPlugin` calls `loadAssets(plugin, director)`, which loads all BlobLib-native asset types (messages, sounds, translatables, etc.) from the plugin's directories.
3. **On disable**: `BlobPlugin.onDisable()` calls `getManagerDirector().unload()` (or `realUnload()`) and then `unregisterFromBlobLib()`, which removes all asset-manager registrations from `PluginManager`.

### Serializable Managers

`BlobPlugin` also supports runtime serializable objects managed via `BukkitSerializableManager`:

```java
public <T extends LehmappSerializable, S extends BukkitSerializableEvent<T>>
    BukkitSerializableManager<T> registerSerializableManager(
        @NotNull Class<T> serializableClass,
        @NotNull Function<LehmappCrudable, T> deserializer,
        @Nullable Function<T, S> joinEvent,
        @Nullable Function<T, S> quitEvent,
        @Nullable Supplier<Boolean> eventsRegistrationSupplier)
```

---

## `ObjectManager<T>` — Runtime Objects with `CommandTarget`

`ObjectManager<T extends BlobObject>` is an abstract class in `io.github.anjoismysign.bloblib.entities` that extends `Manager` and **implements `CommandTarget<T>` directly**. This is a separate path from the Bukkit manager interfaces — it is used for runtime objects that are loaded into memory and tracked by key, often from files in a custom directory.

```java
public abstract class ObjectManager<T extends BlobObject>
        extends Manager
        implements CommandTarget<T>, RunnableReloadable
```

### Key Methods

| Method | Returns | Description |
|---|---|---|
| `getObject(String key)` | `T` or `null` | Look up an object by its key |
| `searchObject(String key)` | `Result<T>` | Wraps lookup in a `Result` monad |
| `values()` | `Collection<T>` | All loaded objects |
| `keys()` | `Set<String>` | All loaded keys |
| `addObject(String key, T object, File file)` | `void` | Add an object and track its file |
| `addObject(String key, T object)` | `void` | Add an object and save it to file automatically |
| `removeObject(String key)` | `void` | Remove an object and delete its file |
| `pickRandom()` | `Optional<T>` | Random object from the set |
| `makeEditor(Player player)` | `BlobEditor<T>` | Create an in-game editor for the objects |
| `reload()` | `void` | Re-initialise and reload all files |
| `get()` | `List<String>` | **CommandTarget**: returns all object keys |
| `parse(String key)` | `T` or `null` | **CommandTarget**: delegates to `getObject(key)` |

### Abstract Methods to Implement

```java
public abstract void loadFiles(File path, CompletableFuture<Void> mainFuture);
public abstract void loadFile(@NotNull File file, Consumer<Throwable> ifFail);
```

---

## `CommandTarget` Integration in BlobLib's Own Commands

BlobLib's `/bloblib` command (`BlobLibCommand`) demonstrates how `CommandTarget` is used in practice with SkeramidCommands' `CommandTargetBuilder` and `BukkitCommandTarget`.

### Patterns Used in BlobLibCommand

**From a `Map<String, T>` (entries are known at call time):**
```java
CommandTarget<ItemMaterial> target = CommandTargetBuilder.fromMap(materialManager::getItems);
```

**From a computed `Map` with filtering logic:**
```java
CommandTarget<BlobPlugin> target = CommandTargetBuilder.fromMap(() ->
    pluginManager.getPluginsAsMap().entrySet().stream()
        .filter(entry -> entry.getValue().getPluginUpdater() != null)
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
);
```

**From a Bukkit player list (built-in SkeramidCommands convenience):**
```java
CommandTarget<Player> onlinePlayers = BukkitCommandTarget.ONLINE_PLAYERS();
```

**As a custom inline `CommandTarget` implementation:**
```java
CommandTarget<Double> coordinateTarget = new CommandTarget<Double>() {
    @Override
    public @NotNull List<String> get() { return List.of("~", "~ ~", "~ ~ ~"); }

    @Override
    public @Nullable Double parse(String s) {
        try { return Double.parseDouble(s); } catch (NumberFormatException e) { return null; }
    }
};
```

**Setting parameters on a SkeramidCommands `Command`:**
```java
Command command = bloblib.child("edit");
command.setParameters(target);  // CommandTarget<T> provides tab-completion and parsing
```

---

## `DataAssetManager<T>` — Per-Type YAML Loader

`DataAssetManager<T extends DataAsset>` (and its locale-aware counterpart `LocalizableDataAssetManager<T extends DataAsset & Localizable>`) is the internal YAML-loading engine. While external plugins typically use the Bukkit manager interfaces above, understanding this class is useful for advanced use cases.

### Construction

```java
DataAssetManager<T> manager = DataAssetManager.of(
    assetDirectory,        // File — where the .yml files live
    readFunction,          // BiFunction<ConfigurationSection, String, T>
    type,                  // DataAssetType
    filter,                // Predicate<ConfigurationSection>
    saveConsumer           // BiConsumer<YamlConfiguration, T> or null
);
```

### Key Public Methods

| Method | Description |
|---|---|
| `reload()` | Re-scan the directory, load all `.yml` files, log duplicates |
| `reload(BlobPlugin, IManagerDirector)` | Load assets from a specific plugin's directory |
| `unload(BlobPlugin)` | Remove all assets registered by the given plugin |
| `getAsset(String identifier)` | Look up a loaded asset by its key |
| `getAssets()` | List all loaded assets |
| `mapAssets()` | Unmodifiable `Map<String, T>` of identifier → asset |
| `saveAsset(File, T)` | Serialise an asset back to a `.yml` file |
| `continueLoadingAssets(BlobPlugin, boolean, File...)` | Load additional files at runtime, tracking ownership to the plugin |

### `LocalizableDataAssetManager<T>`

Same pattern but locale-aware. Each asset has a `locale` field read from the `Locale` key in the YAML (defaults to `"en_us"`). Provides:

| Method | Description |
|---|---|
| `getAsset(String identifier)` | Returns the `"en_us"` version |
| `getAsset(String identifier, String locale)` | Returns the locale-specific version, falling back to `"en_us"` |
| `getAssets(String locale)` | All assets for a given locale (with `"en_us"` fallback) |
| `getDefault()` | Returns the `"en_us"` map |

---

## `DataAssetType` Enum

The `DataAssetType` enum defines all built-in asset types and their file/directory conventions. External plugins are **not required** to register new enum values — the Bukkit manager interfaces use their own directory paths derived from the plugin's data folder.

| Enum Constant | Directory Path | File Suffix | Format |
|---|---|---|---|
| `BLOB_MESSAGE` | `/BlobMessage` | `_lang.yml` | YAML |
| `BLOB_SOUND` | `/BlobSound` | `_sounds.yml` | YAML |
| `BLOB_INVENTORY` | `/BlobInventory` | `_inventories.yml` | YAML |
| `META_BLOB_INVENTORY` | `/MetaBlobInventory` | `_meta_inventories.yml` | YAML |
| `ACTION` | `/Action` | `_actions.yml` | YAML |
| `TRANSLATABLE_BLOCK` | `/TranslatableBlock` | `_translatable_blocks.yml` | YAML |
| `TRANSLATABLE_SNIPPET` | `/TranslatableSnippet` | `_translatable_snippets.yml` | YAML |
| `TRANSLATABLE_ITEM` | `/TranslatableItem` | `_translatable_items.yml` | YAML |
| `TAG_SET` | `/TagSet` | `_tag_sets.yml` | YAML |
| `TRANSLATABLE_POSITIONABLE` | `/TranslatablePositionable` | `_translatable_positionables.yml` | YAML |
| `TRANSLATABLE_AREA` | `/TranslatableArea` | `_translatable_areas.yml` | YAML |
| `LOOT_TABLE` | `/LootTable` | `_loot_tables.json` | JSON |

---

## Lifecycle & Registration Order

```
Server start
  └─ BlobLib.onEnable()
       ├─ Creates all built-in DataAssetManagers
       ├─ Loads core BlobLib assets from plugins/BlobLib/<Type>/
       └─ Registers BlobLib's own commands (with CommandTarget parameters)

Plugin enables (BlobPlugin subclass)
  └─ ManagerDirector constructor
       ├─ Creates BlobFileManager
       ├─ Calls plugin.registerToBlobLib(this)
       │    └─ PluginManager.registerPlugin(plugin, director)
       │         ├─ put(plugin, director) in the registry
       │         └─ loadAssets(plugin, director)
       │              └─ For each DataAssetType, calls manager.reload(plugin, director)
       │                   which reads YAML/JSON from the plugin's directory
       └─ Creates BDirector (scans @BManager and @BListener annotations)

External plugin registers custom managers
  ├─ PluginManager.addAssetManager(myAssetClass, plugin, ...)
  │    └─ Creates SimpleBukkitAssetManager → wraps SingletonManagerFactory.assetManager()
  ├─ PluginManager.addGeneratorManager(myGenClass, plugin, ...)
  │    └─ Creates SimpleBukkitGeneratorManager → wraps SingletonManagerFactory.generatorManager()
  └─ PluginManager.addIdentityManager(myIdClass, plugin, ...)
       └─ Creates SimpleBukkitIdentityManager → wraps SingletonManagerFactory.identityManager()

Runtime (in commands)
  └─ SkeramidCommands resolves CommandTarget<T>
       ├─ get() → tab-completion list (e.g. asset names, object keys)
       └─ parse(s) → resolved T instance

BlobLib reload (/bloblib reload)
  └─ PluginManager.reload()
       ├─ For each registered BlobPlugin: blobLibReload()
       │    ├─ unloadAssets(plugin)
       │    └─ loadAssets(plugin)
       └─ For each registered Manager: reload()

Plugin disables
  └─ BlobPlugin.onDisable()
       ├─ ManagerDirector.realUnload() or unload()
       └─ unregisterFromBlobLib()
            └─ PluginManager.unregisterPlugin(plugin)
                 ├─ syncSaveAll() for serializable managers
                 ├─ Removes all asset/generator/identity managers from registries
                 └─ Removes plugin from the plugin map
```

---

## Class Diagram

```
                    ┌─────────────────────────────┐
                    │  CommandTarget<T>            │  ← SkeramidCommands
                    │  ─────────────────          │
                    │  + get() : List<String>      │
                    │  + parse(String) : T         │
                    └──────────┬──────────────────┘
                               │ implements
          ┌────────────────────┼────────────────────┐
          │                    │                    │
 ┌────────▼────────┐  ┌───────▼────────┐  ┌───────▼────────┐
 │ BukkitIdentity  │  │ BukkitAsset    │  │ BukkitGenerator│
 │ Manager<T>      │  │ Manager<T>     │  │ Manager<T>     │
 │ extends         │  │ extends        │  │ extends        │
 │ IdentityManager │  │ AssetManager   │  │ GeneratorManager│
 │ + CommandTarget │  │ + CommandTarget│  │ + CommandTarget │
 └────────┬────────┘  └───────┬────────┘  └───────┬────────┘
          │                   │                    │
 ┌────────▼────────┐  ┌───────▼────────┐  ┌───────▼────────┐
 │SimpleBukkit     │  │SimpleBukkit    │  │SimpleBukkit    │
 │IdentityManager  │  │AssetManager    │  │GeneratorManager│
 │(record)         │  │(record)        │  │(record)        │
 └─────────────────┘  └────────────────┘  └────────────────┘

┌─────────────────────────────────────────────────────────┐
│  PluginManager (singleton)                               │
│  ─────────────────────────                               │
│  + addIdentityManager(...) : BukkitIdentityManager<T>    │
│  + addAssetManager(...) : BukkitAssetManager<T>          │
│  + addGeneratorManager(...) : BukkitGeneratorManager<T>  │
│  + getIdentityManager(...)                               │
│  + getAssetManager(...)                                  │
│  + getGeneratorManager(...)                              │
│                                                          │
│  Map<Class, BukkitIdentityManager> identityManagers     │
│  Map<Class, BukkitAssetManager> assetManagers           │
│  Map<Class, BukkitGeneratorManager> generatorManagers   │
└──────────────────────────────────────────────────────────┘
```

---

## Complete Integration Example

Below is a full end-to-end example of an external plugin that defines a custom `DataAsset` class, loads it from YAML, registers it with BlobLib, and gets automatic `CommandTarget` support in SkeramidCommands.

### 1. Define your DataAsset class

```java
package com.example.myplugin.asset;

import io.github.anjoismysign.holoworld.asset.DataAsset;
import org.jetbrains.annotations.NotNull;

public record MyCustomData(@NotNull String identifier,
                           @NotNull String displayName,
                           double value,
                           int cooldown) implements DataAsset {

    @Override
    public @NotNull String identifier() {
        return identifier;
    }
}
```

### 2. Create an identity generator (reads from YAML)

```java
package com.example.myplugin.asset;

import io.github.anjoismysign.holoworld.asset.DataAssetEntry;
import io.github.anjoismysign.holoworld.asset.IdentityGeneration;
import io.github.anjoismysign.holoworld.asset.IdentityGenerator;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MyCustomIdentityGenerator implements IdentityGenerator<MyCustomData> {

    @Override
    public @NotNull DataAssetEntry<MyCustomData> generate(
            @NotNull ConfigurationSection section,
            @NotNull String identifier) {
        String displayName = section.getString("display-name");
        double value = section.getDouble("value", 0.0);
        int cooldown = section.getInt("cooldown", 0);
        MyCustomData data = new MyCustomData(identifier, displayName, value, cooldown);
        return DataAssetEntry.of(identifier, data);
    }

    @Override
    public @Nullable String[] getDefaultFileNames() {
        return new String[]{"myplugin_custom.yml"};
    }
}
```

### 3. Extend `BlobPlugin` and register

```java
package com.example.myplugin;

import com.example.myplugin.asset.MyCustomData;
import com.example.myplugin.asset.MyCustomIdentityGenerator;
import io.github.anjoismysign.bloblib.managers.BlobPlugin;
import io.github.anjoismysign.bloblib.managers.PluginManager;
import io.github.anjoismysign.bloblib.managers.asset.BukkitIdentityManager;
import io.github.anjoismysign.bloblib.managers.ManagerDirector;
import io.github.anjoismysign.bloblib.entities.IManagerDirector;
import org.jetbrains.annotations.NotNull;

public class MyPlugin extends BlobPlugin {

    private MyManagerDirector director;
    private BukkitIdentityManager<MyCustomData> customManager;

    @Override
    public void onEnable() {
        // ManagerDirector constructor calls registerToBlobLib() automatically
        director = new MyManagerDirector(this);

        // Register our custom identity manager
        customManager = PluginManager.getInstance().addIdentityManager(
                MyCustomIdentityGenerator.class,
                this,
                "MyCustomData",     // becomes "my_custom_data/" directory
                true                // fail on null fields
        );

        // The manager is now a CommandTarget<MyCustomData>
        // SkeramidCommands can use it for tab-completion and parsing
    }

    @Override
    public @NotNull IManagerDirector getManagerDirector() {
        return director;
    }
}
```

### 4. Use as a `CommandTarget` in your commands

```java
import io.github.anjoismysign.skeramidcommands.command.Command;
import io.github.anjoismysign.skeramidcommands.commandtarget.CommandTargetBuilder;
import io.github.anjoismysign.skeramidcommands.server.bukkit.BukkitAdapter;

// In your command setup:
Command myCmd = ...;
myCmd.setParameters(customManager);  // direct CommandTarget<T>

// Or via CommandTargetBuilder:
myCmd.setParameters(CommandTargetBuilder.fromMap(
    () -> {
        Map<String, MyCustomData> map = new HashMap<>();
        for (String id : customManager.getIdentifiers()) {
            DataAssetEntry<MyCustomData> entry = customManager.fetchGeneration(id);
            if (entry != null && entry.data() != null)
                map.put(id, entry.data());
        }
        return map;
    }
));

myCmd.onExecute((permissionMessenger, args) -> {
    String name = args[0];
    MyCustomData data = customManager.parse(name);
    if (data != null) {
        // Use the resolved data
    }
});
```

### 5. YAML directory structure

```
plugins/MyPlugin/my_custom_data/
  └─ myplugin_custom.yml
```

```yaml
# plugins/MyPlugin/my_custom_data/myplugin_custom.yml
legendary_sword:
  display-name: "Legendary Sword"
  value: 1000.0
  cooldown: 30

health_potion:
  display-name: "Health Potion"
  value: 50.0
  cooldown: 5
```

---

## Using `CommandTargetBuilder` with BlobLib's Built-in Managers

BlobLib's own API classes reveal how to access the built-in managers as `CommandTarget` sources:

```java
// Messages (localizable)
CommandTarget<BlobMessage> msgTarget = CommandTargetBuilder.fromMap(
    () -> BlobLibMessageAPI.getInstance().getMessageManager().getDefault()
);

// Sounds
CommandTarget<BlobSound> soundTarget = CommandTargetBuilder.fromMap(
    () -> BlobLibSoundAPI.getInstance().getSoundManager().mapAssets()
);

// Actions
CommandTarget<Action<Entity>> actionTarget = CommandTargetBuilder.fromMap(
    () -> BlobLibActionAPI.getInstance().getActionManager().mapAssets()
);

// LootTables
CommandTarget<LootTable> lootTarget = CommandTargetBuilder.fromMap(
    lootApi::getLootTables
);

// Pre-built player target (from SkeramidCommands)
CommandTarget<Player> onlinePlayers = BukkitCommandTarget.ONLINE_PLAYERS();
```

---

---

## See Also

- [SkeramidCommands documentation](https://github.com/anjoismysign/skeramidcommands) — the `CommandTarget`, `CommandTargetBuilder`, and `BukkitCommandTarget` API
- [holoworld documentation](https://github.com/anjoismysign/holoworld) — the `DataAsset`, `IdentityManager`, `AssetManager`, `GeneratorManager` interfaces
- [Actions](action.md), [Blob Messages](blob-message.md), [Blob Sounds](blob-sound.md) — built-in DataAsset types
- `io.github.anjoismysign.bloblib.managers.PluginManager` — central registry JavaDoc
- `io.github.anjoismysign.bloblib.managers.BlobPlugin` — base class JavaDoc
