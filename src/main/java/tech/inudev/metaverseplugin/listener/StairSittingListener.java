package tech.inudev.metaverseplugin.listener;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.spigotmc.event.entity.EntityDismountEvent;
import tech.inudev.metaverseplugin.utils.StairSittingUtil;

/**
 * 階段ブロックへ座る、階段ブロックから立ち上がる処理を行うためのリスナー
 * 主に以下の場合の処理を行う
 * - プレイヤーが階段ブロックに座ろうとする場合
 * - 階段に座ったプレイヤーが自分の意志で立ち上がる場合
 * - 階段に座ったプレイヤーがサーバーから退出する場合
 * - 階段に座ったプレイヤーがデスした場合
 * - 階段ブロックが破壊された場合
 * - サーバーが停止された場合
 * - 何らかの理由で座席用エンティティが消滅した場合。
 *
 * @author toru-toruto
 */
public class StairSittingListener implements Listener {
    // 座るときのリスナー
    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent e) {
        Block stair = e.getClickedBlock();
        if (!StairSittingUtil.isAbleToSit(stair, e.getAction(), e.getPlayer())) {
            return;
        }
        e.setCancelled(true);

        StairSittingUtil.sitDown(stair, e.getPlayer());
    }


    // 何らかの理由で降りたときのリスナー
    @EventHandler
    public void onEntityDismount(EntityDismountEvent e) {
        if (!StairSittingUtil.getSeatEntityList().contains(e.getDismounted())) {
            return;
        }
        e.setCancelled(true);

        StairSittingUtil.standUp(e.getDismounted(), (Player) e.getEntity());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        // プレイヤーが退出するときにはdismount eventで乗り物のremoveができない
        // （おそらく退出したプレイヤーはテレポートできないため）のでこちらで退出前に処理
        Entity seatEntity = e.getPlayer().getVehicle();
        if (seatEntity == null || !StairSittingUtil.getSeatEntityList().contains(seatEntity)) {
            return;
        }
        StairSittingUtil.standUp(seatEntity, e.getPlayer());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Entity seatEntity = e.getPlayer().getVehicle();
        if (seatEntity == null || !StairSittingUtil.getSeatEntityList().contains(seatEntity)) {
            return;
        }
        StairSittingUtil.standUpInAccident(seatEntity);
    }


    // 階段ブロックの破壊に関するリスナー
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Entity brokenSeatEntity = StairSittingUtil.findSeatEntity(e.getBlock());
        if (brokenSeatEntity == null) {
            return;
        }
        StairSittingUtil.standUpInAccident(brokenSeatEntity);
    }

    @EventHandler
    public void onBlockDestroy(BlockDestroyEvent e) {
        Entity brokenSeatEntity = StairSittingUtil.findSeatEntity(e.getBlock());
        if (brokenSeatEntity == null) {
            return;
        }
        StairSittingUtil.standUpInAccident(brokenSeatEntity);
    }

    @EventHandler
    public void onBlockFadeEvent(BlockFadeEvent e) {
        Entity brokenSeatEntity = StairSittingUtil.findSeatEntity(e.getBlock());
        if (brokenSeatEntity == null) {
            return;
        }
        StairSittingUtil.standUpInAccident(brokenSeatEntity);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        for (Block block : e.blockList()) {
            if (!StairSittingUtil.isTargetStair(block)) {
                continue;
            }
            Entity brokenSeatEntity = StairSittingUtil.findSeatEntity(block);
            if (brokenSeatEntity == null) {
                return;
            }
            StairSittingUtil.standUpInAccident(brokenSeatEntity);
        }
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent e) {
        for (Block block : e.blockList()) {
            if (!StairSittingUtil.isTargetStair(block)) {
                continue;
            }
            Entity brokenSeatEntity = StairSittingUtil.findSeatEntity(block);
            if (brokenSeatEntity == null) {
                return;
            }
            StairSittingUtil.standUpInAccident(brokenSeatEntity);
        }
    }
}
