package com.bindglam.goldengine.test

import com.bindglam.goldengine.GoldEngine
import com.bindglam.goldengine.account.Operation
import com.bindglam.goldengine.test.utils.won
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIBukkitConfig
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.DoubleArgument
import dev.jorel.commandapi.arguments.OfflinePlayerArgument
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.plugin.java.JavaPlugin
import java.math.BigDecimal
import kotlin.use

class GoldEngineTestPlugin : JavaPlugin() {
    override fun onLoad() {
        CommandAPI.onLoad(CommandAPIBukkitConfig(this))

        registerCommands()
    }

    override fun onEnable() {
        CommandAPI.onEnable()
    }

    override fun onDisable() {
        CommandAPI.onDisable()
    }

    private fun registerCommands() {
        CommandAPICommand("돈")
            .withSubcommands(
                CommandAPICommand("보내기")
                    .withArguments(OfflinePlayerArgument("받는플레이어"), DoubleArgument("돈"))
                    .executesPlayer(PlayerCommandExecutor { player, args ->
                        val target = args["받는플레이어"] as OfflinePlayer
                        val amount = BigDecimal.valueOf(args["돈"] as Double)

                        GoldEngine.instance().accountManager().getAccount(player.uniqueId).thenAccept { account -> account.use {
                            GoldEngine.instance().accountManager().getAccount(target.uniqueId).thenAccept { targetAccount -> targetAccount.use {
                                if(account.balance(won()) < amount) {
                                    player.sendMessage(Component.text("돈이 부족합니다!").color(NamedTextColor.RED))
                                    return@thenAccept
                                }
                                targetAccount.modifyBalance(won(), amount, Operation.ADD)
                                account.modifyBalance(won(), amount, Operation.SUBTRACT)

                                player.sendMessage(Component.text("${target.name ?: "누군가"}님에게 성공적으로 ${won().format(amount)}을 보냈습니다! ( 잔액: ${won().format(account.balance(won()))} )")
                                    .color(NamedTextColor.YELLOW))
                                target.player?.sendMessage(Component.text("${player.name}님께서 ${won().format(amount)}을 보냈습니다! ( 잔액: ${won().format(targetAccount.balance(won()))}")
                                    .color(NamedTextColor.GREEN))
                            } }
                        } }
                    }),
                CommandAPICommand("자랑")
                    .executesPlayer(PlayerCommandExecutor { player, _ ->
                        GoldEngine.instance().accountManager().getAccount(player.uniqueId).thenAccept { account -> account.use {
                            val cost = BigDecimal.valueOf(50000)

                            if(account.balance(won()) < cost) {
                                player.sendMessage(Component.text("돈이 부족합니다! ( 필요 금액: ${won().format(cost)} )").color(NamedTextColor.RED))
                                return@thenAccept
                            }

                            Bukkit.broadcast(player.name().color(NamedTextColor.YELLOW).append(Component.text("님은 ").color(NamedTextColor.WHITE))
                                .append(Component.text(won().format(account.balance(won()))).color(NamedTextColor.GOLD))
                                .append(Component.text("을 가지고 있습니다!").color(NamedTextColor.WHITE)))

                            account.modifyBalance(won(), cost, Operation.SUBTRACT)

                            player.sendMessage(Component.text("성공적으로 자랑하였습니다! ( 잔액: ${won().format(account.balance(won()))} )").color(NamedTextColor.GREEN))
                        } }
                    })
            )
            .executesPlayer(PlayerCommandExecutor { player, _ ->
                GoldEngine.instance().accountManager().getAccount(player.uniqueId).thenAccept { account ->
                    player.sendMessage(Component.text("현재 잔액 : ${won().format(account.balance(won()))}")
                        .color(NamedTextColor.YELLOW))
                    account.close()
                }
            })
            .register()
    }
}