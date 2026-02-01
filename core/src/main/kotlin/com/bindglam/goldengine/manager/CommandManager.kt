package com.bindglam.goldengine.manager

import com.bindglam.goldengine.GoldEngine
import com.bindglam.goldengine.account.Operation
import com.bindglam.goldengine.utils.lang
import com.bindglam.goldengine.utils.plugin
import org.bukkit.Bukkit
import org.incendo.cloud.SenderMapper
import org.incendo.cloud.bukkit.CloudBukkitCapabilities
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.paper.LegacyPaperCommandManager
import org.incendo.cloud.parser.standard.DoubleParser
import org.incendo.cloud.parser.standard.StringParser
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

        manager.command(manager.commandBuilder("goldengine")
            .literal("reload")
            .handler { ctx ->
                ctx.sender().sendMessage(lang("command_reload_start"))
                GoldEngine.instance().reload()
                ctx.sender().sendMessage(lang("command_reload_end"))
            })
        manager.command(manager.commandBuilder("goldengine")
            .literal("balance")
            .literal("get")
            .required("target", StringParser.stringParser())
            .required("currency", StringParser.stringParser())
            .handler { ctx ->
                val target = Bukkit.getOfflinePlayer(ctx.get<String>("target"))
                val currency = GoldEngine.instance().currencyManager().registry().get(ctx.get("currency")).orElse(null)
                if(currency == null) {
                    ctx.sender().sendMessage(lang("error_invalid_currency"))
                    return@handler
                }

                AccountManagerImpl.getAccount(target.uniqueId).thenAccept { account ->
                    ctx.sender().sendMessage(lang("command_money_balance_get", target.name ?: "Unknown", currency.format(account.balance(currency))))
                    account.close()
                }
            })
        manager.command(manager.commandBuilder("goldengine")
            .literal("balance")
            .literal("set")
            .required("target", StringParser.stringParser())
            .required("currency", StringParser.stringParser())
            .required("amount", DoubleParser.doubleParser(0.0))
            .handler { ctx ->
                val target = Bukkit.getOfflinePlayer(ctx.get<String>("target"))
                val currency = GoldEngine.instance().currencyManager().registry().get(ctx.get("currency")).orElse(null)
                val amount = ctx.get<Double>("amount")
                if(currency == null) {
                    ctx.sender().sendMessage(lang("error_invalid_currency"))
                    return@handler
                }

                AccountManagerImpl.getAccount(target.uniqueId).thenAccept { account ->
                    account.balance(currency, BigDecimal.valueOf(amount))
                    ctx.sender().sendMessage(lang("command_money_balance_get", target.name ?: "Unknown", currency.format(account.balance(currency))))
                    account.close()
                }
            })
        manager.command(manager.commandBuilder("goldengine")
            .literal("balance")
            .literal("add")
            .required("target", StringParser.stringParser())
            .required("currency", StringParser.stringParser())
            .required("amount", DoubleParser.doubleParser(0.0))
            .handler { ctx ->
                val target = Bukkit.getOfflinePlayer(ctx.get<String>("target"))
                val currency = GoldEngine.instance().currencyManager().registry().get(ctx.get("currency")).orElse(null)
                val amount = ctx.get<Double>("amount")
                if(currency == null) {
                    ctx.sender().sendMessage(lang("error_invalid_currency"))
                    return@handler
                }

                AccountManagerImpl.getAccount(target.uniqueId).thenAccept { account ->
                    account.modifyBalance(currency, BigDecimal.valueOf(amount), Operation.ADD)
                    ctx.sender().sendMessage(lang("command_money_balance_get", target.name ?: "Unknown", currency.format(account.balance(currency))))
                    account.close()
                }
            })
        manager.command(manager.commandBuilder("goldengine")
            .literal("balance")
            .literal("subtract")
            .required("target", StringParser.stringParser())
            .required("currency", StringParser.stringParser())
            .required("amount", DoubleParser.doubleParser(0.0))
            .handler { ctx ->
                val target = Bukkit.getOfflinePlayer(ctx.get<String>("target"))
                val currency = GoldEngine.instance().currencyManager().registry().get(ctx.get("currency")).orElse(null)
                val amount = ctx.get<Double>("amount")
                if(currency == null) {
                    ctx.sender().sendMessage(lang("error_invalid_currency"))
                    return@handler
                }

                AccountManagerImpl.getAccount(target.uniqueId).thenAccept { account ->
                    account.modifyBalance(currency, BigDecimal.valueOf(amount), Operation.SUBTRACT)
                    ctx.sender().sendMessage(lang("command_money_balance_get", target.name ?: "Unknown", currency.format(account.balance(currency))))
                    account.close()
                }
            })
    }
}