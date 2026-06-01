# BlobInventory

BlobInventory is a locale-aware GUI inventory system built on Bukkit's `Inventory` API. It allows you to define complete GUIs (title, size, buttons, actions, permissions, prices) entirely through YAML configuration. Ideal for menus, shops, settings panels, and any interface whose text should change per player locale.

Buttons support actions, permission checks, economy payments, and TranslatableItem displays.

> For an extended variant with meta/submeta pagination and type-grouped shards, see [MetaBlobInventory](meta-blob-inventory.md).

---

## Directory & Discovery

- **BlobLib core directory:** `plugins/BlobLib/BlobInventory/`
- **Per-plugin directory:** `plugins/<PluginName>/BlobInventory/`
- **File format:** `.yml` (Bukkit `YamlConfiguration`)
- **Identifier:** the filename (without `.yml` extension) for file-level inventories; the section key for section-level inventories
- **Default file (core):** `bloblib_inventories.yml` (unpacked from the BlobLib jar)
- **Default file (plugin):** `<pluginname>_inventories.yml` (unpacked from the plugin jar if present, name is lowercased)
- **Subdirectories:** supported — all `.yml` files in subfolders are loaded recursively
- **Load filter:** a section is considered a BlobInventory if it has a valid `Size` integer field (must be > 0 and a multiple of 9)
- **Duplicate identifiers:** logged and skipped (first loaded wins)

---

## YAML Schema

```yaml
# File root → identifier = filename (without .yml)
Title: '&8My GUI'
Size: 54
Locale: en_us
Buttons:
  example_button:
    ItemStack:
      Material: DIAMOND
      DisplayName: '&b&lExample'
      Lore:
        - '&7Click me!'
    Slot: '13'
    Action: 'my_plugin:example_action'
    Permission: 'myplugin.example'
    Price: 100.0
    Cancel-Interaction: true

# OR organized in sections (each section = one inventory)
shop_menu:
  Title: '&6Shop'
  Size: 27
  Locale: en_us
  Buttons:
    # ...buttons...
```

### Top-Level Fields

| Field     | Type    | Required | Description                                                                                   |
|-----------|---------|----------|-----------------------------------------------------------------------------------------------|
| `Title`   | string  | **yes**  | The inventory title shown at the top. Supports `&`-color codes. Parsed via `TextColor.PARSE`. |
| `Size`    | int     | **yes**  | Number of slots. Must be a positive multiple of 9 (9, 18, 27, 36, 45, 54). Defaults to 54 if invalid. |
| `Locale`  | string  | no       | Locale tag for this inventory (default: `en_us`).                                             |
| `Buttons` | section | **yes**  | Map of button definitions (see below).                                                        |

### Buttons Section

Each key in the `Buttons` section defines a named button. The key is used to reference the button in code (via `getButton(key)`, `getSlots(key)`, etc.).

#### Button Fields

| Field                       | Type            | Required | Description                                                                                                              |
|-----------------------------|-----------------|----------|--------------------------------------------------------------------------------------------------------------------------|
| `ItemStack`                 | section/string  | **yes**  | The item displayed. Either an **inline `ItemStack` section** (using OmniStack fields) or a **string reference** to a TranslatableItem identifier (e.g. `BlobLib.Background`). |
| `Slot`                      | string          | **yes**  | Slot range. Single slot: `"13"`. Range: `"0-8"`. Multiple ranges not supported in one string; use separate buttons.      |
| `Amount`                    | int             | no       | Override the stack size for the displayed item.                                                                          |
| `Permission`                | string          | no       | Permission required to click this button. If the player lacks it, the action is blocked.                                 |
| `Permission-Denied`         | string          | no       | Inverted permission check — the action only proceeds if the player **lacks** this permission.                            |
| `Price`                     | double          | no       | Economy price to click this button. The amount is withdrawn from the player on click.                                    |
| `Price-Denied`              | double          | no       | Inverted price check — the action only proceeds if the player **does not have** this amount.                             |
| `Price-Currency`            | string          | no       | Custom currency identifier for multi-economy setups.                                                                     |
| `Has-Translatable-Item`     | string          | no       | The player must have this TranslatableItem identifier in their inventory to click.                                       |
| `Has-Translatable-Item-Denied` | string       | no       | Inverted TranslatableItem check — the player must **not** have this item.                                                |
| `Action`                    | string/section  | no       | Single action performed on click. Either a string (action reference) or a section with `Action` + `Action-Type` fields.  |
| `Actions`                   | section         | no       | Map of multiple named actions performed on click. Each entry is either a string reference or a section with `Action` + `Action-Type`. |
| `Cancel-Interaction`        | boolean         | no       | Whether the click event should be cancelled (default: `true`).                                                           |

#### Action Formats

A button can have a single action or multiple actions:

```yaml
# Simple string reference (must match an Action defined in the Actions system)
Action: 'my_plugin:open_shop'

# Section with explicit ActionType
Action:
  Action: 'my_plugin:teleport'
  Action-Type: 'PLAYER'

# Multiple named actions
Actions:
  open_menu:
    Action: 'my_plugin:open_main_menu'
    Action-Type: 'PLAYER'
  send_message:
    Action: 'my_plugin:welcome_message'
    Action-Type: 'PLAYER'

# String shorthand for multiple actions
Actions:
  open_menu: 'my_plugin:open_main_menu'
  send_message: 'my_plugin:welcome_message'
```

Valid `ActionType` values are defined in the `ActionType` enum (see [Action documentation](action.md)).

#### ItemStack Inline Fields

When `ItemStack` is specified as an inline section (not a string reference), it supports the full OmniStack field set:

| Field            | Type            | Description                                                                                     |
|------------------|-----------------|-------------------------------------------------------------------------------------------------|
| `Material`       | string          | **Required.** Material name (e.g. `DIAMOND`, `STONE`). Supports `HEAD-<url>`, `WM-<weapon>`, `IA-<id>`. |
| `DisplayName`    | string          | Display name with `&`-color codes.                                                              |
| `ItemName`       | string          | Item name component.                                                                            |
| `Lore`           | list of string  | Lore lines with `&`-color codes.                                                                |
| `CustomModelData`| int             | Custom model data integer.                                                                      |
| `Amount`         | int             | Stack size.                                                                                     |
| `Enchantments`   | list of string  | Enchantments in `enchantment:level` format.                                                     |
| `Unbreakable`    | boolean         | If true, item is unbreakable.                                                                   |
| `HideToolTip`    | boolean         | If true, tooltip is hidden.                                                                     |
| `ItemModel`      | string          | Item model as `namespace:path`.                                                                 |
| `Rarity`         | string          | Minecraft item rarity.                                                                          |
| `Color`          | string          | Leather armor color as `R,G,B`.                                                                 |
| `minimessage`    | boolean         | If true, `DisplayName`, `ItemName`, and `Lore` are parsed as MiniMessage.                       |

See [translatable-item.md](translatable-item.md) for the full OmniStack reference.

### ItemStack as a TranslatableItem reference

Instead of an inline `ItemStack` section, you can reference a TranslatableItem by its identifier:

```yaml
ItemStack: 'BlobLib.Background'   # references the "Background" TranslatableItem
ItemStack: 'MyPlugin.MyItem'      # references a plugin-defined TranslatableItem
```

The referenced TranslatableItem is resolved at load time for the inventory's locale.

---

## BlobInventory API

### Creating BlobInventory instances

```java
// From a registered key (locale-specific)
BlobInventory inventory = BlobInventory.ofKeyOrThrow("shop_menu", "en_us");

// From a key using a player's locale
BlobInventory inventory = BlobInventory.ofKeyAddressOrThrow("shop_menu", playerAddress);

// From a carrier (internal use)
BlobInventory inventory = BlobInventory.ofInventoryBuilderCarrier(carrier);
```

### Instance methods (inherited from `SharableInventory` / `InventoryBuilder`)

| Method | Returns | Description |
|--------|---------|-------------|
| `getTitle()` | `String` | The inventory title. |
| `setTitle(String)` | `void` | Updates the title. |
| `getSize()` | `int` | Inventory size in slots. |
| `getInventory()` | `Inventory` | The Bukkit `Inventory` instance. |
| `getButtonManager()` | `ButtonManager<InventoryButton>` | The underlying button manager. |
| `getButton(String key)` | `InventoryButton` or `null` | Gets the button definition by key. |
| `getButton(int slot)` | `ItemStack` | Gets the ItemStack at a slot. |
| `getSlots(String key)` | `Set<Integer>` or `null` | Gets all slot numbers for a button. |
| `getKeys()` | `Collection<String>` | All button keys. |
| `getLocale()` | `String` or `null` | The locale of this inventory. |
| `getKey()` | `String` or `null` | The registry key of this inventory. |
| `getPath()` | `String` or `null` | The file path this inventory was loaded from. |
| `copy()` | `BlobInventory` | Creates a deep copy. |
| `open(Player)` | `void` | Opens the inventory for a player. |
| `handleAll(String key, Player)` | `boolean` | Checks permission + money + item, then performs actions. |
| `modify(String key, Function<ItemStack, ItemStack>)` | `void` | Modifies all ItemStacks of a button. |
| `modder(String key, Consumer<ItemStackModder>)` | `void` | Modifies ItemStacks using `ItemStackModder`. |
| `setButton(int slot, ItemStack)` | `void` | Sets an item at the given slot. |
| `refillButton(String key)` | `void` | Refreshes all slots for a button. |
| `addDefaultButton(String key)` | `void` | Saves the current button as a default (persists across refreshes). |
| `loadDefaultButtons()` | `void` | Reloads all default buttons. |
| `getContents(String key)` | `Map<Integer, ItemStack>` | Gets current items for a button's slots. |
| `setContents(String key, Map<Integer, ItemStack>)` | `void` | Sets items for a button's slots. |
| `getDefaultButton(String key)` | `ItemStack` or `null` | Gets the default ItemStack for a button. |
| `isInsideButton(String key, int slot)` | `boolean` | Checks if a slot belongs to a button. |

---

## BlobLibInventoryAPI

The primary entry point for inventory operations.

```java
BlobLibInventoryAPI api = BlobLibInventoryAPI.getInstance();
```

### BlobInventory retrieval

| Method | Returns | Description |
|--------|---------|-------------|
| `getInventoryBuilderCarrier(String key, String locale)` | `InventoryBuilderCarrier<InventoryButton>` or `null` | Gets a BlobInventory carrier by key and locale. |
| `getInventoryBuilderCarrier(String key)` | `InventoryBuilderCarrier<InventoryButton>` or `null` | Gets the default locale carrier. |
| `getInventoryDataRegistry(String key)` | `InventoryDataRegistry<InventoryButton>` or `null` | Gets the full data registry for a BlobInventory. |

### Inventory tracking

| Method | Returns | Description |
|--------|---------|-------------|
| `trackInventory(Player, String key)` | `BlobInventoryTracker` or `null` | Creates a tracker that dynamically updates when the player's locale changes. |

### Selectors & Editors

| Method | Description |
|--------|-------------|
| `customSelector(blobInventoryKey, player, buttonRangeKey, dataType, selectorList, onSelect, display, onReturn, onClose, clickSound)` | Opens a paginated selection GUI based on a BlobInventory template. |
| `selector(player, dataType, selectorList, onSelect, display)` | Simplified selector using the default `VariableSelector` inventory. |
| `customEditor(blobInventoryKey, player, buttonRangeKey, dataType, addCollection, onAdd, addDisplay, viewCollection, removeDisplay, onRemove, onReturn, onClose, clickSound)` | Opens a paginated editor GUI for add/remove operations. |
| `editor(player, dataType, addCollection, onAdd, addDisplay, viewCollection, removeDisplay, onRemove)` | Simplified editor using the default `BlobEditor` inventory. |

---

## InventoryManager

The `InventoryManager` handles all inventory loading and retrieval.

```java
InventoryManager manager = BlobLib.getInstance().getInventoryManager();
```

### Static methods

| Method | Description |
|--------|-------------|
| `loadBlobPlugin(BlobPlugin, IManagerDirector)` | Loads inventories from a plugin's asset directories. |
| `loadBlobPlugin(BlobPlugin)` | Loads using the plugin's own `ManagerDirector`. |
| `unloadBlobPlugin(BlobPlugin)` | Removes all inventories registered by the plugin. |
| `continueLoadingBlobInventories(BlobPlugin, boolean, File...)` | Loads BlobInventories from specific files. |
| `continueLoadingBlobInventories(BlobPlugin, File...)` | Same with `warnDuplicates = true`. |

### Instance methods

| Method | Returns | Description |
|--------|---------|-------------|
| `getInventoryDataRegistry(String key)` | `InventoryDataRegistry<InventoryButton>` or `null` | Gets the registry by key. |
| `getInventoryBuilderCarrier(String key, String locale)` | `InventoryBuilderCarrier<InventoryButton>` or `null` | Gets a carrier by key and locale. |
| `getInventoryBuilderCarrier(String key)` | `InventoryBuilderCarrier<InventoryButton>` or `null` | Gets the default locale carrier. |
| `cloneInventory(String key, String locale)` | `BlobInventory` or `null` | Clones an inventory for a locale. |
| `cloneInventory(String key)` | `BlobInventory` or `null` | Clones the default locale inventory. |
| `getBlobInventories()` | `Map<String, InventoryDataRegistry<InventoryButton>>` | All registered BlobInventories (unmodifiable). |

---

## InventoryDataRegistry

BlobInventories are stored in `InventoryDataRegistry<InventoryButton>` objects. The registry holds locale-specific carriers and allows registering click/close event handlers programmatically.

```java
InventoryDataRegistry<InventoryButton> registry = manager.getInventoryDataRegistry("shop_menu");
registry.onClick("buy_button", event -> {
    // handle click
});
registry.onClose("shop_menu", (event, inventory) -> {
    // handle close
});
```

| Method | Description |
|--------|-------------|
| `process(InventoryBuilderCarrier)` | Registers a carrier for its locale. Returns `false` if duplicate. |
| `get(String locale)` | Gets the carrier for a locale, falling back to default. |
| `getDefault()` | Gets the default locale carrier. |
| `onClick(String button, Consumer<BlobInventoryClickEvent>)` | Registers a click handler for a specific button. |
| `onClick(String key, BiConsumer<InventoryClickEvent, T>)` | Registers a click handler for all buttons. |
| `onClose(String key, BiConsumer<InventoryCloseEvent, SharableInventory<?>>)` | Registers a close handler. |
| `onPlayerInventoryClick(String key, BiConsumer<BlobInventoryClickEvent, SharableInventory<?>>)` | Registers a handler for clicks in the player's own inventory. |
| `processSingleClickEvent(String, InventoryClickEvent)` | Internal — processes specific button click events. |
| `processClickEvent(InventoryClickEvent, T)` | Internal — processes general click events. |
| `processCloseEvents(InventoryCloseEvent, SharableInventory<?>)` | Internal — processes close events. |
| `processPlayerInventoryClickEvent(InventoryClickEvent, SharableInventory<?>)` | Internal — processes player inventory clicks. |

---

## InventoryTracker

`BlobInventoryTracker` dynamically updates the open inventory when a player's locale changes.

```java
BlobInventoryTracker tracker = BlobLibInventoryAPI.getInstance()
    .trackInventory(player, "shop_menu");
if (tracker != null) {
    tracker.getInventory().open(player);
    // When the player's locale changes, the inventory auto-updates
}
```

---

## DataAssetType

| Property          | Value                        |
|-------------------|------------------------------|
| Enum constant     | `BLOB_INVENTORY`             |
| Key               | `"blobInventories"`          |
| Directory path    | `"/BlobInventory"`           |
| Default file path | `"_inventories.yml"`         |
| Object name       | `"BlobInventory"`            |

---

## Loading & Lifecycle

1. **BlobLib startup** — `BlobLib.onEnable()` creates the `InventoryManager` and calls `load()`, which scans `plugins/BlobLib/BlobInventory/` for all `.yml` files and registers them.

2. **Plugin registration** — When a `BlobPlugin` calls `registerToBlobLib()`, `InventoryManager.loadBlobPlugin(plugin, director)` loads inventories from the plugin's own `BlobInventory/` directory.

3. **Manual loading** — Plugins can call `continueLoadingBlobInventories(plugin, warnDuplicates, files...)` to load additional inventory files at any point.

4. **Duplicate detection** — Duplicate identifiers are collected, logged, and skipped (first loaded wins).

5. **BlobLib reload** — `BlobLib.reload()` calls `inventoryManager.reload()`, which clears all registries and re-scans the core directory.

6. **Plugin unload** — When a `BlobPlugin` is disabled, `InventoryManager.unloadBlobPlugin(plugin)` removes all BlobInventories registered by that plugin.

---

## Usage Examples

### Opening an inventory

```java
BlobInventory inventory = BlobInventory.ofKeyOrThrow("shop_menu", player.getLocale());
inventory.open(player);
```

### Cloning for a player

```java
InventoryManager manager = BlobLib.getInstance().getInventoryManager();
BlobInventory inventory = manager.cloneInventory("shop_menu", player.getLocale());
inventory.open(player);
```

### Tracking locale changes

```java
BlobInventoryTracker tracker = BlobLibInventoryAPI.getInstance()
    .trackInventory(player, "shop_menu");
if (tracker != null) {
    player.openInventory(tracker.getInventory().getInventory());
    // The tracker auto-updates if the player's locale changes
}
```

### Using InventoryDataRegistry events

```java
InventoryDataRegistry<InventoryButton> registry = manager.getInventoryDataRegistry("shop_menu");
registry.onClick("buy_button", event -> {
    // Custom click handling
    event.setPlayClickSound(true);
});
```

---

## Examples

See the [examples](examples/) directory for ready-to-use YAML files:

- [`blobinventory_simple.yml`](examples/blobinventory_simple.yml) — a simple BlobInventory with a few buttons
- [`blobinventory_shop.yml`](examples/blobinventory_shop.yml) — a shop GUI with permissions, prices, and item references
- [`blobinventory_locale_overrides.yml`](examples/blobinventory_locale_overrides.yml) — locale-specific overrides for an existing inventory
