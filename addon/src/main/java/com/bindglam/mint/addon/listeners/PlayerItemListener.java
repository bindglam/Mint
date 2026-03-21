package com.bindglam.mint.addon.listeners;

import com.bindglam.mint.Mint;
import com.bindglam.mint.account.operation.Operation;
import com.bindglam.mint.addon.MintAddonPlugin;
import com.bindglam.mint.currency.Currency;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.math.BigDecimal;

public final class PlayerItemListener implements Listener {
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if(!event.getAction().isRightClick()) return;
        Player player = event.getPlayer();
        ItemStack itemStack = event.getItem();
        if(itemStack == null || !itemStack.hasItemMeta()) return;

        Double value = itemStack.getItemMeta().getPersistentDataContainer().get(MintAddonPlugin.PAPER_VALUE_KEY, PersistentDataType.DOUBLE);
        String currencyId = itemStack.getItemMeta().getPersistentDataContainer().get(MintAddonPlugin.PAPER_CURRENCY_KEY, PersistentDataType.STRING);
        if(value == null || currencyId == null) return;
        Currency currency = Mint.instance().currencyManager().registry().get(currencyId).orElseThrow();
        BigDecimal totalValue = BigDecimal.valueOf(value).multiply(BigDecimal.valueOf(itemStack.getAmount()));

        event.setCancelled(true);
        itemStack.setAmount(0);

        Mint.instance().accountManager().getAccount(player.getUniqueId()).modifyBalance(Operation.DEPOSIT, currency, totalValue).thenRun(() ->
                player.sendMessage(Component.text("수표를 사용하였습니다. ( 금액: " + currency.format(totalValue) + " )").color(NamedTextColor.YELLOW)));
    }
}
