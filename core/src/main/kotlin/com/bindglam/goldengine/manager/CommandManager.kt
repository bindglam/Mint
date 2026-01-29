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
import org.bukkit.OfflinePlayer
import java.math.BigDecimal

object CommandManager : Managerial {
    override fun start() {
        CommandAPI.onLoad(CommandAPIBukkitConfig(GoldEngine.instance().plugin()))

        CommandAPICommand("goldengine")
            .withPermission(CommandPermission.OP)
            .withSubcommands(
                CommandAPICommand("balance")
                    .withSubcommands(
                        CommandAPICommand("get")
                            .withArguments(OfflinePlayerArgument("target"))
                            .executes(CommandExecutor { sender, args ->
                                val target = args["target"] as OfflinePlayer

                                AccountManagerImpl.getAccount(target.uniqueId).thenAccept { account ->
                                    sender.sendMessage(Component.text(account.balance().toString()))
                                    account.close()
                                }
                            }),
                        CommandAPICommand("set")
                            .withArguments(OfflinePlayerArgument("target"), DoubleArgument("amount"))
                            .executes(CommandExecutor { sender, args ->
                                val target = args["target"] as OfflinePlayer
                                val amount = args["amount"] as Double

                                AccountManagerImpl.getAccount(target.uniqueId).thenAccept { account ->
                                    account.balance(BigDecimal.valueOf(amount))
                                    sender.sendMessage(Component.text(account.balance().toString()))
                                    account.close()
                                }
                            }),
                        CommandAPICommand("add")
                            .withArguments(OfflinePlayerArgument("target"), DoubleArgument("amount"))
                            .executes(CommandExecutor { sender, args ->
                                val target = args["target"] as OfflinePlayer
                                val amount = args["amount"] as Double

                                AccountManagerImpl.getAccount(target.uniqueId).thenAccept { account ->
                                    account.modifyBalance(BigDecimal.valueOf(amount), Operation.ADD)
                                    sender.sendMessage(Component.text(account.balance().toString()))
                                    account.close()
                                }
                            }),
                        CommandAPICommand("subtract")
                            .withArguments(OfflinePlayerArgument("target"), DoubleArgument("amount"))
                            .executes(CommandExecutor { sender, args ->
                                val target = args["target"] as OfflinePlayer
                                val amount = args["amount"] as Double

                                AccountManagerImpl.getAccount(target.uniqueId).thenAccept { account ->
                                    account.modifyBalance(BigDecimal.valueOf(amount), Operation.SUBTRACT)
                                    sender.sendMessage(Component.text(account.balance().toString()))
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