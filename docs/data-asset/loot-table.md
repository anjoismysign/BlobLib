# Loot Tables

Loot tables define a weighted, randomised pool of items that can be dropped or given to players. BlobLib reads loot tables from JSON files placed in the `LootTable` directory and exposes them through `BlobLibLootAPI`.

---

## Directory & Discovery

- **Runtime directory:** `plugins/BlobLib/LootTable/`
- **File format:** `.json`
- **Identifier:** the filename without extension (e.g. `dungeon_chest.json` → `dungeon_chest`)
- **Subdirectories:** supported — all `.json` files in subfolders are loaded recursively
- **Duplicate identifiers:** logged and skipped (first loaded wins)
- **Bundled example:** `BlobLib.Example.json` is written automatically on first run if absent

---

## JSON Schema

```json
{
  "pools": [
    {
      "rolls": <int>,
      "entries": [
        {
          "type": "<entry-type>",
          "name": "<namespaced-id>",
          "weight": <int>,
          "functions": [
            {
              "function": "<function-type>",
              "count": {
                "min": <int>,
                "max": <int>
              }
            }
          ]
        }
      ]
    }
  ]
}
```

### Top level

| Field   | Type  | Description                           |
|---------|-------|---------------------------------------|
| `pools` | array | One or more loot pools (see below).   |

### Pool object

| Field     | Type  | Description                                              |
|-----------|-------|----------------------------------------------------------|
| `rolls`   | int   | Number of times this pool is rolled per generation.      |
| `entries` | array | Weighted entries that can be selected on each roll.      |

### Entry object

| Field       | Type    | Description                                                    |
|-------------|---------|----------------------------------------------------------------|
| `type`      | string  | Entry type key (see below).                                    |
| `name`      | string  | Namespaced item ID (for `minecraft:item`) or translatable key. |
| `weight`    | int     | Relative weight (defaults to `1` if omitted).                  |
| `functions` | array   | Optional list of functions to apply after selection.           |

### Function object (currently only `minecraft:set_count`)

| Field      | Type   | Description                                          |
|------------|--------|------------------------------------------------------|
| `function` | string | Function type key.                                   |
| `count`    | object | Range object with `min` and `max` (both int).        |

---

## Entry Types

| Type key                      | `name` expected value                | Behaviour                                     |
|-------------------------------|--------------------------------------|-----------------------------------------------|
| `minecraft:item`              | Item namespaced ID (e.g. `minecraft:diamond`) | Creates an `ItemStack` from the vanilla item registry. |
| `minecraft:empty`             | Ignored                              | Entry is skipped (produces nothing).          |
| `bloblib:translatableitem`    | Translatable item key                 | Looks up a `TranslatableItem` and returns a clone.    |

---

## Functions

### `minecraft:set_count`

Randomises the stack amount within a range.

| Parameter | Type | Description |
|-----------|------|-------------|
| `count.min` | int | Minimum stack size (inclusive). |
| `count.max` | int | Maximum stack size (inclusive). |

If omitted from an entry, the item's default stack size (typically `1`) is used.

---

## BlobLibLootAPI

Access the API via the singleton:

```java
BlobLibLootAPI api = BlobLibLootAPI.getInstance();
```

### `getLootTables()`

Returns an immutable map of all loaded `LootTable` instances keyed by identifier.

### `spawn(Location location, String identifier)`

Drops the loot table's items naturally at the given world location. No localization is applied.

| Parameter    | Type     | Description                       |
|--------------|----------|-----------------------------------|
| `location`   | Location | Where items are dropped.          |
| `identifier` | String   | Loot table identifier.            |

Returns `true` if the loot table exists and items were dropped.

### `giveToInventory(Inventory inventory, String identifier, String locale)`

Places the generated items directly into the given inventory. Supports optional localization of translatable items.

| Parameter    | Type      | Description                                              |
|--------------|-----------|----------------------------------------------------------|
| `inventory`  | Inventory | Target inventory.                                        |
| `identifier` | String    | Loot table identifier.                                   |
| `locale`     | String    | Locale string (e.g. `"en_us"`) or `null` to skip localization. |

Returns `true` if the loot table exists and items were added.

### `generateLoot(String identifier, String locale)`

Generates the loot and returns the `Collection<ItemStack>` without spawning or inventory insertion — useful for previews or custom distribution.

| Parameter    | Type   | Description                                              |
|--------------|--------|----------------------------------------------------------|
| `identifier` | String | Loot table identifier.                                   |
| `locale`     | String | Locale string or `null` to skip localization.           |

Returns an empty collection if the loot table identifier is not registered.

---

### LootTable.by(String key)

Static convenience method on the record itself:

```java
LootTable table = LootTable.by("dungeon_chest");
```

---

## Loading Order

1. BlobLib loads its own `LootTable/` directory on startup.
2. Plugins using BlobLib's asset system can register additional loot tables via `LootTableManager#reload(BlobPlugin, IManagerDirector)`.
3. Plugins can call `LootTableManager#continueLoadingAssets(BlobPlugin, boolean, File...)` to load extra files at any point.
4. When a plugin is unloaded, all loot tables it registered are removed automatically.

---

## Examples

See the [examples](examples/) directory for ready-to-use JSON files:

- [`simple_drop.json`](examples/loottable_simple_drop.json) — a basic single-pool table
- [`multiple_pools.json`](examples/loottable_multiple_pools.json) — two pools with different entry types
- [`weighted_entries.json`](examples/loottable_weighted_entries.json) — demonstrates weighted probability
- [`translatable_item_example.json`](examples/loottable_translatable_item_example.json) — uses `bloblib:translatableitem`
