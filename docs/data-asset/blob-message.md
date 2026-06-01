# BlobMessage

BlobMessage is a unified message abstraction layer for Minecraft (Spigot/Paper). It wraps chat messages, actionbar messages, title/subtitle combinations, and sounds into a single configurable data asset. Messages are defined in YAML files and resolved at runtime through a locale-aware manager, enabling per-player language support. The system supports in-memory modification via `BlobMessageModder`, making it straightforward to inject placeholders, transform text, or re-color messages before sending.

---

## Directory & Discovery

- **Runtime directory:** `plugins/BlobLib/BlobMessage/`
- **File format:** `.yml`
- **File suffix:** `_lang.yml` (e.g. `myplugin_lang.yml`)
- **Default files:** `BlobMessage/bloblib_lang.yml` (shipped with BlobLib)
- **Identifier:** the YAML path joined by dots (e.g. `System.No-Permission`)
- **Subdirectories:** supported — all `.yml` files in subfolders are loaded recursively
- **Load filter:** a section is a message entry if it contains a `Type` key with a valid string value
- **Duplicate identifiers:** logged and skipped (first loaded wins)
- **Registration by plugins:** via `ManagerDirector#registerBlobMessage(String... fileNames)`

---

## YAML Configuration

### File-Level Locale

Each YAML file may declare a `Locale` key at the root. If absent, the locale defaults to `en_us`.

```yaml
Locale: "en_us"
```

All entries in that file inherit this locale. To support multiple languages, create separate `_lang.yml` files (e.g. `myplugin_lang.yml` for en_us, `myplugin_es_es_lang.yml` for Spanish).

### Message Types

There are **7 message types**, each represented by a concrete Java class. The `Type` field is **required** and must be one of:

| Type | Class | Outputs |
|---|---|---|
| `CHAT` | `BlobChatMessage` | Chat message (optionally with hover & click event) |
| `ACTIONBAR` | `BlobActionbarMessage` | Actionbar message |
| `TITLE` | `BlobTitleMessage` | Title + subtitle |
| `ACTIONBAR_TITLE` | `BlobActionbarTitleMessage` | Actionbar + title + subtitle |
| `CHAT_ACTIONBAR` | `BlobChatActionbarMessage` | Chat + actionbar |
| `CHAT_TITLE` | `BlobChatTitleMessage` | Chat + title + subtitle |
| `CHAT_ACTIONBAR_TITLE` | `BlobChatActionbarTitleMessage` | Chat + actionbar + title + subtitle |

### Required & Optional Fields

| Field | Applies To | Type | Required | Default | Description |
|---|---|---|---|---|---|
| `Type` | All | String | **Yes** | — | One of the 7 types above |
| `Message` | `CHAT`, `ACTIONBAR` | String | **Yes** | — | The message text |
| `Title` | `TITLE`, `ACTIONBAR_TITLE`, `CHAT_TITLE`, `CHAT_ACTIONBAR_TITLE` | String | **Yes** | — | Title text |
| `Subtitle` | `TITLE`, `ACTIONBAR_TITLE`, `CHAT_TITLE`, `CHAT_ACTIONBAR_TITLE` | String | **Yes** | — | Subtitle text |
| `Actionbar` | `ACTIONBAR_TITLE`, `CHAT_ACTIONBAR`, `CHAT_ACTIONBAR_TITLE` | String | **Yes** | — | Actionbar text |
| `Chat` | `CHAT_ACTIONBAR`, `CHAT_TITLE`, `CHAT_ACTIONBAR_TITLE` | String | **Yes** | — | Chat text |
| `FadeIn` | Types with title | Integer | No | `10` | Title fade-in time (ticks) |
| `Stay` | Types with title | Integer | No | `40` | Title stay time (ticks) |
| `FadeOut` | Types with title | Integer | No | `10` | Title fade-out time (ticks) |
| `Hover` | Types with chat (`CHAT`, `CHAT_ACTIONBAR`, `CHAT_TITLE`, `CHAT_ACTIONBAR_TITLE`) | String | No | — | Hover text shown on mouse over |
| `BlobSound` | All | String or Section | No | — | Reference or inline sound definition |
| `ClickEvent.Action` | Types with chat | String | No | — | Click action type (e.g. `OPEN_URL`, `RUN_COMMAND`, `SUGGEST_COMMAND`) |
| `ClickEvent.Value` | Types with chat | String | No | — | Click action value |

### `BlobSound` Sub-Section

The `BlobSound` field can be either:

1. **A string reference** to a sound registered in the BlobSound system (e.g. `System.Alert`). Optionally append `:WORLD` to override the audience to world-level playback.
2. **A ConfigurationSection** with inline sound definition. See [BlobSound documentation](blob-sound.md) for the full schema.

### YAML Examples

**Example 1 — CHAT with sound reference:**
```yaml
Player:
  Not-Found:
    Type: CHAT
    Message: '&cPlayer not found'
    BlobSound: System.Alert
```

**Example 2 — TITLE with inline sound:**
```yaml
MyPlugin:
  Welcome:
    Type: TITLE
    Title: '&a&lWELCOME'
    Subtitle: '&7Welcome, &f%player%'
    FadeIn: 5
    Stay: 60
    FadeOut: 10
    BlobSound:
      Sound: entity.player.levelup
      Volume: 1.0
      Pitch: 1.2
      Category: PLAYERS
```

**Example 3 — CHAT_ACTIONBAR with hover:**
```yaml
MyPlugin:
  Level-Up:
    Type: CHAT_ACTIONBAR
    Chat: '&aYou reached level &f%level%'
    Actionbar: '&aLEVEL &f%level%'
    Hover: '&7Click to view stats'
    BlobSound: MySounds.LevelUp
```

**Example 4 — CHAT with ClickEvent:**
```yaml
MyPlugin:
  Click-Here:
    Type: CHAT
    Message: '&bClick here to visit our website'
    ClickEvent:
      Action: OPEN_URL
      Value: 'https://example.com'
```

**Example 5 — ACTIONBAR_TITLE (simple, no sound):**
```yaml
MyPlugin:
  Cannot-Build:
    Type: ACTIONBAR_TITLE
    Title: '&c&lACCESS DENIED'
    Subtitle: '&7You do not have permission'
    Actionbar: '&cYou cannot build here'
```

### Reference vs. Inline Usage

When a `BlobMessage` appears as a field inside another configuration section (e.g. inside an Action or an Inventory), it may be written as:

- **String reference** — a dot-separated key referencing a message registered elsewhere:
  ```yaml
  SomeAction:
    BlobMessage: MyPlugin.Welcome
  ```
- **Inline section** — an anonymous ConfigurationSection with the full `Type`, `Message`, etc.:
  ```yaml
  SomeAction:
    BlobMessage:
      Type: CHAT
      Message: '&aHello!'
  ```

The `BlobMessageIO.parse()` method handles both forms automatically.

---

## Class Hierarchy

```
BlobMessage (interface, extends Localizable, DataAsset)
│
└── AbstractMessage (abstract class)
    │
    ├── BlobChatMessage
    │   ├── BlobChatActionbarMessage
    │   ├── BlobChatTitleMessage
    │   └── BlobChatActionbarTitleMessage
    │
    ├── BlobActionbarMessage
    │
    ├── BlobTitleMessage
    │   └── BlobActionbarTitleMessage
    │
    └── (No other direct subtypes)
```

| Class | Description |
|---|---|
| `BlobMessage` | Interface: the core abstraction for any message type |
| `AbstractMessage` | Holds common fields: `reference`, `sound`, `locale`, `clickEvent` |
| `BlobChatMessage` | Chat-only message; supports `hover` text and `ClickEvent` |
| `BlobActionbarMessage` | Actionbar-only message |
| `BlobTitleMessage` | Title + subtitle with fade-in/stay/fade-out timing |
| `BlobActionbarTitleMessage` | Extends `BlobTitleMessage`; adds actionbar text |
| `BlobChatActionbarMessage` | Extends `BlobChatMessage`; adds actionbar text |
| `BlobChatTitleMessage` | Extends `BlobChatMessage`; adds title + subtitle |
| `BlobChatActionbarTitleMessage` | Extends `BlobChatMessage`; adds actionbar + title + subtitle |

---

## BlobMessage Interface API

Located in `io.github.anjoismysign.bloblib.entities.message.BlobMessage`.

### Static Methods

```java
@Nullable
static BlobMessage by(@NotNull String key)
```

Performs a locale-agnostic lookup in `en_us`. Equivalent to `BlobLibMessageAPI.getInstance().getMessage(key, "en_us")`.

- **Parameters:**
  - `key` — the dot-separated identifier (e.g. `"MyPlugin.Welcome"`).
- **Returns:** the `BlobMessage`, or `null` if not found.

### Instance Methods

```java
@Nullable
default BlobMessage localize(@NotNull String locale)
```

Resolves a locale-specific variant of this message. If the current message already matches the requested locale, it returns `this`. Otherwise, it delegates to `BlobLibMessageAPI.getInstance().getMessage(identifier(), locale)`.

- **Parameters:**
  - `locale` — the target locale string (e.g. `"es_es"`).
- **Returns:** the localized `BlobMessage`, or `null` if not found.

---

```java
@Nullable
ClickEvent getClickEvent();
```

Returns the `ClickEvent` associated with chat-based message types, or `null` if none is set.

- **Returns:** a BungeeCord `ClickEvent`, or `null`.

---

```java
@NotNull
BlobMessage onClick(ClickEvent clickEvent);
```

Returns a **new** `BlobMessage` with the given `ClickEvent` applied. Implementations create a copy; the original is not mutated.

- **Parameters:**
  - `clickEvent` — the `ClickEvent` to attach.
- **Returns:** a new `BlobMessage` instance with the click event set.

---

```java
@Deprecated
void send(Player player);
```

Sends the message content to the player without playing any associated sound. Deprecated in favor of `handle(Player)`.

---

```java
@Deprecated
default void sendAndPlay(Player player, Location location)
```

Sends the message and, if a sound is present, plays it at the given location with **`MessageAudience.PLAYER`** semantics (player hears it individually). Deprecated in favor of `handle(Player, Location)`.

---

```java
@Deprecated
default void sendAndPlay(Player player)
```

Same as above, but uses `player.getLocation()` as the playback location.

---

```java
@Deprecated
default void sendAndPlayInWorld(Player player, Location location)
```

Sends the message and, if a sound is present, plays it at the given location with **`MessageAudience.WORLD`** semantics (all nearby players hear it). Deprecated in favor of `handle(Player, Location)`.

---

```java
@Deprecated
default void sendAndPlayInWorld(Player player)
```

Same as above, but uses `player.getLocation()`.

---

```java
default void handle(Player player, Location location)
```

The **preferred** dispatch method. Decides how to send the message based on the sound's audience:

1. If `getSound()` is `null` → calls `send(player)`.
2. If `getSound().audience() == PLAYER` → calls `sendAndPlay(player, location)`.
3. Otherwise → calls `sendAndPlayInWorld(player, location)`.

- **Parameters:**
  - `player` — the target player.
  - `location` — the location for sound playback.

---

```java
default void handle(Player player)
```

Same as above, using `player.getLocation()` as the location.

---

```java
@Deprecated
default void broadcast()
```

Calls `handle(player)` for every online player. Deprecated; use `BlobLibMessageAPI#broadcast(String)` instead.

---

```java
void toCommandSender(CommandSender commandSender);
```

If the sender is a `Player`, delegates to `handle(player)`. Otherwise, sends a plain text representation (stripped of interactive features like hover/click) to the console.

- **Parameters:**
  - `commandSender` — the recipient (player or console).

---

```java
@Nullable
BlobSound getSound();
```

Returns the `BlobSound` associated with this message, or `null`.

---

```java
@NotNull
BlobMessage modify(Function<String, String> function);
```

Returns a **new** `BlobMessage` with every text field transformed by the given function. For composite messages (e.g. `BlobChatTitleMessage`), all applicable fields (chat, actionbar, title, subtitle, hover) are transformed.

- **Parameters:**
  - `function` — a mapping function applied to each text field.
- **Returns:** a new `BlobMessage` (original is unchanged).

---

```java
@NotNull
default BlobMessageModder<BlobMessage> modder()
```

Returns a new `BlobMessageModder` wrapping this message, enabling fluent chained modifications.

```java
message.modder()
    .replace("%player%", player.getName())
    .color(ChatColor.GREEN)
    .get();
```

- **Returns:** `BlobMessageModder<BlobMessage>` bound to a copy of this message.

---

## BlobLibMessageAPI (Singleton)

Located in `io.github.anjoismysign.bloblib.api.BlobLibMessageAPI`.

### Instance Access

```java
public static BlobLibMessageAPI getInstance(BlobLib plugin)
public static BlobLibMessageAPI getInstance()
```

Singleton accessor. The first call must supply a non-null `BlobLib` instance; subsequent calls may use the no-arg variant.

- **Returns:** the singleton `BlobLibMessageAPI` instance.

### Methods

```java
public LocalizableDataAssetManager<BlobMessage> getMessageManager()
```

Returns the underlying `LocalizableDataAssetManager<BlobMessage>` that handles storage, caching, and locale resolution.

---

```java
public void broadcast(String key)
public void broadcast(String key, Consumer<BlobMessageModder<BlobMessage>> consumer)
```

Broadcasts a message (identified by `key`) to all online players.

- **Overload with consumer:** before sending to each player, the modder consumer is invoked, allowing per-player placeholder replacement or transformation.
- **Parameters:**
  - `key` — the message identifier.
  - `consumer` — (optional) a consumer that modifies the message per player.

**Example with placeholders:**
```java
BlobLibMessageAPI.getInstance().broadcast("MyPlugin.Welcome", modder -> {
    modder.replace("%player%", player.getName());
});
```

---

```java
@Deprecated
@Nullable
public BlobMessage getMessage(@NotNull String key)
```

Returns the message from the `en_us` locale. Deprecated; use `getMessage(key, locale)` instead.

---

```java
@Nullable
public BlobMessage getMessage(@NotNull String key, @NotNull String locale)
```

Returns the message for the specified locale. Falls back to `en_us` if the locale is not registered.

- **Parameters:**
  - `key` — the dot-separated identifier.
  - `locale` — the target locale (e.g. `"en_us"`, `"es_es"`).
- **Returns:** the message, or `null` if not found in any locale.

---

```java
@Nullable
public BlobMessage getMessage(@NotNull String key, @NotNull Player player)
```

Resolves the player's locale (via `player.getLocale()`) and returns the message for that locale.

---

```java
@Nullable
public BlobMessage getMessage(@NotNull String key, @NotNull CommandSender sender)
```

If the sender is a `Player`, uses their locale. Otherwise, uses the console locale from `BlobLibConfigManager`.

---

```java
@Nullable
public BlobMessage getLocaleMessageOrDefault(String key, String locale)
@Nullable
public BlobMessage getLocaleMessageOrDefault(String key, Player player)
```

Attempts to retrieve the locale-specific message. If not found, falls back to the `en_us` default.

---

```java
@NotNull
public Set<String> getDefaultReferences()
```

Returns the set of all message keys registered in the `en_us` locale.

---

```java
public Map<String, BlobMessage> getDefault()
```

Returns a copy of all `en_us` messages as a `Map<String, BlobMessage>`.

---

## BlobMessageModder (Fluent Builder)

Located in `io.github.anjoismysign.bloblib.entities.BlobMessageModder<T>`.

This is a generic fluent builder that wraps a `BlobMessage` and returns a `BlobMessageModder` for chaining. Call `get()` to retrieve the modified message.

### Static Factory

```java
public static <T extends BlobMessage> BlobMessageModder<T> mod(T blobMessage)
```

### Modification Methods

All methods return `this` for chaining, except `get()`.

| Method | Signature | Description |
|---|---|---|
| `modify` | `modify(Function<String, String>)` | Apply a generic transform to all text fields |
| `replace` | `replace(String old, String replacement)` | `String.replace()` — case-sensitive |
| `replaceAll` | `replaceAll(String regex, String replacement)` | `String.replaceAll()` — regex |
| `replaceFirst` | `replaceFirst(String regex, String replacement)` | `String.replaceFirst()` — regex |
| `replaceLast` | `replaceLast(String regex, String replacement)` | Replace last occurrence of regex |
| `replaceAllIgnoreCase` | `replaceAllIgnoreCase(String regex, String replacement)` | `(?i)` regex replace all |
| `replaceFirstIgnoreCase` | `replaceFirstIgnoreCase(String regex, String replacement)` | `(?i)` regex replace first |
| `replaceLastIgnoreCase` | `replaceLastIgnoreCase(String regex, String replacement)` | `(?i)` regex replace last |
| `lowerCase` | `lowerCase()` / `lowerCase(Locale)` | Convert to lowercase |
| `upperCase` | `upperCase()` / `upperCase(Locale)` | Convert to uppercase |
| `capitalize` | `capitalize()` | Uppercase first character |
| `stripColor` | `stripColor()` | Remove all `ChatColor` codes |
| `translateRGBAndChatColors` | `translateRGBAndChatColors(char altColorChar)` | Parse `&` (or other) color codes including RGB |
| `color` | `color(ChatColor color)` | Prepend a `ChatColor` |
| `trim` | `trim()` | Strip leading/trailing whitespace |
| `concat` | `concat(String s)` | Append string (alias for `append`) |
| `append` | `append(String s)` | Append string at end |
| `prepend` | `prepend(String s)` | Prepend string at beginning |
| `remove` | `remove(String s)` | Remove all occurrences (case-sensitive) |
| `removeIgnoreCase` | `removeIgnoreCase(String s)` | Remove all occurrences (case-insensitive) |
| `removeFirst` | `removeFirst(String s)` | Remove first occurrence |
| `removeLast` | `removeLast(String s)` | Remove last occurrence |
| `removeFirstIgnoreCase` | `removeFirstIgnoreCase(String s)` | Remove first (case-insensitive) |
| `removeLastIgnoreCase` | `removeLastIgnoreCase(String s)` | Remove last (case-insensitive) |
| `removeVowels` | `removeVowels()` | Strip all vowels (`aeiouAEIOU`) |
| `get` | `get()` | Return the modified `BlobMessage` |

**Usage pattern:**
```java
BlobMessage modified = message.modder()
    .replace("%player%", player.getName())
    .replaceAll("&([0-9a-f])", "§$1")
    .color(ChatColor.GRAY)
    .get();
```

---

## BlobMessageIO — YAML Parsing Utilities

Located in `io.github.anjoismysign.bloblib.entities.BlobMessageIO`.

```java
public static BlobMessage read(@NotNull ConfigurationSection section,
                                @NotNull String key)
```

Reads a `BlobMessage` from a `ConfigurationSection` using the default locale `"en_us"`. Delegates to the 3-argument overload.

- **Parameters:**
  - `section` — the section containing `Type`, `Message`, etc.
  - `key` — the identifier to assign to the resulting message.
- **Returns:** a parsed `BlobMessage`.
- **Throws:** `ConfigurationFieldException` if required fields are missing.

---

```java
public static BlobMessage read(@NotNull ConfigurationSection section,
                                @NotNull String locale,
                                @NotNull String key)
```

Same as above, but with an explicit locale.

---

```java
public static Optional<BlobMessage> parse(@NotNull ConfigurationSection parent,
                                           @Nullable String key)
```

Handles **both** reference and inline `BlobMessage` fields:

- If `BlobMessage` is a **string** → looks it up as a reference via `BlobLibMessageAPI.getInstance().getMessage(...)`.
- If `BlobMessage` is a **ConfigurationSection** → reads it inline as a full message definition.
- If `BlobMessage` is absent → returns `Optional.empty()`.

- **Parameters:**
  - `parent` — the configuration section that may contain a `BlobMessage` key.
  - `key` — required when parsing an inline section; may be `null` if only string references are expected.
- **Returns:** `Optional<BlobMessage>`.

---

```java
public static Optional<BlobMessage> readReference(ConfigurationSection section)
```

Reads a string-only `BlobMessage` reference. Throws `IllegalArgumentException` if the value is not a string.

---

## BlobSound & BlobSoundReader

### BlobSound Record

Located in `io.github.anjoismysign.bloblib.entities.message.BlobSound`.

```java
public record BlobSound(
    Sound sound,
    float volume,
    float pitch,
    @Nullable Long seed,
    @Nullable SoundCategory soundCategory,
    @NotNull MessageAudience audience,
    @NotNull String identifier
) implements DataAsset
```

| Field | Type | Description |
|---|---|---|
| `sound` | `Sound` | The Bukkit `Sound` enum value |
| `volume` | `float` | Playback volume (0.0–1.0 typical) |
| `pitch` | `float` | Playback pitch (0.5–2.0 typical) |
| `seed` | `Long` / `null` | Optional seed for sound variation (Paper/1.19.3+) |
| `soundCategory` | `SoundCategory` / `null` | Bukkit sound category (MASTER, MUSIC, RECORDS, etc.) |
| `audience` | `MessageAudience` | Whether sound plays to individual player or world |
| `identifier` | `String` | Unique key for this sound |

### MessageAudience

Located in `io.github.anjoismysign.bloblib.entities.message.MessageAudience`.

```java
public enum MessageAudience {
    PLAYER,   // Sound plays only to the target player
    WORLD     // Sound plays to all nearby players at the location
}
```

### Static Lookup

```java
@Nullable
public static BlobSound by(@NotNull String key)
```

Looks up a `BlobSound` by its identifier via `BlobLibSoundAPI.getInstance().getSound(key)`.

### Playback Methods

| Method | Description |
|---|---|
| `play(Player player, Location location)` | Play sound to a single player at a location |
| `play(Player player)` | Play sound to a single player at their location |
| `playInWorld(Location location)` | Play sound to all nearby players at a location |
| `handle(Player player, Location location)` | Audience-aware: if `PLAYER` → `play()`, if `WORLD` → `playInWorld()` |
| `handle(Player player)` | Audience-aware at player's location |
| `handle(Entity entity)` | If entity is a `Player`, uses `handle(player)`; otherwise `playInWorld(entity.getLocation())` |
| `handle(Block block)` | Always `playInWorld(block.getLocation())` |
| `broadcast()` | Calls `handle(player)` for every online player |

### YAML Structure

A `BlobSound` can be defined inline inside a message:

```yaml
BlobSound:
  Sound: entity.player.levelup
  Volume: 1.0
  Pitch: 1.2
  Category: PLAYERS      # optional
  Audience: PLAYER       # optional, defaults to PLAYER
  Seed: 12345            # optional
```

Or referenced by string key:

```yaml
BlobSound: System.Alert
# With audience override:
BlobSound: System.Alert:WORLD
```

### BlobSoundReader

Located in `io.github.anjoismysign.bloblib.entities.BlobSoundReader`.

```java
public static BlobSound read(@NotNull ConfigurationSection section, @NotNull String key)
```

Parses a full inline `BlobSound` from a section. Requires `Sound`, `Volume`, `Pitch`; optional `Category`, `Audience`, `Seed`.

```java
public static Optional<BlobSound> parse(@NotNull ConfigurationSection parent, @Nullable String key)
```

Handles both reference and inline forms:
- **String** → reference lookup via `BlobLibSoundAPI`. Supports `key:audience` syntax (e.g. `System.Alert:WORLD`).
- **Section** → inline read.
- **Absent** → `Optional.empty()`.

---

## Registration & Lifecycle

### File Location and Naming

- **Directory:** `plugins/BlobLib/BlobMessage/` (configured via `DataAssetType.BLOB_MESSAGE`)
- **Suffix:** files must end with `_lang.yml`
- **Default files:** `bloblib_lang.yml` shipped with BlobLib
- **Plugin files:** third-party plugins supply their own files in the same directory

### Registration via ManagerDirector

```java
// In your BlobPlugin's ManagerDirector setup:
director.registerBlobMessage("myplugin_lang.yml");         // no debug
director.registerBlobMessage(true, "myplugin_lang.yml");   // with debug
```

The `registerBlobMessage` method:

1. Appends `.yml` extension if missing.
2. Resolves the file in the `BlobMessage/` directory.
3. Calls `BlobLib.getInstance().getMessageManager().continueLoadingAssets(plugin, true, files)`.

### LocalizableDataAssetManager Loading Flow

The `LocalizableDataAssetManager<BlobMessage>` (obtained via `BlobLibMessageAPI#getMessageManager()`) performs the following on load:

1. Reads the `Locale` key from the file (default `"en_us"`).
2. Iterates all keys in the YAML file.
3. For each key that is a `ConfigurationSection` containing a `Type` field, invokes the read function `BlobMessageIO.read(section, locale, key)`.
4. Stores the parsed message in a locale-indexed map: `Map<locale, Map<key, BlobMessage>>`.
5. On identifier conflict, the first loaded wins; duplicates are logged.

### Locale Resolution

When `getAsset(identifier, locale)` is called:

1. Normalizes the locale via `BlobLibTranslatableAPI.getRealLocale(locale)`.
2. Tries `locales.get(normalizedLocale).get(identifier)`.
3. If not found, falls back to `en_us`.

### Lifecycle Summary

| Phase | What Happens |
|---|---|
| Server start / plugin load | `ManagerDirector` registers files → `continueLoadingAssets()` parses YAML → stored in locale maps |
| Runtime | `BlobLibMessageAPI.getInstance().getMessage(key, locale)` or `BlobMessage.by(key)` retrieves cached instance |
| Message send | `blobMessage.handle(player)` dispatches message + sound based on audience |
| Plugin unload | `LocalizableDataAssetManager.unload(plugin)` removes the plugin's assets |
| Reload | `reload()` clears all caches and re-reads all files |

---

## Code Examples

### Example 1 — Defining and Registering Message Files

**File: `plugins/BlobLib/BlobMessage/mygame_lang.yml`**
```yaml
MyGame:
  Join:
    Type: CHAT
    Message: '&a%player% joined the game!'
    BlobSound: MyGame.Join
  Death:
    Type: CHAT_ACTIONBAR_TITLE
    Chat: '&c%player% died'
    Actionbar: '&cYou died!'
    Title: '&c&lYOU DIED'
    Subtitle: '&7Better luck next time'
    FadeIn: 5
    Stay: 60
    FadeOut: 10
    BlobSound: MyGame.Death
  Welcome:
    Type: TITLE
    Title: '&6&l%world%'
    Subtitle: '&7Welcome to the world of %world%'
```

**Registration in plugin's ManagerDirector:**
```java
public class MyGameDirector extends ManagerDirector {
    public MyGameDirector(MyGamePlugin plugin) {
        super(plugin);
        // Register files from the BlobMessage directory
        registerBlobMessage("mygame_lang.yml");
        // Also register a Spanish locale file
        registerBlobMessage("mygame_es_es_lang.yml");
    }
}
```

### Example 2 — Retrieving and Sending Messages

```java
import io.github.anjoismysign.bloblib.api.BlobLibMessageAPI;
import io.github.anjoismysign.bloblib.entities.message.BlobMessage;
import org.bukkit.entity.Player;

public void sendWelcome(Player player) {
    // Get message in player's locale
    BlobMessage message = BlobLibMessageAPI.getInstance()
            .getMessage("MyGame.Welcome", player);

    if (message != null) {
        // Replace placeholders, then dispatch
        message = message.modder()
                .replace("%world%", player.getWorld().getName())
                .get();
        message.handle(player); // sends title + plays sound if applicable
    }
}
```

### Example 3 — Modifying Messages with BlobMessageModder

```java
public void sendDeathMessage(Player killer, Player victim) {
    BlobMessage message = BlobLibMessageAPI.getInstance()
            .getMessage("MyGame.Death", victim);

    if (message != null) {
        // Apply multiple transformations fluently
        BlobMessage modified = message.modder()
                .replace("%player%", victim.getName())
                .replace("%killer%", killer.getName())
                .color(ChatColor.RED)
                .translateRGBAndChatColors('&')
                .get();

        // handle() sends chat + actionbar + title + subtitle + sound
        modified.handle(victim);
        // Also send to killer
        modified.handle(killer);
    }
}
```

### Example 4 — Broadcasting with Placeholder Replacement

```java
public void broadcastJoinMessage(Player player) {
    BlobLibMessageAPI.getInstance().broadcast("MyGame.Join", modder -> {
        // Each online player gets their own localized & modified copy
        modder.replace("%player%", player.getName());
    });
}
```

### Example 5 — Using BlobMessageIO.parse() for Inline/Reference Reading

```java
import io.github.anjoismysign.bloblib.entities.BlobMessageIO;
import org.bukkit.configuration.ConfigurationSection;

public void handleActionConfiguration(ConfigurationSection actionSection) {
    // BlobMessageIO parses both reference strings and inline sections
    BlobMessageIO.parse(actionSection, "myAction")
        .ifPresent(message -> {
            // If actionSection has:
            //   BlobMessage: MyGame.Welcome  (string reference)
            // --OR--
            //   BlobMessage:
            //     Type: CHAT
            //     Message: '&aHello!'
            message.handle(somePlayer);
        });
}
```

### Example 6 — Complete handle() Flow

```java
// The handle() method is the recommended way to dispatch a message.
// It automatically decides how to send everything based on the
// message type and the sound's audience.

BlobMessage message = BlobMessage.by("MyGame.Death");

if (message != null) {
    // Handles ALL of the following in one call:
    // - Sends chat text (if CHAT type)
    // - Sends actionbar (if actionbar type)
    // - Sends title + subtitle (if title type)
    // - Plays sound to player only (if audience = PLAYER)
    // - Plays sound to world (if audience = WORLD)
    // - Does nothing sound-wise (if no sound defined)
    message.handle(player);
}

// For console senders, use toCommandSender():
message.toCommandSender(consoleCommandSender);
// Players get the full interactive experience;
// console gets plain text.
```
