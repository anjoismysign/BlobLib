# TranslatableItem

TranslatableItem is a locale-aware `DataAsset` that represents an **`ItemStack`** with locale-specific display name, lore, and other item data. Each item is identified by a unique key, localized per locale tag, and linked to a `TranslatableRarity`. It is ideal for custom items that need translation across locales — GUI buttons, category icons, loot items, rank displays, or any in-game item whose text should change with the player's language.

The item's display name and lore support `&`-style color codes and hex color codes via `TextColor.PARSE`. The `ItemStack` definition supports the full `OmniStack` system — see [ItemStackReader](#itemstackreader--omnistack) for the complete list of configurable fields.

TranslatableItems embed a **key** and **locale** into the `CustomModelDataComponent` strings of the generated `ItemStack`, enabling reverse lookup: given any `ItemStack`, you can retrieve its originating `TranslatableItem` via `TranslatableItem.byItemStack(itemStack)`.

---

## Directory & Discovery

- **BlobLib core directory:** `plugins/BlobLib/TranslatableItem/`
- **Per-plugin directory:** `plugins/<PluginName>/TranslatableItem/` (created automatically by the plugin's `IFileManager`)
- **File format:** `.yml` (Bukkit `YamlConfiguration`)
- **Identifier:** the filename (without `.yml` extension) for file-level assets; the section key for section-level assets
- **Default file (core):** `bloblib_translatable_items.yml` (unpacked from the BlobLib jar)
- **Default file (plugin):** `<pluginname>_translatable_items.yml` (unpacked from the plugin jar if present, name is lowercased)
- **Subdirectories:** supported — all `.yml` files in subfolders are loaded recursively
- **Load filter:** a section is considered a TranslatableItem if it has a `ItemStack` configuration section
- **Duplicate identifiers:** logged and skipped (first loaded wins)
- **Manager:** `LocalizableDataAssetManager<TranslatableItem>` (obtained via `BlobLib.getInstance().getTranslatableItemManager()`)

---

## YAML Schema

TranslatableItems can be defined at the file root (the whole file is one asset, identifier = filename without `.yml`) or as named subsections within a file.

```yaml
# File root → identifier = filename (without .yml)
Locale: en_us
ItemStack:
  Material: DIAMOND
  DisplayName: '&b&lMagic Diamond'
  Lore:
    - '&7A mysterious diamond.'
    - '&7Radiates with power.'
Rarity: epic

# OR organized as sections
magic_sword:
  Locale: en_us
  ItemStack:
    Material: DIAMOND_SWORD
    DisplayName: '&6&lSword of Legends'
    Lore:
      - '&7An ancient sword.'
    CustomModelData: 1001
  Rarity: legendary
```

### Fields

| Field       | Type           | Required | Description                                                                                 |
|-------------|----------------|----------|---------------------------------------------------------------------------------------------|
| `Locale`    | string         | no       | Locale tag for this entry (default: `en_us`).                                               |
| `ItemStack` | section        | **yes**  | The item definition. See [ItemStackReader / OmniStack](#itemstackreader--omnistack) below.  |
| `Rarity`    | string         | no       | The rarity identifier (default: `"common"`). Must match a key in `plugins/BlobLib/rarity.yml`. |
| `Is-Soul`   | boolean        | no       | If true, marks the item as a soulbound item (via `SoulAPI`).                                |
| `Is-Unique` | boolean        | no       | If true, marks the item as unique (via `UniqueAPI`).                                       |
| `Fluid-Pressure` | double   | no       | Sets fluid pressure via `FluidPressureAPI`.                                                 |
| `Projectile-Damage` | double | no      | Sets projectile damage via `ProjectileDamageAPI`.                                           |

### ItemStackReader / OmniStack

The `ItemStack` section supports a rich set of fields inherited from `ItemStackReader.OMNI_STACK`. These cover most Paper API item data components.

| Field | Type | Description |
|-------|------|-------------|
| `Material` | string | **Required.** Material name (e.g. `DIAMOND`, `STONE`). Also supports: `HEAD-<url>` for player heads, `WM-<weaponTitle>` for WeaponMechanics weapons, `IA-<namespacedId>` for ItemsAdder custom items. |
| `Amount` | int | Stack size (1–127). |
| `Damage` | int | Damage value (durability). |
| `RepairCost` | int | Anvil repair cost. |
| `DisplayName` | string | Display name (supports `&`-color codes by default, or MiniMessage if `minimessage: true`). |
| `ItemName` | string | Item name (the text shown when hovering, supports color codes / MiniMessage). |
| `Lore` | list of string | Lore lines (supports color codes / MiniMessage). |
| `minimessage` | boolean | If `true`, `DisplayName`, `ItemName`, and `Lore` are parsed as MiniMessage format instead of `&`-color codes. |
| `CustomModelData` | int | Custom model data integer. |
| `ItemModel` | string | Item model as `namespace:path` (e.g. `minecraft:stick`). |
| `Enchantments` | list of string | Enchantments in `enchantment:level` format (e.g. `sharpness:5`). |
| `Unbreakable` | boolean | If true, item is unbreakable. |
| `HideToolTip` | boolean | If true, tooltip is hidden. |
| `EnchantmentGlintOverride` | boolean | Override enchantment glint. |
| `FireResistant` | boolean | If true, item is fire resistant. |
| `MaxStackSize` | int | Maximum stack size override. |
| `MaxDamage` | int | Maximum damage/durability. |
| `Rarity` | string | Minecraft item rarity (`COMMON`, `UNCOMMON`, `RARE`, `EPIC`). |
| `TooltipStyle` | string | Tooltip style as `namespace:path`. |
| `Color` | string | Leather armor color as `R,G,B` (e.g. `255,0,0`). |
| `Glider` | boolean | If true, item acts as an elytra glider. |
| `ShowAllItemFlags` | boolean | If true, all item flags are shown. |
| `ItemFlags` | list of string | Specific item flags to set. |
| `ArmorTrim` | section | Armor trim configuration (see below). |
| `Equippable` | section | Equippable item configuration (see below). |
| `Consumable` | section | Consumable item configuration (see below). |
| `Tool` | section | Tool properties (see below). |
| `Food` | section | Food properties (see below). |
| `Attributes` | section | Attribute modifiers (see below). |

**ArmorTrim section:**

| Field | Type | Description |
|-------|------|-------------|
| `TrimMaterial` | string | Trim material key (e.g. `minecraft:redstone`). |
| `TrimPattern` | string | Trim pattern key (e.g. `minecraft:eye`). |

**Equippable section:**

| Field | Type | Description |
|-------|------|-------------|
| `EquipmentSlot` | string | Slot (e.g. `HEAD`, `CHEST`, `LEGS`, `FEET`, `OFF_HAND`). |
| `CameraOverlay` | string | Camera overlay as `namespace:path`. |
| `EquipSound` | string | Equip sound as `namespace:path`. |
| `AssetId` | string | Asset ID as `namespace:path`. |
| `AllowedEntities` | string | Entity tag key for allowed entities. |
| `DamageOnHurt` | boolean | Whether the item takes damage on hurt. |
| `Dispensable` | boolean | Whether dispensers can equip this item. |
| `Swappable` | boolean | Whether the item can be swapped. |

**Consumable section:**

| Field | Type | Description |
|-------|------|-------------|
| `ConsumeSeconds` | double | Time in seconds to consume. |
| `HasConsumeParticles` | boolean | Whether consume particles are shown. |
| `Animation` | string | Use animation (`NONE`, `EAT`, `DRINK`, `BLOCK`, `BOW`, `SPEAR`, `CROSSBOW`, `SPYGLASS`, `TOOT_HORN`, `BRUSH`). |
| `ConsumeEffects` | section | Map of named consume effects (see below). |

**ConsumeEffects entry:**

| Field | Type | Description |
|-------|------|-------------|
| `Type` | string | Effect type: `APPLY_STATUS_EFFECTS`, `REMOVE_STATUS_EFFECTS`, `CLEAR_ALL_STATUS_EFFECTS`, `PLAY_SOUND`, `TELEPORT_RANDOMLY`. |
| `Probability` | double | Probability (0.0–1.0, for `APPLY_STATUS_EFFECTS`). |
| `Effects` | list | For `APPLY_STATUS_EFFECTS`: list of `type:duration amplifier [ambient] [particles] [icon]`. For `REMOVE_STATUS_EFFECTS`: list of effect namespaced keys. |
| `Sound` | string | For `PLAY_SOUND`: sound as `namespace:path`. |
| `Diameter` | double | For `TELEPORT_RANDOMLY`: teleport diameter. |

**Tool section:**

| Field | Type | Description |
|-------|------|-------------|
| `DamagePerBlock` | int | Damage taken per block broken. |
| `DefaultMiningSpeed` | double | Default mining speed. |
| `Rules` | section | Map of named mining rule sections (each with `Blocks` tag key, optional `Speed`, and `CorrectForDrops` boolean). |

**Food section:**

| Field | Type | Description |
|-------|------|-------------|
| `Nutrition` | int | Food points restored. |
| `Saturation` | double | Saturation restored. |
| `CanAlwaysEat` | boolean | Can be eaten even when not hungry. |

**Attributes section:**

Each key is a namespaced attribute name (e.g. `minecraft:attack_damage`). Each entry has:

| Field | Type | Description |
|-------|------|-------------|
| `Amount` | double | Attribute amount. |
| `Operation` | string | Operation: `ADD_NUMBER`, `ADD_SCALAR`, `MULTIPLY_SCALAR_1`. |
| `EquipmentSlotGroup` | string | Equipment slot group (e.g. `MAIN_HAND`, `OFF_HAND`, `HAND`, `FEET`, `LEGS`, `CHEST`, `HEAD`, `ARMOR`, `BODY`, `ANY`). |

### Rarity system (`rarity.yml`)

Rarities are defined in `plugins/BlobLib/rarity.yml` and are read by `BlobLibConfigManager` at startup. The file has two top-level sections:

```yaml
minecraft:
  common:
    color: "FFFFFF"
    descriptions:
      en_us: "<white><b>COMMON</b></white>"
      es_es: "<white><b>COMÚN</b></white>"
      # ...
  uncommon:
    color: "FFFF55"
    descriptions:
      en_us: "<yellow><b>UNCOMMON</b></yellow>"
      # ...
  rare:
    color: "55FFFF"
    descriptions:
      en_us: "<blue><b>RARE</b></blue>"
      # ...
  epic:
    color: "FF55FF"
    descriptions:
      en_us: "<light_purple><b>EPIC</b></light_purple>"
      # ...
custom: {}
```

| Field | Type | Description |
|-------|------|-------------|
| `color` | string | **Required.** Hex RGB color (e.g. `"FF5555"`). |
| `descriptions` | section | Map of locale → MiniMessage description string for this rarity. |

The `Rarity` field in the TranslatableItem config references these by identifier (case-insensitive for the four minecraft rarities; exact match for custom rarities).

---

### Locale resolution

When retrieving a TranslatableItem, the `LocalizableDataAssetManager` resolves the locale as follows:

1. The requested locale is normalised via `BlobLibTranslatableAPI.getRealLocale(locale)`, which checks locale-redirect rules from `config.yml` (`Locale.Default-To` section).
2. If an item exists for the resulting locale, it is returned.
3. Otherwise, the `en_us` item is returned as a fallback.
4. If neither exists, `null` is returned.

---

## Example TranslatableItem Files

### Single file-root item (`healing_potion.yml`)

```yaml
# plugins/BlobLib/TranslatableItem/healing_potion.yml
Locale: en_us
ItemStack:
  Material: POTION
  Color: 255,85,85
  DisplayName: '&c&lHealing Potion'
  Lore:
    - '&7Restores &c20❤ &7health.'
  CustomModelData: 5001
Rarity: common
Is-Soul: true
```

### Multi-section item file (`gui_items.yml`)

```yaml
# plugins/BlobLib/TranslatableItem/gui_items.yml
# Each section becomes its own TranslatableItem, keyed by the section name.

background:
  Locale: en_us
  ItemStack:
    Material: BLACK_STAINED_GLASS_PANE
    CustomModelData: 1
    DisplayName: ' '
    HideToolTip: true
    Lore: []

previous_page:
  Locale: en_us
  ItemStack:
    Material: ARROW
    CustomModelData: 1
    DisplayName: '&6Previous page'
    Lore:
      - '&7Click to go to the previous page'
  Rarity: common

next_page:
  Locale: en_us
  ItemStack:
    Material: ARROW
    CustomModelData: 2
    DisplayName: '&6Next page'
    Lore:
      - '&7Click to go to the next page'
  Rarity: common
```

### Item with WeaponMechanics integration

```yaml
# plugins/MyPlugin/TranslatableItem/legendary_sword.yml
Locale: en_us
ItemStack:
  Material: WM-LegendarySword
  DisplayName: '&6&lLegendary Sword'
  Lore:
    - '&7A blade forged in ancient fires.'
Rarity: legendary
```

### Locale overrides (`gui_items_es_es.yml`)

```yaml
# plugins/BlobLib/TranslatableItem/gui_items_es_es.yml
Locale: es_es

previous_page:
  ItemStack:
    Material: ARROW
    CustomModelData: 1
    DisplayName: '&6Página anterior'
    Lore:
      - '&7Haz clic para ir a la página anterior'
  Rarity: common

next_page:
  ItemStack:
    Material: ARROW
    CustomModelData: 2
    DisplayName: '&6Página siguiente'
    Lore:
      - '&7Haz clic para ir a la página siguiente'
  Rarity: common
```

---

## BlobLibTranslatableAPI

The primary entry point for retrieving loaded TranslatableItems.

```java
BlobLibTranslatableAPI api = BlobLibTranslatableAPI.getInstance();
```

---

### `getTranslatableItem(String identifier)`

Looks up a TranslatableItem by its identifier in the default (`en_us`) locale.

| Parameter    | Type   | Description                      |
|--------------|--------|----------------------------------|
| `identifier` | String | The TranslatableItem key.        |

**Returns:** `TranslatableItem` or `null`.

```java
TranslatableItem item = api.getTranslatableItem("magic_sword");
ItemStack stack = item.get();
```

---

### `getTranslatableItem(String identifier, String locale)`

Looks up a TranslatableItem by identifier for a specific locale, falling back to `en_us` if the locale is not available.

| Parameter    | Type   | Description                                |
|--------------|--------|--------------------------------------------|
| `identifier` | String | The TranslatableItem key.                  |
| `locale`     | String | The locale tag (e.g. `"es_es"`, `"fr_fr"`).|

**Returns:** `TranslatableItem` or `null`.

```java
TranslatableItem item = api.getTranslatableItem("magic_sword", "es_es");
ItemStack stack = item.get();
```

---

### `getTranslatableItem(String identifier, Player player)`

Looks up a TranslatableItem by identifier, localized to the player's locale.

| Parameter    | Type   | Description                       |
|--------------|--------|-----------------------------------|
| `identifier` | String | The TranslatableItem key.         |
| `player`     | Player | The player whose locale to use.   |

**Returns:** `TranslatableItem` or `null`.

```java
TranslatableItem item = api.getTranslatableItem("magic_sword", player);
```

---

### `getTranslatableItems(String locale)`

Returns a list of all TranslatableItems available for the given locale, merged over the `en_us` defaults.

| Parameter | Type   | Description                             |
|-----------|--------|-----------------------------------------|
| `locale`  | String | The locale to retrieve items for.       |

**Returns:** `List<TranslatableItem>`.

```java
List<TranslatableItem> items = api.getTranslatableItems("fr_fr");
```

---

## TranslatableItem Interface

```java
public interface TranslatableItem extends Translatable<ItemStack>
```

`Translatable<T>` extends `DataAsset` and `Localizable`.

### Constants

| Constant       | Value                            |
|----------------|----------------------------------|
| `KEY_PREFIX`   | `"translatableitem_key:"`        |
| `LOCALE_PREFIX` | `"translatableitem_locale:"`    |

These prefixes are embedded into the `CustomModelDataComponent` strings of every generated `ItemStack` to enable reverse lookup.

---

### Static methods

#### `TranslatableItem.by(String identifier)`

Shorthand lookup in the default locale. Delegates to `BlobLibTranslatableAPI.getInstance().getTranslatableItem(identifier)`.

| Parameter    | Type   | Description                      |
|--------------|--------|----------------------------------|
| `identifier` | String | The TranslatableItem key.        |

**Returns:** `TranslatableItem` or `null`.

```java
TranslatableItem item = TranslatableItem.by("magic_sword");
```

---

#### `TranslatableItem.byItemStack(ItemStack itemStack)`

Reverse-lookup: given an `ItemStack`, extracts the embedded key and locale from `CustomModelDataComponent` strings, then looks up the TranslatableItem and localizes it. Returns `null` if the `ItemStack` is not a TranslatableItem instance.

| Parameter   | Type       | Description                  |
|-------------|------------|------------------------------|
| `itemStack` | ItemStack  | The item to reverse-lookup.  |

**Returns:** `TranslatableItem` or `null`.

```java
TranslatableItem item = TranslatableItem.byItemStack(player.getInventory().getItemInMainHand());
if (item != null) {
    // This ItemStack originated from a TranslatableItem
}
```

---

#### `TranslatableItem.localize(ItemStack itemStack, String locale)`

In-place localization of an existing `ItemStack`. Extracts the TranslatableItem from the `ItemStack` via `byItemStack`, finds the localized version, and applies its display name and lore to the original `ItemStack`. The locale NBT tag is updated in place.

| Parameter   | Type       | Description                           |
|-------------|------------|---------------------------------------|
| `itemStack` | ItemStack  | The item to localize in place.        |
| `locale`    | String     | The target locale tag.                |

```java
TranslatableItem.localize(player.getItemInHand(), "es_es");
```

---

### Instance methods

#### `identifier()`

Returns the unique key of this TranslatableItem (inherited from `DataAsset`).

**Returns:** `String`.

```java
String key = item.identifier(); // "magic_sword"
```

---

#### `locale()`

Returns the locale tag of this TranslatableItem (inherited from `Localizable`).

**Returns:** `String`.

```java
String locale = item.locale(); // "en_us"
```

---

#### `get()`

Returns an `ItemStack` generated from the configured supplier. Each call produces a **new copy** of the item.

**Returns:** `ItemStack`.

```java
ItemStack stack = item.get();
player.getInventory().addItem(stack);
```

---

#### `getClone()`

Returns a clone of the item (calls `get()` to produce a new `ItemStack`, then fires a `TranslatableItemCloneEvent` to allow other plugins to modify it).

**Returns:** `ItemStack`.

```java
ItemStack stack = item.getClone();
// Other plugins can listen to TranslatableItemCloneEvent to modify it
```

---

#### `getClone(boolean callEvent)`

Same as `getClone()` but allows suppressing the event.

| Parameter  | Type    | Description                                   |
|------------|---------|-----------------------------------------------|
| `callEvent`| boolean | If `false`, the `TranslatableItemCloneEvent` is not fired. |

**Returns:** `ItemStack`.

```java
ItemStack stack = item.getClone(false); // no event fired
```

---

#### `getRarity()`

Returns the `TranslatableRarity` associated with this item, resolved from the rarity identifier configured in the YAML.

**Returns:** `TranslatableRarity`.

```java
TranslatableRarity rarity = item.getRarity();
TextColor color = rarity.getTextColor();
String desc = rarity.descriptionFor("en_us");
```

---

#### `localize(String locale)`

Returns a new `TranslatableItem` localized to the given locale, or `this` if already in that locale.

| Parameter | Type   | Description                             |
|-----------|--------|-----------------------------------------|
| `locale`  | String | The target locale.                      |

**Returns:** `TranslatableItem` or `null`.

```java
TranslatableItem localized = item.localize("es_es");
```

---

#### `localize(Player player)`

Same as `localize(String)` but uses the player's locale.

| Parameter | Type   | Description                    |
|-----------|--------|--------------------------------|
| `player`  | Player | The player whose locale to use.|

**Returns:** `TranslatableItem` or `null`.

```java
TranslatableItem localized = item.localize(player);
```

---

#### `apply(ItemStack itemStack, String locale)`

Applies this TranslatableItem's display name and lore to an existing `ItemStack` **without** overwriting other data (enchantments, etc.). Also writes the key and locale prefixes into the item's NBT, making it a valid TranslatableItem instance. Useful when another plugin manages the base `ItemStack`.

| Parameter   | Type       | Description                           |
|-------------|------------|---------------------------------------|
| `itemStack` | ItemStack  | The item to apply data onto.          |
| `locale`    | String     | The locale for display name and lore. |

```java
item.apply(somePluginItem, player.getLocale());
```

---

#### `apply(ItemStack itemStack, Player player)`

Same as `apply(ItemStack, String)` but uses the player's locale.

---

#### `modify(Function<String, String> function)`

Returns a new `TranslatableItem` with the display name, item name, and each lore line transformed by the given function. The original item is unchanged — this creates a copy.

| Parameter  | Type                       | Description                        |
|------------|----------------------------|------------------------------------|
| `function` | `Function<String, String>` | Transformation applied to name/lore strings. |

**Returns:** a new `TranslatableItem`.

```java
TranslatableItem modified = item.modify(s -> s.replace("%player%", player.getName()));
```

---

#### `modder()`

Returns a `TranslatableItemModder` for chainable string modifications.

**Returns:** `TranslatableItemModder`.

```java
TranslatableItem result = item.modder()
    .replace("%player%", player.getName())
    .color(ChatColor.GOLD)
    .get();
```

---

## TranslatableItemModder

`TranslatableItemModder` extends `BlobTranslatableModder<TranslatableItem, ItemStack>`, providing a fluent API for modifying the item's display name and lore strings.

### Creation

```java
TranslatableItemModder modder = TranslatableItemModder.mod(item);
// or via the interface default:
TranslatableItemModder modder = item.modder();
```

### Modifier methods

All methods from `BlobTranslatableModder` are available:

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
| `translateRGBAndChatColors(char altColorChar)`             | Translate alternate color codes to Minecraft colors.|
| `color(ChatColor color)`                                   | Prepend a color code to name and lore.              |
| `trim()`                                                   | Trim leading/trailing whitespace.                   |
| `concat(String s)` / `append(String s)`                    | Append text to name and each lore line.             |
| `prepend(String s)`                                        | Prepend text to name and each lore line.            |
| `remove(String s)`                                         | Remove all occurrences (case-sensitive).            |
| `removeIgnoreCase(String s)`                               | Remove all occurrences (case-insensitive).          |
| `removeFirst(String s)`                                    | Remove first occurrence.                            |
| `removeLast(String s)`                                     | Remove last occurrence.                             |
| `removeFirstIgnoreCase(String s)`                          | Remove first occurrence (case-insensitive).         |
| `removeLastIgnoreCase(String s)`                           | Remove last occurrence (case-insensitive).          |
| `removeVowels()`                                           | Remove all vowels (a/e/i/o/u).                     |

### Terminal method

```java
TranslatableItem result = modder.get();
```

Returns the modified `TranslatableItem` with all transformations applied.

---

### Usage examples

```java
// Replace placeholders in display name and lore
TranslatableItem personalized = item.modder()
    .replace("%player%", player.getName())
    .replace("%guild%", guild.getName())
    .get();

// Apply a color to the display name
TranslatableItem colored = item.modder()
    .color(ChatColor.AQUA)
    .get();

// Append text to lore
TranslatableItem withHint = item.modder()
    .append(" &7(right click to use)")
    .get();
```

---

## TranslatableItemCloneEvent

Fired when `TranslatableItem.getClone()` is called (if `callEvent` is `true`). Allows other plugins to modify the cloned `ItemStack` before it is returned.

```java
@Deprecated
public class TranslatableItemCloneEvent extends TranslatableItemEvent {
    public ItemStack getClone();
    public void setClone(ItemStack clone);
}
```

Registered handlers can mutate the clone:

```java
@EventHandler
public void onItemClone(TranslatableItemCloneEvent event) {
    ItemStack clone = event.getClone();
    clone.addUnsafeEnchantment(Enchantment.VANISHING_CURSE, 1);
    event.setClone(clone);
}
```

---

## TranslatableRarity

```java
public interface TranslatableRarity {
    @NotNull String getIdentifier();
    @NotNull TextColor getTextColor();
    @NotNull Map<String, String> descriptions();

    default Color toColor(int alpha);
    @Nullable default Component descriptionFor(String locale);
}
```

The `TranslatableRarity` provides:

| Method             | Returns         | Description                                       |
|--------------------|-----------------|---------------------------------------------------|
| `getIdentifier()`  | `String`        | The rarity identifier (e.g. `"epic"`).            |
| `getTextColor()`   | `TextColor`     | The Adventure `TextColor` parsed from the hex.    |
| `descriptions()`   | `Map<String, String>` | Locale → MiniMessage description map.      |
| `descriptionFor(String locale)` | `Component` or `null` | Gets the description for a locale, falling back through redirects. |
| `toColor(int alpha)` | `Color`        | Converts to a Bukkit `Color` with given alpha.    |

Static utility:

```java
TranslatableRarity rarity = TranslatableRarity.of(itemStack);
```

---

## LocalizableDataAssetManager (for TranslatableItem)

The `TranslatableItem` manager is a `LocalizableDataAssetManager<TranslatableItem>`, obtained from `BlobLib.getInstance().getTranslatableItemManager()`.

### Public Methods

| Method                                                    | Returns                              | Description                                                                   |
|-----------------------------------------------------------|--------------------------------------|-------------------------------------------------------------------------------|
| `getAsset(String identifier)`                             | `TranslatableItem` or `null`         | Returns the default (`en_us`) item.                                           |
| `getAsset(String identifier, String locale)`              | `TranslatableItem` or `null`         | Returns the item for the given locale, falling back to `en_us`.               |
| `getAssets()`                                             | `List<TranslatableItem>`             | Returns all items in the default locale.                                      |
| `getAssets(String locale)`                                | `List<TranslatableItem>`             | Returns all items for a locale, merged with `en_us` defaults.                 |
| `getDefault()`                                            | `Map<String, TranslatableItem>`      | Returns all `en_us` items as a map (identifier → item).                       |
| `saveAsset(File file, TranslatableItem asset)`            | `void`                               | Serialises a TranslatableItem to a `.yml` file (note: saveConsumer is `null`, so this is a no-op for TranslatableItem). |
| `continueLoadingAssets(BlobPlugin plugin, boolean warnDuplicates, File... files)` | `void` | Loads TranslatableItems from specific files.                        |
| `reload()`                                                | `void`                               | Clears all registries and re-scans the core directory.                        |
| `reload(BlobPlugin plugin, IManagerDirector director)`    | `void`                               | Loads TranslatableItems from a plugin's asset directory.                      |
| `unload(BlobPlugin plugin)`                               | `void`                               | Removes all assets that were registered by the given plugin.                  |
| `getAssetDirectory()`                                     | `File`                               | The configured asset directory.                                               |

### Access from BlobLib

```java
LocalizableDataAssetManager<TranslatableItem> manager = BlobLib.getInstance().getTranslatableItemManager();
TranslatableItem item = manager.getAsset("magic_sword", "es_es");
```

---

## DataAssetType

TranslatableItem is registered as `DataAssetType.TRANSLATABLE_ITEM`:

| Property          | Value                                |
|-------------------|--------------------------------------|
| Enum constant     | `TRANSLATABLE_ITEM`                  |
| Key               | `"translatableItems"`                |
| Directory path    | `"/TranslatableItem"`                |
| Default file path | `"_translatable_items.yml"`          |
| Object name       | `"TranslatableItem"`                 |

---

## Loading & Lifecycle

1. **BlobLib startup** — `BlobLib.onEnable()` creates the `LocalizableDataAssetManager<TranslatableItem>` which scans `plugins/BlobLib/TranslatableItem/` for all `.yml` files and registers them.

2. **Plugin registration** — When a `BlobPlugin` calls `registerToBlobLib()`, the `PluginManager.loadAssets()` method calls `getTranslatableItemManager().reload(plugin, director)`. This loads TranslatableItems from the plugin's own `TranslatableItem/` directory.

3. **Manual loading** — Plugins can call `manager.continueLoadingAssets(plugin, warnDuplicates, files...)` to load additional TranslatableItem files at any point.

4. **Duplicate detection** — Duplicate identifiers are collected, logged, and skipped (first loaded wins).

5. **BlobLib reload** — `BlobLib.reload()` calls `translatableItemManager.reload()`, which clears all registries and re-scans the core directory.

6. **Plugin unload** — When a `BlobPlugin` is disabled, `manager.unload(plugin)` removes all TranslatableItems registered by that plugin.

---

## Creating Items Programmatically

```java
LocalizableDataAssetManager<TranslatableItem> manager = BlobLib.getInstance().getTranslatableItemManager();

// Create an inline item with a Supplier
TranslatableItem customItem = BlobTranslatableItem.of(
    "custom_sword",
    "en_us",
    () -> {
        ItemStack stack = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName("§6§lCustom Sword");
        meta.setLore(List.of("§7A custom sword."));
        stack.setItemMeta(meta);
        return stack;
    },
    "epic"
);
```

Note: `saveAsset` has a `null` `saveConsumer` for TranslatableItem, so there is currently no built-in serialisation back to YAML for programmatically created items.

---

## Examples

See the [examples](examples/) directory for ready-to-use YAML files:

- [`translatableitem_single.yml`](examples/translatableitem_single.yml) — a file-root item definition
- [`translatableitem_multi.yml`](examples/translatableitem_multi.yml) — multiple items in one file
- [`translatableitem_locale_overrides.yml`](examples/translatableitem_locale_overrides.yml) — locale-specific item overrides for existing identifiers
- [`translatableitem_advanced.yml`](examples/translatableitem_advanced.yml) — advanced item with full component configuration
