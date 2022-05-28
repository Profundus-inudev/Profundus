package tech.inudev.metaverseplugin.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import tech.inudev.metaverseplugin.define.Gui;
import tech.inudev.metaverseplugin.define.MenuItem;

public class test implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        Gui test = new Gui("test");
        test.addItem(new MenuItem("test"),1,2);
        test.open(e.getPlayer());
    }
}
