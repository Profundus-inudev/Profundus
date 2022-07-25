package tech.inudev.profundus.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.commons.lang.exception.ExceptionUtils;

import tech.inudev.profundus.Profundus;

/**
 * UserテーブルをいじるためのAPI的存在
 * @author kidocchy
 *
 */
public class DBUUser extends DatabaseUtil {

	final static Table table = Table.USER;
	
	static final String createStr = """
			seqID INTEGER PRIMARY KEY AUTOINCREMENT,
			mostSignificantPFID BIGINT NOT NULL,
			leastSignificantPFID BIGINT NOT NULL,
			screenName VARCHAR NOT NULL,
			mostSignificantUUID BIGINT NOT NULL,
			leastSignificantUUID BIGINT NOT NULL,
			createdAt TIMESTAMP,
			lastLogin TIMESTAMP,
			language1 VARCHAR,
			language2 VARCHAR,
			note TEXT
			""";

	   /**
     * User関連　INSERT
     * @param user
     * @return success?
     */
    static boolean insert(User user) {
    	StringBuilder sql = new StringBuilder();
    	sql.append("INSERT INTO " + table.name());
    	sql.append(" (mostSignificantPFID, leastSignificantPFID, screenName, mostSignificantUUID, leastSignificantUUID, createdAt, lastLogin) VALUES (?,?,?,?,?,?,?)");
    	Connection con = getConnection();
    	try {
	    	PreparedStatement preparedStatement = con.prepareStatement(sql.toString());
	    	preparedStatement.setLong(1, user.pfid.getMostSignificantBits());
	    	preparedStatement.setLong(2, user.pfid.getLeastSignificantBits());
	    	preparedStatement.setString(3, user.getScreenName());
	    	preparedStatement.setLong(4, user.uuid.getMostSignificantBits());
	    	preparedStatement.setLong(5, user.uuid.getLeastSignificantBits());
	    	preparedStatement.setTimestamp(6, Timestamp.from(user.createdAt));
	    	preparedStatement.setTimestamp(7, Timestamp.from(user.lastLogin));
	    	int r = preparedStatement.executeUpdate();
	        con.commit();
	    	preparedStatement.close();
	    	Profundus.getInstance().getLogger().info(r +" rows inserted to USER");
	    	return true;
    	}catch(SQLException e) {
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
     * USER関連　UPDATE
     * @param user
     * @return
     */
    static boolean update(User user) {
      StringBuilder sql = new StringBuilder();
      sql.append("UPDATE " + table.name());
      sql.append("""
		
		SET
		screenName = ?,
		lastLogin = ?,
		language1 = ?,
		language2 = ?
		WHERE
		mostSignificantPFID = ?
		AND
		leastSignificantPFID = ?
		""");
	Connection con = getConnection();
	try {
		PreparedStatement preparedStatement = con.prepareStatement(sql.toString());
		preparedStatement.setString(1,user.getScreenName());
		preparedStatement.setTimestamp(2, Timestamp.from(user.lastLogin));
		preparedStatement.setString(3, user.mainLanguage);
		preparedStatement.setString(4, user.subLanguage);
		preparedStatement.setLong(5, user.pfid.getMostSignificantBits());
		preparedStatement.setLong(6, user.pfid.getLeastSignificantBits());
		if(preparedStatement.executeUpdate() == 1) {
			con.commit();
			preparedStatement.close();
			return true;
		}else {
			con.rollback();
			preparedStatement.close();
			return false;
		}
	}catch(SQLException e) {
    	Profundus.getInstance().getLogger().warning(ExceptionUtils.getStackTrace(e));

		try {
		    con.rollback();
		} catch (SQLException e2) {
	    	Profundus.getInstance().getLogger().warning(ExceptionUtils.getStackTrace(e2));
		}
		return false;
	}
	}
    
}