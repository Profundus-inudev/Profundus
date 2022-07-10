package tech.inudev.profundus.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;
import java.util.logging.Level;

import tech.inudev.profundus.Profundus;
import tech.inudev.profundus.utils.TransactionHandler.Payment;
import tech.inudev.profundus.utils.TransactionHandler.Result;

public class DBUTransaction extends DatabaseUtil {

	/*
	 * 
	 * PLANNING TO MOVE CREATE TABLE STATEMENT INTO THIS CLASS
	 * 
	 * 
    				seqID INTEGER PRIMARY KEY AUTOINCREMENT,
    				mostSignificantPFID BIGINT NOT NULL,
    				leastSignificantPFID BIGINT NOT NULL,
    				mostSignificantSellerPFID BIGINT NOT NULL,
    				leastSignificantSellerPFID BIGINT NOT NULL,
    				mostSignificantBuyerPFID BIGINT,
    				leastSignificantBuyerPFID BIGINT,
    				price INT NOT NULL,
    				payMethod VARCHAR,
    				onSale BIT NOT NULL,
    				description VARCHAR NOT NULL,
    				createdAt TIMESTAMP NOT NULL,
    				closedAt TIMESTAMP,
    				result VARCHAR
	*/
	
	public static Boolean insert(TransactionHandler th) {	
    	StringBuilder sql = new StringBuilder();
    	sql.append("INSERT INTO " + Table.TRANSACTION.toString());
    	sql.append("""
    			(
    				mostSignificantPFID,
    				leastSignificantPFID,
    				mostSignificantSellerPFID,
    				leastSignificantSellerPFID,
    				price,
    				onSale,
    				description,
    				createdAt
				)VALUES(?,?,?,?,?,?,?,?)
			""");
    	
    	Connection con = getConnection();

	    try {
	    	PreparedStatement ps = con.prepareStatement(sql.toString());
	    	
	    	ps.setLong(1, th.transID.getMostSignificantBits());
	    	ps.setLong(2, th.transID.getLeastSignificantBits());
	    	ps.setLong(3, th.seller.pfid.getMostSignificantBits());
	    	ps.setLong(4, th.seller.pfid.getLeastSignificantBits());
	    	ps.setInt(5, th.getPrice());
	    	ps.setBoolean(6, th.onSale);
	    	ps.setString(7, th.description);
	    	ps.setTimestamp(8, Timestamp.from(th.createdAt));
	    	
	        ps.executeUpdate();
	        con.commit();
	        ps.close();
	        return true;
	    } catch (SQLException e) {
	    	Profundus.getInstance().getLogger().log(Level.WARNING,"DBUTransaction.insert: " + e);
	        try {
	            con.rollback();
	        } catch (SQLException e2) {
		    	Profundus.getInstance().getLogger().log(Level.WARNING,"DBUTransaction.insert: " + e2);
	        }
	        return false;
      }
	}
	public static TransactionHandler fetch(TransactionHandler th) {
		ResultSet rs = selectUUID(Table.TRANSACTION,"PFID",th.transID);
		try {
			rs.next();
			th.seller = PFAgent.getByPFID(new UUID(rs.getLong("mostSignificantSellerPFID"),rs.getLong("leastSignificantSellerPFID")));


			rs.getLong("mostSignificantBuyerPFID");
			if(!rs.wasNull()) {th.buyer = PFAgent.getByPFID(new UUID(rs.getLong("mostSignificantBuyerPFID"),rs.getLong("leastSignificantBuyerPFID")));}
			
			th.setPrice(rs.getInt("price"));
			
			rs.getString("payMethod");
			if(!rs.wasNull()) {th.payMethod = Payment.valueOf(rs.getString("payMethod"));}
			
			th.onSale = rs.getBoolean("onSale");
			th.description = rs.getString("description");
			th.createdAt = rs.getTimestamp("createdAt").toInstant();
			
			rs.getTimestamp("closedAt");
			if(!rs.wasNull()) {th.closedAt = rs.getTimestamp("closedAt").toInstant();}
			
			rs.getString("result");
			if(!rs.wasNull()) {th.transactionResult = Result.valueOf(rs.getString("result"));}
			
			
			
		}catch(SQLException e) {
			Profundus.getInstance().getLogger().log(Level.WARNING, e.toString());
		}
		return th;
	}
	
	public static Boolean isExist(TransactionHandler th) {
		ResultSet rs = selectUUID(Table.TRANSACTION,"PFID",th.transID);
		Boolean res = false;
		try{
			res = rs.next();		
			rs.close();
		}catch(SQLException e){
			Profundus.getInstance().getLogger().log(Level.WARNING, e.toString());
		}
		return res;
	}
	
	
	
	public static Boolean update(TransactionHandler th) {
		return false;
	}
	
	public static Boolean remove(TransactionHandler th) {
		return deleteByPFID(Table.TRANSACTION,th.transID);
	}
}
