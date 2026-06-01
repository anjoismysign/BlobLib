# MetaBlobInventory

MetaBlobInventory is an extended locale-aware GUI inventory built on [BlobInventory](blob-inventory.md). It adds a **type** identifier and **meta/submeta** button grouping, enabling paginated or categorised GUIs where buttons can be organised into groups and queried at runtime via `MetaInventoryShard`.

MetaBlobInventories with the same `Type` are automatically grouped into a `MetaInventoryShard`, allowing you to retrieve or iterate over all inventories of a given type.

Meta buttons have a `Meta` string (default: `"NONE"`) and an optional `SubMeta` string for finer granularity.

---

## Directory & Discovery

- **BlobLib core directory:** `plugins/BlobLib/MetaBlobInventory/`
- **Per-plugin directory:** `plugins/<PluginName>/MetaBlobInventory/`
- **File format:** `.yml` (Bukkit `YamlConfiguration`)
- **Identifier:** the filename (without `.yml` extension) for file-level inventories; the section key for section-level inventories
- **Subdirectories:** supported — all `.yml` files in subfolders are loaded recursively
- **Load filter:** a section is considered a MetaBlobInventory if it has a valid `Size` integer field (must be > 0 and a multiple of 9)
- **Duplicate identifiers:** logged and skipped (first loaded wins)
- **Type grouping:** Meta inventories with the same `Type` are grouped into a `MetaInventoryShard`

---

## YAML Schema

MetaBlobInventory uses the same YAML structure as [BlobInventory](blob-inventory.md) with two additions:
- A `Type` field at the inventory level
- `Meta` and `SubMeta` fields on buttons

```yaml
# File root → identifier = filename (without .yml)
Title: '&6📜 Quest Menu'
Size: 54
Type: QUEST
Locale: en_us
Buttons:
  background:
    ItemStack: BlobLib.Background
    Slot: '45-53'

  daily_tab:
    ItemStack:
      Material: CLOCK
      DisplayName: '&e&lDaily Quests'
    Slot: '46'
    Meta: 'tab_daily'
    Action: 'myplugin:filter_daily'

  daily_kill_zombies:
    ItemStack:
      Material: ZOMBIE_HEAD
      DisplayName: '&aKill 10 Zombies'
      Lore:
        - '&7Progress: &e5/10'
    Slot: '10'
    Meta: 'quest_daily'
    SubMeta: 'combat'
    Action: 'myplugin:quest_info'

# OR organized in sections
potion_shop:
  Title: '&6Potion Shop'
  Size: 36
  Type: SHOP
  Locale: en_us
  Buttons:
    # ...
```

### Top-Level Fields

| Field     | Type    | Required | Description                                                                                                        |
|-----------|---------|----------|--------------------------------------------------------------------------------------------------------------------|
| `Title`   | string  | **yes**  | The inventory title shown at the top. Supports `&`-color codes. Parsed via `TextColor.PARSE`.                      |
| `Size`    | int     | **yes**  | Number of slots. Must be a positive multiple of 9 (9, 18, 27, 36, 45, 54). Defaults to 54 if invalid.              |
| `Type`    | string  | no       | The type identifier for this inventory. Inventories with the same type are grouped into a `MetaInventoryShard`. Default: `"DEFAULT"`. |
| `Locale`  | string  | no       | Locale tag for this inventory (default: `en_us`).                                                                  |
| `Buttons` | section | **yes**  | Map of button definitions (see below).                                                                             |

### Button Fields

MetaBlobInventory buttons support all standard [BlobInventory button fields](blob-inventory.md#button-fields) (`ItemStack`, `Slot`, `Permission`, `Price`, `Action`, `Actions`, `Cancel-Interaction`, etc.) plus the following extras:

| Field     | Type   | Required | Description                                                                                 |
|-----------|--------|----------|---------------------------------------------------------------------------------------------|
| `Meta`    | string | no       | Meta identifier for this button (default: `"NONE"`). Used to group buttons by category/page. |
| `SubMeta` | string | no       | Optional sub-meta for finer grouping within a meta category.                                |

The shared fields (`ItemStack`, `Slot`, `Amount`, `Permission`, `Permission-Denied`, `Price`, `Price-Denied`, `Price-Currency`, `Has-Translatable-Item`, `Has-Translatable-Item-Denied`, `Action`, `Actions`, `Cancel-Interaction`) behave identically to BlobInventory. See the [BlobInventory button field table](blob-inventory.md#button-fields) for the full reference.

---

## MetaBlobInventory API

`MetaBlobInventory` extends `SharableInventory<MetaInventoryButton>` and adds the following:

| Method | Returns | Description |
|--------|---------|-------------|
| `getType()` | `String` | The type identifier for this inventory. |
| `belongsToAMetaButton(int slot)` | `Result<MetaInventoryButton>` | Finds the first meta button that contains the given slot. |
| `copy()` | `MetaBlobInventory` | Creates a deep copy. |

All methods inherited from `SharableInventory` / `InventoryBuilder` (such as `getTitle()`, `getButton()`, `getSlots()`, `open()`, `handleAll()`, `modify()`, etc.) are also available — see the [BlobInventory instance methods](blob-inventory.md#instance-methods-inherited-from-sharableinventory--inventorybuilder) for the full list.

### Factory methods

```java
// From a registered key (locale-specific)
MetaBlobInventory inventory = MetaBlobInventory.fromInventoryBuilderCarrier(carrier);

// Via InventoryManager:
MetaBlobInventory inventory = manager.getMetaInventory("some_key", "en_us");
MetaBlobInventory clone = manager.cloneMetaInventory("some_key", "en_us");
```

---

## BlobLibInventoryAPI (MetaBlobInventory)

```java
BlobLibInventoryAPI api = BlobLibInventoryAPI.getInstance();
```

### MetaBlobInventory retrieval

| Method | Returns | Description |
|--------|---------|-------------|
| `getMetaInventoryBuilderCarrier(String key, String locale)` | `InventoryBuilderCarrier<MetaInventoryButton>` or `null` | Gets a MetaBlobInventory carrier by key and locale. |
| `getMetaInventoryBuilderCarrier(String key)` | `InventoryBuilderCarrier<MetaInventoryButton>` or `null` | Gets the default locale carrier. |
| `getMetaInventoryDataRegistry(String key)` | `InventoryDataRegistry<MetaInventoryButton>` or `null` | Gets the full data registry for a MetaBlobInventory. |
| `hasMetaInventoryShard(String type)` | `Optional<MetaInventoryShard>` | Gets the shard for a MetaBlobInventory type. |

### Inventory tracking

| Method | Returns | Description |
|--------|---------|-------------|
| `trackMetaInventory(Player, String key)` | `MetaBlobInventoryTracker` or `null` | Creates a tracker that dynamically updates when the player's locale changes. |

---

## InventoryManager (MetaBlobInventory)

```java
InventoryManager manager = BlobLib.getInstance().getInventoryManager();
```

### Static methods

| Method | Description |
|--------|-------------|
| `continueLoadingMetaInventories(BlobPlugin, boolean, File...)` | Loads MetaBlobInventories from specific files. |
| `continueLoadingMetaInventories(BlobPlugin, File...)` | Same with `warnDuplicates = true`. |

### Instance methods

| Method | Returns | Description |
|--------|---------|-------------|
| `getMetaInventoryDataRegistry(String key)` | `InventoryDataRegistry<MetaInventoryButton>` or `null` | Gets the registry by key. |
| `getMetaInventoryBuilderCarrier(String key, String locale)` | `InventoryBuilderCarrier<MetaInventoryButton>` or `null` | Gets a carrier by key and locale. |
| `getMetaInventoryBuilderCarrier(String key)` | `InventoryBuilderCarrier<MetaInventoryButton>` or `null` | Gets the default locale carrier. |
| `getMetaInventory(String key, String locale)` | `MetaBlobInventory` or `null` | Builds a MetaBlobInventory from a carrier. |
| `getMetaInventory(String key)` | `MetaBlobInventory` or `null` | Builds from the default locale carrier. |
| `cloneMetaInventory(String key, String locale)` | `MetaBlobInventory` or `null` | Clones a MetaBlobInventory for a locale. |
| `cloneMetaInventory(String key)` | `MetaBlobInventory` or `null` | Clones the default locale. |
| `getMetaInventoryShard(String type)` | `MetaInventoryShard` or `null` | Gets the shard for a type. |
| `getMetaInventories()` | `Map<String, InventoryDataRegistry<MetaInventoryButton>>` | All registered MetaBlobInventories (unmodifiable). |

---

## MetaInventoryShard

When MetaBlobInventories share the same `Type`, they are grouped into a `MetaInventoryShard`. This allows you to retrieve or iterate over all inventories of a given type.

```java
Optional<MetaInventoryShard> shard = BlobLibInventoryAPI.getInstance().hasMetaInventoryShard("SHOP");
shard.ifPresent(s -> {
    List<MetaBlobInventory> all = s.allInventories();
    MetaBlobInventory inv = s.getInventory("potion_shop");
    MetaBlobInventory copy = s.copyInventory("potion_shop");
    int count = s.size();
});
```

| Method | Returns | Description |
|--------|---------|-------------|
| `getMetaInventoryBuilderCarrier(String key)` | `InventoryBuilderCarrier<MetaInventoryButton>` or `null` | Gets a carrier by key. |
| `getInventory(String key)` | `MetaBlobInventory` or `null` | Builds an inventory from the carrier. |
| `copyInventory(String key)` | `MetaBlobInventory` or `null` | Clones the inventory. |
| `allInventories()` | `List<MetaBlobInventory>` | All inventories in this shard. |
| `size()` | `int` | Number of inventories in the shard. |

---

## MetaBlobInventoryTracker

`MetaBlobInventoryTracker` dynamically updates the open inventory when a player's locale changes.

```java
MetaBlobInventoryTracker tracker = BlobLibInventoryAPI.getInstance()
    .trackMetaInventory(player, "quest_menu");
if (tracker != null) {
    tracker.getInventory().open(player);
    // When the player's locale changes, the inventory auto-updates
}
```

---

## DataAssetType

| Property          | Value                              |
|-------------------|------------------------------------|
| Enum constant     | `META_BLOB_INVENTORY`              |
| Key               | `"metaBlobInventories"`            |
| Directory path    | `"/MetaBlobInventory"`             |
| Default file path | `"_meta_inventories.yml"`          |
| Object name       | `"MetaBlobInventory"`              |

---

## Loading & Lifecycle

1. **BlobLib startup** — `BlobLib.onEnable()` creates the `InventoryManager` and calls `load()`, which scans `plugins/BlobLib/MetaBlobInventory/` for all `.yml` files and registers them.

2. **Plugin registration** — When a `BlobPlugin` calls `registerToBlobLib()`, `InventoryManager.loadBlobPlugin(plugin, director)` loads MetaBlobInventories from the plugin's own `MetaBlobInventory/` directory.

3. **Manual loading** — Plugins can call `continueLoadingMetaInventories(plugin, warnDuplicates, files...)` to load additional inventory files at any point.

4. **Duplicate detection** — Duplicate identifiers are collected, logged, and skipped (first loaded wins).

5. **BlobLib reload** — `BlobLib.reload()` calls `inventoryManager.reload()`, which clears all registries and re-scans the core directory.

6. **Plugin unload** — When a `BlobPlugin` is disabled, `InventoryManager.unloadBlobPlugin(plugin)` removes all MetaBlobInventories registered by that plugin.

---

## Usage Examples

### Creating and opening a MetaBlobInventory

```java
InventoryManager manager = BlobLib.getInstance().getInventoryManager();
MetaBlobInventory inventory = manager.cloneMetaInventory("quest_menu", player.getLocale());
if (inventory != null) {
    inventory.open(player);
}
```

### Querying by Meta/SubMeta

```java
// Get all quests with a specific meta
MetaBlobInventory inventory = manager.getMetaInventory("quest_menu", "en_us");
if (inventory != null) {
    // iterate buttons and filter by Meta/SubMeta via getButton(key)
    for (String key : inventory.getKeys()) {
        MetaInventoryButton button = (MetaInventoryButton) inventory.getButton(key);
        if ("quest_daily".equals(button.getMeta())) {
            // This is a daily quest button
        }
    }
}
```

### MetaBlobInventory with shard

```java
// All MetaBlobInventories with Type: "SHOP" are in one shard
Optional<MetaInventoryShard> shard = BlobLibInventoryAPI.getInstance()
    .hasMetaInventoryShard("SHOP");
shard.ifPresent(s -> {
    MetaBlobInventory shop = s.copyInventory("potion_shop");
    shop.open(player);
});
```

### Using a MetaInventoryButton

```java
MetaInventoryButton button = (MetaInventoryButton) inventory.getButton("daily_kill_zombies");
String meta = button.getMeta();       // "quest_daily"
String subMeta = button.getSubMeta(); // "combat"
boolean hasMeta = button.hasMeta();   // true (meta != "NONE")
```

---

## Examples

See the [examples](examples/) directory for a ready-to-use YAML file:

- [`metablobinventory_categories.yml`](examples/metablobinventory_categories.yml) — a MetaBlobInventory demonstrating Meta/SubMeta button grouping with Type-based sharding
