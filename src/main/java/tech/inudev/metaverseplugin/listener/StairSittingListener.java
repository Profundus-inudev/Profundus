package tech.inudev.metaverseplugin.listener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.Objects;

public class StairSittingListener implements Listener {
    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent e) {
        Block stair = e.getClickedBlock();
        if (stair == null
                || !isTargetStair(stair, e.getAction())
                || !hasEnoughSpace(stair)) {
            return;
        }
        e.setCancelled(true);
        LivingEntity seatEntity = createSeatEntity(e.getPlayer(), getSeatLocation(stair));
        seatEntity.addPassenger(e.getPlayer());
    }

    private boolean isTargetStair(Block stair, Action action) {
        // 指定の階段ブロックでなければ座れない
        return action == Action.RIGHT_CLICK_BLOCK
                && stair.getType() == Material.OAK_STAIRS;
    }

    private boolean hasEnoughSpace(Block stair) {
        Bisected.Half dir = ((Stairs) stair.getBlockData()).getHalf();
        if (dir == Bisected.Half.TOP) {
            // 階段ブロックが逆さならば座れない
            return false;
        }

        Location upLoc = stair.getLocation().clone().add(0, 1.0, 0);
        if (upLoc.getBlock().getType() != Material.AIR) {
            // 上が開いてなければ座れない
            return false;
        }
        return true;
    }

    private Location getSeatLocation(Block stair) {
        // 階段ブロックの中心位置へ（すこしだけ下へ）
        Vector offsetToCenter = new Vector(0.5, -0.4, 0.5);
        // 階段ブロック中心から少しだけ前方へ
        double forwardOffset = 0.2;

        Location seatLoc = stair.getLocation().clone();
        // 階段ブロックは椅子の背もたれ方向が前方
        Vector seatDir = ((Stairs) stair.getBlockData()).getFacing().getDirection().clone();
        seatDir = new Vector(-seatDir.getX(), 0, -seatDir.getZ());

        seatLoc.add(
                offsetToCenter.getX() + forwardOffset * seatDir.getX(),
                offsetToCenter.getY() + forwardOffset * seatDir.getY(),
                offsetToCenter.getZ() + forwardOffset * seatDir.getZ());
        seatLoc.setDirection(seatDir);
        return seatLoc;
    }

    private LivingEntity createSeatEntity(Player player, Location seatLoc) {
        Bat bat = (Bat) player.getWorld().spawnEntity(seatLoc, EntityType.BAT);
        Objects.requireNonNull(bat.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(1);
        bat.setSilent(true);
        bat.setInvisible(true);
        bat.setPersistent(true);
        bat.setInvulnerable(true);
        bat.setAwake(true);
        bat.setAI(false);
        return bat;
    }

    @EventHandler
    public void onEntityDismount(EntityDismountEvent e) {
        Player player = ((Player) e.getEntity());
        player.teleport(player.getLocation().add(0, 1.0, 0));
        e.getDismounted().remove();
    }
}
