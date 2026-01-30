package com.bindglam.goldengine.manager

import com.bindglam.goldengine.GoldEngine
import com.bindglam.goldengine.account.Operation
import com.bindglam.goldengine.utils.plugin
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIBukkitConfig
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.CommandPermission
import dev.jorel.commandapi.arguments.DoubleArgument
import dev.jorel.commandapi.arguments.OfflinePlayerArgument
import dev.jorel.commandapi.executors.CommandExecutor
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.OfflinePlayer
import java.math.BigDecimal
import java.text.DecimalFormat

object CommandManager : Managerial {
    private val decimalFormat = DecimalFormat("###,###")

    override fun start() {
        CommandAPI.onLoad(CommandAPIBukkitConfig(GoldEngine.instance().plugin()))

        CommandAPICommand("goldengine")
            .withPermission(CommandPermission.OP)
            .withAliases("돈")
            .withSubcommands(
                CommandAPICommand("balance")
                    .withAliases("잔액")
                    .withSubcommands(
                        CommandAPICommand("get")
                            .withAliases("확인")
                            .withArguments(OfflinePlayerArgument("target"))
                            .executes(CommandExecutor { sender, args ->
                                val target = args["target"] as OfflinePlayer

                                AccountManagerImpl.getAccount(target.uniqueId).thenAccept { account ->
                                    sender.sendMessage(Component.text("Current balance : ${decimalFormat.format(account.balance())}${GoldEngine.instance().config().economy.currencyName.value()}")
                                        .color(NamedTextColor.YELLOW))
                                    account.close()
                                }
                            }),
                        CommandAPICommand("set")
                            .withAliases("설정")
                            .withArguments(OfflinePlayerArgument("target"), DoubleArgument("amount"))
                            .executes(CommandExecutor { sender, args ->
                                val target = args["target"] as OfflinePlayer
                                val amount = args["amount"] as Double

                                AccountManagerImpl.getAccount(target.uniqueId).thenAccept { account ->
                                    account.balance(BigDecimal.valueOf(amount))
                                    sender.sendMessage(Component.text("Current balance : ${decimalFormat.format(account.balance())}${GoldEngine.instance().config().economy.currencyName.value()}")
                                        .color(NamedTextColor.YELLOW))
                                    account.close()
                                }
                            }),
                        CommandAPICommand("add")
                            .withAliases("추가")
                            .withArguments(OfflinePlayerArgument("target"), DoubleArgument("amount"))
                            .executes(CommandExecutor { sender, args ->
                                val target = args["target"] as OfflinePlayer
                                val amount = args["amount"] as Double

                                AccountManagerImpl.getAccount(target.uniqueId).thenAccept { account ->
                                    account.modifyBalance(BigDecimal.valueOf(amount), Operation.ADD)
                                    sender.sendMessage(Component.text("Current balance : ${decimalFormat.format(account.balance())}${GoldEngine.instance().config().economy.currencyName.value()}")
                                        .color(NamedTextColor.YELLOW))
                                    account.close()
                                }
                            }),
                        CommandAPICommand("subtract")
                            .withAliases("차감")
                            .withArguments(OfflinePlayerArgument("target"), DoubleArgument("amount"))
                            .executes(CommandExecutor { sender, args ->
                                val target = args["target"] as OfflinePlayer
                                val amount = args["amount"] as Double

                                AccountManagerImpl.getAccount(target.uniqueId).thenAccept { account ->
                                    account.modifyBalance(BigDecimal.valueOf(amount), Operation.SUBTRACT)
                                    sender.sendMessage(Component.text("Current balance : ${decimalFormat.format(account.balance())}${GoldEngine.instance().config().economy.currencyName.value()}")
                                        .color(NamedTextColor.YELLOW))
                                    account.close()
                                }
                            })
                    )
            )
            .register()

        CommandAPI.onEnable()
    }

    override fun end() {
        CommandAPI.onDisable()
    }
}