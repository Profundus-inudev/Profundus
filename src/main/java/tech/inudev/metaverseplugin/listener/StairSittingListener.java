package tech.inudev.metaverseplugin.listener;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
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
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityDismountEvent;
import tech.inudev.metaverseplugin.Metaverseplugin;

import java.util.*;

public class StairSittingListener implements Listener {
    private static final List<Entity> seatEntityList = new ArrayList<>();
    private static final HashMap<UUID, Block> entityToBlockMap = new HashMap<>();
    private static final String metadataKey = "SEAT_ENTITY_UUID";

    public static void removeSeatEntities() {
        for (Entity entity : seatEntityList) {
            for (Entity passenger : entity.getPassengers()) {
                passenger.teleport(passenger.getLocation().add(0, 1.0, 0));
            }
            entityToBlockMap.get(entity.getUniqueId()).removeMetadata(
                    StairSittingListener.metadataKey, Metaverseplugin.getInstance());
            entity.remove();
        }
    }


    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent e) {
        logging("" + e.getEventName());

        Block stair = e.getClickedBlock();

        if (stair == null
                || e.getAction() != Action.RIGHT_CLICK_BLOCK
                || e.getPlayer().isSneaking()
                || !isTargetStair(stair)
                || !hasEnoughSpace(stair)
                || !isNoOneSitting(stair)) {
            logging(StairSittingListener.seatEntityList.size() + ", not mount");
            return;
        }
        e.setCancelled(true);


        LivingEntity seatEntity = createSeatEntity(e.getPlayer(), getSeatLocation(stair));
        seatEntity.addPassenger(e.getPlayer());
        StairSittingListener.seatEntityList.add(seatEntity);
        StairSittingListener.entityToBlockMap.put(seatEntity.getUniqueId(), stair);

        stair.setMetadata(metadataKey, new FixedMetadataValue(
                Metaverseplugin.getInstance(),
                seatEntity.getUniqueId().toString()));

        logging(StairSittingListener.seatEntityList.size() + ", mount");
    }

    private boolean isTargetStair(Block stair) {
        // 指定の階段ブロックでなければ座れない
        return stair.getType() == Material.OAK_STAIRS;
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

    private boolean isNoOneSitting(Block stair) {
        return findSeatEntity(stair) == null;
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
        bat.setInvisible(false);
        bat.setPersistent(true);
        bat.setInvulnerable(true);
        bat.setAwake(true);
        bat.setAI(false);
        return bat;
    }

    @EventHandler
    public void onEntityDismount(EntityDismountEvent e) {
        logging("" + e.getEventName());

        if (!StairSittingListener.seatEntityList.contains(e.getDismounted())) {
            logging("dismount, return");
            return;
        }
        e.setCancelled(true);

        Player player = ((Player) e.getEntity());
        player.teleport(player.getLocation().add(0, 1.0, 0));
        removeSeat(e.getDismounted());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        // プレイヤーが退出するときにはdismount eventで乗り物のremoveができない
        // （おそらく退出したプレイヤーはテレポートできないため）のでこちらで退出前に処理
        logging("" + e.getEventName());
        Entity seatEntity = e.getPlayer().getVehicle();
        if (seatEntity == null || !StairSittingListener.seatEntityList.contains(seatEntity)) {
            logging("quit, return");
            return;
        }
        e.getPlayer().teleport(e.getPlayer().getLocation().add(0, 1.0, 0));
        removeSeat(seatEntity);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
//        logging("" + seatEntity.getUniqueId());
        logging("" + e.getEventName());

        Entity seatEntity = e.getPlayer().getVehicle();
        if (seatEntity == null || !StairSittingListener.seatEntityList.contains(seatEntity)) {
            logging(StairSittingListener.seatEntityList.size() + ", not unmount");
            return;
        }
        removeSeat(seatEntity);
    }


    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        logging("" + e.getEventName());
        Entity brokenSeatEntity = findSeatEntity(e.getBlock());
        if (brokenSeatEntity != null) {
            removeSeat(brokenSeatEntity);
        }
    }

    @EventHandler
    public void onBlockDestroy(BlockDestroyEvent e) {
        logging("" + e.getEventName());
        Entity brokenSeatEntity = findSeatEntity(e.getBlock());
        if (brokenSeatEntity != null) {
            removeSeat(brokenSeatEntity);
        }
    }

    @EventHandler
    public void onBlockFadeEvent(BlockFadeEvent e) {
        logging("" + e.getEventName());
        Entity brokenSeatEntity = findSeatEntity(e.getBlock());
        if (brokenSeatEntity != null) {
            removeSeat(brokenSeatEntity);
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        logging("" + e.getEventName());
        for (Block block : e.blockList()) {
            if (!isTargetStair(block)) {
                continue;
            }
            Entity brokenSeatEntity = findSeatEntity(block);
            if (brokenSeatEntity != null) {
                removeSeat(brokenSeatEntity);
            }
        }
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent e) {
        logging("" + e.getEventName());
        for (Block block : e.blockList()) {
            if (!isTargetStair(block)) {
                continue;
            }
            Entity brokenSeatEntity = findSeatEntity(block);
            if (brokenSeatEntity != null) {
                removeSeat(brokenSeatEntity);
            }
        }
    }

    private Entity findSeatEntity(Block block) {
        String seatEntityUUID = "";
        for (MetadataValue v : block.getMetadata(metadataKey)) {
            if (v.getOwningPlugin() == null) {
                return null;
            }
            if (v.getOwningPlugin().getName().equals(Metaverseplugin.getInstance().getName())) {
                seatEntityUUID = v.asString();
                break;
            }
        }
        if (seatEntityUUID.isEmpty()) {
            logging("seatEntityUUID is empty");
            return null;
        }
        logging("seatEntityUUID: " + seatEntityUUID);

        String finalSeatEntityUUID = seatEntityUUID;
        Optional<Entity> entityOpt = seatEntityList.stream()
                .filter(ent -> ent.getUniqueId().toString().equalsIgnoreCase(finalSeatEntityUUID))
                .findFirst();
        return entityOpt.orElse(null);
    }

    private void removeSeat(Entity seatEntity) {
        logging("remove");
        Block stair = StairSittingListener.entityToBlockMap.get(seatEntity.getUniqueId());
        if (stair == null) {
            logging("stair is null");
            return;
        }
//        logging("" + findBrokenSeatEntity(stair).getUniqueId().toString());
        stair.removeMetadata(StairSittingListener.metadataKey, Metaverseplugin.getInstance());
        StairSittingListener.entityToBlockMap.remove(seatEntity.getUniqueId());
        StairSittingListener.seatEntityList.remove(seatEntity);
        seatEntity.remove();
        logging(StairSittingListener.seatEntityList.size() + ", " + ", unmount");
    }

    private void logging(String msg) {
        Metaverseplugin.getInstance().getLogger().info(msg);
    }
}
