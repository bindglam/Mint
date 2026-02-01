package com.bindglam.goldengine.manager

import com.bindglam.goldengine.GoldEngine
import com.bindglam.goldengine.account.Operation
import com.bindglam.goldengine.utils.lang
import com.bindglam.goldengine.utils.plugin
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIBukkitConfig
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.CommandPermission
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.DoubleArgument
import dev.jorel.commandapi.arguments.OfflinePlayerArgument
import dev.jorel.commandapi.arguments.TextArgument
import dev.jorel.commandapi.executors.CommandExecutor
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import java.math.BigDecimal

object CommandManager : Managerial {
    override fun start(context: Context) {
        CommandAPI.onLoad(CommandAPIBukkitConfig(context.plugin().plugin()))

        CommandAPICommand("goldengine")
            .withPermission(CommandPermission.OP)
            .withSubcommands(
                CommandAPICommand("balance")
                    .withSubcommands(
                        CommandAPICommand("get")
                            .withArguments(OfflinePlayerArgument("target"), TextArgument("currency").replaceSuggestions(ArgumentSuggestions.strings { GoldEngine.instance().currencyManager().registry().entries().map { it.id() }.toTypedArray() }))
                            .executes(CommandExecutor { sender, args ->
                                val target = args["target"] as OfflinePlayer
                                val currency = GoldEngine.instance().currencyManager().registry().get(args["currency"] as String).orElse(null)
                                if(currency == null) {
                                    sender.sendMessage(lang("error_invalid_currency"))
                                    return@CommandExecutor
                                }

                                AccountManagerImpl.getAccount(target.uniqueId).thenAccept { account ->
                                    sender.sendMessage(lang("command_money_balance_get", target.name ?: "Unknown", currency.format(account.balance(currency))))
                                    account.close()
                                }
                            }),
                        CommandAPICommand("set")
                            .withArguments(OfflinePlayerArgument("target"), TextArgument("currency").replaceSuggestions(ArgumentSuggestions.strings { GoldEngine.instance().currencyManager().registry().entries().map { it.id() }.toTypedArray() }),
                                DoubleArgument("amount"))
                            .executes(CommandExecutor { sender, args ->
                                val target = args["target"] as OfflinePlayer
                                val currency = GoldEngine.instance().currencyManager().registry().get(args["currency"] as String).orElse(null)
                                val amount = args["amount"] as Double
                                if(currency == null) {
                                    sender.sendMessage(lang("error_invalid_currency"))
                                    return@CommandExecutor
                                }

                                AccountManagerImpl.getAccount(target.uniqueId).thenAccept { account ->
                                    account.balance(currency, BigDecimal.valueOf(amount))
                                    sender.sendMessage(lang("command_money_balance_get", target.name ?: "Unknown", currency.format(account.balance(currency))))
                                    account.close()
                                }
                            }),
                        CommandAPICommand("add")
                            .withArguments(OfflinePlayerArgument("target"), TextArgument("currency").replaceSuggestions(ArgumentSuggestions.strings { GoldEngine.instance().currencyManager().registry().entries().map { it.id() }.toTypedArray() }),
                                DoubleArgument("amount"))
                            .executes(CommandExecutor { sender, args ->
                                val target = args["target"] as OfflinePlayer
                                val currency = GoldEngine.instance().currencyManager().registry().get(args["currency"] as String).orElse(null)
                                val amount = args["amount"] as Double
                                if(currency == null) {
                                    sender.sendMessage(lang("error_invalid_currency"))
                                    return@CommandExecutor
                                }

                                AccountManagerImpl.getAccount(target.uniqueId).thenAccept { account ->
                                    account.modifyBalance(currency, BigDecimal.valueOf(amount), Operation.ADD)
                                    sender.sendMessage(lang("command_money_balance_get", target.name ?: "Unknown", currency.format(account.balance(currency))))
                                    account.close()
                                }
                            }),
                        CommandAPICommand("subtract")
                            .withArguments(OfflinePlayerArgument("target"), TextArgument("currency").replaceSuggestions(ArgumentSuggestions.strings { GoldEngine.instance().currencyManager().registry().entries().map { it.id() }.toTypedArray() }),
                                DoubleArgument("amount"))
                            .executes(CommandExecutor { sender, args ->
                                val target = args["target"] as OfflinePlayer
                                val currency = GoldEngine.instance().currencyManager().registry().get(args["currency"] as String).orElse(null)
                                val amount = args["amount"] as Double
                                if(currency == null) {
                                    sender.sendMessage(lang("error_invalid_currency"))
                                    return@CommandExecutor
                                }

                                AccountManagerImpl.getAccount(target.uniqueId).thenAccept { account ->
                                    account.modifyBalance(currency, BigDecimal.valueOf(amount), Operation.SUBTRACT)
                                    sender.sendMessage(lang("command_money_balance_get", target.name ?: "Unknown", currency.format(account.balance(currency))))
                                    account.close()
                                }
                            })
                    )
            )
            .register()

        CommandAPI.onEnable()
    }

    override fun end(context: Context) {
        CommandAPI.onDisable()
    }
}