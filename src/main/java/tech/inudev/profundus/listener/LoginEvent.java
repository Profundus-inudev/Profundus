package tech.inudev.profundus.listener;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.Listener;

import tech.inudev.profundus.utils.*;
/**
 * Login関係listener実装クラス
 * @author kidocchy
 *
 */
public class LoginEvent implements Listener {
		
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerJoinEvent(PlayerJoinEvent e) {
		User user = User.getByPlayer(e.getPlayer(), true);
		String lastLoginStr = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.ofInstant(user.getLastLogin(), ZoneId.of("Asia/Tokyo")));
		user.updateLastLogin();
		user.getPlayer().sendMessage("Profundus[LOGIN]/DATA_SOURCE:" + user.getSource());
		user.sendMessage("LastLogin:" + lastLoginStr,false);
		
		PFGroup g = PFGroup.newGroup("hogehoge");
		g.newMember(user.getPfid(), "member");
	}

}
