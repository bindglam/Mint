# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

- **Build the project**: `./gradlew build`
- **Run the plugin locally**: `./gradlew runServer` (downloads Vault and PlaceholderAPI automatically)
- **Run with Folia**: `./gradlew runFolia`

The project uses Gradle with Kotlin DSL. The main JAR is built via the `shadowJar` task, which relocates Kotlin and bstats dependencies.

## Project Structure

The project is a multi-module Gradle project:

- **api/** - Java API module that other plugins depend on for accessing Mint functionality
- **core/** - Kotlin implementation module containing all plugin logic

The core module is shadowed into a single JAR that contains both API and implementation.

## Architecture Overview

Mint follows a **Manager-based architecture** where subsystems are organized as `Managerial` objects with lifecycle methods:

1. **preload(context)** - Called synchronously during plugin enable
2. **start(context)** - Called asynchronously during plugin enable (after preload)
3. **end(context)** - Called during plugin disable
4. **priority()** - Returns `Priority(start, end)` values that control execution order

Managers are registered in `MintPluginImpl` and executed in order based on their priority values. Higher numbers execute first.

### Key Managers

| Manager | Priority (start/end) | Purpose |
|---------|---------------------|---------|
| DatabaseManagerImpl | MAX_VALUE / MIN_VALUE | Database connections (SQL, Redis) |
| CommandManager | default | Cloud command registration |
| CompatibilityManager | default | Vault/PAPI integrations |
| LanguageManager | default | Localization loading |
| CurrencyManagerImpl | default | Currency registry management |
| AccountManagerImpl | -1 / 1 | Account data and Redis sync |

### Manager Lifecycle Context

The `Context` class provides access to common dependencies:
- `plugin()` - The MintPlugin instance
- `config()` - MintConfiguration instance
- `logger()` - Bukkit logger

## Account and Currency System

### Account Model
- `Account` (API interface in Java) - Represents a player's economic account
- `AccountImpl` (Kotlin implementation) - Handles balance operations

**Key behaviors**:
- All balance operations are asynchronous (return `CompletableFuture`)
- Balances are first cached in Redis (if enabled), with a 120-second TTL
- On cache miss, data is fetched from SQL and cached back to Redis
- "Dirty" accounts (those with changes) are tracked in a Redis set and periodically synced to SQL
- Every balance modification triggers a `TransactionLog` entry and `AccountOperationEvent`

### Currency Model
- `Currency` (Java record) - Defines a currency with ID and display settings
- `CurrencyRegistry` (API interface) - Manages available currencies
- `CurrencyRegistryImpl` (Kotlin object) - Implementation using HashMap

Currencies are registered via `CurrencyManagerImpl` and can have formatted display strings using `Currency.format(amount)`.

### Transaction Logging
- `TransactionLogger` interface provides paginated log retrieval
- `TransactionLog` records: timestamp, operation, currency, result, and original value
- Logs are stored in the `mint_logs` table with pagination support

## Operations

The `Operation` enum defines balance modification operations:
- `DEPOSIT` - Adds value to balance (always succeeds)
- `WITHDRAW` - Subtracts value, fails if insufficient funds

Operations return a `Result` record containing `success` boolean and the resulting balance.

## Configuration System

`MintConfiguration` uses a custom ConfigLib with nested class structure:

```kotlin
config.database.sql.type.value()        // SQLITE or MYSQL
config.database.redis.enabled.value()   // boolean
config.economy.currency.defaultCurrency.value() // string
```

The config is auto-reloaded via the `Reloadable` interface.

## Database Abstraction

The `DatabaseManagerImpl` uses a generic `Database<T, E>` interface from DatabaseLib:
- SQL: `Database<Connection, SQLException>`
- Redis: `Database<Jedis, JedisException>`

Database type is configurable between SQLite (file-based) and MySQL (connection pool).

## Integration Layer

### Vault Compatibility
`VaultEconomy` and `LegacyVaultEconomy` implement Vault's Economy interface, routing calls through Mint's Account/Currency systems.

### PlaceholderAPI
`MintExpansion` provides placeholders via the PAPI expansion system.

## Language System

Languages are YAML files loaded by `LanguageManager`:
- Keys map to `LanguageComponent` instances that support Adventure Component formatting
- Access via `lang("key", args...)` utility function

## Events

- `AccountEvent` - Base event for account-related actions
- `AccountOperationEvent` - Fired on any balance modification (deposit/withdraw)

## Constants

Plugin constants are in `utils/Constants.kt`:
- `PLUGIN_ID = "mint"`
- `PLUGIN_NAME = "Mint"`
- `BSTATS_PLUGIN_ID = 29488`

## API Usage

External plugins access Mint via:

```java
MintPlugin mint = Mint.instance();
Account account = mint.accountManager().getAccount(player.getUniqueId());
account.getBalance(currency).thenAccept(balance -> { /* ... */ });
account.modifyBalance(Operation.DEPOSIT, currency, amount).thenAccept(result -> { /* ... */ });
```

The API module (`com.github.bindglam:Mint:api:<VERSION>`) is published to JitPack for external consumption.

## Async Execution

All database operations and balance modifications run asynchronously using:
- `CompletableFuture.supplyAsync { }` for balance operations
- `server.asyncScheduler.runNow(plugin) { }` for database initialization
- Fixed-rate scheduler for periodic Redis sync

This ensures zero impact on server TPS.
