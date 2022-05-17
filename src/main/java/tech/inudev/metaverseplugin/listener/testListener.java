package tech.inudev.metaverseplugin.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import tech.inudev.metaverseplugin.utils.Gui;
import tech.inudev.metaverseplugin.utils.MenuItem;

public class testListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        Gui gui = new Gui("タイトルぅぅぅぅ");
        gui.addItem(new MenuItem("ボタン",2,3));
        gui.addItem(new MenuItem("ボタン",3,5));
        gui.open(e.getPlayer());
    }
}
