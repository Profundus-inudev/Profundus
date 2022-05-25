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
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK
                || stair == null
                || stair.getType() != Material.OAK_STAIRS) {
            // 指定の階段ブロックでなければ座れない
            return;
        }

        Bisected.Half dir =  ((Stairs) stair.getBlockData()).getHalf();
        if (dir == Bisected.Half.TOP) {
            // 階段ブロックが逆さならば座れない
            return;
        }

        Location upLoc = stair.getLocation().clone().add(0, 1.0, 0);
        if (upLoc.getBlock().getType() != Material.AIR) {
            // 上が開いてなければ座れない
            return;
        }

        e.setCancelled(true);

        // 階段ブロックの中心位置へ（すこしだけ下へ）
        Vector offsetToCenter = new Vector(0.5, -0.4, 0.5);
        // 階段ブロック中心から少しだけ前方へ
        double forwardOffset = 0.2;
        Vector stairDir = ((Stairs) stair.getBlockData()).getFacing().getDirection();

        Location seatEntityLoc = stair.getLocation().clone();

//        e.getPlayer().sendMessage(Component.text("($x, $y, $z)"
//                .replace("$x", "" + (offsetToCenter.getX() - forwardOffset * stairDir.getX()))
//                .replace("$y", "" + (offsetToCenter.getY() - forwardOffset * stairDir.getY()))
//                .replace("$z", "" + (offsetToCenter.getZ() - forwardOffset * stairDir.getZ()))));

        seatEntityLoc.add(
                offsetToCenter.getX() - forwardOffset * stairDir.getX(),
                offsetToCenter.getY() - forwardOffset * stairDir.getY(),
                offsetToCenter.getZ() - forwardOffset * stairDir.getZ());

        Bat bat = (Bat) e.getPlayer().getWorld().spawnEntity(seatEntityLoc, EntityType.BAT);
        Objects.requireNonNull(bat.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(1);
        bat.setSilent(true);
        bat.setInvisible(true);
        bat.setPersistent(true);
        bat.setInvulnerable(true);
        bat.setAwake(true);
        bat.setAI(false);
        bat.teleport(bat.getLocation().setDirection(
                new Vector(-stairDir.getX(), 0, -stairDir.getZ())));

        bat.addPassenger(e.getPlayer());
    }

    @EventHandler
    public void onEntityDismount(EntityDismountEvent e) {
        Player player = ((Player) e.getEntity());
        player.teleport(player.getLocation().add(0, 1.0, 0));
        e.getDismounted().remove();
    }
}
