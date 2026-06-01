# TranslatableSnippet

TranslatableSnippet is a locale-aware `DataAsset` that holds a **single-line string** of text (a `String`). Each snippet is identified by a unique key and localized per locale tag. It is ideal for short, reusable text strings such as GUI labels, action bar messages, item lore placeholders, chat prefixes, rarity names, or any brief translatable content.

The snippet string supports `&`-style color codes and hex color codes, which are parsed at load time via `TextColor.PARSE`.

Additionally, snippets support **recursive reference resolution**: at construction time the snippet text is scanned for `{r=<key>}` tokens, and each token is replaced with the resolved `TranslatableSnippet` identified by `<key>` in the same locale. This enables composition — a snippet can include other snippets as sub-components.

---

## Directory & Discovery

- **BlobLib core directory:** `plugins/BlobLib/TranslatableSnippet/`
- **Per-plugin directory:** `plugins/<PluginName>/TranslatableSnippet/` (created automatically by the plugin's `IFileManager`)
- **File format:** `.yml` (Bukkit `YamlConfiguration`)
- **Identifier:** the filename (without `.yml` extension) for file-level assets; the section key for section-level assets
- **Default file (core):** `bloblib_translatable_snippets.yml` (unpacked from the BlobLib jar if present)
- **Default file (plugin):** `<pluginname>_translatable_snippets.yml` (unpacked from the plugin jar if present, name is lowercased)
- **Subdirectories:** supported — all `.yml` files in subfolders are loaded recursively
- **Load filter:** a section is considered a TranslatableSnippet if it has a non-empty `Snippet` string field
- **Duplicate identifiers:** logged and skipped (first loaded wins)

---

## YAML Schema

TranslatableSnippets can be defined at the file root (the whole file is one asset, identifier = filename without `.yml`) or as named subsections within a file.

```yaml
# File root → identifier = filename (without .yml)
Locale: en_us
Snippet: '&aWelcome to MyServer!'

# OR organized as sections
welcome_message:
  Locale: en_us
  Snippet: '&a&lWelcome to MyServer!'

rarity_common:
  Locale: en_us
  Snippet: '&f&lCOMMON'

rarity_epic:
  Locale: en_us
  Snippet: '&d&lEPIC'
```

You can also reference other snippets using the `{r=<key>}` syntax:

```yaml
player_greeting:
  Locale: en_us
  Snippet: '&6Welcome, {r=player_name_label}!'

player_name_label:
  Locale: en_us
  Snippet: '&fPlayer'
```

### Fields

| Field    | Type   | Required | Description                                                                                                 |
|----------|--------|----------|-------------------------------------------------------------------------------------------------------------|
| `Locale` | string | no       | Locale tag for this entry (default: `en_us`).                                                               |
| `Snippet` | string | **yes**  | The translatable text. Color codes with `&` are parsed. Supports hex colors (`&#rrggbb`). Supports `{r=<key>}` references to other snippets. |

### Reference resolution (`{r=...}`)

At construction time (`BlobTranslatableSnippet` constructor), the snippet string is scanned for `{r=<key>}` tokens. Each token is resolved by looking up the `TranslatableSnippet` with identifier `<key>` in the **same locale** as the current snippet. The token is replaced with the referenced snippet's resolved content (which may itself contain further `{r=...}` references, resolved recursively).

- If the referenced key does not exist, the token is replaced with an empty string `""`.
- This resolution happens **once** at construction time, not at runtime.
- After reference resolution, `TextColor.PARSE` is applied to the final string.

### Locale resolution

At load time (`TranslatableReader.SNIPPET`) the snippet is processed as follows:

1. The raw string is read from the YAML `Snippet` field.
2. A `BlobTranslatableSnippet` is constructed from the raw string.
3. During construction, `{r=...}` references are resolved (see above).
4. After reference resolution, `TextColor.PARSE` is applied to translate `&`-codes (`&a`, `&l`, etc.) and hex codes (`&#rrggbb`) into Minecraft's internal `§`-based color codes.

When retrieving a snippet at runtime, the `TranslatableManager` resolves the locale:

1. The requested locale is first normalised via `BlobLibTranslatableAPI.getRealLocale(locale)`, which checks locale-redirect rules configured in the main `config.yml` (`Locale.Default-To` section).
2. If a snippet for the resulting locale exists, it is returned.
3. Otherwise, the default (`en_us`) snippet is returned as a fallback.
4. If neither exists, `null` is returned.

---

## Example TranslatableSnippet Files

### Single file-root asset (`greeting.yml`)

```yaml
# plugins/BlobLib/TranslatableSnippet/greeting.yml
Locale: en_us
Snippet: '&6&lHello, &e&l%player%&6&l!'
```

### Multi-section snippet file (`labels.yml`)

```yaml
# plugins/BlobLib/TranslatableSnippet/labels.yml
# Each section becomes its own TranslatableSnippet, keyed by the section name.

rarity_common:
  Locale: en_us
  Snippet: '&f&lCOMMON'

rarity_uncommon:
  Locale: en_us
  Snippet: '&e&lUNCOMMON'

rarity_rare:
  Locale: en_us
  Snippet: '&b&lRARE'

rarity_epic:
  Locale: en_us
  Snippet: '&d&lEPIC'
```

### Snippet with references (`composites.yml`)

```yaml
# plugins/BlobLib/TranslatableSnippet/composites.yml
# Demonstrates {r=...} reference resolution.

death_message:
  Locale: en_us
  Snippet: '{r=death_prefix} {r=player_name} {r=death_suffix}'

death_prefix:
  Locale: en_us
  Snippet: '&c&l☠'

death_suffix:
  Locale: en_us
  Snippet: '&7has fallen in battle!'

player_name:
  Locale: en_us
  Snippet: '&f%player%'
```

### Locale overrides (`labels_es_es.yml`)

Any file loaded with a non-`en_us` locale creates a locale-specific overlay. Only the `Snippet` string needs to be provided.

```yaml
# plugins/BlobLib/TranslatableSnippet/labels_es_es.yml
Locale: es_es

rarity_common:
  Snippet: '&f&lCOMÚN'

rarity_uncommon:
  Snippet: '&e&lPOCO COMÚN'

rarity_rare:
  Snippet: '&b&lRARO'

rarity_epic:
  Snippet: '&d&lÉPICO'
```

---

## BlobLibTranslatableAPI

The primary entry point for retrieving loaded TranslatableSnippets.

```java
BlobLibTranslatableAPI api = BlobLibTranslatableAPI.getInstance();
```

---

### `getTranslatableSnippet(String identifier)`

Looks up a TranslatableSnippet by its identifier in the default (`en_us`) locale.

| Parameter    | Type   | Description                      |
|--------------|--------|----------------------------------|
| `identifier` | String | The TranslatableSnippet key.     |

**Returns:** `TranslatableSnippet` or `null`.

```java
TranslatableSnippet snippet = api.getTranslatableSnippet("rarity_epic");
String text = snippet.get(); // "§d§lEPIC"
```

---

### `getTranslatableSnippet(String identifier, String locale)`

Looks up a TranslatableSnippet by identifier for a specific locale, falling back to `en_us` if the locale is not available.

| Parameter    | Type   | Description                                |
|--------------|--------|--------------------------------------------|
| `identifier` | String | The TranslatableSnippet key.               |
| `locale`     | String | The locale tag (e.g. `"es_es"`, `"fr_fr"`). |

**Returns:** `TranslatableSnippet` or `null`.

```java
TranslatableSnippet snippet = api.getTranslatableSnippet("rarity_epic", "es_es");
String text = snippet.get(); // "§d§lÉPICO"
```

---

### `getTranslatableSnippet(String identifier, Player player)`

Looks up a TranslatableSnippet by identifier, localized to the player's locale.

| Parameter    | Type   | Description                       |
|--------------|--------|-----------------------------------|
| `identifier` | String | The TranslatableSnippet key.      |
| `player`     | Player | The player whose locale to use.   |

**Returns:** `TranslatableSnippet` or `null`.

```java
TranslatableSnippet snippet = api.getTranslatableSnippet("rarity_epic", player);
```

---

## TranslatableSnippet Interface

```java
public interface TranslatableSnippet extends Translatable<String>
```

`Translatable<T>` extends `DataAsset` and `Localizable`.

### Static methods

#### `TranslatableSnippet.by(String key)`

Shorthand lookup in the default locale. Delegates to `BlobLibTranslatableAPI.getInstance().getTranslatableSnippet(key)`.

| Parameter | Type   | Description                      |
|-----------|--------|----------------------------------|
| `key`     | String | The TranslatableSnippet key.     |

**Returns:** `TranslatableSnippet` or `null`.

```java
TranslatableSnippet snippet = TranslatableSnippet.by("rarity_common");
```

---

### Instance methods

#### `identifier()`

Returns the unique key of this TranslatableSnippet (inherited from `DataAsset`).

**Returns:** `String`.

```java
String key = snippet.identifier(); // "rarity_epic"
```

---

#### `locale()`

Returns the locale tag of this TranslatableSnippet (inherited from `Localizable`).

**Returns:** `String`.

```java
String locale = snippet.locale(); // "en_us"
```

---

#### `get()`

Returns the resolved snippet text (single string with color codes applied).

**Returns:** `String`.

```java
String text = snippet.get();
player.sendMessage(text);
```

---

#### `modify(Function<String, String> function)`

Returns a new `TranslatableSnippet` with the text transformed by the given function. The original snippet is unchanged — this creates a copy.

| Parameter  | Type                       | Description              |
|------------|----------------------------|--------------------------|
| `function` | `Function<String, String>` | String transformation.   |

**Returns:** a new `TranslatableSnippet`.

```java
TranslatableSnippet stripped = snippet.modify(s -> ChatColor.stripColor(s));
TranslatableSnippet withPlayer = snippet.modify(s -> s.replace("%player%", player.getName()));
```

---

#### `modder()`

Returns a `TranslatableSnippetModder` for chainable string modifications (see below).

**Returns:** `TranslatableSnippetModder`.

```java
TranslatableSnippet result = snippet.modder()
    .replace("%player%", player.getName())
    .color(ChatColor.GOLD)
    .get();
```

---

## TranslatableSnippetModder

`TranslatableSnippetModder` extends `BlobTranslatableModder<TranslatableSnippet, String>`, providing a fluent API for modifying the snippet text.

### Creation

```java
TranslatableSnippetModder modder = TranslatableSnippetModder.mod(snippet);
// or via the interface default:
TranslatableSnippetModder modder = snippet.modder();
```

### Modifier methods (all return the modder for chaining)

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
| `capitalize()`                                             | Capitalize first letter.                            |
| `stripColor()`                                             | Remove all color codes.                             |
| `translateRGBAndChatColors(char altColorChar)`             | Translate alternate color codes (e.g. `&`) to Minecraft colors. |
| `color(ChatColor color)`                                   | Prepend a color code to the text.                   |
| `trim()`                                                   | Trim leading/trailing whitespace.                   |
| `concat(String s)` / `append(String s)`                    | Append text to the end.                             |
| `prepend(String s)`                                        | Prepend text to the beginning.                      |
| `remove(String s)`                                         | Remove all occurrences (case-sensitive).            |
| `removeIgnoreCase(String s)`                               | Remove all occurrences (case-insensitive).          |
| `removeFirst(String s)`                                    | Remove first occurrence.                            |
| `removeLast(String s)`                                     | Remove last occurrence.                             |
| `removeFirstIgnoreCase(String s)`                          | Remove first occurrence (case-insensitive).         |
| `removeLastIgnoreCase(String s)`                           | Remove last occurrence (case-insensitive).          |
| `removeVowels()`                                           | Remove all vowels (a/e/i/o/u).                     |

### Terminal method

```java
TranslatableSnippet result = modder.get();
```

Returns the modified `TranslatableSnippet` with all transformations applied.

---

### Usage examples

```java
// Replace placeholders
TranslatableSnippet personalized = snippet.modder()
    .replace("%player%", player.getName())
    .replace("%balance%", String.valueOf(economy.getBalance(player)))
    .get();

// Strip all color codes for logging
TranslatableSnippet plain = snippet.modder()
    .stripColor()
    .get();

// Prepend a prefix
TranslatableSnippet withPrefix = snippet.modder()
    .prepend("&8» ")
    .get();

// Append a suffix and color gold
TranslatableSnippet styled = snippet.modder()
    .append(" &7(right click)")
    .color(ChatColor.GOLD)
    .get();
```

---

## BlobTranslatableSnippet — Static Parse Helpers

The concrete implementation `BlobTranslatableSnippet` provides two static parse methods that are used internally during construction but are also available for manual use.

### `BlobTranslatableSnippet.PARSE(String text, String locale)`

Parses the given text, resolving `{r=<key>}` references in the specified locale, then applies `TextColor.PARSE` to translate color codes.

| Parameter | Type   | Description                                    |
|-----------|--------|------------------------------------------------|
| `text`    | String | The raw text containing `{r=...}` references.  |
| `locale`  | String | The locale to resolve references in.           |

**Returns:** the fully resolved and color-parsed string.

### `BlobTranslatableSnippet.PARSE(String text)`

Same as above, but defaults the locale to `"en_us"`.

---

## TranslatableManager (for TranslatableSnippet)

`TranslatableManager` is the registry that holds all TranslatableSnippets and TranslatableBlocks. It loads `.yml` files from the configured directory and organises them by identifier → locale.

### Public Static Methods

| Method                                                                              | Description                                                                          |
|-------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------|
| `loadBlobPlugin(BlobPlugin plugin, IManagerDirector director)`                      | Loads snippets/blocks from a plugin's asset directories.                             |
| `loadBlobPlugin(BlobPlugin plugin)`                                                 | Loads snippets/blocks using the plugin's own `ManagerDirector`.                      |
| `unloadBlobPlugin(BlobPlugin plugin)`                                               | Removes all snippets and blocks that were registered by the given plugin.            |
| `continueLoadingSnippets(BlobPlugin plugin, boolean warnDuplicates, File... files)` | Loads TranslatableSnippets from specific files, tracking ownership. Duplicates warning is optional. |
| `continueLoadingSnippets(BlobPlugin plugin, File... files)`                         | Same as above with `warnDuplicates = true`.                                          |

### Public Instance Methods (for TranslatableSnippet)

| Method                                          | Returns                              | Description                                                                     |
|-------------------------------------------------|--------------------------------------|---------------------------------------------------------------------------------|
| `getSnippetRegistry(String identifier)`         | `TranslatableRegistry<TranslatableSnippet>` or `null` | Returns the registry for the given identifier.                  |
| `getSnippet(String identifier)`                 | `TranslatableSnippet` or `null`      | Returns the default (`en_us`) snippet.                                          |
| `getSnippet(String identifier, String locale)`  | `TranslatableSnippet` or `null`      | Returns the snippet for the given locale, falling back to default.              |
| `saveSnippet(File file, TranslatableSnippet snippet)` | `void`                        | Serialises a TranslatableSnippet to a `.yml` file.                              |

### Access from BlobLib

```java
TranslatableManager manager = BlobLib.getInstance().getTranslatableManager();
```

---

## DataAssetType

TranslatableSnippet is registered as `DataAssetType.TRANSLATABLE_SNIPPET`:

| Property          | Value                                  |
|-------------------|----------------------------------------|
| Enum constant     | `TRANSLATABLE_SNIPPET`                 |
| Key               | `"translatableSnippets"`               |
| Directory path    | `"/TranslatableSnippet"`               |
| Default file path | `"_translatable_snippets.yml"`         |
| Object name       | `"TranslatableSnippet"`                |

---

## Loading & Lifecycle

1. **BlobLib startup** — `BlobLib.onEnable()` creates the `TranslatableManager` and calls `load()`, which scans `plugins/BlobLib/TranslatableSnippet/` for all `.yml` files and registers them in the snippet registry.

2. **Plugin registration** — When a `BlobPlugin` calls `registerToBlobLib()`, the `PluginManager.loadAssets()` method invokes `TranslatableManager.loadBlobPlugin(plugin, director)`. This loads TranslatableSnippets from the plugin's own `TranslatableSnippet/` directory (resolved via the plugin's `IFileManager`).

3. **Manual loading** — Plugins can call `TranslatableManager.continueLoadingSnippets(plugin, warnDuplicates, files...)` to load additional TranslatableSnippet files at any point (e.g., from a custom folder, a world-specific directory, or a downloaded resource).

4. **Duplicate detection** — Within a single load batch, duplicate identifiers are collected. After the batch completes, a warning listing all duplicates is logged. The first file loaded for a given identifier wins; subsequent attempts to load the same identifier are silently ignored.

5. **BlobLib reload** — `BlobLib.reload()` calls `translatableManager.reload()`, which clears all registries and re-scans the core directory.

6. **Plugin unload** — When a `BlobPlugin` is disabled, `TranslatableManager.unloadBlobPlugin(plugin)` removes all TranslatableSnippets that were registered by that plugin.

7. **In-game saving** — The `TranslatableManager.saveSnippet(File, TranslatableSnippet)` method can be used to persist snippets programmatically. It writes the snippet text (with `§` translated back to `&`) and locale to a `.yml` file.

---

## Saving Snippets Programmatically

```java
TranslatableManager manager = BlobLib.getInstance().getTranslatableManager();
IFileManager fileManager = BlobLib.getInstance().getFileManager();
File snippetDir = fileManager.getDirectory(DataAssetType.TRANSLATABLE_SNIPPET);

// Create a snippet in code
TranslatableSnippet snippet = BlobTranslatableSnippet.of(
    "custom_snippet",
    "en_us",
    "&aCustom snippet text!"
);

// Save to a file (identifier is derived from filename)
File file = new File(snippetDir, "custom_snippet.yml");
manager.saveSnippet(file, snippet);
```

---

## Examples

See the [examples](examples/) directory for ready-to-use YAML files:

- [`translatablesnippet_single.yml`](examples/translatablesnippet_single.yml) — a file-root snippet definition
- [`translatablesnippet_multi.yml`](examples/translatablesnippet_multi.yml) — multiple snippets in one file
- [`translatablesnippet_references.yml`](examples/translatablesnippet_references.yml) — snippets using `{r=...}` reference resolution
- [`translatablesnippet_locale_overrides.yml`](examples/translatablesnippet_locale_overrides.yml) — locale-specific snippet overrides for existing identifiers
