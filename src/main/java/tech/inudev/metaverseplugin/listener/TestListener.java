package tech.inudev.metaverseplugin.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import tech.inudev.metaverseplugin.Metaverseplugin;
import tech.inudev.metaverseplugin.define.Money;

public class TestListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        final String torutoBank = "とると's bank";
//        Money.createBankAccount(torutoBank);
//        Money.createBankAccount(torutoBank);
//        Money.createBankAccount(e.getPlayer().getUniqueId().toString());
//        Money.createBankAccount(torutoBank);

        Money money = new Money(e.getPlayer().getUniqueId());
        money.add(100);
        money.remove(75);
        Metaverseplugin.getInstance().getLogger().info("" + money.getAmount());
        money.push();

        Money money2 = new Money(e.getPlayer().getUniqueId().toString());
        money2.remove(20);
        money2.add(200);
        money2.remove(15);
        Metaverseplugin.getInstance().getLogger().info("" + money2.getAmount());
        money2.push();

        Money money3 = new Money(torutoBank);
        money3.add(500);
        money3.remove(10);
        Metaverseplugin.getInstance().getLogger().info("" + money3.getAmount());
        money.push();
    }
}
