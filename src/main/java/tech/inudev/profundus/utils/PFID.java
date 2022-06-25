package tech.inudev.profundus.utils;

import java.util.UUID;
import java.sql.*;
import tech.inudev.profundus.utils.DatabaseUtil.Table;

public class PFID{

	static UUID newPFID(Table type) {
		//とりあえず，ランダムで発行。
		UUID yourID = UUID.randomUUID();
		DatabaseUtil.insertPFIDEntry(yourID,type);
		return yourID;
	}
	
	/**
	 * PFIDを与えると，Table Enum(USER/ITEM/GROUPなど)を返却。
	 * @param pfid
	 * @return DatabaseUtil.Table Enum
	 */
	static Table getType(UUID pfid){
		ResultSet rs = DatabaseUtil.selectUUID(Table.PFID, "PFID", pfid);
		try {
			rs.first();
			return DatabaseUtil.stringToEnum(rs.getString("type"));
		}catch(SQLException e) {
			System.out.println(e);
		}
		return null;
	}
	
}
