package tech.inudev.metaverseplugin.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import tech.inudev.metaverseplugin.Metaverseplugin;
import tech.inudev.metaverseplugin.define.Gui;
import tech.inudev.metaverseplugin.define.MenuItem;

public class test implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        Bukkit.getScheduler().runTaskLater(Metaverseplugin.getInstance(), () -> {
            Gui test = new Gui("test");
            test.addItem(new MenuItem("test"), 1, 2);
            test.open(e.getPlayer());
        },100);
    }
}
