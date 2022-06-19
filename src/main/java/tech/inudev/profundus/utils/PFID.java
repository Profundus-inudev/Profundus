package tech.inudev.profundus.utils;

import java.util.UUID;
import java.sql.*;
import tech.inudev.profundus.utils.DatabaseUtil.Table;

public class PFID{
	enum Type{
		User,
		Group,
		Land,
		Item
	}
	static UUID newPFID(PFID.Type type) {
		//とりあえず，ランダムで発行。
		UUID yourID = UUID.randomUUID();
		DatabaseUtil.insertPFIDEntry(yourID,type);
		return yourID;
	}
	static PFID.Type getType(UUID pfid){
		ResultSet rs = DatabaseUtil.selectUUID(Table.PROFUNDUS_ID, "PFID", pfid);
		try {
			rs.first();
			String result = rs.getString("type");
			switch(result) {
			case "User":
				return Type.User;
			case "Group":
				return Type.Group;
			case "Land":
				return Type.Land;
			case "Item":
				return Type.Item;
			}
		}catch(SQLException e) {
			System.out.println(e);
		}
		return null;
	}
}
