package tech.inudev.metaverseplugin.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import tech.inudev.metaverseplugin.define.Money;
import tech.inudev.metaverseplugin.utils.DatabaseUtil;

/**
 * ロールバックを確認する
 */
public class TestListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Money.createPlayerWallet(e.getPlayer().getUniqueId());
        String partnerBank = "partner bank";
        Money.createBankAccount(partnerBank);
        DatabaseUtil.updateMoneyAmount(partnerBank, 1000);

        Money m = new Money(e.getPlayer().getUniqueId());
        m.add(100);
        m.push(partnerBank);
    }
}
