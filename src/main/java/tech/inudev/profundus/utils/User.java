package tech.inudev.profundus.utils;

import org.bukkit.entity.*;

import lombok.Getter;
import tech.inudev.profundus.utils.DatabaseUtil.Table;

import java.util.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.*;

public class User extends PFAgent{
	private static Map<UUID,User> storedList = new HashMap<UUID,User>();
	
	UUID uuid;
	String mainLanguage;
	String subLanguage;
	
	@Getter
	private Instant lastLogin;
	@Getter
	private String source;
	@Getter
	private Player player;

	
	private User(Player p) {
		super(Table.USER,p.getName()); //getNewPFID
		uuid = p.getUniqueId();
		createdAt = java.time.Instant.now();
		lastLogin = java.time.Instant.now();
		mainLanguage = "";
		subLanguage = "";
	}
	private User() {};
	
	/**
	 * Playerインスタンスから，UUIDでProfundusユーザーエントリーを検索。
	 * 高速化のために，StoredListを内部で管理している。
	 * 第二引数にtrueを指定すると，エントリーがない場合新規作成を行う。
	 * ログイン時実行はtrue,その他情報検索時などはfalseで呼び出す想定。
	 * @see getUser(Player, Booelan)
	 * @param player BUKKIT API
	 * @return User Profundus User クラスインスタンス
	 */
	public static User getByPlayer(Player p) {
		return getByPlayer(p, false);
	}
	
	/**
	 * Playerインスタンスから，UUIDでProfundusユーザーエントリーを検索。
	 * 高速化のために，StoredListを内部で管理している。
	 * 第二引数にtrueを指定すると，エントリーがない場合新規作成を行う。
	 * ログイン時実行はtrue,その他情報検索時などはfalseで呼び出す想定。
	 * @see getUser(Player, Booelan)
	 * @param player BUKKIT API
	 * @return User Profundus User クラスインスタンス
	 */
	public static User getByPlayer(Player p,Boolean createIfNotExist) {
		UUID queryUUID = p.getUniqueId();
		User u = getByUUID(queryUUID);
		if(u == null) {
			if(!createIfNotExist) {return null;}
			u = new User(p);
			u.source = "CreateNew";
			u.addToDB();
			u.addToStoredList();
		}
		u.player = p;
		return u;
	}
	
	/**
	 * これで呼び出すと，Playerインスタンスをラップしない。
	 * @see getUser(Player)
	 * @param queryUUID
	 * @param createIfNotExist
	 * @return User
	 */
	static User getByUUID(UUID queryUUID) {
		User u;
		//まず高速化のためにstoredListを検索
		if(storedList.containsKey(queryUUID)) {
			u = storedList.get(queryUUID);
			u.source = "StoredList";
		//なければ，データベースを検索,storedListに追加。
		}else if(isExistOnDB(queryUUID)) {
			u = fetchDB(queryUUID,"UUID");
			u.source = "Database";
			u.addToStoredList();

		//それでもなければ，新規作成，データベース,storedListに追加。
		}else {
			return null;
		}
		return u;
	}
	
	public static User getByPFID(UUID pfid) {
		User tmp = fetchDB(pfid,"PFID");
		return getByUUID(tmp.uuid);
	}
	
	/**
	 * UserのUUIDが存在するかDB上で検索
	 * @param q UUID
	 * @return Boolean
	 */
	private static Boolean isExistOnDB(UUID q) {
		try {
			ResultSet rs = DatabaseUtil.selectUUID(Table.USER, "UUID", q);
			if(rs == null) {return false;}
			return rs.next();
		}catch(SQLException e) {
			System.out.println(e);
		}
		return false;
	}
	
	/**
	 * DBからUserを検索，Userインスタンスに格納。
	 * 外部からは，getUserで呼び出す。
	 * @see getUser(Player)
	 * @param q UUID
	 * @return User class instance
	 */
	private static User fetchDB(UUID q, String ID) {
		try {
			ResultSet rs = DatabaseUtil.selectUUID(Table.USER, ID, q);
			rs.next();
			User ret = new User();
			ret.uuid = new UUID(rs.getLong("mostSignificantUUID"),rs.getLong("leastSignificantUUID"));
			ret.pfid = new UUID(rs.getLong("mostSignificantPFID"),rs.getLong("leastSignificantPFID"));
			ret.screenName = rs.getString("screenName");
			ret.createdAt = rs.getTimestamp("createdAt").toInstant();
			ret.lastLogin = rs.getTimestamp("lastLogin").toInstant();
			ret.mainLanguage = rs.getString("language1");
			ret.subLanguage = rs.getString("language2");
			ret.type = Table.USER;
			return ret;
		}catch(SQLException e) {
			System.out.println("fetch:" + e);
		}
		return null;
	}
	
	/**
	 * UserインスタンスをDBに登録(INSERT INTO)
	 */
	protected void addToDB() {
		DatabaseUtil.insertUserEntry(this);
	}
	
	/**
	 * UserインスタンスでDBを更新(UPDATE)
	 */
	protected void updateDB() {
		DatabaseUtil.updateUserEntry(this);
	}
	
	/**
	 * UserのPFIDでDBから削除(DELETE FROM)
	 */
	protected void removeFromDB() {
		DatabaseUtil.deleteByPFID(Table.USER, pfid);
	}
	
	/**
	 * 検索高速化のためのstoredListに登録。
	 * storedListは，サーバーリスタート/プラグインreloadでクリアされる。
	 */
	private void addToStoredList() {
		storedList.put(uuid, this);
	}
	
	/**
	 * 検索高速化のためのstoredListから削除。ログインしていないユーザーとか。
	 */
	void removeFromStoredList() {
		storedList.remove(uuid);
	}
	
	/**
	 * lastLoginを更新。ログイン時に呼ばれるべき。
	 */
	public void updateLastLogin() {
		lastLogin = java.time.Instant.now();
		updateDB();
	}
	
	/**
	 * ユーザープロフィール「mainLanguage」を更新。出番があるかは不明。
	 * @param lang
	 */
	public void setMainLanguage(String lang) {
		mainLanguage = lang;
		updateDB();
	}
	
	/**
	 * ユーザープロフィール「subLanguage」を更新。出番があるかは不明。
	 * @param lang
	 */
	public void setSubLanguage(String lang) {
		subLanguage = lang;
		updateDB();
	}

	@Override
	public void sendMessage(String str, Boolean sendOnLogin) {
		if(player != null) {
			player.sendMessage(str);
		} else {
			// TODO messageStore to send on Login
		}
		
	}
	
	// TODO //
	/*
	 * 他に登録したい項目・プロフィールとかはないですかね。
	 * public Boolean joinGroup(Group); グループに参加
	 * public Boolean isMemberOf(Group); グループのメンバーであるかを判定
	 * public Group[] getGroups(); 所属するグループを取得
	 * public String getRoleFor(Group); グループ内での役割情報を取得。使い道不明。
	 * public Boolean canEditChunk(x, z); チャンクの編集権を判定
	 * public Boolean canEditChunk(location); 同上。
	*/
	
}
