package tech.inudev.profundus.utils;

import org.bukkit.entity.*;

import tech.inudev.profundus.utils.DatabaseUtil.Table;
import tech.inudev.profundus.utils.PFID.Type;

import java.util.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.*;

public class User {
	private static Map<UUID,User> storedList = new HashMap<UUID,User>();
	String screenName;
	UUID uuid;
	UUID pfid;
	Instant memberSince;
	Instant lastLogin;
	String mainLanguage;
	String subLanguage;
	
	private User(Player player) {
		uuid = player.getUniqueId();
		pfid = PFID.newPFID(Type.User);
		screenName = player.getName();
		memberSince = java.time.Instant.now();
		lastLogin = java.time.Instant.now();
		mainLanguage = "";
		subLanguage = "";
	}
	private User() {};
	
	/**
	 * bukkit API の Playerから，Profundusユーザーエントリーを検索。
	 * エントリーが存在しない場合には現在のUUID,displayNameから新規作成。
	 * 第二引数にfalseを指定すると，エントリーがなくても新規作成は行わない。
	 * @param player BUKKIT API
	 * @return User Profundusユーザーエントリー
	 */
	public User getUser(Player player) {
		return getUser(player, true);
	}
	
	public User getUser(Player player,Boolean createIfNotExist) {
		UUID queryUUID = player.getUniqueId();
		User user;
		//まず高速化のためにstoredListを検索
		if(storedList.containsKey(queryUUID)) {
			user = storedList.get(queryUUID);
		//なければ，データベースを検索,storedListに追加。
		}else if(isExistsOnDB(queryUUID)) {
			//searchDBforExistingUser
			user = fetch(queryUUID);
			user.addToStoredList();
		//それでもなければ，新規作成，データベース,storedListに追加。
		}else {
			if(!createIfNotExist) {return null;}
			user = new User(player);
			user.addToDB();
			user.addToStoredList();
		}
		return user;
	}
	
	private Boolean isExistsOnDB(UUID q) {
		try {
			ResultSet rs = DatabaseUtil.selectUUID(Table.USER, "UUID", q);
			return rs.next();
		}catch(SQLException e) {
			System.out.println(e);
		}
		return false;
	}
	
	private User fetch(UUID q) {
		try {
			ResultSet rs = DatabaseUtil.selectUUID(Table.USER, "UUID", q);
			rs.first();
			User ret = new User();
			ret.uuid = q;
			ret.pfid = new UUID(rs.getLong("mostSignificantPFID"),rs.getLong("leastSignificantPFID"));
			ret.screenName = rs.getString("screenName");
			ret.memberSince = rs.getTimestamp("memberSince").toInstant();
			ret.lastLogin = rs.getTimestamp("lastLogin").toInstant();
			ret.mainLanguage = rs.getString("language1");
			ret.subLanguage = rs.getString("language2");
			return ret;
		}catch(SQLException e) {
			System.out.println(e);
		}
		return null;
	}
	
	private void addToDB() {
		DatabaseUtil.insertUserEntry(this);
	}
	
	private void updateDB() {
		DatabaseUtil.updateUserEntry(this);
	}
	
	private void removeFromDB() {
		DatabaseUtil.deleteByPFID(Table.USER, pfid);
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
	
	public void setMainLanguage(String lang) {
		mainLanguage = lang;
		updateDB();
	}
	
	public void setSubLanguage(String lang) {
		subLanguage = lang;
		updateDB();
	}
	
	// TODO //
	/*
	public Boolean isMemberOf(Group);
	public Group[] getGroups();
	public String getRoleFor(Group)
	public Boolean canEditChunk(x, z);
	public Boolean canEditChunk(location);
	*/
	
}
