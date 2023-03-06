package io.github.evercraftmc.roleplayutils.util.bukkit;

import java.util.function.Consumer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import io.github.evercraftmc.roleplayutils.listeners.Listener;

public class ItemStackAction extends Listener {
    private ItemStack stack;
    private Consumer<Player> action;
    private Boolean movable;

    public ItemStackAction(ItemStack stack, Consumer<Player> action, Boolean movable) {
        this.stack = stack;
        this.action = action;

        this.register();

        this.movable = movable;
    }

    public ItemStackAction(ItemStack stack, Consumer<Player> action) {
        this(stack, action, false);
    }

    @EventHandler
    public void onItemUse(PlayerInteractEvent event) {
        if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && event.getItem() != null && event.getItem().getType() == stack.getType() && event.getItem().getItemMeta().displayName().equals(stack.getItemMeta().displayName())) {
            event.setCancelled(true);

            action.accept(event.getPlayer());
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack clickedStack = event.getCurrentItem();
        if (clickedStack != null && clickedStack.equals(this.stack) && !this.movable) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getOldCursor().equals(this.stack) && !this.movable) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryMove(InventoryMoveItemEvent event) {
        if (event.getItem().equals(this.stack) && !this.movable) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        if (event.getItemDrop().getItemStack().equals(this.stack) && !this.movable) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemSwap(PlayerSwapHandItemsEvent event) {
        if ((event.getMainHandItem().equals(this.stack) || event.getOffHandItem().equals(this.stack)) && !this.movable) {
            event.setCancelled(true);
        }
    }
}