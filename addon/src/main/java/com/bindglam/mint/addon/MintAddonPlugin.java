package com.bindglam.mint.addon;

import com.bindglam.mint.Mint;
import com.bindglam.mint.account.operation.Operation;
import com.bindglam.mint.addon.listeners.PlayerItemListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.bukkit.CloudBukkitCapabilities;
import org.incendo.cloud.bukkit.parser.OfflinePlayerParser;
import org.incendo.cloud.bukkit.parser.PlayerParser;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.incendo.cloud.parser.standard.DoubleParser;
import org.incendo.cloud.parser.standard.IntegerParser;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.suggestion.Suggestion;
import org.incendo.cloud.suggestion.SuggestionProvider;

import java.math.BigDecimal;
import java.util.List;

public final class MintAddonPlugin extends JavaPlugin {
    public static final NamespacedKey PAPER_VALUE_KEY = new NamespacedKey("mintaddon", "paper_value");
    public static final NamespacedKey PAPER_CURRENCY_KEY = new NamespacedKey("mintaddon", "paper_currency");
    public static final BigDecimal BOAST_PRICE = BigDecimal.valueOf(10000);

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new PlayerItemListener(), this);

        registerCommands();
    }

    @Override
    public void onDisable() {
    }

    private void registerCommands() {
        var manager = new LegacyPaperCommandManager<>(
                this,
                ExecutionCoordinator.simpleCoordinator(),
                SenderMapper.identity()
        );

        if (manager.hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
            manager.registerBrigadier();
        } else if (manager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            manager.registerAsynchronousCompletions();
        }

        manager.command(manager.commandBuilder("재화")
                .required("화폐단위", StringParser.quotedStringParser(),
                        SuggestionProvider.blocking((sender, context) -> Mint.instance().currencyManager().registry().entries().stream().map(it -> Suggestion.suggestion("\"" + it.display().displayName() + "\"")).toList()))
                .literal("확인")
                .handler( ctx -> {
                    if(!(ctx.sender() instanceof Player player)) {
                        ctx.sender().sendMessage(Component.text("플레이어만 사용할 수 있는 명령어입니다.").color(NamedTextColor.RED));
                        return;
                    }
                    var currency = Mint.instance().currencyManager().registry().entries().stream().filter(it -> it.display().displayName().equals(ctx.<String>get("화폐단위"))).findAny().orElse(null);
                    if(currency == null) {
                        player.sendMessage(Component.text("알 수 없는 화폐 단위입니다.").color(NamedTextColor.RED));
                        return;
                    }
                    Mint.instance().accountManager().getAccount(player.getUniqueId()).getBalance(currency).thenAccept(balance ->
                            player.sendMessage(Component.text(player.getName() + "님의 '" + currency.display().displayName() + "' 잔액: " + currency.format(balance)).color(NamedTextColor.YELLOW)));
                }));

        manager.command(manager.commandBuilder("재화")
                .required("화폐단위", StringParser.quotedStringParser(),
                        SuggestionProvider.blocking((sender, context) -> Mint.instance().currencyManager().registry().entries().stream().map(it -> Suggestion.suggestion("\"" + it.display().displayName() + "\"")).toList()))
                .literal("보내기")
                .required("플레이어", OfflinePlayerParser.offlinePlayerParser())
                .required("금액", DoubleParser.doubleParser(1.0))
                .handler( ctx -> {
                    if(!(ctx.sender() instanceof Player player)) {
                        ctx.sender().sendMessage(Component.text("플레이어만 사용할 수 있는 명령어입니다.").color(NamedTextColor.RED));
                        return;
                    }
                    var currency = Mint.instance().currencyManager().registry().entries().stream().filter(it -> it.display().displayName().equals(ctx.<String>get("화폐단위"))).findAny().orElse(null);
                    if(currency == null) {
                        player.sendMessage(Component.text("알 수 없는 화폐 단위입니다.").color(NamedTextColor.RED));
                        return;
                    }
                    var target = ctx.<OfflinePlayer>get("플레이어");
                    var amount = BigDecimal.valueOf(ctx.<Double>get("금액"));

                    if(target.getUniqueId().equals(player.getUniqueId())) {
                        player.sendMessage(Component.text("자기자신에게 돈을 송금할 수 없습니다.").color(NamedTextColor.RED));
                        return;
                    }

                    Mint.instance().accountManager().getAccount(player.getUniqueId()).modifyBalance(Operation.WITHDRAW, currency, amount).thenAccept(result -> {
                        if(result.isSuccess()) {
                            Mint.instance().accountManager().getAccount(target.getUniqueId()).modifyBalance(Operation.DEPOSIT, currency, amount).thenRun(() -> {
                                player.sendMessage(Component.text("성공적으로 해당 플레이어에게 송금했습니다! ( 금액: " + currency.format(amount) + " )").color(NamedTextColor.GREEN));
                            });
                        } else {
                            player.sendMessage(Component.text("돈을 송금할 수 없습니다.").color(NamedTextColor.RED));
                        }
                    });
                }));

        manager.command(manager.commandBuilder("재화")
                .required("화폐단위", StringParser.quotedStringParser(),
                        SuggestionProvider.blocking((sender, context) -> Mint.instance().currencyManager().registry().entries().stream().map(it -> Suggestion.suggestion("\"" + it.display().displayName() + "\"")).toList()))
                .literal("수표발행")
                .required("금액", DoubleParser.doubleParser(1.0))
                .optional("개수", IntegerParser.integerParser(1, 64))
                .handler( ctx -> {
                    if(!(ctx.sender() instanceof Player player)) {
                        ctx.sender().sendMessage(Component.text("플레이어만 사용할 수 있는 명령어입니다.").color(NamedTextColor.RED));
                        return;
                    }
                    var currency = Mint.instance().currencyManager().registry().entries().stream().filter(it -> it.display().displayName().equals(ctx.<String>get("화폐단위"))).findAny().orElse(null);
                    if(currency == null) {
                        player.sendMessage(Component.text("알 수 없는 화폐 단위입니다.").color(NamedTextColor.RED));
                        return;
                    }
                    var value = BigDecimal.valueOf(ctx.<Double>get("금액"));
                    int amount = ctx.<Integer>getOrDefault("개수", 1);

                    Mint.instance().accountManager().getAccount(player.getUniqueId()).modifyBalance(Operation.WITHDRAW, currency, value.multiply(BigDecimal.valueOf(amount))).thenAccept(result -> {
                        if(result.isSuccess()) {
                            Bukkit.getRegionScheduler().run(this, player.getEyeLocation(), (task) -> {
                                var paperItem = player.getWorld().spawn(player.getEyeLocation(), Item.class);
                                var paperItemStack = new ItemStack(Material.PAPER, amount);
                                paperItemStack.editMeta(meta -> {
                                    meta.displayName(Component.text("수표").color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
                                    meta.lore(List.of(
                                            Component.text("금액: " + currency.format(value)).color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                                            Component.text("발급자: " + player.getName()).color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
                                    ));
                                    meta.getPersistentDataContainer().set(PAPER_CURRENCY_KEY, PersistentDataType.STRING, currency.id());
                                    meta.getPersistentDataContainer().set(PAPER_VALUE_KEY, PersistentDataType.DOUBLE, value.doubleValue());
                                });
                                paperItem.setItemStack(paperItemStack);

                                player.sendMessage(Component.text("성공적으로 수표를 발행했습니다! ( 총 금액: " + currency.format(value.multiply(BigDecimal.valueOf(amount))) + " )").color(NamedTextColor.GREEN));
                            });
                        } else {
                            player.sendMessage(Component.text("수표를 발행할 수 없습니다.").color(NamedTextColor.RED));
                        }
                    });
                }));

        manager.command(manager.commandBuilder("재화")
                .required("화폐단위", StringParser.quotedStringParser(),
                        SuggestionProvider.blocking((sender, context) -> Mint.instance().currencyManager().registry().entries().stream().map(it -> Suggestion.suggestion("\"" + it.display().displayName() + "\"")).toList()))
                .literal("자랑")
                .handler( ctx -> {
                    if(!(ctx.sender() instanceof Player player)) {
                        ctx.sender().sendMessage(Component.text("플레이어만 사용할 수 있는 명령어입니다.").color(NamedTextColor.RED));
                        return;
                    }
                    var currency = Mint.instance().currencyManager().registry().entries().stream().filter(it -> it.display().displayName().equals(ctx.<String>get("화폐단위"))).findAny().orElse(null);
                    if(currency == null) {
                        player.sendMessage(Component.text("알 수 없는 화폐 단위입니다.").color(NamedTextColor.RED));
                        return;
                    }

                    Mint.instance().accountManager().getAccount(player.getUniqueId()).modifyBalance(Operation.WITHDRAW, currency, BOAST_PRICE).thenAccept(result -> {
                        if(result.isSuccess()) {
                            Bukkit.broadcast(Component.text("[ ").color(NamedTextColor.WHITE).append(Component.text("돈 자랑").color(NamedTextColor.GOLD)).append(Component.text(" ] ").color(NamedTextColor.WHITE))
                                    .append(Component.text(player.getName() + "님은 " + currency.format(result.result()) + "이나 가지고 계십니다!").color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD)));
                        } else {
                            player.sendMessage(Component.text("잔액이 부족합니다. ( 필요 금액: " + currency.format(BOAST_PRICE) + " )").color(NamedTextColor.RED));
                        }
                    });
                }));
    }
}
