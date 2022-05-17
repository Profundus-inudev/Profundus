package tech.inudev.metaverseplugin.utils;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import tech.inudev.metaverseplugin.Metaverseplugin;

import java.util.ArrayList;
import java.util.List;

public class Gui implements Listener {
    @Getter
    private final List<MenuItem> menuItems = new ArrayList<>();
    @Getter
    @Setter
    private String title;
    @Getter
    private Inventory inventory;

    public Gui(String title) {
        this.title = title;
    }

    public void add(MenuItem menuItem) {
        menuItems.add(menuItem);
    }

    public void open(Player player) {
        inventory = Bukkit.createInventory(null, menuItems.stream().map(MenuItem::getY).mapToInt(Integer::intValue).max().orElseThrow(), Component.text(title));
        for (MenuItem menuItem : menuItems) {
            inventory.setItem(menuItem.getX() + menuItem.getY() * 9, menuItem.getIcon());
        }

        Bukkit.getPluginManager().registerEvents(this, Metaverseplugin.getInstance());
        player.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e){
        if(e.getInventory().equals(inventory)){
            // GC
            HandlerList.unregisterAll(this);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        if(e.getInventory().equals(inventory)){
            // Handle click
            for(MenuItem menuItem : menuItems){
                if(e.getSlot() == menuItem.getX() + menuItem.getY() * 9){
                    menuItem.getOnClick().accept(menuItem, (Player) e.getWhoClicked());
                }
            }
        }
    }
}
