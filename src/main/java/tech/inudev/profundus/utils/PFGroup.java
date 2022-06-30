package tech.inudev.profundus.utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import tech.inudev.profundus.utils.*;
import tech.inudev.profundus.utils.DatabaseUtil.Table;

/**
 * PLUGIN内でグループ情報をもつ為のクラス。
 * 土地の所有者にはPFAgent型が入るので，
 * サブクラスであるPFGroupも所有者になれる。
 * @author kidocchy
 *
 */
public class PFGroup extends PFAgent{

	List<User> members;
	
	
	PFGroup(String name) {
		super(Table.PFGROUP,name);
	}
	
	PFGroup() {}
	/**
	 * nameという名前でGROUPを作成。
	 * 名前の重複チェックはしていない。
	 * 同じ名前で複数登録すると，別IDになる。
	 * @param name 名称
	 * @return PFGroup
	 */
	public static PFGroup newGroup(String name) {
		PFGroup g = new PFGroup(name);
		g.addToDB();
		return g;
	}
/**
 * IDでグループを検索
 * @param pfid UUID型
 * @return PFGroup
 */
	public static PFGroup getByPFID(UUID pfid) {
		try {
			ResultSet rs = DatabaseUtil.selectUUID(Table.PFGROUP, "PFID", pfid);
			rs.next();
			PFGroup g = new PFGroup();
			g.pfid = pfid;
			g.screenName = rs.getString("groupName");
			g.members = g.getMembers();
			return g;
		}catch(SQLException e) {
			System.out.println("getGroup:" + e);
		}
		return null;
	}
/**
 * 名前でグループ検索。完全一致
 * @param name 検索する名前
 * @return PFGroup。該当なければnull
 */
	public static PFGroup getByName(String name) {
		try {
			ResultSet rs = DatabaseUtil.select(Table.PFGROUP, "screenName='" + name + "'");
			if(rs.next()) {
				PFGroup g = new PFGroup();
				g.pfid = new UUID(rs.getLong("mostSignificantPFID"),rs.getLong("leastSignificantPFID"));
				g.screenName = name;
				g.members = g.getMembers();
				return g;
			}
		}catch(SQLException e) {
			
		}
		return null;
	}
/**
 * 新規メンバー追加。roleに入る文字列に現在制限なし。
 * @param userPFID マイクラのUUIDではない。
 * @param role 役割を示す文字列。
 */
	public void newMember(UUID userPFID, String role) {
		User u = User.getByPFID(userPFID);
		if(u != null) {
			DatabaseUtil.insertGMemberEntry(this, u, role);
			members = getMembers();
			this.sendMessage("NEW MEMBER to " + this.screenName +": "+u.screenName + " joined!", false);
			System.out.println("NEW MEMBER to " + this.screenName +": "+u.screenName + " joined!");

		}else {
			System.out.println("No such a user:" + userPFID.toString());
		}
	}
	
	/**
	 * インスタンス変数memberの更新用。
	 * GMEMBERテーブルからメンバーを取得してUser型で格納。
	 * @return
	 */
	private LinkedList<User> getMembers() {
		LinkedList<User> r = new LinkedList<User>();
		StringBuilder sql = new StringBuilder();
		sql.append("mostSignificantPFID=" + pfid.getMostSignificantBits());
		sql.append(" AND leastSignificantPFID=" + pfid.getLeastSignificantBits());
		System.out.println(sql.toString());
		ResultSet rs = DatabaseUtil.select(Table.GMEMBER, sql.toString());
		try {
			while(rs.next()) {
				r.add(User.getByPFID(new UUID(rs.getLong("mostSignificantUserPFID"),rs.getLong("leastSignificantUserPFID"))));
			}
			return r;
		}catch(SQLException e) {
			return null;
		}
	}
/**
 * members全員にsendMessage呼び出し。
 */
	@Override
	public void sendMessage(String str, Boolean sendOnLogin) {
		members.forEach(i -> i.sendMessage(str, sendOnLogin));
	}

	@Override
	protected void addToDB() {
		DatabaseUtil.insertGroupEntry(this);
	}

	@Override
	protected void updateDB() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void removeFromDB() {
		// TODO Auto-generated method stub
		
	}

	
}
