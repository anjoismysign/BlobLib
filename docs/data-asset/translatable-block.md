# TranslatableBlock

TranslatableBlock is a locale-aware `DataAsset` that holds a multi-line **block of text** (a `List<String>`). Each block is identified by a unique key and is localized per locale tag. It is ideal for in-game lore panels, Help GUI pages, rules displays, sign text, or any content that spans multiple lines and needs translation.

The block lines support `&`-style color codes and hex color codes, which are parsed at load time via `TextColor.PARSE`.

---

## Directory & Discovery

- **BlobLib core directory:** `plugins/BlobLib/TranslatableBlock/`
- **Per-plugin directory:** `plugins/<PluginName>/TranslatableBlock/` (created automatically by the plugin's `IFileManager`)
- **File format:** `.yml` (Bukkit `YamlConfiguration`)
- **Identifier:** the filename (without `.yml` extension) for file-level assets; the section key for section-level assets
- **Default file (core):** `bloblib_translatable_blocks.yml` (unpacked from the BlobLib jar if present)
- **Default file (plugin):** `<pluginname>_translatable_blocks.yml` (unpacked from the plugin jar if present, name is lowercased)
- **Subdirectories:** supported — all `.yml` files in subfolders are loaded recursively
- **Load filter:** a section is considered a TranslatableBlock if it has a non-empty `Block` list (YAML list of strings)
- **Duplicate identifiers:** logged and skipped (first loaded wins)

---

## YAML Schema

TranslatableBlocks can be defined at the file root (the whole file is one asset, identifier = filename without `.yml`) or as named subsections within a file.

```yaml
# File root → identifier = filename (without .yml)
Locale: en_us
Block:
  - '&6Welcome to MyServer!'
  - '&7Enjoy your stay.'

# OR organized as sections
server_rules:
  Locale: en_us
  Block:
    - '&c&lServer Rules'
    - '&71. Be respectful.'
    - '&72. No griefing.'
    - '&73. Have fun!'
```

### Fields

| Field    | Type          | Required | Description                                                             |
|----------|---------------|----------|-------------------------------------------------------------------------|
| `Locale` | string        | no       | Locale tag for this entry (default: `en_us`).                           |
| `Block`  | list of string | **yes**  | The translatable block lines. Color codes with `&` are parsed. Supports hex colors (`&#rrggbb`). |

### Locale resolution

At load time (`TranslatableReader.BLOCK`) the block lines are parsed as follows:

1. Each line is read as a raw string from the YAML list.
2. `TextColor.PARSE` is applied per line, translating `&`-codes (`&a`, `&l`, etc.) and hex codes (`&#rrggbb`) into Minecraft's internal `§`-based color codes.
3. The resulting `List<String>` is wrapped in a `BlobTranslatableBlock`.

When retrieving a block at runtime, the `TranslatableManager` resolves the locale:

1. The requested locale is first normalised via `BlobLibTranslatableAPI.getRealLocale(locale)`, which checks locale-redirect rules configured in the main `config.yml` (`Locale.Default-To` section).
2. If a block for the resulting locale exists, it is returned.
3. Otherwise, the default (`en_us`) block is returned as a fallback.
4. If neither exists, `null` is returned.

---

## Example TranslatableBlock Files

### Single file-root asset (`welcome.yml`)

```yaml
# plugins/BlobLib/TranslatableBlock/welcome.yml
Locale: en_us
Block:
  - '&6&lWelcome to MyServer!'
  - '&7We hope you enjoy your time here.'
  - ''
  - '&aVisit our website: &f&nwww.myserver.com'
```

### Multi-section block file (`panels.yml`)

```yaml
# plugins/BlobLib/TranslatableBlock/panels.yml
# Each section becomes its own TranslatableBlock, keyed by the section name.

help_panel:
  Locale: en_us
  Block:
    - '&6&lHelp'
    - '&7/help &8- &fShow this panel'
    - '&7/warp &8- &fOpen warps menu'
    - '&7/shop &8- &fOpen shop'
    - '&7/kit &8- &fClaim your kit'

rules_panel:
  Locale: en_us
  Block:
    - '&c&lRules'
    - '&71. Be respectful to others.'
    - '&72. No hacking or cheating.'
    - '&73. No spamming.'
    - '&74. Have fun!'

server_info:
  Locale: en_us
  Block:
    - '&a&lServer Info'
    - '&7Version: &f1.20.4'
    - '&7Online Players: &f%online%'
    - '&7TPS: &f%tps%'
```

### Locale overrides (`panels_es_es.yml`)

Any file loaded with a non-`en_us` locale creates a locale-specific overlay. Only the `Block` list needs to be provided; other fields from the `en_us` reference are not inherited (the block is entirely replaced).

```yaml
# plugins/BlobLib/TranslatableBlock/panels_es_es.yml
Locale: es_es

help_panel:
  Block:
    - '&6&lAyuda'
    - '&7/help &8- &fMostrar este panel'
    - '&7/warp &8- &fAbrir menú de warps'
    - '&7/shop &8- &fAbrir tienda'
    - '&7/kit &8- &fReclamar tu kit'

rules_panel:
  Block:
    - '&c&lReglas'
    - '&71. Respeta a los demás.'
    - '&72. No hagas trampa.'
    - '&73. No spamees.'
    - '&74. ¡Diviértete!'

server_info:
  Block:
    - '&a&lInfo del Servidor'
    - '&7Versión: &f1.20.4'
    - '&7Jugadores: &f%online%'
```
---

## BlobLibTranslatableAPI

The primary entry point for retrieving loaded TranslatableBlocks.

```java
BlobLibTranslatableAPI api = BlobLibTranslatableAPI.getInstance();
```

---

### `getTranslatableBlock(String identifier)`

Looks up a TranslatableBlock by its identifier in the default (`en_us`) locale.

| Parameter    | Type   | Description                      |
|--------------|--------|----------------------------------|
| `identifier` | String | The TranslatableBlock key.       |

**Returns:** `TranslatableBlock` or `null`.

```java
TranslatableBlock block = api.getTranslatableBlock("help_panel");
```

---

### `getTranslatableBlock(String identifier, String locale)`

Looks up a TranslatableBlock by identifier for a specific locale, falling back to `en_us` if the locale is not available.

| Parameter    | Type   | Description                                |
|--------------|--------|--------------------------------------------|
| `identifier` | String | The TranslatableBlock key.                 |
| `locale`     | String | The locale tag (e.g. `"es_es"`, `"fr_fr"`). |

**Returns:** `TranslatableBlock` or `null`.

```java
TranslatableBlock block = api.getTranslatableBlock("help_panel", "es_es");
```

---

### `getTranslatableBlock(String identifier, Player player)`

Looks up a TranslatableBlock by identifier, localized to the player's locale.

| Parameter    | Type   | Description                       |
|--------------|--------|-----------------------------------|
| `identifier` | String | The TranslatableBlock key.        |
| `player`     | Player | The player whose locale to use.   |

**Returns:** `TranslatableBlock` or `null`.

```java
TranslatableBlock block = api.getTranslatableBlock("help_panel", player);
```

---

## TranslatableBlock Interface

```java
public interface TranslatableBlock extends Translatable<List<String>>
```

`Translatable<T>` extends `DataAsset` and `Localizable`.

### Static methods

#### `TranslatableBlock.by(String key)`

Shorthand lookup in the default locale. Delegates to `BlobLibTranslatableAPI.getInstance().getTranslatableBlock(key)`.

| Parameter | Type   | Description                      |
|-----------|--------|----------------------------------|
| `key`     | String | The TranslatableBlock key.       |

**Returns:** `TranslatableBlock` or `null`.

```java
TranslatableBlock block = TranslatableBlock.by("help_panel");
```

---

### Instance methods

#### `identifier()`

Returns the unique key of this TranslatableBlock (inherited from `DataAsset`).

**Returns:** `String`.

```java
String key = block.identifier(); // "help_panel"
```

---

#### `locale()`

Returns the locale tag of this TranslatableBlock (inherited from `Localizable`).

**Returns:** `String`.

```java
String locale = block.locale(); // "en_us"
```

---

#### `get()`

Returns the multi-line block text.

**Returns:** `List<String>`.

```java
List<String> lines = block.get();
for (String line : lines) {
    // send to player, write to sign, etc.
}
```

---

#### `modify(Function<String, String> function)`

Returns a new `TranslatableBlock` with every line transformed by the given function. The original block is unchanged — this creates a copy.

| Parameter  | Type                       | Description              |
|------------|----------------------------|--------------------------|
| `function` | `Function<String, String>` | Line transformation.     |

**Returns:** a new `TranslatableBlock`.

```java
TranslatableBlock stripped = block.modify(s -> ChatColor.stripColor(s));
TranslatableBlock replaced = block.modify(s -> s.replace("%player%", player.getName()));
```

---

#### `modder()`

Returns a `TranslatableBlockModder` for chainable string modifications (see below).

**Returns:** `TranslatableBlockModder`.

```java
TranslatableBlock result = block.modder()
    .replace("%player%", player.getName())
    .color(ChatColor.GOLD)
    .get();
```

---

## TranslatableBlockModder

`TranslatableBlockModder` extends `BlobTranslatableModder<TranslatableBlock, List<String>>`, providing a fluent API for modifying every line of the block text.

### Creation

```java
TranslatableBlockModder modder = TranslatableBlockModder.mod(block);
// or via the interface default:
TranslatableBlockModder modder = block.modder();
```

### Modifier methods (all return the modder for chaining)

Each method below applies the transformation to **every line** in the block.

| Method                                                     | Description                                         |
|------------------------------------------------------------|-----------------------------------------------------|
| `modify(Function<String, String>)`                         | Apply a custom transformation function.             |
| `replace(String old, String replacement)`                  | Replace all occurrences (case-sensitive).           |
| `replaceAll(String regex, String replacement)`             | Replace all regex matches.                          |
| `replaceFirst(String regex, String replacement)`           | Replace first regex match.                          |
| `replaceLast(String regex, String replacement)`            | Replace last regex match.                           |
| `replaceAllIgnoreCase(String regex, String replacement)`   | Replace all regex matches (case-insensitive).       |
| `replaceFirstIgnoreCase(String regex, String replacement)` | Replace first regex match (case-insensitive).       |
| `replaceLastIgnoreCase(String regex, String replacement)`  | Replace last regex match (case-insensitive).        |
| `matchReplace(String match, String wildcard, Function)`    | Wildcard-based replacement with custom wildcard.    |
| `matchReplace(String match, Function)`                     | Wildcard-based replacement (wildcard defaults to `@`). |
| `lowerCase()`                                              | Convert to lowercase (ROOT locale).                 |
| `lowerCase(Locale locale)`                                 | Convert to lowercase (specific locale).             |
| `upperCase()`                                              | Convert to uppercase (ROOT locale).                 |
| `upperCase(Locale locale)`                                 | Convert to uppercase (specific locale).             |
| `capitalize()`                                             | Capitalize first letter of each line.               |
| `stripColor()`                                             | Remove all color codes.                             |
| `translateRGBAndChatColors(char altColorChar)`             | Translate alternate color codes (e.g. `&`) to Minecraft colors. |
| `color(ChatColor color)`                                   | Prepend a color code to each line.                  |
| `trim()`                                                   | Trim leading/trailing whitespace from each line.    |
| `concat(String s)` / `append(String s)`                    | Append text to the end of each line.                |
| `prepend(String s)`                                        | Prepend text to the beginning of each line.         |
| `remove(String s)`                                         | Remove all occurrences (case-sensitive).            |
| `removeIgnoreCase(String s)`                               | Remove all occurrences (case-insensitive).          |
| `removeFirst(String s)`                                    | Remove first occurrence.                            |
| `removeLast(String s)`                                     | Remove last occurrence.                             |
| `removeFirstIgnoreCase(String s)`                          | Remove first occurrence (case-insensitive).         |
| `removeLastIgnoreCase(String s)`                           | Remove last occurrence (case-insensitive).          |
| `removeVowels()`                                           | Remove all vowels (a/e/i/o/u).                     |

### Terminal method

```java
TranslatableBlock result = modder.get();
```

Returns the modified `TranslatableBlock` with all transformations applied.

---

### Usage examples

```java
// Replace placeholders in every line
TranslatableBlock personalized = block.modder()
    .replace("%player%", player.getName())
    .replace("%balance%", String.valueOf(economy.getBalance(player)))
    .get();

// Strip all color codes for logging
TranslatableBlock plain = block.modder()
    .stripColor()
    .get();

// Prepend a header line equivalent (in code)
TranslatableBlock withPrefix = block.modder()
    .prepend("&8» ")
    .get();
```

---

## TranslatableManager (for TranslatableBlock)

`TranslatableManager` is the registry that holds all TranslatableBlocks and TranslatableSnippets. It loads `.yml` files from the configured directory and organises them by identifier → locale.

### Public Static Methods

| Method                                                                              | Description                                                                          |
|-------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------|
| `loadBlobPlugin(BlobPlugin plugin, IManagerDirector director)`                      | Loads blocks/snippets from a plugin's asset directories.                             |
| `loadBlobPlugin(BlobPlugin plugin)`                                                 | Loads blocks/snippets using the plugin's own `ManagerDirector`.                      |
| `unloadBlobPlugin(BlobPlugin plugin)`                                               | Removes all blocks and snippets that were registered by the given plugin.            |
| `continueLoadingBlocks(BlobPlugin plugin, boolean warnDuplicates, File... files)`   | Loads TranslatableBlocks from specific files, tracking ownership. Duplicates warning is optional. |
| `continueLoadingBlocks(BlobPlugin plugin, File... files)`                           | Same as above with `warnDuplicates = true`.                                          |
| `continueLoadingSnippets(BlobPlugin plugin, boolean warnDuplicates, File... files)` | Loads TranslatableSnippets from specific files.                                      |
| `continueLoadingSnippets(BlobPlugin plugin, File... files)`                         | Same as above with `warnDuplicates = true`.                                          |

### Public Instance Methods (for TranslatableBlock)

| Method                                    | Returns                          | Description                                                             |
|-------------------------------------------|----------------------------------|-------------------------------------------------------------------------|
| `getBlockRegistry(String identifier)`     | `TranslatableRegistry<TranslatableBlock>` | Returns the registry for the given identifier, or `null`.          |
| `getBlock(String identifier)`             | `TranslatableBlock` or `null`    | Returns the default (`en_us`) block.                                    |
| `getBlock(String identifier, String locale)` | `TranslatableBlock` or `null` | Returns the block for the given locale, falling back to default.        |
| `saveBlock(File file, TranslatableBlock block)` | `void`                     | Serialises a TranslatableBlock to a `.yml` file.                        |

### Access from BlobLib

```java
TranslatableManager manager = BlobLib.getInstance().getTranslatableManager();
```

---

## DataAssetType

TranslatableBlock is registered as `DataAssetType.TRANSLATABLE_BLOCK`:

| Property          | Value                                |
|-------------------|--------------------------------------|
| Enum constant     | `TRANSLATABLE_BLOCK`                 |
| Key               | `"translatableBlocks"`               |
| Directory path    | `"/TranslatableBlock"`               |
| Default file path | `"_translatable_blocks.yml"`         |
| Object name       | `"TranslatableBlock"`                |

---

## Loading & Lifecycle

1. **BlobLib startup** — `BlobLib.onEnable()` creates the `TranslatableManager` and calls `load()`, which scans `plugins/BlobLib/TranslatableBlock/` for all `.yml` files and registers them in the block registry.

2. **Plugin registration** — When a `BlobPlugin` calls `registerToBlobLib()`, the `PluginManager.loadAssets()` method invokes `TranslatableManager.loadBlobPlugin(plugin, director)`. This loads TranslatableBlocks from the plugin's own `TranslatableBlock/` directory (resolved via the plugin's `IFileManager`).

3. **Manual loading** — Plugins can call `TranslatableManager.continueLoadingBlocks(plugin, warnDuplicates, files...)` to load additional TranslatableBlock files at any point (e.g., from a custom folder, a world-specific directory, or a downloaded resource).

4. **Duplicate detection** — Within a single load batch, duplicate identifiers are collected. After the batch completes, a warning listing all duplicates is logged. The first file loaded for a given identifier wins; subsequent attempts to load the same identifier are silently ignored.

5. **BlobLib reload** — `BlobLib.reload()` calls `translatableManager.reload()`, which clears all registries and re-scans the core directory.

6. **Plugin unload** — When a `BlobPlugin` is disabled, `TranslatableManager.unloadBlobPlugin(plugin)` removes all TranslatableBlocks that were registered by that plugin.

7. **In-game saving** — The `TranslatableManager.saveBlock(File, TranslatableBlock)` method can be used to persist blocks programmatically. It writes the block lines (with `§` translated back to `&`) and locale to a `.yml` file.

---

## Saving Blocks Programmatically

```java
TranslatableManager manager = BlobLib.getInstance().getTranslatableManager();
IFileManager fileManager = BlobLib.getInstance().getFileManager();
File blockDir = fileManager.getDirectory(DataAssetType.TRANSLATABLE_BLOCK);

// Create a block in code
TranslatableBlock block = BlobTranslatableBlock.of(
    "custom_block",
    "en_us",
    List.of("&aLine 1", "&bLine 2", "&cLine 3")
);

// Save to a file (identifier is derived from filename)
File file = new File(blockDir, "custom_block.yml");
manager.saveBlock(file, block);
```

---

## Examples

See the [examples](examples/) directory for ready-to-use YAML files:

- [`translatableblock_single_block.yml`](examples/translatableblock_single_block.yml) — a file-root block definition
- [`translatableblock_multi_block.yml`](examples/translatableblock_multi_block.yml) — multiple blocks in one file with mixed locales
- [`translatableblock_locale_overrides.yml`](examples/translatableblock_locale_overrides.yml) — locale-specific block overrides for existing identifiers
