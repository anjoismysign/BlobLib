# Actions

Actions are configurable behaviours that run a command when performed. They are typed, support placeholders, and can be deserialised from YAML via `Action.fromConfigurationSection`.

---

## Directory & Discovery

- **Runtime directory:** `plugins/BlobLib/Action/`
- **File format:** `.yml`
- **Identifier:** the section name under which the action is defined
- **Subdirectories:** supported — all `.yml` files in subfolders are loaded recursively
- **Load filter:** a section is considered an action if it contains a `Type` key with a valid string value
- **Duplicate identifiers:** logged and skipped (first loaded wins)

---

## YAML Schema

```yaml
<identifier>:
  Type: <action-type>
  Command: '<command-text>'
```

### Fields

| Field     | Type   | Description                                       |
|-----------|--------|---------------------------------------------------|
| `Type`    | enum   | One of `ACTOR_COMMAND` or `CONSOLE_COMMAND`.      |
| `Command` | string | The command to execute. Supports `%actor%` placeholder. |

### Action Types

| Type key            | Executor          | Behaviour                                       |
|---------------------|-------------------|-------------------------------------------------|
| `ACTOR_COMMAND`     | The entity itself | Runs as `/`-prefixed command **by** the actor.  |
| `CONSOLE_COMMAND`   | Console           | Runs as `/`-prefixed command **by** the console. |
| `NO_ACTOR`          | —                 | Not currently wired from configuration (reserved for programmatic use). |

The `%actor%` placeholder in `Command` is replaced with the actor's name — even for `CONSOLE_COMMAND` — at execution time (see `CommandAction.updateActor` and `ConsoleCommandAction.updateActor`).

---

## Example (`bloblib_actions.yml`)

```yaml
Gamemode:
  Creative:
    Type: CONSOLE_COMMAND
    Command: 'gamemode creative %actor%'
  Survival:
    Type: CONSOLE_COMMAND
    Command: 'gamemode survival %actor%'
Inventory:
  Close:
    Type: CONSOLE_COMMAND
    Command: 'bloblib closeinventory %actor%'
```

---

## BlobLibActionAPI

Access all loaded actions through the singleton:

```java
BlobLibActionAPI api = BlobLibActionAPI.getInstance();
```

### `getActionManager()`

Returns the `DataAssetManager<Action<Entity>>` that holds every loaded action.

### `getAction(String key)`

Looks up a single action by its YAML section name (the identifier).

| Parameter | Type   | Description              |
|-----------|--------|--------------------------|
| `key`     | String | Action identifier.       |

Returns the `Action` or `null` if no action with that key is registered.

---

## Action Class

`Action<T extends Entity>` is the abstract base. Concrete implementations:

| Class                                   | ActionType         | Behaves as                                  |
|-----------------------------------------|--------------------|---------------------------------------------|
| `CommandAction<T>`                      | `ACTOR_COMMAND`    | `Bukkit.dispatchCommand(getActor(), cmd)`   |
| `ConsoleCommandAction<T>`               | `CONSOLE_COMMAND`  | `Bukkit.dispatchCommand(console, cmd)`      |

### Programmatic Usage

```java
// Create an action directly
CommandAction<Player> action = CommandAction.build("say Hello!", "my-action");

// Set the actor
action.updateActor(player);

// Run
action.run();

// Or a one-liner that updates actor + runs
action.perform(player);

// With a modifier function (e.g. replace placeholders)
action.perform(player, cmd -> cmd.replace("%custom%", "world"));
```

### Key Methods

| Method                                      | Description                                                   |
|---------------------------------------------|---------------------------------------------------------------|
| `fromConfigurationSection(ConfigurationSection)` | Deserialises an `Action<Entity>` from a YAML section.   |
| `run()`                                     | Executes the action.                                          |
| `perform(U actor)`                          | Calls `updateActor(actor)` then `run()`.                      |
| `perform(U actor, Function<String, String>)`| Applies a modifier to the command, then performs.             |
| `updateActor(U actor)`                      | Returns a **new** action instance with the actor set.         |
| `modify(Function<String, String>)`          | Returns a **new** action instance with the command transformed. |
| `save(ConfigurationSection)`                | Serialises the action back to a YAML section.                 |
| `getActor()`                                | Returns the current actor (`null` if not set).                |
| `getActionType()`                           | Returns the `ActionType` enum.                                |
| `isAsync()`                                 | Whether the action is asynchronous (default `true`).          |
| `updatesActor()`                            | Whether the action creates a new instance on `perform` (`true` for both built-in types). |
| `identifier()`                              | Returns the action's key.                                     |

---

## ActionMemo

`ActionMemo` is a lightweight record that stores a reference to an action without loading it immediately:

```java
ActionMemo memo = new ActionMemo("say Hello!", ActionType.ACTOR_COMMAND, "identifier-key");
Action<Entity> action = memo.getAction(); // returns CommandAction or ConsoleCommandAction
```

| Field            | Type        | Description                              |
|------------------|-------------|------------------------------------------|
| `getReference()` | `@NotNull`  | The command string.                      |
| `getActionType()`| `@Nullable` | The `ActionType`, or `null` for no-op.  |
| `getPath()`      | `@NotNull`  | The identifier/key for the action.       |

---

## Examples

See the [examples](examples/) directory for ready-to-use YAML files:

- [`simple_commands.yml`](examples/action_simple_commands.yml) — basic `ACTOR_COMMAND` and `CONSOLE_COMMAND` actions
- [`hierarchical.yml`](examples/action_hierarchical.yml) — nested action structure with organised groupings
- [`plugin_integration.yml`](examples/action_plugin_integration.yml) — integration example showing an RPG-style plugin's action definitions

---

## Loading Order

1. BlobLib loads its own `Action/` directory on startup.
2. Plugins using BlobLib's asset system can register additional actions via `DataAssetManager#reload(BlobPlugin, IManagerDirector)`.
3. Plugins can call `DataAssetManager#continueLoadingAssets(BlobPlugin, boolean, File...)` to load extra files at any point.
4. When a plugin is unloaded, all actions it registered are removed automatically.
