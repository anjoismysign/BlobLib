# BlobLibEconomyAPI

BlobLibEconomyAPI provides a unified access point to the server's Vault-compatible economy system. It bridges **single-currency** (legacy `Economy`) and **multi-currency** (`MultiEconomy`) providers through a single abstraction — `ElasticEconomy` — so your plugin works unchanged whether the server has one currency or fifty.

---

## Getting the API

`BlobLibEconomyAPI` is a singleton. Obtain it from `BlobLibAPI`:

```java
BlobLibAPI api = BlobLibAPI.getInstance();
BlobLibEconomyAPI economyAPI = api.getEconomyAPI();
```

> The `BlobLibEconomyAPI.getInstance(BlobLib)` overload is internal to BlobLib and should not be called by external plugins. Always use `BlobLibAPI.getInstance().getEconomyAPI()`.

Once you have the singleton, call `getElasticEconomy()` to retrieve the economy:

```java
ElasticEconomy economy = economyAPI.getElasticEconomy();
```

---

## ElasticEconomy — The Unified Abstraction

`ElasticEconomy` implements `MultiEconomy`, so it exposes the full multi-currency interface regardless of what the underlying provider actually supports. It can be in one of three states:

| State | `ElasticEconomyType` | Meaning |
|---|---|---|
| **Absent** | `ABSENT` | No Vault-compatible economy plugin is installed. All operations return no-ops / null / false. |
| **Single** | `SINGLE` | A legacy `Economy` (single-currency) provider is registered. BlobLib wraps it with [`SingleEconomy`](#singleeconomy-class) so it behaves like a `MultiEconomy` — every method returns the same currency. |
| **Multi** | `MULTI` | A true `MultiEconomy` provider is registered. Multiple named currencies are available. |

Check the state with:

```java
if (economy.isAbsent())       { /* no economy at all */ }
if (economy.isSingleEconomy()){ /* single currency, but still use MultiEconomy API */ }
if (economy.isMultiEconomy()) { /* true multi-currency */ }
```

You can also retrieve the enum directly:

```java
ElasticEconomyType type = economy.getType(); // ABSENT, SINGLE, or MULTI
```

---

## Getting the Default Economy

In both single-currency and multi-currency setups you almost always want the **default** currency — the one players interact with by default.

```java
ElasticEconomy elastic = BlobLibAPI.getInstance().getEconomyAPI().getElasticEconomy();

IdentityEconomy defaultEco = elastic.getDefault();
//               — or per-world —
IdentityEconomy perWorld = elastic.getDefault("world_nether");
```

### Fallback behaviour

| State | `getDefault()` | `getDefault(world)` |
|---|---|---|
| `ABSENT` | `null` | `null` |
| `SINGLE` | The single wrapped `IdentityEconomy` | The same single `IdentityEconomy` |
| `MULTI` | The default implementation for the server | The default implementation for that world (if supported), otherwise the server default |

### Safe access pattern

```java
ElasticEconomy economy = economyAPI.getElasticEconomy();
if (!economy.isAbsent()) {
    IdentityEconomy defaultCurrency = economy.getDefault();
    // use defaultCurrency...
}
```

---

## Multi-Economy Usage

When `isMultiEconomy()` is `true` you can discover and use named currencies.

### Listing all currencies

```java
// All currencies available on the server
Collection<IdentityEconomy> all = economy.getAllImplementations();

// Currencies available in a specific world
Collection<IdentityEconomy> worldCurrencies = economy.getAllImplementations("world_nether");
```

### Looking up a named currency

```java
// Get a specific implementation by name
IdentityEconomy gems = economy.getImplementation("gems");

// Check existence
if (economy.existsImplementation("gems")) { ... }
if (economy.existsImplementation("gems", "world_nether")) { ... }
```

### Pattern: use a named currency, fall back to default

The `ElasticEconomy.map(Optional<String>)` convenience method does exactly this:

```java
IdentityEconomy currency = economy.map(Optional.ofNullable(configCurrencyName));
// If configCurrencyName is present and valid → that implementation
// If configCurrencyName is null / absent          → getDefault()
```

---

## Single Economy (Fallback)

When `isSingleEconomy()` is `true`, the server only has a legacy `Economy` provider. `ElasticEconomy` wraps it into a `SingleEconomy` so that **all `MultiEconomy` methods work**, but every one returns the same underlying `IdentityEconomy`.

This means your multi-currency-aware code works unchanged:

```java
// This works identically for SINGLE and MULTI:
IdentityEconomy eco = economy.getDefault();
eco.deposit(player, 100.0);

// getAllImplementations() always returns the single economy
economy.getAllImplementations().size(); // 1
```

### SingleEconomy class

`SingleEconomy` is the adapter that implements `MultiEconomy` by wrapping a single `IdentityEconomy`:

| Method | Behaviour |
|---|---|
| `getDefault()` / `getDefault(world)` | Always returns the wrapped economy |
| `getImplementation(name)` | Always returns the wrapped economy (any name) |
| `existsImplementation(name)` | Always returns `true` |
| `getAllImplementations()` | Returns a single-element list |

---

## `IdentityEconomy` — Capability Flags

`IdentityEconomy` extends `Economy` with UUID-based operations. Not all providers support every operation. Check these flags before calling the associated methods:

| Flag method | When `false`, these throw `UnsupportedOperationException` |
|---|---|
| `supportsAllRecordsOperation()` | `getAllRecords()` |
| `supportsAllOnlineOperation()` | `getAllOnline()` |
| `supportsOfflineOperations()` | All withdrawal/deposit/balance methods for offline players |
| `supportsUUIDOperations()` | All `UUID`-parameter methods (`getBalance(UUID)`, `has(UUID, ...)`, `withdraw(UUID, ...)`, `deposit(UUID, ...)`, etc.) |

> **LegacyEconomy** (produced by `EconomyWrapper.legacy()`) returns `false` for all four flags and throws `UnsupportedOperationException` on every UUID-based method.

---

## API Reference

### BlobLibEconomyAPI

| Method | Returns | Description |
|---|---|---|
| `getInstance(BlobLib)` | `BlobLibEconomyAPI` | *(internal)* — used by `BlobLibAPI` to initialise the singleton. External plugins should not call this. |
| `getInstance()` | `BlobLibEconomyAPI` | Singleton accessor for external plugins. |
| `getElasticEconomy()` | `ElasticEconomy` | The unified economy object. Never null — check `isAbsent()`. |
| `hasVaultPermissionsProvider()` | `boolean` | `true` if a Vault-compatible permissions provider is registered. |

### BlobLibAPI (convenience)

| Method | Returns | Description |
|---|---|---|
| `getInstance(BlobLib)` | `BlobLibAPI` | *(internal)* — used to initialise the singleton. External plugins should use the no-arg overload. |
| `getInstance()` | `BlobLibAPI` | Singleton accessor for external plugins. |
| `getEconomyAPI()` | `BlobLibEconomyAPI` | Pre-wired sub-API for economy operations. |

### ElasticEconomy

`ElasticEconomy` implements `MultiEconomy`. All methods delegate to the underlying provider.

| Method | Returns | Description |
|---|---|---|
| `isEnabled()` | `boolean` | Whether the underlying economy is enabled. |
| `getName()` | `String` | Name of the economy provider. |
| `existsImplementation(String)` | `boolean` | Whether a named implementation exists. |
| `existsImplementation(String, String world)` | `boolean` | Whether a named implementation exists in a world. |
| `getImplementation(String)` | `IdentityEconomy` | The named implementation, or `null` if absent. |
| `getDefault()` | `IdentityEconomy` | The default implementation (server-wide). |
| `getDefault(String world)` | `IdentityEconomy` | The default implementation for a world. |
| `getAllImplementations()` | `Collection<IdentityEconomy>` | All available implementations. |
| `getAllImplementations(String world)` | `Collection<IdentityEconomy>` | All implementations available in a world. |
| `isSingleEconomy()` | `boolean` | `true` if backed by a single legacy `Economy`. |
| `isMultiEconomy()` | `boolean` | `true` if backed by a true `MultiEconomy` provider. |
| `isAbsent()` | `boolean` | `true` if no Vault economy provider exists at all. |
| `getType()` | `ElasticEconomyType` | Returns the enum value: `ABSENT`, `SINGLE`, or `MULTI`. |
| `map(Optional<String>)` | `IdentityEconomy` | If the optional is present, calls `getImplementation(name)`; otherwise calls `getDefault()`. |

### IdentityEconomy (key UUID-based additions to `Economy`)

| Method | Returns | Description |
|---|---|---|
| `supportsAllRecordsOperation()` | `boolean` | Whether `getAllRecords()` is supported. |
| `supportsAllOnlineOperation()` | `boolean` | Whether `getAllOnline()` is supported. |
| `supportsOfflineOperations()` | `boolean` | Whether offline player operations work. |
| `supportsUUIDOperations()` | `boolean` | Whether UUID-parameter methods are supported. |
| `createAccount(UUID, String)` | `boolean` | Create an account by UUID with an associated name. |
| `createAccount(UUID, String, String worldName)` | `boolean` | Create a world-specific account by UUID. |
| `getAllRecords()` | `Map<UUID, String>` | All stored accounts (UUID → last-known-name). |
| `getAllOnline()` | `Collection<UUID>` | All UUIDs that currently have online accounts. |
| `getAccountName(UUID)` | `String` | Last-known name for a UUID. |
| `hasAccount(UUID)` | `boolean` | Whether a UUID has an account. |
| `hasAccount(UUID, String worldName)` | `boolean` | Whether a UUID has a world-specific account. |
| `renameAccount(UUID, String)` | `boolean` | Update the stored name for a UUID. |
| `getBalance(UUID)` | `double` | Balance of the account associated with the UUID. |
| `getBalance(UUID, String world)` | `double` | World-specific balance by UUID. |
| `has(UUID, double)` | `boolean` | Whether a UUID has at least `amount`. |
| `has(UUID, String, double)` | `boolean` | World-specific balance check by UUID. |
| `withdraw(UUID, double)` | `EconomyResponse` | Withdraw from a UUID's account. |
| `withdraw(UUID, String, double)` | `EconomyResponse` | World-specific withdraw by UUID. |
| `deposit(UUID, double)` | `EconomyResponse` | Deposit to a UUID's account. |
| `deposit(UUID, String, double)` | `EconomyResponse` | World-specific deposit by UUID. |
| `createBank(String, UUID)` | `EconomyResponse` | Create a bank owned by a UUID. |
| `deleteBank(String)` | `EconomyResponse` | Delete a bank. |
| `bankBalance(String)` | `EconomyResponse` | Get bank balance. |
| `bankHas(String, double)` | `EconomyResponse` | Check if a bank has funds. |
| `bankWithdraw(String, double)` | `EconomyResponse` | Withdraw from a bank. |
| `bankDeposit(String, double)` | `EconomyResponse` | Deposit to a bank. |
| `isBankOwner(String, UUID)` | `EconomyResponse` | Whether a UUID owns a bank. |
| `isBankMember(String, UUID)` | `EconomyResponse` | Whether a UUID is a bank member. |
| `getBanks()` | `List<String>` | List of all bank names. |

### EconomyResponse

| Field / Method | Type | Description |
|---|---|---|
| `amount` | `double` | Amount modified by the operation. |
| `balance` | `double` | New balance after the operation. |
| `type` | `ResponseType` | `SUCCESS`, `FAILURE`, or `NOT_IMPLEMENTED`. |
| `errorMessage` | `String` | Error description when type is `FAILURE`. |
| `transactionSuccess()` | `boolean` | `true` when `type == SUCCESS`. |

---

## Lifecycle & Service Registration

BlobLib detects economy providers automatically through Bukkit's `ServicesManager`. No manual wiring is required.

1. **On server start**, `VaultManager` queries the `ServicesManager` for:
   - `Economy.class` (legacy single-currency provider)
   - `MultiEconomy.class` (multi-currency provider from Vault2)
2. If only an `Economy` is found, it is wrapped into a `SingleEconomy` (state: `SINGLE`).
3. If a `MultiEconomy` is found, it is used directly (state: `MULTI`).
4. If neither is found, the state is `ABSENT` — all calls are no-ops.
5. **At runtime**, `ServiceRegisterEvent` / `ServiceUnregisterEvent` listeners update the state dynamically, so economy plugins loaded or unloaded after startup are handled.

---

## Examples

| File | Description |
|---|---|
| [`BlobLibEconomyAPISimple.java`](examples/bloblibeconomyapi_simple.java) | Basic deposit/withdraw/balance using the default economy (works for both single and multi). |
| [`BlobLibEconomyAPIMulti.java`](examples/bloblibeconomyapi_multi.java) | Named currency lookup, fallback to default, listing all currencies. |
| [`BlobLibEconomyAPIDefault.java`](examples/bloblibeconomyapi_default.java) | Various ways to obtain the default economy — server-wide and per-world. |
