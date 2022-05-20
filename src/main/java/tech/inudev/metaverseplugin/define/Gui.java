package tech.inudev.metaverseplugin.define;

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

/**
 * GUIを簡単に作れるようになるクラス。
 *
 * @author kumitatepazuru
 */
public class Gui implements Listener {
    @Getter
    private final List<MenuItem> menuItems = new ArrayList<>();
    @Getter
    @Setter
    private String title;
    @Getter
    private Inventory inventory;

    /**
     * コンストラクタ
     *
     * @param title GUIのタイトル
     */
    public Gui(String title) {
        this.title = title;
    }

    /**
     * GUIにアイテムを追加する。
     *
     * @param menuItem 追加するアイテム
     */
    public void addItem(MenuItem menuItem) {
        menuItems.add(menuItem);
    }

    /**
     * GUIを開く。
     *
     * @param player GUIを開くプレイヤー
     */
    public void open(Player player) {
        inventory = Bukkit.createInventory(null, menuItems.stream().map(MenuItem::getY).mapToInt(Integer::intValue).max().orElseThrow() * 9, Component.text(title));
        for (MenuItem menuItem : menuItems) {
            inventory.setItem(menuItem.getX() - 1 + (menuItem.getY() - 1) * 9, menuItem.getIcon());
        }

        Bukkit.getPluginManager().registerEvents(this, Metaverseplugin.getInstance());
        player.openInventory(inventory);
    }

    /**
     * GUIを閉じたときにGCをするリスナー
     *
     * @param e イベント
     */
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (e.getInventory().equals(inventory)) {
            // GC
            HandlerList.unregisterAll(this);
        }
    }

    /**
     * GUIをクリックしたときにアイテムの処理をするリスナー
     *
     * @param e イベント
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().equals(inventory)) {
            e.setCancelled(true);
            // Handle click
            for (MenuItem menuItem : menuItems) {
                if (e.getSlot() == menuItem.getX() - 1 + (menuItem.getY() - 1) * 9) {
                    if (menuItem.getOnClick() != null) {
                        menuItem.getOnClick().accept(menuItem, (Player) e.getWhoClicked());
                    }
                    if (menuItem.isClose()) {
                        inventory.close();
                    }
                }
            }
        }
    }
}
