package tech.inudev.metaverseplugin.scheduler;

import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;
import tech.inudev.metaverseplugin.Metaverseplugin;

public class StairSeatElimination extends BukkitRunnable {
    private final Entity seatEntity;

    public StairSeatElimination(Entity entity) {
        seatEntity = entity;
    }

    @Override
    public void run() {
        if (seatEntity == null || seatEntity.isDead()) {
            Metaverseplugin.getInstance().getLogger().info(
                    "already eliminated");
            return;
        }
        Metaverseplugin.getInstance().getLogger().info(
                "elimination!");
        seatEntity.remove();
    }
}
