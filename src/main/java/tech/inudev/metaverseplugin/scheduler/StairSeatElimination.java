package tech.inudev.metaverseplugin.scheduler;

import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;
import tech.inudev.metaverseplugin.Metaverseplugin;

/**
 * バグっても座席用Entityが永続化しないように、一定時間後に消滅させるためのスケジューラ
 *
 * @author toru-toruto
 */
public class StairSeatElimination extends BukkitRunnable {
    private final Entity seatEntity;

    public StairSeatElimination(Entity entity) {
        seatEntity = entity;
    }

    @Override
    public void run() {
        if (seatEntity == null || seatEntity.isDead()) {
            return;
        }
        seatEntity.remove();
    }
}
