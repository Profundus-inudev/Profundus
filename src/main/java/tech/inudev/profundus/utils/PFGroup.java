package tech.inudev.profundus.utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import tech.inudev.profundus.utils.*;
import tech.inudev.profundus.utils.DatabaseUtil.Table;

public class PFGroup extends PFAgent{

	LinkedList<User> members;
	
	
	PFGroup(String name) {
		super(Table.GROUP,name);
	}
	
	PFGroup() {}
	
	public static PFGroup newGroup(String name) {
		PFGroup g = new PFGroup(name);
		g.addToDB();
		return g;
	}

	public static PFGroup getByPFID(UUID pfid) {
		try {
			ResultSet rs = DatabaseUtil.selectUUID(Table.GROUP, "PFID", pfid);
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

	public static PFGroup getByName(String name) {
		try {
			ResultSet rs = DatabaseUtil.select(Table.GROUP, "screenName='" + name + "'");
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

	public void newMember(UUID userPFID, String role) {
		User u = User.getByPFID(userPFID);
		if(u != null) {
			DatabaseUtil.insertGMemberEntry(this, u, role);
			members = getMembers();
			this.sendMessage("NEW MEMBER to " + this.screenName +": "+u.screenName + " joined!", false);
		}else {
			System.out.println("No such a user:" + userPFID.toString());
		}
	}
	private LinkedList<User> getMembers() {
		LinkedList<User> r = new LinkedList<User>();
		StringBuilder sql = new StringBuilder();
		sql.append("mostSignificantGroupPFID=" + pfid.getMostSignificantBits());
		sql.append("leastSignificantGroupPFID=" + pfid.getLeastSignificantBits());
		ResultSet rs = DatabaseUtil.select(Table.GMEMBER, sql.toString());
		try {
			while(rs.next()) {
				r.add(User.getByPFID(new UUID(rs.getLong("mostSignificantUserPFID"),rs.getLong("leastSignificantUserPFID"))));
			}
		}catch(SQLException e) {
			
		}
		return r;
	}

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