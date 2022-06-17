package tech.inudev.profundus.utils;

import org.bukkit.entity.*;
import java.util.UUID;

public class User {
	private String screenName;
	private UUID uuid;
	private UUID pfid;
	
	User(Player player) {
		uuid = player.getUniqueId();
		pfid = PFID.getPFID(uuid);
		
	}
}
