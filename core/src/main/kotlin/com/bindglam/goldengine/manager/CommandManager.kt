package com.bindglam.goldengine.manager

import com.bindglam.goldengine.GoldEngine
import com.bindglam.goldengine.account.Operation
import com.bindglam.goldengine.utils.lang
import com.bindglam.goldengine.utils.plugin
import com.bindglam.goldengine.utils.won
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

        CommandAPICommand("돈")
            .withSubcommands(
                CommandAPICommand("보내기")
                    .withArguments(OfflinePlayerArgument("받는플레이어"), DoubleArgument("돈"))
                    .executesPlayer(PlayerCommandExecutor { player, args ->
                        val target = args["받는플레이어"] as OfflinePlayer
                        val amount = BigDecimal.valueOf(args["돈"] as Double)

                        AccountManagerImpl.getAccount(player.uniqueId).thenAccept { account -> account.use {
                            AccountManagerImpl.getAccount(target.uniqueId).thenAccept { targetAccount -> targetAccount.use {
                                if(account.balance(won()) < amount) {
                                    player.sendMessage(lang("error_insufficient_money"))
                                    return@thenAccept
                                }
                                targetAccount.modifyBalance(won(), amount, Operation.ADD)
                                account.modifyBalance(won(), amount, Operation.SUBTRACT)

                                player.sendMessage(lang("command_money_send_sender", target.name ?: "Unknown", won().format(account.balance(won()))))
                                target.player?.sendMessage(lang("command_money_send_target", player.name, won().format(targetAccount.balance(won()))))
                            } }
                        } }
                    }),
                CommandAPICommand("자랑")
                    .executesPlayer(PlayerCommandExecutor { player, _ ->
                        if(!context.config().features.boast.enabled.value()) {
                            player.sendMessage(lang("error_inavailable_feature"))
                            return@PlayerCommandExecutor
                        }

                        AccountManagerImpl.getAccount(player.uniqueId).thenAccept { account -> account.use {
                            val cost = BigDecimal.valueOf(context.config().features.boast.cost.value())

                            if(account.balance(won()) < cost) {
                                player.sendMessage(lang("error_insufficient_money_with_cost", won().format(cost)))
                                return@thenAccept
                            }

                            Bukkit.broadcast(lang("command_money_boast_broadcast", player.name, won().format(account.balance(won()))))

                            account.modifyBalance(won(), cost, Operation.SUBTRACT)

                            player.sendMessage(lang("command_money_boast_sender", won().format(account.balance(won()))))
                        } }
                    })
            )
            .executesPlayer(PlayerCommandExecutor { player, _ ->
                AccountManagerImpl.getAccount(player.uniqueId).thenAccept { account ->
                    player.sendMessage(lang("command_money", won().format(account.balance(won()))))
                    account.close()
                }
            })
            .register()

        CommandAPI.onEnable()
    }

    override fun end(context: Context) {
        CommandAPI.onDisable()
    }
}