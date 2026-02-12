package com.bindglam.mint.manager

import com.bindglam.mint.Mint
import com.bindglam.mint.account.operation.Operation
import com.bindglam.mint.utils.lang
import com.bindglam.mint.utils.plugin
import org.bukkit.Bukkit
import org.incendo.cloud.SenderMapper
import org.incendo.cloud.bukkit.CloudBukkitCapabilities
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.paper.LegacyPaperCommandManager
import org.incendo.cloud.parser.standard.DoubleParser
import org.incendo.cloud.parser.standard.IntegerParser
import org.incendo.cloud.parser.standard.StringParser
import org.incendo.cloud.permission.Permission
import org.incendo.cloud.suggestion.Suggestion
import org.incendo.cloud.suggestion.SuggestionProvider
import java.math.BigDecimal

object CommandManager : Managerial {
    override fun start(context: Context) {
        val manager = LegacyPaperCommandManager(
            context.plugin().plugin(),
            ExecutionCoordinator.simpleCoordinator(),
            SenderMapper.identity()
        )

        if (manager.hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
            manager.registerBrigadier()
        } else if (manager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            manager.registerAsynchronousCompletions()
        }

        manager.command(manager.commandBuilder("mint")
            .permission(Permission.of("mint.command.reload"))
            .literal("reload")
            .handler { ctx ->
                ctx.sender().sendMessage(lang("command_reload_start"))
                Mint.instance().reload()
                ctx.sender().sendMessage(lang("command_reload_end"))
            })
        manager.command(manager.commandBuilder("mint")
            .permission(Permission.of("mint.command.balance.get"))
            .literal("balance")
            .literal("get")
            .required("target", StringParser.stringParser(), SuggestionProvider.blocking { _, _ -> Bukkit.getOnlinePlayers().map { Suggestion.suggestion(it.name) } })
            .required("currency", StringParser.stringParser(), SuggestionProvider.blocking { _, _ -> CurrencyManagerImpl.registry().entries().map { Suggestion.suggestion(it.id()) } })
            .handler { ctx ->
                val target = Bukkit.getOfflinePlayer(ctx.get<String>("target"))
                val currency = Mint.instance().currencyManager().registry().get(ctx.get("currency")).orElse(null)
                if(currency == null) {
                    ctx.sender().sendMessage(lang("error_invalid_currency"))
                    return@handler
                }

                val account = AccountManagerImpl.getAccount(target.uniqueId)
                account.getBalance(currency).thenAccept { balance ->
                    ctx.sender().sendMessage(lang("command_money_balance_get", target.name ?: "Unknown", currency.format(balance)))
                }
            })
        manager.command(manager.commandBuilder("mint")
            .permission(Permission.of("mint.command.balance.modify"))
            .literal("balance")
            .literal("modify")
            .required("target", StringParser.stringParser(), SuggestionProvider.blocking { _, _ -> Bukkit.getOnlinePlayers().map { Suggestion.suggestion(it.name) } })
            .required("currency", StringParser.stringParser(), SuggestionProvider.blocking { _, _ -> CurrencyManagerImpl.registry().entries().map { Suggestion.suggestion(it.id()) } })
            .required("amount", DoubleParser.doubleParser(0.0))
            .required("operation", StringParser.stringParser(), SuggestionProvider.suggesting(Operation.entries.map { Suggestion.suggestion(it.name) }))
            .handler { ctx ->
                val target = Bukkit.getOfflinePlayer(ctx.get<String>("target"))
                val currency = Mint.instance().currencyManager().registry().get(ctx.get("currency")).orElse(null)
                val amount = ctx.get<Double>("amount")
                val operation: Operation
                try {
                    operation = Operation.valueOf(ctx.get("operation"))
                } catch (_: IllegalArgumentException) {
                    ctx.sender().sendMessage(lang("error_invalid_operation"))
                    return@handler
                }
                if(currency == null) {
                    ctx.sender().sendMessage(lang("error_invalid_currency"))
                    return@handler
                }

                val account = AccountManagerImpl.getAccount(target.uniqueId)
                account.modifyBalance(operation, currency, BigDecimal.valueOf(amount)).thenAccept { result ->
                    ctx.sender().sendMessage(lang("command_money_balance_get", target.name ?: "Unknown", currency.format(result.result())))
                }
            })
        manager.command(manager.commandBuilder("mint")
            .permission(Permission.of("mint.command.balance.get"))
            .literal("balance")
            .literal("logs")
            .literal("view")
            .required("target", StringParser.stringParser(), SuggestionProvider.blocking { _, _ -> Bukkit.getOnlinePlayers().map { Suggestion.suggestion(it.name) } })
            .optional("page", IntegerParser.integerParser(1))
            .handler { ctx ->
                val target = Bukkit.getOfflinePlayer(ctx.get<String>("target"))
                val page = ctx.getOrDefault("page", 1)

                val account = AccountManagerImpl.getAccount(target.uniqueId)
                account.logger().retrieveLogs(10, (page - 1) * 10).thenAccept { logs ->
                    logs.forEach { log ->
                        ctx.sender().sendMessage(lang("command_money_balance_logs_view", log.timestamp(), log.operation(), log.currency().format(log.value()), log.currency().format(log.result().result()),
                            if(log.result().isSuccess) "<green>O" else "<red>X"))
                    }
                }
            })
        manager.command(manager.commandBuilder("mint")
            .permission(Permission.of("mint.command.balance.get"))
            .literal("balance")
            .literal("logs")
            .literal("clear")
            .required("target", StringParser.stringParser(), SuggestionProvider.blocking { _, _ -> Bukkit.getOnlinePlayers().map { Suggestion.suggestion(it.name) } })
            .handler { ctx ->
                val target = Bukkit.getOfflinePlayer(ctx.get<String>("target"))

                val account = AccountManagerImpl.getAccount(target.uniqueId)
                account.logger().clear()
                ctx.sender().sendMessage(lang("command_money_balance_logs_clear", target.name ?: "Unknown"))
            })
    }
}