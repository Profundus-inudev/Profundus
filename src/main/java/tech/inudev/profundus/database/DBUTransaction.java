package tech.inudev.profundus.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;

import org.apache.commons.lang.exception.ExceptionUtils;

import tech.inudev.profundus.Profundus;
import tech.inudev.profundus.database.TransactionHandler.Payment;
import tech.inudev.profundus.database.TransactionHandler.Result;


public class DBUTransaction extends DatabaseUtil {

	static final Table table = Table.TRANSACT;


    static final String createStr = """
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
    		""";
	
	public static boolean insert(TransactionHandler th) {	
    	StringBuilder sql = new StringBuilder();
    	sql.append("INSERT INTO " + table.toString());
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
	    	ps.setLong(3, th.seller.getPfid().getMostSignificantBits());
	    	ps.setLong(4, th.seller.getPfid().getLeastSignificantBits());
	    	ps.setInt(5, th.price);
	    	ps.setBoolean(6, th.onSale);
	    	ps.setString(7, th.description);
	    	ps.setTimestamp(8, Timestamp.from(th.createdAt));
	    	
	        ps.executeUpdate();
	        con.commit();
	        ps.close();
	        return true;
	    } catch (SQLException e) {
	    	Profundus.getInstance().getLogger().warning(ExceptionUtils.getStackTrace(e));
	        try {
	            con.rollback();
	        } catch (SQLException e2) {
		    	Profundus.getInstance().getLogger().warning(ExceptionUtils.getStackTrace(e2));
	        }
	        return false;
      }
	}
	public static TransactionHandler fetch(TransactionHandler th) {
		ResultSet rs = selectUUID(table,"PFID",th.transID);
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
	    	Profundus.getInstance().getLogger().warning(ExceptionUtils.getStackTrace(e));
		}
		return th;
	}
	
	public static boolean isExist(TransactionHandler th) {
		ResultSet rs = selectUUID(table,"PFID",th.transID);
		boolean res = false;
		try{
			res = rs.next();		
			rs.close();
		}catch(SQLException e){
	    	Profundus.getInstance().getLogger().warning(ExceptionUtils.getStackTrace(e));
		}
		return res;
	}
	
	
	
	public static boolean update(TransactionHandler th) {
		return false;
	}
	
	public static boolean remove(TransactionHandler th) {
		return deleteByPFID(table,th.transID);
	}
}
