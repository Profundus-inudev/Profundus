package tech.inudev.profundus.listener;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import tech.inudev.profundus.define.Gui;
import tech.inudev.profundus.define.MenuItem;

public class test implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Gui gui = new Gui("test");
        gui.addItem(new MenuItem("test", null, null, new ItemStack(Material.BARRIER)),1,1);
        gui.open(e.getPlayer());
    }
}
