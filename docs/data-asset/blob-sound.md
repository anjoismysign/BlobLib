# BlobSound

BlobSound is a configurable sound data asset that maps Bukkit `Sound` events to playable, named entries. Each entry defines a sound key, volume, pitch, and playback audience. Sounds are loaded from YAML files and can be played to individual players, to entire worlds, or broadcast globally.

---

## Directory & Discovery

- **Runtime directory:** `plugins/BlobLib/BlobSound/`
- **File format:** `.yml`
- **File suffix:** `_sounds.yml` (e.g. `myplugin_sounds.yml`)
- **Default file:** `bloblib_sounds.yml` (shipped with BlobLib)
- **Identifier:** the YAML path joined by dots (e.g. `System.Alert`)
- **Subdirectories:** supported — all `.yml` files in subfolders are loaded recursively
- **Load filter:** a section is a sound entry if it contains a `Sound` key with a valid string value
- **Duplicate identifiers:** logged and skipped (first loaded wins)

---

## YAML Schema

```yaml
<group>:
  <name>:
    Sound: <namespaced-sound-key>
    Volume: <number>
    Pitch: <number>
    Category: <sound-category>      # optional
    Audience: <audience>            # optional, defaults to PLAYER
    Seed: <long>                    # optional
```

### Fields

| Field      | Type   | Required | Description                                                   |
|------------|--------|----------|---------------------------------------------------------------|
| `Sound`    | string | **Yes**  | A Minecraft [sound event key](https://minecraft.wiki/w/Sounds.json#Sound_events), e.g. `block.note_block.pling`. |
| `Volume`   | number | **Yes**  | Volume level (0.0–1.0+, cast to float).                      |
| `Pitch`    | number | **Yes**  | Pitch level (0.5–2.0, cast to float).                         |
| `Category` | string | No       | One of: `MASTER`, `MUSIC`, `RECORDS`, `WEATHER`, `BLOCKS`, `HOSTILE`, `NEUTRAL`, `PLAYERS`, `AMBIENT`, `VOICE`. |
| `Audience` | string | No       | `PLAYER` (only the target hears it) or `WORLD` (everyone in the world hears it). Defaults to `PLAYER`. |
| `Seed`     | long   | No       | Seed for sound variation (Minecraft 1.21+ feature).           |

---

## Default File (`bloblib_sounds.yml`)

```yaml
System:
  Alert:
    Sound: block.note_block.pling
    Volume: 1.0
    Pitch: 0.9
    Audience: PLAYER
  Done:
    Sound: entity.arrow.hit_player
    Volume: 1.0
    Pitch: 0.9
    Audience: PLAYER
  Welcome:
    Sound: block.note_block.bell
    Volume: 1.0
    Pitch: 1.25
    Audience: WORLD
Builder:
  Button-Click:
    Sound: ui.button.click
    Volume: 1.0
    Pitch: 1.0
    Audience: PLAYER
  Build-Complete:
    Sound: block.anvil.use
    Volume: 1.0
    Pitch: 1.35
    Audience: PLAYER
  Timeout:
    Sound: block.note_block.flute
    Volume: 1.0
    Pitch: 0.9
    Audience: PLAYER
Economy:
  Received-Deposit:
    Sound: block.note_block.pling
    Volume: 1.0
    Pitch: 2.0
    Audience: PLAYER
  Received-Withdrawal:
    Sound: block.note_block.pling
    Volume: 1.0
    Pitch: 0.85
    Audience: PLAYER
```

---

## BlobLibSoundAPI

Access all loaded sounds through the singleton:

```java
BlobLibSoundAPI api = BlobLibSoundAPI.getInstance();
```

### `getSoundManager()`

Returns the `DataAssetManager<BlobSound>` that holds every loaded sound.

```java
DataAssetManager<BlobSound> manager = api.getSoundManager();
```

### `getSound(String key)`

| Parameter | Type   | Description                |
|-----------|--------|----------------------------|
| `key`     | String | Sound identifier (dot-separated path). |

Returns the `BlobSound` or `null` if no sound with that key is registered.

### `playSound(String key, Player player)`

Combines lookup and playback in one call. Throws `NullPointerException` if the key is not found.

| Parameter | Type   | Description                |
|-----------|--------|----------------------------|
| `key`     | String | Sound identifier.          |
| `player`  | Player | The player to play to. |

```java
BlobLibSoundAPI.getInstance().playSound("System.Alert", player);
```

### `getDefault()` / `mapDefault()`

| Method       | Returns                        | Description                   |
|--------------|--------------------------------|-------------------------------|
| `getDefault()` | `List<BlobSound>`            | All loaded sounds as a list.  |
| `mapDefault()` | `Map<String, BlobSound>`    | All loaded sounds keyed by identifier. |

---

## BlobSound Record

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

### `BlobSound.by(String key)`

Static convenience method:

```java
BlobSound sound = BlobSound.by("System.Alert"); // may be null
```

### Playback Methods

| Method                                      | Description                                                                 |
|---------------------------------------------|-----------------------------------------------------------------------------|
| `play(Player player, Location location)`    | Plays the sound to a single player at the given location.                   |
| `play(Player player)`                       | Plays the sound to a single player at their current location.               |
| `playInWorld(Location location)`            | Plays the sound in the world (all nearby players hear it).                   |
| `handle(Player player, Location location)`  | Routes to `play` or `playInWorld` based on the sound's `audience` setting.  |
| `handle(Player player)`                     | Same as above, uses `player.getLocation()`.                                  |
| `handle(Entity entity)`                     | If `entity` is a `Player`, calls `handle(player)`; otherwise `playInWorld(entity.getLocation())`. |
| `handle(Block block)`                       | Calls `playInWorld(block.getLocation())`.                                    |
| `broadcast()`                               | Calls `handle` on every online player.                                       |

### Usage Examples

```java
// Play to a specific player (respects Audience setting)
BlobSound.by("System.Alert").handle(player);

// Play directly in the world (ignores Audience — always world-audible)
BlobSound.by("Builder.Build-Complete").playInWorld(location);

// Broadcast to all online players (each hears according to Audience)
BlobSound.by("System.Welcome").broadcast();

// Play with a custom location
BlobSound.by("Economy.Received-Deposit").play(player, block.getLocation());
```

---

## Inline Sound References

When embedding a `BlobSound` inside another data asset (e.g. a `BlobMessage`), use the `BlobSound` key in YAML:

### String reference (looks up an existing BlobSound by identifier)

```yaml
BlobSound: "System.Alert"
```

### Reference with audience override

```yaml
BlobSound: "System.Alert:WORLD"
```

### Inline definition (defines a sound in place)

```yaml
BlobSound:
  Sound: entity.experience_orb.pickup
  Volume: 0.5
  Pitch: 1.5
  Audience: PLAYER
```

---

## Plugin Registration

External plugins can register additional sound files during their BlobLib integration:

```java
// Register a single file (no debug)
managerDirector.registerBlobSound("my_plugin_sounds.yml");

// Register with debug logging
managerDirector.registerBlobSound(true, "my_plugin_sounds.yml");

// Register multiple files at once
managerDirector.registerBlobSound("sounds_a.yml", "sounds_b.yml");
```

The files are resolved from the plugin's `BlobSound/` directory (`plugins/<YourPlugin>/BlobSound/`). When the plugin is disabled, all its registered sounds are automatically removed.

---

## Commands

```
/bloblib blobsound send <key> [player]
```

Plays a BlobSound to yourself or to the specified online player. The sound's `audience` setting (`PLAYER`/`WORLD`) is respected.

---

## Examples

See the [examples](examples/) directory for ready-to-use YAML files:

- [`simple_definitions.yml`](examples/blobsound_simple_definitions.yml) — basic sound definitions with `PLAYER` and `WORLD` audience variants
- [`themed_categories.yml`](examples/blobsound_themed_categories.yml) — sounds grouped by game mechanics (Combat, Weather, Ambience, Boss) with `SoundCategory` and `Seed` usage
- [`plugin_integration.yml`](examples/blobsound_plugin_integration.yml) — RPG-style plugin example with Quest, Shop, Level-Up, and Teleport sound families

---

## Loading Order

1. BlobLib loads its own `BlobSound/` directory on startup (including `bloblib_sounds.yml`).
2. Plugins using BlobLib's asset system can register additional sound files via `DataAssetManager#reload(BlobPlugin, IManagerDirector)`.
3. Plugins can call `DataAssetManager#continueLoadingAssets(BlobPlugin, boolean, File...)` to load extra files at any point.
4. When a plugin is unloaded, all sounds it registered are removed automatically.
