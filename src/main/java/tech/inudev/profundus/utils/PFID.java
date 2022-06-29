package tech.inudev.profundus.utils;

import java.util.UUID;

import lombok.Getter;

import java.sql.*;
import java.time.Instant;

import tech.inudev.profundus.utils.DatabaseUtil.Table;

public abstract class PFID{

	@Getter
	UUID pfid;
	Table type;
	Instant createdAt;

	
	PFID(Table t){
		pfid = newPFID(t);
		type = t;
	}
	
	PFID(){}
	
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
	
	@SuppressWarnings("unchecked")
	public static <T extends PFID> T getByPFID(UUID pfid){
		switch(getType(pfid)) {
		case USER:
			return (T) User.getByPFID(pfid);
		case GROUP:
			return (T) PFGroup.getByPFID(pfid);
		default:
			return null;
		}
	}
	
	protected abstract void addToDB();
	protected abstract void updateDB();
	protected abstract void removeFromDB();
	
}
