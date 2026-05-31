# TagSet

TagSets are named collections of string tags used for filtering, categorisation, and membership checks. They support direct inclusions and exclusions, as well as references to other TagSets whose inclusions are merged or subtracted at load time.

TagSets are the unit behind BlobLib's tag-based filtering system — any system that asks "does this entity/object have tag X?" can be backed by a TagSet.

---

## Directory & Discovery

- **BlobLib core directory:** `plugins/BlobLib/TagSet/`
- **Per-plugin directory:** `plugins/<PluginName>/TagSet/` (created automatically by `BlobFileManager`)
- **File format:** `.yml`
- **Identifier:** the filename (without `.yml` extension) for file-level assets; the section key for section-level assets
- **Default file (core):** `bloblib_tag_sets.yml` (unpacked from the BlobLib jar if present)
- **Default file (plugin):** `<pluginname>_tag_sets.yml` (unpacked from the plugin jar if present, name is lowercased)
- **Subdirectories:** supported — all `.yml` files in subfolders are loaded recursively
- **Load filter:** a section is considered a TagSet if it has a list at `Inclusions` **or** a non-empty list at `Include-Tags`
- **Duplicate identifiers:** logged and skipped (first loaded wins)

---

## YAML Schema

TagSets can be defined at the file root (the entire file is one TagSet, its identifier is the filename) or as named subsections within a file.

```yaml
# File root → identifier = filename (without .yml)
Inclusions:
  - 'some_tag'
  - 'another_tag'

# OR organized as sections
my_weapons:
  Inclusions:
    - 'sword'
    - 'bow'
  Exclusions:
    - 'wooden_sword'
```

### Fields

| Field          | Type        | Description                                                                 |
|----------------|-------------|-----------------------------------------------------------------------------|
| `Inclusions`   | list of str | Tags to include in this set.                                                |
| `Exclusions`   | list of str | Tags to exclude from the final set (subtracted after merges).               |
| `Include-Tags` | list of str | References to other TagSets by identifier. Their **inclusions** are merged into this set. |
| `Exclude-Tags` | list of str | References to other TagSets by identifier. Their **inclusions** are added to this set's exclusions. |

### Resolution Order

When a TagSet is read:

1. Direct `Inclusions` are collected.
2. Each identifier in `Include-Tags` is resolved via `BlobLibTagAPI#getTagSet`; if found, its **inclusions** are added.
3. Direct `Exclusions` are collected.
4. Each identifier in `Exclude-Tags` is resolved; if found, its **inclusions** are added to the exclusions.
5. The final TagSet contains `(merged inclusions) - (merged exclusions)`.

> **Note:** `Exclude-Tags` uses the **inclusions** of the referenced TagSet, not its exclusions. This lets you build a "deny list" TagSet that is reused across multiple consumers.

---

## Example TagSet (`tag_sets.yml`)

```yaml
# plugins/BlobLib/TagSet/weapon_tiers.yml
legendary:
  Inclusions:
    - 'tier_legendary'

rare:
  Inclusions:
    - 'tier_rare'

common:
  Inclusions:
    - 'tier_common'

# plugins/BlobLib/TagSet/damage_types.yml
physical:
  Inclusions:
    - 'damage_slash'
    - 'damage_blunt'
    - 'damage_pierce'

magical:
  Inclusions:
    - 'damage_fire'
    - 'damage_frost'
    - 'damage_arcane'

all_physical_weapons:
  Include-Tags:
    - 'legendary'
    - 'rare'
    - 'common'
    - 'physical'
  Exclusions:
    - 'tier_common'             # exclude common tier directly
  Exclude-Tags:
    - 'legendary'               # excludes legendary's inclusions too
```

In the example above, `all_physical_weapons` would resolve to:
`{damage_slash, damage_blunt, damage_pierce, tier_rare}` — `legendary`'s inclusions are used as exclusions, and `common`'s inclusions (`tier_common`) is an exclusion.

---

## BlobLibTagAPI

The main entry point for accessing loaded TagSets.

```java
BlobLibTagAPI api = BlobLibTagAPI.getInstance();
```

### `getTagSetManager()`

Returns the `DataAssetManager<TagSet>` that manages all loaded TagSets.

**Signature:**
```java
public DataAssetManager<TagSet> getTagSetManager()
```

---

### `getTagSet(String key)`

Looks up a TagSet by its identifier.

| Parameter | Type   | Description                        |
|-----------|--------|------------------------------------|
| `key`     | String | The TagSet identifier.             |

**Returns:** `TagSet` or `null` if no TagSet with that key is registered.

```java
TagSet tierSet = BlobLibTagAPI.getInstance().getTagSet("legendary");
if (tierSet != null && tierSet.contains("tier_legendary")) {
    // ...
}
```

---

### `getAll()`

Returns every loaded TagSet.

**Signature:**
```java
public List<TagSet> getAll()
```

**Returns:** a list of all `TagSet` instances currently in memory.

```java
for (TagSet set : BlobLibTagAPI.getInstance().getAll()) {
    // process each set
}
```

---

## TagSet Record

`TagSet` is an immutable Java record.

```java
public record TagSet(@NotNull Set<String> getInclusions,
                     @NotNull String identifier) implements DataAsset
```

| Component         | Type            | Description                    |
|-------------------|-----------------|--------------------------------|
| `getInclusions()` | `Set<String>`   | The resolved set of tag strings. |
| `identifier()`    | `String`        | The unique key of this TagSet.   |

### `by(String key)`

Static lookup shorthand. Delegates to `BlobLibTagAPI.getInstance().getTagSet(key)`.

| Parameter | Type   | Description                        |
|-----------|--------|------------------------------------|
| `key`     | String | The TagSet identifier.             |

**Returns:** `TagSet` or `null`.

```java
TagSet set = TagSet.by("my_tags");
```

### `contains(String tag)`

Checks whether the tag is in the resolved inclusions set.

| Parameter | Type   | Description              |
|-----------|--------|--------------------------|
| `tag`     | String | The tag to check for.    |

**Returns:** `true` if the inclusions set contains the tag.

```java
if (tagSet.contains("damage_fire")) {
    // apply fire damage
}
```

---

## TagSetIO

The `TagSetIO` utility class handles YAML serialisation and deserialisation.

### `READ(ConfigurationSection section, String key)`

Reads a `TagSet` from a `ConfigurationSection`.

| Parameter | Type                  | Description                                      |
|-----------|-----------------------|--------------------------------------------------|
| `section` | `ConfigurationSection`| The YAML section to read from.                   |
| `key`     | `String`              | The identifier for the resulting TagSet.          |

**Returns:** a new `TagSet` with inclusions resolved from `Inclusions`, `Exclusions`, `Include-Tags`, and `Exclude-Tags`.

**Throws:** `NullPointerException` if either argument is null.

---

### `WRITE(ConfigurationSection section, TagSet tagSet)`

Writes a `TagSet`'s inclusions to a `ConfigurationSection`.

| Parameter | Type                  | Description                                        |
|-----------|-----------------------|----------------------------------------------------|
| `section` | `ConfigurationSection`| The YAML section to write into.                    |
| `tagSet`  | `TagSet`              | The TagSet to serialise (only `getInclusions`).    |

> **Note:** Only the resolved `Inclusions` set is serialised. References (`Include-Tags`, `Exclude-Tags`) and `Exclusions` are **not** written back — they are resolved at read time and the final set is what gets persisted.

**Throws:** `NullPointerException` if either argument is null.

```java
TagSetIO.WRITE(section, myTagSet);
```

---

## DataAssetManager\<TagSet\>

The underlying manager is a `DataAssetManager<TagSet>` constructed in `BlobLib.onEnable()`:

```java
tagSetManager = DataAssetManager.of(
    fileManager.getDirectory(DataAssetType.TAG_SET),
    TagSetIO::READ,
    DataAssetType.TAG_SET,
    section -> section.isList("Inclusions") ||
               !section.getStringList("Include-Set").isEmpty(),
    TagSetIO::WRITE
);
```

### Public Methods (from `DataAssetManager<TagSet>`)

| Method                                                      | Description                                                             |
|-------------------------------------------------------------|-------------------------------------------------------------------------|
| `getAsset(String identifier)`                               | Returns the `TagSet` for the given identifier, or `null`.              |
| `getAssets()`                                               | Returns a list of all loaded `TagSet` instances.                        |
| `mapAssets()`                                               | Returns an unmodifiable `Map<String, TagSet>` of identifier → TagSet.   |
| `reload()`                                                  | Scans the asset directory and reloads all TagSets.                      |
| `reload(BlobPlugin, IManagerDirector)`                      | Loads TagSets from a plugin's directory.                                |
| `unload(BlobPlugin)`                                        | Removes all TagSets that were registered by the given plugin.           |
| `saveAsset(File file, TagSet asset)`                        | Serialises a TagSet to a `.yml` file using `TagSetIO.WRITE`.           |
| `continueLoadingAssets(BlobPlugin, boolean, File...)`       | Loads TagSets from specific files, tracking ownership to the plugin.    |

---

## DataAssetType

TagSet is registered as `DataAssetType.TAG_SET`:

| Property          | Value                        |
|-------------------|------------------------------|
| Enum constant     | `TAG_SET`                    |
| Key               | `"tagSets"`                  |
| Directory path    | `"/TagSet"`                  |
| Default file path | `"_tag_sets.yml"`            |
| Object name       | `"TagSet"`                   |

---

## Loading & Lifecycle

1. **BlobLib startup** — `BlobLib.onEnable()` creates the `DataAssetManager<TagSet>` and calls `reload()`, scanning `plugins/BlobLib/TagSet/` for all `.yml` files.
2. **Plugin registration** — When a `BlobPlugin` calls `registerToBlobLib()`, the `PluginManager` calls `tagSetManager.reload(plugin, director)`, loading TagSets from the plugin's own `TagSet/` directory.
3. **Manual loading** — Plugins can call `tagSetManager.continueLoadingAssets(plugin, true, files...)` to load additional TagSet files at any point.
4. **BlobLib reload** — `BlobLib.reload()` calls `tagSetManager.reload()`, re-scanning the core directory.
5. **Plugin unload** — When a `BlobPlugin` is disabled, `tagSetManager.unload(plugin)` removes all TagSets it registered. Duplicate identifiers from other plugins are preserved.

---

## Examples

See the [examples](examples/) directory for ready-to-use YAML files:

- [`tagset_simple_tags.yml`](examples/tagset_simple_tags.yml) — basic TagSets with direct inclusions and exclusions
- [`tagset_referencing.yml`](examples/tagset_referencing.yml) — TagSets that reference other TagSets via `Include-Tags` and `Exclude-Tags`
- [`tagset_plugin_integration.yml`](examples/tagset_plugin_integration.yml) — example of a plugin organising its tags with cross-references
