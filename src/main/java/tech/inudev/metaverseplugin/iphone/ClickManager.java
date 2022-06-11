package tech.inudev.metaverseplugin.iphone;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import tech.inudev.metaverseplugin.iphone.item.AutoMine;
import tech.inudev.metaverseplugin.iphone.item.SurvivalServer;
import tech.inudev.metaverseplugin.iphone.item.TrapTower;
import tech.inudev.metaverseplugin.iphone.item.WorldTeleport;

import java.util.HashMap;

public class ClickManager implements Listener {

    public static HashMap<Integer, String> Iphone_ItemManager = new HashMap<>();
    public static HashMap<Integer, String> WorldTeleport_ItemManager = new HashMap<>();

    // メニューのアイテムクリックを管理するクラスです。 for Java User
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null) return;
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (event.getView().getTitle().equalsIgnoreCase("§eIphone")) {
            String Type = Iphone_ItemManager.get(event.getRawSlot());
            if (Type.equalsIgnoreCase("TrapTower")) {
                new TrapTower((Player) event.getWhoClicked());

            } else if (Type.equalsIgnoreCase("AutoMine")) {
                new AutoMine((Player) event.getWhoClicked());

            } else if (Type.equalsIgnoreCase("SurvivalServer")) {
                new SurvivalServer((Player) event.getWhoClicked());

            } else if (Type.equalsIgnoreCase("WorldTeleport")) {
                new WorldTeleport((Player) event.getWhoClicked());

            }
        } else if (event.getView().getTitle().equalsIgnoreCase("§eWorld Teleport")) {

        }
    }
}