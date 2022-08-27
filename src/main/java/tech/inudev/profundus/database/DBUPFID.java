package tech.inudev.profundus.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

import org.apache.commons.lang.exception.ExceptionUtils;

import tech.inudev.profundus.Profundus;
import tech.inudev.profundus.database.DatabaseUtil.Table;


public class DBUPFID extends DatabaseUtil {

	final static Table table = Table.USER;
	
	static final String createStr = """
			seqID INTEGER PRIMARY KEY AUTOINCREMENT,
			mostSignificantPFID BIGINT NOT NULL,
			leastSignificantPFID BIGINT NOT NULL,
			type VARCHAR NOT NULL,
			createdAt TIMESTAMP NOT NULL
			""";
	   /**
     * PFID関連　INSERT
     * @param pfid
     * @param type
     * @return success?
     */
    static boolean insertPFIDEntry(UUID pfid, Table type) {
    	StringBuilder sql = new StringBuilder();
    	sql.append("INSERT INTO " + Table.PFID.name());
    	sql.append("""
    			(
    			mostSignificantPFID,
    			leastSignificantPFID,
    			type,
    			createdAt
    			) VALUES(?, ?, ?, ?)
    			""");
    	Connection con = getConnection();

	    try {

	    	PreparedStatement preparedStatement = con.prepareStatement(sql.toString());

	    	preparedStatement.setLong(1, pfid.getMostSignificantBits());
	    	preparedStatement.setLong(2, pfid.getLeastSignificantBits());
	    	preparedStatement.setString(3, type.name());
	    	preparedStatement.setTimestamp(4, Timestamp.from(Instant.now()) );
	        preparedStatement.executeUpdate();
	        con.commit();
	        preparedStatement.close();
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
    
    /**
     * PFIDテーブルのみ検索高速化のために，専用のpreparedStatementを準備。
     */
    private static PreparedStatement psPFID;
    static PreparedStatement preparePsPFID() {
   		try {
   			if(psPFID==null || psPFID.isClosed()) {
   		       	Connection con = getConnection();
       			psPFID = con.prepareStatement("""
       					SELECT PFID.type, USER.screenName FROM PFID
       					LEFT JOIN USER
       					ON(
	       					PFID.mostSignificantPFID = USER.mostSignificantPFID
	       					AND
	       					PFID.leastSignificantPFID = USER.leastSignificantPFID
       					)
       					WHERE
       					PFID.mostSignificantPFID = ?
       					AND
       					PFID.leastSignificantPFID = ?
       					""");
   			}
   		} catch (SQLException e) {
	    	Profundus.getInstance().getLogger().warning(ExceptionUtils.getStackTrace(e));
   		}
   		return psPFID;
    }
    
    
}
