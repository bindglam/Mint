package io.github.bindglam.mint.manager

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import io.github.bindglam.mint.Mint
import io.github.bindglam.mint.account.log.TransactionLogger
import io.github.bindglam.mint.account.operation.Operation
import io.github.bindglam.mint.utils.lang
import io.github.bindglam.mint.utils.plugin
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import io.papermc.paper.command.brigadier.argument.resolvers.PlayerProfileListResolver
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import java.math.BigDecimal

object CommandManager : Managerial {
    override fun preload(context: Context) {
        context.plugin().plugin().lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS) { commands ->
            commands.registrar().register(
                Commands.literal("mint")
                    .then(Commands.literal("reload")
                        .requires { source -> (source.executor ?: source.sender).hasPermission("mint.command.reload") }
                        .executes { ctx ->
                            ctx.source.sender.sendMessage(lang("command_reload_start"))
                            Mint.instance().reload()
                            ctx.source.sender.sendMessage(lang("command_reload_end"))

                            return@executes Command.SINGLE_SUCCESS
                        })
                    .then(Commands.literal("balance")
                        .then(Commands.literal("get")
                            .then(Commands.argument("target", ArgumentTypes.playerProfiles())
                                .then(Commands.argument("currency", StringArgumentType.word())
                                    .suggests { _, builder ->
                                        CurrencyManagerImpl.registry().entries().forEach { builder.suggest(it.id()) }
                                        builder.buildFuture()
                                    }
                                    .executes { ctx ->
                                        val targetResolver = ctx.getArgument("target", PlayerProfileListResolver::class.java)
                                        val target = targetResolver.resolve(ctx.source).first()
                                        val currency = Mint.instance().currencyManager().registry().get(ctx.getArgument("currency", String::class.java)).orElse(null)

                                        if(currency == null) {
                                            ctx.source.sender.sendMessage(lang("error_invalid_currency"))
                                            return@executes Command.SINGLE_SUCCESS
                                        }

                                        val account = AccountManagerImpl.getAccount(target.id!!)
                                        account.getBalance(currency).thenAccept { balance ->
                                            ctx.source.sender.sendMessage(lang("command_money_balance_get", target.name ?: "Unknown", currency.format(balance)))
                                        }

                                        return@executes Command.SINGLE_SUCCESS
                                    })))
                        .then(Commands.literal("modify")
                            .then(Commands.argument("target", ArgumentTypes.playerProfiles())
                                .then(Commands.argument("currency", StringArgumentType.word())
                                    .suggests { _, builder ->
                                        CurrencyManagerImpl.registry().entries().forEach { builder.suggest(it.id()) }
                                        builder.buildFuture()
                                    }
                                    .then(Commands.argument("amount", DoubleArgumentType.doubleArg(0.0))
                                        .then(Commands.argument("operation", StringArgumentType.word())
                                            .suggests { _, builder ->
                                                Operation.entries.forEach { builder.suggest(it.toString()) }
                                                builder.buildFuture()
                                            }
                                            .executes { ctx ->
                                                val targetResolver = ctx.getArgument("target", PlayerProfileListResolver::class.java)
                                                val target = targetResolver.resolve(ctx.source).first()
                                                val currency = Mint.instance().currencyManager().registry().get(ctx.getArgument("currency", String::class.java)).orElse(null)
                                                val amount = ctx.getArgument("amount", Double::class.java)
                                                val operation: Operation
                                                try {
                                                    operation = Operation.valueOf(ctx.getArgument("operation", String::class.java))
                                                } catch (_: IllegalArgumentException) {
                                                    ctx.source.sender.sendMessage(lang("error_invalid_operation"))
                                                    return@executes Command.SINGLE_SUCCESS
                                                }

                                                if(currency == null) {
                                                    ctx.source.sender.sendMessage(lang("error_invalid_currency"))
                                                    return@executes Command.SINGLE_SUCCESS
                                                }

                                                val account = AccountManagerImpl.getAccount(target.id!!)
                                                account.modifyBalance(operation, currency, BigDecimal.valueOf(amount)).thenAccept { result ->
                                                    ctx.source.sender.sendMessage(lang("command_money_balance_get", target.name ?: "Unknown", currency.format(result.result())))
                                                }

                                                return@executes Command.SINGLE_SUCCESS
                                            })))))
                        .then(Commands.literal("logs")
                            .then(Commands.literal("view")
                                .then(Commands.argument("target", ArgumentTypes.playerProfiles())
                                    .then(Commands.argument("page", IntegerArgumentType.integer(1))
                                        .executes { ctx ->
                                            val targetResolver = ctx.getArgument("target", PlayerProfileListResolver::class.java)
                                            val target = targetResolver.resolve(ctx.source).first()
                                            val page = ctx.getArgument("page", Int::class.java)

                                            val account = AccountManagerImpl.getAccount(target.id!!)
                                            account.logger().retrieveLogs(TransactionLogger.Pagination.builder().page(page - 1).size(10).build()).thenAccept { logs ->
                                                logs.forEach { log ->
                                                    ctx.source.sender.sendMessage(lang("command_money_balance_logs_view", log.loggedAt(), log.operation(), log.currency().format(log.value()), log.currency().format(log.result().result()),
                                                        if(log.result().isSuccess) "<green>O" else "<red>X"))
                                                }
                                            }

                                            return@executes Command.SINGLE_SUCCESS
                                        })))
                            .then(Commands.literal("clear")
                                .then(Commands.argument("target", ArgumentTypes.playerProfiles())
                                    .executes { ctx ->
                                        val targetResolver = ctx.getArgument("target", PlayerProfileListResolver::class.java)
                                        val target = targetResolver.resolve(ctx.source).first()

                                        val account = AccountManagerImpl.getAccount(target.id!!)
                                        account.logger().clear()
                                        ctx.source.sender.sendMessage(lang("command_money_balance_logs_clear", target.name ?: "Unknown"))

                                        return@executes Command.SINGLE_SUCCESS
                                    }))))
                    .build()
            )
        }
    }
}