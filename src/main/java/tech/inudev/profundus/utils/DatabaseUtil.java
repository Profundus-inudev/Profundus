package tech.inudev.profundus.utils;

import tech.inudev.profundus.Profundus;
import tech.inudev.profundus.config.ConfigHandler;

import java.sql.*;
import java.time.Instant;
import java.util.UUID;

/**
 * Databaseを管理するためのクラス
 * 絶対にConfigHandler初期化後に使用すること
 *
 * @author tererun
 */

public class DatabaseUtil {

    private static Connection connection;

    private static final String databaseUrl;

    static {
        ConfigHandler configHandler = Profundus.getInstance().getConfigHandler();
        if(configHandler.getDatabaseType().equals("mysql")) {
            databaseUrl = "jdbc:mysql://$address/$name?useUnicode=true&characterEncoding=utf8&autoReconnect=true&maxReconnects=10&useSSL=false"
                    .replace("$address", configHandler.getDatabaseAddress())
                    .replace("$name", configHandler.getDatabaseName());
        } else if (configHandler.getDatabaseType().equals("sqlite")) {
            databaseUrl = "jdbc:sqlite:$path".replace("$path", Profundus.getInstance().getDataFolder().getPath() + "/database.db");
        } else {
            throw new IllegalArgumentException("Invalid database type");
        }
    }

    /**
     * Databaseに接続する
     */
    public static void connect() {
        ConfigHandler configHandler = Profundus.getInstance().getConfigHandler();
        try {
            connection = DriverManager.getConnection(databaseUrl, configHandler.getDatabaseUsername(), configHandler.getDatabasePassword());
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Databaseから切断する
     */
    public static void disconnect() {
        try {
            connection.close();
            connection = null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * DatabaseにPingを送信する
     * Connectionが切断されないように送信する用
     */
    public static void ping() {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("""
            VALUES('ping', current_timestamp)
            """);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private static void createMoneyTable() {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("""
                CREATE TABLE IF NOT EXISTS 'money' (
                        'name' VARCHAR(36) NOT NULL,
                        'amount' INT NOT NULL,
                        PRIMARY KEY ('name'))
                    """);
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e2) {
                throw new RuntimeException(e2);
            }
        }
    }

    /**
     * Databaseに新たに金額データを登録する。
     * 処理が完了した場合は、トランザクションをコミットする。
     * 一方で、処理中にエラーが発生した場合は、トランザクションをロールバックする。
     *
     * @param name 金額データの名前
     */
    public static void createMoneyRecord(String name) {
        try {
            createMoneyTable();

            if (loadMoneyAmount(name) != null) {
                throw new SQLException();
            }

            PreparedStatement preparedStatement = connection.prepareStatement("""
                    INSERT INTO money (name, amount) VALUES (?, 0)
                    """);
            preparedStatement.setString(1, name);
            preparedStatement.execute();
            preparedStatement.close();

            commitTransaction();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e2) {
                throw new RuntimeException(e2);
            }
        }
    }

    /**
     * Databaseのnameに対応する金額データを取得する。
     * 処理中にエラーが発生した場合は、トランザクションをロールバックする。
     *
     * @param name Databaseの検索に使用する金額データの名前
     * @return 金額。Database上にデータが存在しなければnullを返す
     */
    public static Integer loadMoneyAmount(String name) {
        try {
            createMoneyTable();

            PreparedStatement preparedStatement = connection.prepareStatement("""
                SELECT * FROM money WHERE name=?
                """);
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();

            Integer result = resultSet.next() ? resultSet.getInt("amount") : null;
            preparedStatement.close();
            return result;
        } catch (SQLException e) {
            try {
                connection.rollback();
                return null;
            } catch (SQLException e2) {
                throw new RuntimeException(e2);
            }
        }
    }

    /**
     * 送金処理のトランザクションを実行する。
     * 処理が完了した場合は、トランザクションをコミットする。
     * 一方で、処理中にエラーが発生した場合は、トランザクションはロールバックする。
     *
     * @param selfName      自分の金額データの名前
     * @param selfAmount    自分の金額データの金額
     * @param partnerName   相手の金額データの名前
     * @param partnerAmount 相手の金額データの金額
     */
    public static void remitTransaction(
            String selfName,
            int selfAmount,
            String partnerName,
            int partnerAmount) {
        updateMoneyAmount(selfName, selfAmount);
        updateMoneyAmount(partnerName, partnerAmount);
        commitTransaction();
    }

    private static void updateMoneyAmount(String bankName, int amount) {
        try {
            createMoneyTable();

            if (loadMoneyAmount(bankName) == null) {
                throw new SQLException();
            }

            PreparedStatement preparedStatement = connection.prepareStatement("""
                    UPDATE money SET amount=? WHERE name=?
                    """);
            preparedStatement.setInt(1, amount);
            preparedStatement.setString(2, bankName);
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e2) {
                throw new RuntimeException(e2);
            }
        }
    }

    private static void commitTransaction() {
        try {
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e2) {
                throw new RuntimeException(e2);
            }
        }
    }
    
    static Connection getConnected() {
    	if (connection == null) {
    		//pingなどで接続チェック？
    		connect();
    	}
    	return connection;
    }
    
    enum Table{
    	ACCOUNT("account"),
    	USER("user"),
    	GROUP("group"),
    	PROFUNDUS_ID("pfid");
    	
    	private String name;
    	private Table(String name) {
    		this.name = name;
    	}
    	public String getTableName() {
    		return this.name;
    	}
    }
    static Boolean createTable(Table tableName, Boolean dropIfExists) {
    	StringBuffer sql = new StringBuffer();
    	if(dropIfExists) {sql.append("DROP TABLE IF EXISTS ?;");}
    	sql.append("CREATE TABLE IF NOT EXISTS ? (");
    	
    	switch(tableName) {
    	case USER:
    		sql.append("""
    				seqID INT AUTO_INCREMENT NOT NULL,
    				mostSignificantPFID BIGINT NOT NULL,
    				leastSignificantPFID BIGINT NOT NULL,
    				screenName VARCHAR NOT NULL,
    				mostSignificantUUID BIGINT NOT NULL,
    				leastSignificantUUID BIGINT NOT NULL,
    				memberSince TIMESTAMP,
    				lastLogin TIMESTAMP,
    				language1 VARCHAR,
    				language2 VARCHAR,
    				note TEXT
    				PRIMARY KEY(seqID)
    				""");
    		break;
    	case PROFUNDUS_ID:
    		sql.append("""
    				seqID INT AUTO_INCREMENT NOT NULL,
    				mostSignificantPFID BIGINT NOT NULL,
    				leastSignificantPFID BIGINT NOT NULL,
    				type VARCHAR NOT NULL,
    				timeStamp TIMESTAMP NOT NULL,
    				PRIMARY KEY(seqID)
    				""");
    		break;
    	case GROUP:
    		
    		break;
    	case ACCOUNT:
    		
    		break;
    	}
    	sql.append(");");
    	
    	Connection con = getConnected();
	    try {
	    	PreparedStatement preparedStatement = con.prepareStatement(sql.toString());
	    	preparedStatement.setString(1, tableName.getTableName());
	        preparedStatement.executeUpdate();
	        con.commit();
	        preparedStatement.close();
	        return true;
	    } catch (SQLException e) {
	        try {
	            con.rollback();
	        } catch (SQLException e2) {
	            System.out.println(e2);
	        }
	        return false;
	    }
    }
    
    static Boolean insertPFIDEntry(UUID pfid, PFID.Type type) {
    	StringBuffer sql = new StringBuffer();
    	sql.append("INSERT INTO " + Table.PROFUNDUS_ID.getTableName());
    	sql.append("""
    			(
    			mostSignificantPFID,
    			leastSignificantPFID,
    			type,
    			timeStamp
    			) VALUES(?, ?, ?, ?);
    			""");
    	Connection con = getConnected();
	    try {
	    	PreparedStatement preparedStatement = con.prepareStatement(sql.toString());
	    	preparedStatement.setLong(1, pfid.getMostSignificantBits());
	    	preparedStatement.setLong(2, pfid.getLeastSignificantBits());
	    	preparedStatement.setString(3, type.toString());
	    	preparedStatement.setTimestamp(4, Timestamp.from(Instant.now()) );
	        int res = preparedStatement.executeUpdate();
	        con.commit();
	        preparedStatement.close();
	        return true;
	    } catch (SQLException e) {
	        try {
	            con.rollback();
	        } catch (SQLException e2) {
	            System.out.println(e2);
	        }
	        return null;
	    }    	

    }
 
    static Boolean insertUserEntry(User user) {
    	StringBuffer sql = new StringBuffer();
    	sql.append("INSERT INTO " + Table.USER.getTableName());
    	sql.append("""
    			mostSignificantPFID,
    			leastSignificantPFID,
    			screenName,
    			mostSignificantUUID,
    			leastSignificantUUID,
    			memberSince,
    			lastLogin
    			) VALUES(?,?,?,?,?,?,?);
    			""");
    	Connection con = getConnected();
    	try {
	    	PreparedStatement preparedStatement = con.prepareStatement(sql.toString());
	    	preparedStatement.setLong(1, user.pfid.getMostSignificantBits());
	    	preparedStatement.setLong(2, user.pfid.getLeastSignificantBits());
	    	preparedStatement.setString(3, user.screenName);
	    	preparedStatement.setLong(4, user.uuid.getMostSignificantBits());
	    	preparedStatement.setLong(5, user.uuid.getLeastSignificantBits());
	    	preparedStatement.setTimestamp(6, Timestamp.from(user.memberSince));
	    	preparedStatement.setTimestamp(7, Timestamp.from(user.lastLogin));
	    	preparedStatement.executeUpdate();
	        con.commit();
	    	preparedStatement.close();
	    	return true;
    	}catch(SQLException e) {
	        try {
	            con.rollback();
	        } catch (SQLException e2) {
	            System.out.println(e2);
	        }
	        return false;
    	}	
    }
    
    static java.sql.ResultSet select(Table tableName, String query){
    	StringBuffer sql = new StringBuffer();
    	sql.append("SELECT * FROM ?");
    	if(query!=null) {sql.append(" WHERE " + query);}
    	sql.append(";");
       	Connection con = getConnected();
	    try {
	    	PreparedStatement preparedStatement = con.prepareStatement(sql.toString());
	    	preparedStatement.setString(1, tableName.getTableName());
	        ResultSet rs = preparedStatement.executeQuery();
	        preparedStatement.close();
	        return rs;
	    } catch (SQLException e) {
	        try {
	            con.rollback();
	        } catch (SQLException e2) {
	            System.out.println(e2);
	        }
	        return null;
	    }    	
    }
    
    static java.sql.ResultSet selectUUID(Table tableName,String idName, UUID uuid){
    	StringBuffer sql = new StringBuffer();
    	sql.append("SELECT * FROM ?");
    	sql.append(" WHERE mostSignificant"+idName+" = ? AND ");
    	sql.append("leastSignificant"+idName+" = ?");
    	sql.append(";");
       	Connection con = getConnected();
	    try {
	    	PreparedStatement preparedStatement = con.prepareStatement(sql.toString());
	    	preparedStatement.setString(1, tableName.getTableName());
	    	preparedStatement.setLong(2, uuid.getMostSignificantBits());
	    	preparedStatement.setLong(3, uuid.getLeastSignificantBits());
	        ResultSet rs = preparedStatement.executeQuery();
	        preparedStatement.close();
	        return rs;
	    } catch (SQLException e) {
	        try {
	            con.rollback();
	        } catch (SQLException e2) {
	            System.out.println(e2);
	        }
	        return null;
	    }    	
    }
    
    static Boolean updateUserEntry(User user) {
    	StringBuffer sql = new StringBuffer();
    	sql.append("UPDATE " + Table.USER.getTableName());
    	sql.append("""
    			SET 
    			screenName = ?,
    			lastLogin = ?,
    			language1 = ?,
    			language2 = ?
    			WHERE 
    			mostSignificantPFID = ?,
    			leastSignificantPFID = ?;
    			""");
    	Connection con = getConnected();
    	try {
	    	PreparedStatement preparedStatement = con.prepareStatement(sql.toString());
	    	preparedStatement.setString(1,user.screenName);
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
	        try {
	            con.rollback();
	        } catch (SQLException e2) {
	            System.out.println(e2);
	        }
	        return false;
    	}	
    }
    static Boolean deleteByPFID(Table tableName, UUID pfid) {
    	if(tableName != Table.PROFUNDUS_ID) {deleteByPFID(Table.PROFUNDUS_ID,pfid);}
    	Connection con = getConnected();
    	try {
    		PreparedStatement preparedStatement = con.prepareStatement("DELETE FROM ? WHERE mostSignificantPFID = ? AND leastSignificantPFID = ?;");
    		preparedStatement.setString(1, tableName.getTableName());
    		preparedStatement.setLong(2, pfid.getMostSignificantBits());
    		preparedStatement.setLong(3, pfid.getLeastSignificantBits());
    		con.commit();
    		preparedStatement.close();
    		return true;
    	}catch(SQLException e) {
    		con.rollback();
    		System.out.println(e);
    		return false;
    	}
    }
}
