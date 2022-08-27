package tech.inudev.profundus.listener;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.Listener;

import tech.inudev.profundus.database.PFChunk;
import tech.inudev.profundus.utils.*;
/**
 * Login関係listener実装クラス
 * @author kidocchy
 *
 */
public class BlockEvent implements Listener {
		/**
		 */
	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockBreakEvent(BlockBreakEvent e) {
		Block tgt = e.getBlock();
		PFChunk c = PFChunk.getByChunk(tgt.getChunk());
		if(!c.canEdit(e.getPlayer())) {
			e.setCancelled(true);
		}
	}
	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockCanBuildEvent(BlockCanBuildEvent e) {
		Block tgt = e.getBlock();
		PFChunk c = PFChunk.getByChunk(tgt.getChunk());
		if(!c.canEdit(e.getPlayer())) {
			e.setBuildable(false);
		}
	}
	

}
