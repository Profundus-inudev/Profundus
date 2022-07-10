package tech.inudev.profundus.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;
import java.util.logging.Level;

import tech.inudev.profundus.Profundus;

public class DBUChunk extends DatabaseUtil {

	final static Table table = Table.CHUNK;
	
	/*
	 * 
	 * PLANNING TO MOVE CREATE TABLE STATEMENT INTO THIS CLASS
	 * 
	 * 
	seqID INTEGER PRIMARY KEY AUTOINCREMENT,
	mostSignificantWorldUUID BIGINT NOT NULL,
	leastSignificantWorldUUID BIGINT NOT NULL,
	chunkX INT NOT NULL,
	chunkZ INT NOT NULL,
	mostSignificantOwnerPFID BIGINT,
	leastSignificantOwnerPFID BIGINT,
	mostSignificantEditorPFID BIGINT,
	leastSignificantEditorPFID BIGINT,
	createdAt TIMESTAMP NOT NULL,
	saleSignX INT,
	saleSignY INT,
	saleSignZ INT,
	mostSignificantTransactionPFID BIGINT,
	leastSignificantTransactionPFID BIGINT
	*/
	
	public static Boolean insert(PFChunk c) {	
    	StringBuilder sql = new StringBuilder();
    	sql.append("INSERT INTO " + table.toString());
    	sql.append("""
    			(
				mostSignificantWorldUUID,
				leastSignificantWorldUUID,
				chunkX,
				chunkZ,
				createdAt
				)VALUES(?,?,?,?,?)
			""");
    	
    	Connection con = getConnection();

	    try {
	    	PreparedStatement ps = con.prepareStatement(sql.toString());
	    	
	    	ps.setLong(1, c.worldUUID.getMostSignificantBits());
	    	ps.setLong(2, c.worldUUID.getLeastSignificantBits());
	    	ps.setInt(3, c.chunkX);
	    	ps.setInt(4, c.chunkZ);
	    	ps.setTimestamp(4, Timestamp.from(c.timestamp) );
	    	
	        ps.executeUpdate();
	        con.commit();
	        ps.close();
	        return true;
	    } catch (SQLException e) {
	    	Profundus.getInstance().getLogger().log(Level.WARNING,"DBUChunk.insert: " + e);
	        try {
	            con.rollback();
	        } catch (SQLException e2) {
		    	Profundus.getInstance().getLogger().log(Level.WARNING,"DBUChunk.insert: " + e2);
	        }
	        return false;
      }
	}
	public static PFChunk fetch(PFChunk c) {
		ResultSet rs = select(c);
		try {
			if(rs.next()) {
			
			rs.getLong("mostSignificantOwnerPFID");
			if(!rs.wasNull()) {c.owner = PFAgent.getByPFID(new UUID(rs.getLong("mostSignificantOwnerPFID"),rs.getLong("leastSignificantOwnerPFID")));}
			
			rs.getLong("mostSignificantEditorPFID");
			if(!rs.wasNull()) {c.editor = PFAgent.getByPFID(new UUID(rs.getLong("mostSignificantEditorPFID"),rs.getLong("leastSignificantEditorPFID")));}
			
			c.timestamp =  rs.getTimestamp("createdAt").toInstant();
			
			rs.getInt("saleSignX");
			if(!rs.wasNull()) {c.saleSign = c.chunk.getBlock(rs.getInt("saleSignX"), rs.getInt("saleSignY"), rs.getInt("saleSignZ"));}
			
			//c.trans = transaction
			rs.getLong("mostSignificantTransactionPFID");
			if(!rs.wasNull()) {c.trans = TransactionHandler.getById(new UUID(rs.getLong("mostSignificantTransactionPFID"),rs.getLong("leastSignificantTransactionPFID")));}
			
			} else {
				insert(c);
			}
		}catch(SQLException e) {
			Profundus.getInstance().getLogger().log(Level.WARNING, e.toString());
		}
		return c;
	}
	
	public static Boolean isExist(PFChunk c) {
		ResultSet rs = select(c);
		Boolean res = false;
		try{
			res = rs.next();		
			rs.close();
		}catch(SQLException e){
			Profundus.getInstance().getLogger().log(Level.WARNING, e.toString());
		}
		return res;
	}
	
	public static ResultSet select(PFChunk c) {
		StringBuilder query = new StringBuilder();
		query.append("mostSignificantWorldUUID = " + c.worldUUID.getMostSignificantBits());
		query.append(" AND leastSignificantWorldUUID = " + c.worldUUID.getLeastSignificantBits());
		query.append(" AND chunkX = " + c.chunkX);
		query.append(" AND chunkZ = " + c.chunkZ);
		
		ResultSet rs = DatabaseUtil.select(table,query.toString());
		return rs;
	}
	
	public static Boolean update(PFChunk c) {
	      StringBuilder sql = new StringBuilder();
	      sql.append("UPDATE " + table.toString());
	      sql.append("""
			
			SET
			mostSignificantOwnerPFID = ?,
			leastSignificantOwnerPFID = ?,
			mostSignificantEditorPFID = ?,
			leastSignificantEditorPFID = ?,
			saleSignX = ?,
			saleSignY = ?,
			saleSignZ = ?,
			mostSignificantTransactionPFID = ?,
			leastSignificantTransactionPFID = ?
			WHERE
			mostSignificantWorldPFID = ?
			AND
			leastSignificantWorldPFID = ?
			AND
			chunkX = ?
			AND
			chunkZ = ?
			""");
	  	Connection con = getConnection();
		try {
			PreparedStatement ps = con.prepareStatement(sql.toString());
			ps.setLong(1, c.owner.pfid.getMostSignificantBits());
			ps.setLong(2, c.owner.pfid.getLeastSignificantBits());
			ps.setLong(3, c.editor.pfid.getMostSignificantBits());
			ps.setLong(4, c.editor.pfid.getLeastSignificantBits());
			ps.setInt(5, c.saleSign.getX());
			ps.setInt(6, c.saleSign.getY());
			ps.setInt(7, c.saleSign.getZ());
			ps.setLong(8, c.trans.transID.getMostSignificantBits());
			ps.setLong(9, c.trans.transID.getLeastSignificantBits());
			ps.setLong(10, c.worldUUID.getMostSignificantBits());
			ps.setLong(11, c.worldUUID.getLeastSignificantBits());
			ps.setInt(12, c.chunkX);
			ps.setInt(13, c.chunkZ);
			
			if(ps.executeUpdate() == 1) {
				con.commit();
				ps.close();
				return true;
			}else {
				con.rollback();
				ps.close();
				return false;
			}
		}catch(SQLException e) {
	    	Profundus.getInstance().getLogger().log(Level.WARNING,"updateChunk:" + e);

			try {
			    con.rollback();
			} catch (SQLException e2) {
		    	Profundus.getInstance().getLogger().log(Level.WARNING,"updateChunk:" + e2);
			}
			return false;
		}

	}
	
	public static Boolean remove(PFChunk c) {
		return false;
	}
}
