# TranslatablePositionable

TranslatablePositionable is a locale-aware DataAsset that associates a named, spatial **Positionable** (a position on the map, optionally with rotation and world) with a localized display name. It allows you to define teleport locations, spawn points, event hubs, or any named coordinates — and retrieve the correct localized display name for each player's language.

The underlying position supports three levels of detail:

- **Positionable** — simple X, Y, Z coordinates.
- **Spatial** — X, Y, Z plus Yaw and Pitch (rotation).
- **Locatable** — X, Y, Z, Yaw, Pitch, plus a world name (fully resolved location).

---

## Directory & Discovery

- **BlobLib core directory:** `plugins/BlobLib/TranslatablePositionable/`
- **Per-plugin directory:** `plugins/<PluginName>/TranslatablePositionable/` (created automatically by `BlobFileManager`)
- **File format:** `.yml` (Bukkit `YamlConfiguration`)
- **Identifier:** the filename (without `.yml` extension) for file-level assets; the section key for section-level assets
- **Default file (core):** `bloblib_translatable_positionables.yml` (unpacked from the BlobLib jar if present)
- **Default file (plugin):** `<pluginname>_translatable_positionables.yml` (unpacked from the plugin jar if present, name is lowercased)
- **Subdirectories:** supported — all `.yml` files in subfolders are loaded recursively
- **Load filter:** a section is considered a TranslatablePositionable if it has `X`, `Y`, and `Z` set as doubles
- **Duplicate identifiers:** logged and skipped (first loaded wins)

---

## YAML Schema

TranslatablePositionables can be defined at the file root (the whole file is one asset, identifier = filename without `.yml`) or as named subsections within a file.

```yaml
# File root → identifier = filename (without .yml)
Locale: en_us
Display: '&aMy Spawn Point'
X: 100.5
Y: 64.0
Z: -200.3
Yaw: 90.0
Pitch: 0.0
World: world

# OR organized as sections
event_hub:
  Locale: en_us
  Display: '&6Event Hub'
  X: 0.0
  Y: 100.0
  Z: 0.0
  Yaw: 180.0
  Pitch: 45.0
  World: world_nether
```

### Fields

| Field     | Type   | Required | Description                                                              |
|-----------|--------|----------|--------------------------------------------------------------------------|
| `Locale`  | string | no       | Locale tag for this entry (default: `en_us`).                            |
| `Display` | string | **yes**  | Localized display name; supports `{r=key}` snippet references and color codes (`&`). |
| `X`       | double | **yes**  | X coordinate.                                                            |
| `Y`       | double | **yes**  | Y coordinate.                                                            |
| `Z`       | double | **yes**  | Z coordinate.                                                            |
| `Yaw`     | float  | no       | Yaw rotation in degrees. Presence makes this a `Spatial`.                |
| `Pitch`   | float  | no       | Pitch rotation in degrees. Presence makes this a `Spatial`.              |
| `World`   | string | no       | Bukkit world name. Presence makes this a `Locatable`.                    |

### Positionable type resolution

At load time the reader (`PositionableIO.INSTANCE.read`) determines the positionable type as follows:

1. **If only `X`, `Y`, `Z` are present** — creates a simple `Positionable` (coordinates only).
2. **If `Yaw` and `Pitch` are present** (in addition to `X`, `Y`, `Z`) — creates a `Spatial` (coordinates + rotation).
3. **If `World` is also present** (in addition to everything above) — creates a `Locatable` (fully resolved coordinate with world, rotation, and position).

> **Note:** `Yaw` and `Pitch` must both be present for a `Spatial`; if only one is set, they are treated as absent and a simple `Positionable` is created. `World` requires `Yaw`/`Pitch` to also be present — if `World` is set without them, it is ignored.

---

## Example TranslatablePositionable Files

### Single file-root asset (`spawn.yml`)

```yaml
# plugins/BlobLib/TranslatablePositionable/spawn.yml
Locale: en_us
Display: '&a&lServer Spawn'
X: 0.5
Y: 64.0
Z: 0.5
Yaw: 0.0
Pitch: 0.0
World: world
```

### Multi-section positionable file (`locations.yml`)

```yaml
# plugins/BlobLib/TranslatablePositionable/locations.yml
# Each section becomes its own TranslatablePositionable, keyed by the section name.

spawn:
  Locale: en_us
  Display: '&a&lSpawn'
  X: 0.5
  Y: 64.0
  Z: 0.5
  Yaw: 180.0
  Pitch: 0.0
  World: world

event_hub:
  Locale: en_us
  Display: '&6&lEvent Hub'
  X: -100.0
  Y: 50.0
  Z: 200.0
  Yaw: 90.0
  Pitch: -10.0
  World: world

crate_location:
  Locale: en_us
  Display: '&b&lCrate'
  X: 10.0
  Y: 70.0
  Z: -15.5
  # No Yaw/Pitch/World → simple Positionable
```

### Locale overrides (`locations_es_es.yml`)

Any file loaded with a non-`en_us` locale creates a locale-specific overlay. The `get()` call on such an asset returns its own `Positionable` (same coordinates), and `getDisplay()` returns the translated name.

```yaml
# plugins/BlobLib/TranslatablePositionable/locations_es_es.yml
Locale: es_es

spawn:
  Display: '&a&lAparición'

event_hub:
  Display: '&6&lCentro de Eventos'

crate_location:
  Display: '&b&lCofre'
```

Only `Display` needs to be provided; the `X`, `Y`, `Z`, `Yaw`, `Pitch`, and `World` fields are inherited from the `en_us` reference. Locale-override files can exist as separate files or as sections within other files.

---

## BlobLibTranslatableAPI

The primary entry point for retrieving loaded TranslatablePositionables.

```java
BlobLibTranslatableAPI api = BlobLibTranslatableAPI.getInstance();
```

---

### `getTranslatablePositionable(String identifier)`

Looks up a TranslatablePositionable by its identifier in the default (`en_us`) locale.

| Parameter    | Type   | Description                        |
|--------------|--------|------------------------------------|
| `identifier` | String | The TranslatablePositionable key.  |

**Returns:** `TranslatablePositionable` or `null`.

```java
TranslatablePositionable pos = api.getTranslatablePositionable("spawn");
```

---

### `getTranslatablePositionable(String identifier, String locale)`

Looks up a TranslatablePositionable by identifier for a specific locale, falling back to `en_us` if the locale is not available.

| Parameter    | Type   | Description                           |
|--------------|--------|---------------------------------------|
| `identifier` | String | The TranslatablePositionable key.     |
| `locale`     | String | The locale tag (e.g. `"es_es"`, `"fr_fr"`). |

**Returns:** `TranslatablePositionable` or `null`.

```java
TranslatablePositionable pos = api.getTranslatablePositionable("spawn", "es_es");
```

---

### `getTranslatablePositionable(String identifier, Player player)`

Looks up a TranslatablePositionable by identifier, localized to the player's locale.

| Parameter    | Type   | Description                           |
|--------------|--------|---------------------------------------|
| `identifier` | String | The TranslatablePositionable key.     |
| `player`     | Player | The player whose locale to use.       |

**Returns:** `TranslatablePositionable` or `null`.

```java
TranslatablePositionable pos = api.getTranslatablePositionable("event_hub", player);
```

---

### `getTranslatablePositionables(String locale)`

Returns all TranslatablePositionables merged for a given locale. Entries from the `en_us` locale are included as a base, overridden by any matching entries in the requested locale.

| Parameter | Type   | Description               |
|-----------|--------|---------------------------|
| `locale`  | String | The locale tag to filter. |

**Returns:** `List<TranslatablePositionable>`.

```java
List<TranslatablePositionable> positions = api.getTranslatablePositionables("fr_fr");
```

---

## TranslatablePositionable Interface

```java
public interface TranslatablePositionable extends Displayable<Positionable>
```

### Static methods

#### `TranslatablePositionable.by(String key)`

Shorthand lookup in the default locale. Delegates to `BlobLibTranslatableAPI.getInstance().getTranslatablePositionable(key)`.

| Parameter | Type   | Description                        |
|-----------|--------|------------------------------------|
| `key`     | String | The TranslatablePositionable key.  |

**Returns:** `TranslatablePositionable` or `null`.

```java
TranslatablePositionable pos = TranslatablePositionable.by("spawn");
```

---

### Instance methods

#### `identifier()`

Returns the unique key of this TranslatablePositionable.

**Returns:** `String`.

```java
String key = pos.identifier(); // "spawn"
```

---

#### `locale()`

Returns the locale tag of this TranslatablePositionable.

**Returns:** `String`.

```java
String locale = pos.locale(); // "en_us"
```

---

#### `get()`

Returns the underlying `Positionable`. The `Positionable` can be a plain position, a `Spatial`, or a `Locatable` depending on the fields that were present in the YAML.

**Returns:** `Positionable`.

```java
Positionable positionable = pos.get();
Location location = positionable.toLocation(player.getWorld());
player.teleport(location);
```

---

#### `getDisplay()`

Returns the localized display name.

**Returns:** `String` (never null).

---

#### `localize(String locale)`

Returns a locale-specific version of this TranslatablePositionable. If the given locale matches the current locale, returns `this`.

| Parameter | Type   | Description        |
|-----------|--------|--------------------|
| `locale`  | String | Target locale tag. |

**Returns:** `TranslatablePositionable` or `null` if the localized version is not available.

```java
TranslatablePositionable localized = pos.localize("es_es");
```

---

#### `localize(Player player)`

Convenience method: calls `localize(player.getLocale())`.

| Parameter | Type   | Description                         |
|-----------|--------|-------------------------------------|
| `player`  | Player | The player whose locale to target.  |

**Returns:** `TranslatablePositionable` or `null`.

---

#### `modify(Function<String, String> function)`

Returns a new `TranslatablePositionable` with the display string transformed by the given function. The underlying `Positionable` is unchanged.

| Parameter  | Type                        | Description                |
|------------|-----------------------------|----------------------------|
| `function` | `Function<String, String>`  | Display transformation.    |

**Returns:** a new `TranslatablePositionable`.

```java
TranslatablePositionable modified = pos.modify(s -> ChatColor.stripColor(s));
```

---

#### `modder()`

Returns a `TranslatablePositionableModder` for chainable string modifications.

**Returns:** `TranslatablePositionableModder`.

```java
TranslatablePositionable result = pos.modder()
    .replace("%player%", player.getName())
    .color(ChatColor.GOLD)
    .get();
```

---

## TranslatablePositionableModder

`TranslatablePositionableModder` extends `BlobTranslatableModder<TranslatablePositionable, Positionable>`, providing a fluent API for modifying the display string.

### Creation

```java
TranslatablePositionableModder modder = TranslatablePositionableModder.mod(pos);
// or via the interface default:
TranslatablePositionableModder modder = pos.modder();
```

### Modifier methods (all return the modder for chaining)

| Method                                                        | Description                                         |
|---------------------------------------------------------------|-----------------------------------------------------|
| `modify(Function<String, String>)`                            | Apply a custom transformation function.             |
| `replace(String old, String replacement)`                     | Replace all occurrences (case-sensitive).           |
| `replaceAll(String regex, String replacement)`                | Replace all regex matches.                          |
| `replaceFirst(String regex, String replacement)`              | Replace first regex match.                          |
| `replaceLast(String regex, String replacement)`               | Replace last regex match.                           |
| `replaceAllIgnoreCase(String regex, String replacement)`      | Replace all regex matches (case-insensitive).       |
| `replaceFirstIgnoreCase(String regex, String replacement)`    | Replace first regex match (case-insensitive).       |
| `replaceLastIgnoreCase(String regex, String replacement)`     | Replace last regex match (case-insensitive).        |
| `matchReplace(String match, String wildcard, Function)`       | Wildcard-based replacement with custom wildcard.    |
| `matchReplace(String match, Function)`                        | Wildcard-based replacement (wildcard defaults to `@`). |
| `lowerCase()`                                                 | Convert to lowercase (ROOT locale).                 |
| `lowerCase(Locale locale)`                                    | Convert to lowercase (specific locale).             |
| `upperCase()`                                                 | Convert to uppercase (ROOT locale).                 |
| `upperCase(Locale locale)`                                    | Convert to uppercase (specific locale).             |
| `capitalize()`                                                | Capitalize first letter.                            |
| `stripColor()`                                                | Remove all color codes.                             |
| `translateRGBAndChatColors(char altColorChar)`                | Translate alternate color codes (e.g. `&`) to Minecraft colors. |
| `color(ChatColor color)`                                      | Prepend a color code.                               |
| `trim()`                                                      | Trim leading/trailing whitespace.                   |
| `concat(String s)` / `append(String s)`                       | Append text to the display.                         |
| `prepend(String s)`                                           | Prepend text to the display.                        |
| `remove(String s)`                                            | Remove all occurrences (case-sensitive).            |
| `removeIgnoreCase(String s)`                                  | Remove all occurrences (case-insensitive).          |
| `removeFirst(String s)`                                       | Remove first occurrence.                            |
| `removeLast(String s)`                                        | Remove last occurrence.                             |
| `removeFirstIgnoreCase(String s)`                             | Remove first occurrence (case-insensitive).         |
| `removeLastIgnoreCase(String s)`                              | Remove last occurrence (case-insensitive).          |
| `removeVowels()`                                              | Remove all vowels (a/e/i/o/u).                      |

### Terminal method

```java
TranslatablePositionable result = modder.get();
```

---

## Positionable Interface

The `Positionable` interface represents a spatial position on the map. It has three concrete forms:

| Form         | Fields                             | Description                              |
|--------------|------------------------------------|------------------------------------------|
| `Positionable` | `X`, `Y`, `Z`                    | Simple coordinates.                      |
| `Spatial`      | `X`, `Y`, `Z`, `Yaw`, `Pitch`    | Coordinates with rotation.               |
| `Locatable`    | `X`, `Y`, `Z`, `Yaw`, `Pitch`, `World` | Fully resolved location with world. |

### Positionable methods

| Method                                     | Returns     | Description                                              |
|--------------------------------------------|-------------|----------------------------------------------------------|
| `getX()`                                   | `double`    | X coordinate.                                            |
| `getY()`                                   | `double`    | Y coordinate.                                            |
| `getZ()`                                   | `double`    | Z coordinate.                                            |
| `toVector()`                               | `Vector`    | Converts to a Bukkit `Vector`.                           |
| `toLocation()`                             | `Location`  | Converts to a Bukkit `Location` (world may be null).     |
| `toLocation(World world)`                  | `Location`  | Converts to a Bukkit `Location` with the given world.    |
| `getPositionableType()`                    | `PositionableType` | Returns the type (`POSITIONABLE`, `SPATIAL`, or `LOCATABLE`). |

### PositionableType

The `PositionableType` enum is returned by `getPositionableType()` and has:

| Constant        | Description                    |
|-----------------|--------------------------------|
| `POSITIONABLE`  | Coordinates only.              |
| `SPATIAL`       | Coordinates + rotation.        |
| `LOCATABLE`     | Coordinates + rotation + world. |

The method `PositionableType.isLocatable()` returns `true` only for `LOCATABLE`.

---

## LocalizableDataAssetManager (for TranslatablePositionable)

The `TranslatablePositionableManager` is a `LocalizableDataAssetManager<TranslatablePositionable>`, a locale-aware manager that loads `.yml` files from the configured directory.

### Creation

The manager is created in `BlobLib.java`:

```java
LocalizableDataAssetManager<TranslatablePositionable> manager = LocalizableDataAssetManager
    .of(fileManager.getDirectory(DataAssetType.TRANSLATABLE_POSITIONABLE),
        (section, locale, key) -> {
            Positionable positionable = PositionableIO.INSTANCE.read(section);
            String display = section.getString("Display");
            return BlobTranslatablePositionable.of(key, locale, display, positionable);
        },
        DataAssetType.TRANSLATABLE_POSITIONABLE,
        section -> section.isDouble("X") && section.isDouble("Y") && section.isDouble("Z"),
        PositionableIO.INSTANCE::write);
```

### Public Methods

| Method                                                                 | Description                                                             |
|------------------------------------------------------------------------|-------------------------------------------------------------------------|
| `getAsset(String identifier)`                                          | Returns the TranslatablePositionable for the given identifier (default locale). |
| `getAsset(String identifier, String locale)`                           | Returns the TranslatablePositionable for a specific locale, with fallback. |
| `getAssets()`                                                          | Returns all TranslatablePositionables (default locale).                 |
| `getAssets(String locale)`                                             | Returns all TranslatablePositionables merged for the given locale.      |
| `getDefault()`                                                         | Returns a `Map<String, T>` of all `en_us` assets.                       |
| `reload()`                                                             | Scans the asset directory and reloads all TranslatablePositionables.    |
| `reload(BlobPlugin, IManagerDirector)`                                 | Loads TranslatablePositionables from a plugin's directory.              |
| `unload(BlobPlugin)`                                                   | Removes all TranslatablePositionables registered by the given plugin.   |
| `saveAsset(File file, TranslatablePositionable asset)`                 | Serialises a TranslatablePositionable to a `.yml` file.                 |
| `continueLoadingAssets(BlobPlugin, boolean, File...)`                  | Loads TranslatablePositionables from specific files, tracking ownership.|
| `getAssetDirectory()`                                                  | Returns the `File` for the configured asset directory.                  |

### Access from BlobLib

```java
LocalizableDataAssetManager<TranslatablePositionable> manager = BlobLib.getInstance()
    .getTranslatablePositionableManager();
```

---

## DataAssetType

TranslatablePositionable is registered as `DataAssetType.TRANSLATABLE_POSITIONABLE`:

| Property          | Value                                     |
|-------------------|-------------------------------------------|
| Enum constant     | `TRANSLATABLE_POSITIONABLE`               |
| Key               | `"translatablePositionables"`             |
| Directory path    | `"/TranslatablePositionable"`             |
| Default file path | `"_translatable_positionables.yml"`       |
| Object name       | `"TranslatablePositionable"`              |

---

## Loading & Lifecycle

1. **BlobLib startup** — `BlobLib.onEnable()` creates the `LocalizableDataAssetManager<TranslatablePositionable>` via the factory shown above and calls `reload()`, scanning `plugins/BlobLib/TranslatablePositionable/` for all `.yml` files.
2. **Plugin registration** — When a `BlobPlugin` calls `registerToBlobLib()`, the `PluginManager` calls `translatablePositionableManager.reload(plugin, director)`, loading TranslatablePositionables from the plugin's own `TranslatablePositionable/` directory.
3. **Manual loading** — Plugins can call `translatablePositionableManager.continueLoadingAssets(plugin, true, files...)` to load additional TranslatablePositionable files at any point.
4. **BlobLib reload** — `BlobLib.reload()` calls `translatablePositionableManager.reload()`, re-scanning the core directory.
5. **Plugin unload** — When a `BlobPlugin` is disabled, `translatablePositionableManager.unload(plugin)` removes all TranslatablePositionables it registered.
6. **In-game creation** — The `/bloblib translatablepositionable save` and `random` commands use `PositionableIO.writeWithKey()` and `PositionableIO.writeRandom()` to serialize the player's current location as a new `.yml` file in the TranslatablePositionable directory.

---

## In-Game Commands

These commands are registered by `BlobLibCommand.INSTANCE.initialize()` in `BlobLibCommand.java`.

| Command                                                            | Description                                                                      |
|--------------------------------------------------------------------|----------------------------------------------------------------------------------|
| `/bloblib translatablepositionable save <key>`                     | Saves the player's current location as a TranslatablePositionable with the given key. Creates a `.yml` file in the asset directory with `Display: "Change me later!"`. |
| `/bloblib translatablepositionable random`                         | Saves the player's current location with a random UUID key.                      |
| `/bloblib translatablepositionable teleport <key> [player]`        | Teleports the executing player (or the specified player) to the TranslatablePositionable's location. |

### Command behavior details

**`save <key>`:**
- Captures the executing player's current `Location` (including world, yaw, and pitch).
- Delegates to `PositionableIO.INSTANCE.writeWithKey(location, key)`.
- Writes a new `.yml` file with `Locale: en_us`, the full position data, and `Display: "Change me later!"` as a placeholder.
- The player is notified via the `TranslatablePositionable.Save` message.

**`random`:**
- Same as `save` but generates a `UUID.randomUUID().toString()` as the key.
- The player is notified via the `TranslatablePositionable.Random` message.

**`teleport <key> [player]`:**
- Looks up the TranslatablePositionable by key.
- If the positionable is a `Locatable`, the world from the asset is used.
- Otherwise, the player's current world is used.
- The player is teleported to the resolved `Location`.

---

## Examples

See the [examples](examples/) directory for ready-to-use YAML files:

- [`translatablepositionable_single_location.yml`](examples/translatablepositionable_single_location.yml) — a file-root Locatable definition
- [`translatablepositionable_multi_location.yml`](examples/translatablepositionable_multi_location.yml) — multiple locations in one file with mixed positionable types
- [`translatablepositionable_locale_overrides.yml`](examples/translatablepositionable_locale_overrides.yml) — locale-specific display overrides for existing locations
