package tech.inudev.metaverseplugin.scheduler;

import org.bukkit.scheduler.BukkitRunnable;
import tech.inudev.metaverseplugin.utils.DatabaseUtil;

/**
 * DatabaseのConnectionが切断されないように定期的にPingを送るためのクラス
 *
 * @author tererun
 */
public class DatabasePingRunnable extends BukkitRunnable {

    @Override
    public void run() {
        DatabaseUtil.ping();
    }

}
