package tech.inudev.profundus.utils;

import org.bukkit.entity.*;
import java.util.*;
import java.time.*;

public class User {
	private static Map<UUID,User> storedList = new HashMap<UUID,User>();
	private String screenName;
	private UUID uuid;
	private UUID pfid;
	private Instant memberSince;
	private Instant lastLogin;
	private String mainLanguage;
	private String subLanguage;
	private Boolean isPrincipal;
	
	private User(Player player) {
		uuid = player.getUniqueId();
		pfid = PFID.getPFID(uuid);
		screenName = player.getName();
		memberSince = java.time.Instant.now();
		lastLogin = java.time.Instant.now();
	}
	
	public User getUser(Player player) {
		return getUser(player, true);
	}
	
	public User getUser(Player player,Boolean createIfNotExist) {
		UUID queryUUID = player.getUniqueId();
		User user;
		if(storedList.containsKey(queryUUID)) {
			user = storedList.get(queryUUID);
		}else if(isExistsOnDB(queryUUID)) {
			//searchDBforExistingUser
			user = fetch(queryUUID);
			user.addToStoredList();
		}else {
			if(!createIfNotExist) {return null;}
			user = new User(player);
			user.addToDB();
			user.addToStoredList();
		}
		return user;
	}
	
	private Boolean isExistsOnDB(UUID q) {
		return false;
	}
	
	private User fetch(UUID q) {
		return null;
	}
	
	private void addToDB() {
		
	}
	
	private void updateDB() {
		
	}
	
	private void removeFromDB() {
		
	}
	
	private void addToStoredList() {
		storedList.put(uuid, this);
	}
	
	void removeFromStoredList() {
		storedList.remove(uuid);
	}
	
	public void updateLastLogin() {
		lastLogin = java.time.Instant.now();
		updateDB();
	}
	
	public void setPrinicipal(Boolean set) {
		isPrincipal = set;
		updateDB();
	}
	
	public void setMainLanguage(String lang) {
		mainLanguage = lang;
		updateDB();
	}
	
	public void setSubLanguage(String lang) {
		subLanguage = lang;
		updateDB();
	}
	
}
