# TranslatableArea

TranslatableArea is a locale-aware DataAsset that associates a named, bounded **Area** (a box or WorldGuard region) with a display name. It lets you define "zones" on the map — such as spawn regions, event arenas, or PvP zones — and retrieve the correct localized display name for any player's language.

TranslatableAreas support two underlying area types:

- **BoxArea** — a cuboid defined by minimum/maximum vectors.
- **WorldGuardArea** — references an existing WorldGuard `ProtectedRegion` by ID (requires WorldGuard on the server).

---

## Directory & Discovery

- **BlobLib core directory:** `plugins/BlobLib/TranslatableArea/`
- **Per-plugin directory:** `plugins/<PluginName>/TranslatableArea/` (created automatically by `BlobFileManager`)
- **File format:** `.yml` (Bukkit `YamlConfiguration`)
- **Identifier:** the filename (without `.yml` extension) for file-level assets; the section key for section-level assets
- **Default file (core):** `bloblib_translatable_areas.yml` (unpacked from the BlobLib jar if present)
- **Default file (plugin):** `<pluginname>_translatable_areas.yml` (unpacked from the plugin jar if present, name is lowercased)
- **Subdirectories:** supported — all `.yml` files in subfolders are loaded recursively
- **Load filter:** a section is considered a TranslatableArea if it has `World` set as a string
- **Duplicate identifiers:** logged and skipped (first loaded wins)

---

## YAML Schema

TranslatableAreas can be defined at the file root (the whole file is one asset, identifier = filename without `.yml`) or as named subsections within a file.

```yaml
# File root → identifier = filename (without .yml)
Locale: en_us
Display: '&aMy Spawn Area'
World: world
Min:
  X: -100
  Y: 0
  Z: -100
Max:
  X: 100
  Y: 100
  Z: 100

# OR organized as sections
my_pvp_zone:
  Locale: en_us
  Display: '&cPvP Arena'
  World: world_nether
  Min:
    X: -50
    Y: 64
    Z: -50
  Max:
    X: 50
    Y: 150
    Z: 50
```

### Fields

| Field       | Type           | Description                                                              |
|-------------|----------------|--------------------------------------------------------------------------|
| `Locale`    | string         | Locale tag for this entry (default: `en_us`).                            |
| `Display`   | string         | Localized display name; supports `{r=key}` snippet references and color codes (`&`). |
| `World`     | string         | **(Required)** Bukkit world name.                                       |
| `Min`       | section (vec)  | Minimum corner coordinates for a `BoxArea`.                             |
| `Max`       | section (vec)  | Maximum corner coordinates for a `BoxArea`.                             |
| `Id`        | string         | WorldGuard region ID (alternative to `Min`/`Max`).                      |
| `Center`    | string         | Optional serialized `BlockVector` center location.                      |

#### Vector section (`Min` / `Max`)

Each vector section contains three integer fields:

| Sub-field | Type | Description  |
|-----------|------|--------------|
| `X`       | int  | X coordinate |
| `Y`       | int  | Y coordinate |
| `Z`       | int  | Z coordinate |

### Area type resolution

At load time the reader (`AreaIO.INSTANCE.read`) determines the area type as follows:

1. **If both `Min` and `Max` are present** — creates a `BoxArea` from those bounds.
2. **If `Id` is present** (and no `Min`/`Max`) — creates a `WorldGuardArea` referencing that region. WorldGuard must be installed or loading will fail.
3. **If neither** — a `ConfigurationFieldException` is thrown.

> **Note:** `Min`/`Max` and `Id` are mutually exclusive. If both are present, an exception is thrown.

---

## Example TranslatableArea Files

### Single file-root area (`spawn_area.yml`)

```yaml
# plugins/BlobLib/TranslatableArea/spawn_area.yml
Locale: en_us
Display: '&aWelcome Spawn'
World: world
Min:
  X: -100
  Y: 0
  Z: -100
Max:
  X: 100
  Y: 100
  Z: 100
Center: '0.0, 64.0, 0.0'
```

### Multi-section area file (`zones.yml`)

```yaml
# plugins/BlobLib/TranslatableArea/zones.yml
# Each section becomes its own TranslatableArea, keyed by the section name.

spawn_area:
  Locale: en_us
  Display: '&a&lSpawn'
  World: world
  Min:
    X: -50
    Y: 0
    Z: -50
  Max:
    X: 50
    Y: 80
    Z: 50

pvp_arena:
  Locale: en_us
  Display: '&c&lPvP Arena'
  World: world
  Id: pvp_region      # WorldGuard region reference

shop_area:
  Locale: en_us
  Display: '&6&lPlayer Shops'
  World: world
  Min:
    X: 200
    Y: 0
    Z: 200
  Max:
    X: 300
    Y: 256
    Z: 300
```

### Locale overrides (`zones_es_es.yml`)

Any file loaded with a non-`en_us` locale creates a locale-specific overlay. The `get()` call on such an asset delegates to the `en_us` version (the Area is the same), but `getDisplay()` returns the translated name.

```yaml
# plugins/BlobLib/TranslatableArea/zones_es_es.yml
Locale: es_es

spawn_area:
  Display: '&a&lAparición'

pvp_arena:
  Display: '&c&lArena PvP'

shop_area:
  Display: '&6&lTiendas de Jugadores'
```

Only `Display` needs to be provided; the `World`, `Min`/`Max`, and `Id` fields are inherited from the `en_us` reference.

---

## BlobLibTranslatableAPI

The primary entry point for retrieving loaded TranslatableAreas.

```java
BlobLibTranslatableAPI api = BlobLibTranslatableAPI.getInstance();
```

### `getTranslatableArea(String identifier)`

Looks up a TranslatableArea by its identifier in the default (`en_us`) locale.

| Parameter    | Type   | Description                     |
|--------------|--------|---------------------------------|
| `identifier` | String | The TranslatableArea key.       |

**Returns:** `TranslatableArea` or `null`.

```java
TranslatableArea area = api.getTranslatableArea("spawn_area");
```

---

### `getTranslatableArea(String identifier, String locale)`

Looks up a TranslatableArea by identifier for a specific locale, falling back to `en_us` if the locale is not available.

| Parameter    | Type   | Description                     |
|--------------|--------|---------------------------------|
| `identifier` | String | The TranslatableArea key.       |
| `locale`     | String | The locale tag (e.g. `"es_es"`, `"fr_fr"`). |

**Returns:** `TranslatableArea` or `null`.

```java
TranslatableArea area = api.getTranslatableArea("pvp_arena", "es_es");
```

---

### `getTranslatableArea(String identifier, Player player)`

Looks up a TranslatableArea by identifier, localized to the player's locale.

| Parameter    | Type   | Description                        |
|--------------|--------|------------------------------------|
| `identifier` | String | The TranslatableArea key.          |
| `player`     | Player | The player whose locale to use.    |

**Returns:** `TranslatableArea` or `null`.

```java
TranslatableArea area = api.getTranslatableArea("shop_area", player);
```

---

### `getTranslatableAreas(String locale)`

Returns all TranslatableAreas merged for a given locale. Entries from the `en_us` locale are included as a base, overridden by any matching entries in the requested locale.

| Parameter | Type   | Description               |
|-----------|--------|---------------------------|
| `locale`  | String | The locale tag to filter. |

**Returns:** `List<TranslatableArea>`.

```java
List<TranslatableArea> zones = api.getTranslatableAreas("fr_fr");
```

---

## TranslatableArea Interface

```java
public interface TranslatableArea extends Displayable<Area>
```

### Static methods

#### `TranslatableArea.by(String key)`

Shorthand lookup in the default locale. Delegates to `BlobLibTranslatableAPI.getInstance().getTranslatableArea(key)`.

| Parameter | Type   | Description               |
|-----------|--------|---------------------------|
| `key`     | String | The TranslatableArea key. |

**Returns:** `TranslatableArea` or `null`.

```java
TranslatableArea area = TranslatableArea.by("spawn_area");
```

---

#### `TranslatableArea.forLocale(String reference, String locale, String display)`

Creates an anonymous `TranslatableArea` instance for a specific locale. The `get()` method of the returned instance delegates to the `en_us` asset identified by `reference`.

| Parameter   | Type   | Description                                |
|-------------|--------|--------------------------------------------|
| `reference` | String | The key of the `en_us` TranslatableArea.   |
| `locale`    | String | The locale tag.                            |
| `display`   | String | The localized display string.              |

**Returns:** a new `TranslatableArea` (never null).

---

### Instance methods

#### `identifier()`

Returns the unique key of this TranslatableArea.

**Returns:** `String`.

---

#### `locale()`

Returns the locale tag of this TranslatableArea.

**Returns:** `String`.

---

#### `get()`

Returns the underlying `Area` (either a `BoxArea` or `WorldGuardArea`). For non-default-locale instances, this delegates to the `en_us` version.

**Returns:** `Area`.

---

#### `getDisplay()`

Returns the localized display name.

**Returns:** `String`.

---

#### `localize(String locale)`

Returns a locale-specific version of this TranslatableArea. If the given locale matches the current locale, returns `this`.

| Parameter | Type   | Description        |
|-----------|--------|--------------------|
| `locale`  | String | Target locale tag. |

**Returns:** `TranslatableArea` or `null` if the localized version is not available.

```java
TranslatableArea localized = area.localize("es_es");
```

---

#### `localize(Player player)`

Convenience method: calls `localize(player.getLocale())`.

| Parameter | Type   | Description                         |
|-----------|--------|-------------------------------------|
| `player`  | Player | The player whose locale to target.  |

**Returns:** `TranslatableArea` or `null`.

---

#### `modify(Function<String, String> function)`

Returns a new `TranslatableArea` with the display string transformed by the given function. The underlying `Area` is unchanged.

| Parameter  | Type                        | Description                |
|------------|-----------------------------|----------------------------|
| `function` | `Function<String, String>`  | Display transformation.    |

**Returns:** a new `TranslatableArea`.

---

#### `modder()`

Returns a `TranslatableAreaModder` for chainable string modifications.

**Returns:** `TranslatableAreaModder`.

```java
TranslatableArea modified = area.modder()
    .replace("%player%", player.getName())
    .color(ChatColor.GOLD)
    .get();
```

---

## TranslatableAreaModder

`TranslatableAreaModder` extends `BlobTranslatableModder<TranslatableArea, Area>`, providing a fluent API for modifying the display string.

### Creation

```java
TranslatableAreaModder modder = TranslatableAreaModder.mod(area);
// or via the interface default:
TranslatableAreaModder modder = area.modder();
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
TranslatableArea result = modder.get();
```

---

## TranslatableAreaWand

`TranslatableAreaWand` is a Bukkit `Listener` that lets players define box areas in-game using a wand item.

### Obtaining the Wand

The wand is a `TranslatableItem` with the identifier `"TranslatableArea.Wand"`. It can be obtained via the `/bloblib translatableitem` command:

| Command | Description |
|---------|-------------|
| `/bloblib translatableitem get TranslatableArea.Wand` | Gives the wand item to the executing player. |
| `/bloblib translatableitem give TranslatableArea.Wand <player>` | Gives the wand item to a specific online player. |

### Activation

1. The player must hold the `TranslatableItem` with identifier `"TranslatableArea.Wand"` in their main hand.
2. **Left-click** a block — sets the **minimum** corner (displayed via the `TranslatableArea.Wand-Min` message).
3. **Right-click** a block — sets the **maximum** corner (displayed via the `TranslatableArea.Wand-Max` message).
4. Both corners must be in the same world for a valid `BoundingBox`.

### API

The wand is accessible through `BlobLibListenerManager`:

```java
BlobLibListenerManager.getInstance().getAreaWand();
```

#### `has(Player player)`

Returns the `BoundingBox` defined by the player's wand selections, or `null` if either corner is missing or they are in different worlds.

| Parameter | Type   | Description              |
|-----------|--------|--------------------------|
| `player`  | Player | The player to check.     |

**Returns:** `BoundingBox` or `null`.

---

## In-Game Commands

| Command                                              | Description                                              |
|------------------------------------------------------|----------------------------------------------------------|
| `/bloblib translatablearea random`                   | Creates a TranslatableArea from the wand selection, writes it as a `.yml` file with a random UUID key, and announces the key via the `TranslatableArea.Random` message. |
| `/bloblib translatableitem get TranslatableArea.Wand`| Gives the TranslatableArea wand item to the executing player. |
| `/bloblib translatableitem give TranslatableArea.Wand <player>` | Gives the TranslatableArea wand item to a specific online player. |

---

## TranslatableAreaManager

The `TranslatableAreaManager` is a `LocalizableDataAssetManager<TranslatableArea>`, a locale-aware manager that loads `.yml` files from the configured directory.

### Creation

```java
TranslatableAreaManager manager = TranslatableAreaManager.of();
```

The `of()` factory configures:

- **Asset directory:** `plugins/BlobLib/TranslatableArea/` (from `DataAssetType.TRANSLATABLE_AREA`)
- **Locale read logic:**
  - For `en_us` — reads the `Area` via `AreaIO.INSTANCE.read(section)` and the `Display` string, creating a `BlobTranslatableArea`.
  - For other locales — creates a locale overlay via `TranslatableArea.forLocale(key, locale, display)` that delegates its `get()` to the `en_us` version.
- **Filter:** requires `section.isString("World")`.
- **Save consumer:** `null` (saving is not supported via the manager).

### Public Methods (from `LocalizableDataAssetManager<TranslatableArea>`)

| Method                                                                 | Description                                                             |
|------------------------------------------------------------------------|-------------------------------------------------------------------------|
| `getAsset(String identifier)`                                          | Returns the TranslatableArea for the given identifier (default locale). |
| `getAsset(String identifier, String locale)`                           | Returns the TranslatableArea for a specific locale, with fallback.      |
| `getAssets()`                                                          | Returns all TranslatableAreas (default locale).                         |
| `getAssets(String locale)`                                             | Returns all TranslatableAreas merged for the given locale.              |
| `getDefault()`                                                         | Returns a `Map<String, T>` of all `en_us` assets.                       |
| `reload()`                                                             | Scans the asset directory and reloads all TranslatableAreas.            |
| `reload(BlobPlugin, IManagerDirector)`                                 | Loads TranslatableAreas from a plugin's directory.                      |
| `unload(BlobPlugin)`                                                   | Removes all TranslatableAreas registered by the given plugin.           |
| `saveAsset(File file, TranslatableArea asset)`                         | Serialises a TranslatableArea to a `.yml` file (requires a save consumer). |
| `continueLoadingAssets(BlobPlugin, boolean, File...)`                  | Loads TranslatableAreas from specific files, tracking ownership.        |
| `getAssetDirectory()`                                                  | Returns the `File` for the configured asset directory.                  |

### Spatial queries

The manager also provides spatial query methods:

#### `unorderedContains(Location location)`

Returns all TranslatableAreas whose area contains the given location. Both `BoxArea` and `WorldGuardArea` types are included.

| Parameter  | Type     | Description            |
|------------|----------|------------------------|
| `location` | Location | The location to test.  |

**Returns:** `List<TranslatableArea>`.

```java
List<TranslatableArea> zones = manager.unorderedContains(player.getLocation());
```

#### `orderedContains(Location location)`

Like `unorderedContains`, but restricted to `BoxArea` types only, sorted so that smaller (inner) areas come first. Useful for nested zone hierarchies where the smallest containing zone should be checked first.

| Parameter  | Type     | Description            |
|------------|----------|------------------------|
| `location` | Location | The location to test.  |

**Returns:** `List<TranslatableArea>` (inner-most first).

```java
List<TranslatableArea> zones = manager.orderedContains(player.getLocation());
```

---

## DataAssetType

TranslatableArea is registered as `DataAssetType.TRANSLATABLE_AREA`:

| Property          | Value                            |
|-------------------|----------------------------------|
| Enum constant     | `TRANSLATABLE_AREA`              |
| Key               | `"translatableAreas"`            |
| Directory path    | `"/TranslatableArea"`            |
| Default file path | `"_translatable_areas.yml"`      |
| Object name       | `"TranslatableArea"`             |

---

## Loading & Lifecycle

1. **BlobLib startup** — `BlobLib.onEnable()` creates the `TranslatableAreaManager` via `TranslatableAreaManager.of()` and calls `reload()`, scanning `plugins/BlobLib/TranslatableArea/` for all `.yml` files.
2. **Plugin registration** — When a `BlobPlugin` calls `registerToBlobLib()`, the `PluginManager` calls `translatableAreaManager.reload(plugin, director)`, loading TranslatableAreas from the plugin's own `TranslatableArea/` directory.
3. **Manual loading** — Plugins can call `translatableAreaManager.continueLoadingAssets(plugin, true, files...)` to load additional TranslatableArea files at any point.
4. **BlobLib reload** — `BlobLib.reload()` calls `translatableAreaManager.reload()`, re-scanning the core directory.
5. **Plugin unload** — When a `BlobPlugin` is disabled, `translatableAreaManager.unload(plugin)` removes all TranslatableAreas it registered.
6. **In-game creation** — The `/bloblib translatablearea random` command uses `AreaIO.writeRandom(player)` to serialize the wand selection as a new `.yml` file in the TranslatableArea directory.

---

## Examples

See the [examples](examples/) directory for ready-to-use YAML files:

- [`translatablearea_single_zone.yml`](examples/translatablearea_single_zone.yml) — a file-root BoxArea definition
- [`translatablearea_multi_zone.yml`](examples/translatablearea_multi_zone.yml) — multiple areas in one file with mixed BoxArea and WorldGuard types
- [`translatablearea_locale_overrides.yml`](examples/translatablearea_locale_overrides.yml) — locale-specific display overrides for existing areas
