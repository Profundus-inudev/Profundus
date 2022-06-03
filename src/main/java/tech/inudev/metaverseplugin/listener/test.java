package tech.inudev.metaverseplugin.listener;

import com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import tech.inudev.metaverseplugin.define.MenuItem;
import tech.inudev.metaverseplugin.define.MultiPageGui;

import java.util.Collections;
import java.util.List;

public class test implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        MultiPageGui gui = new MultiPageGui("test");
        List<MenuItem> items = new java.util.ArrayList<>(Collections.nCopies(100, new MenuItem("test")));
        items.add(new MenuItem("最後のアイテムだよ〜ん", null,null, new ItemStack(Material.BIRCH_LOG)));
        gui.addMenuItems(items);
        gui.open(event.getPlayer());
    }
}
