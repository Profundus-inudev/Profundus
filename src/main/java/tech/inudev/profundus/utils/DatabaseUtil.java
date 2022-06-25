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
            System.out.println("Couldn't connect DB");
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

    static Connection getConnected(){
    	try {
			if (connection == null || connection.isClosed()) {connect();}
    	} catch(SQLException e) {
    		System.out.println("getConnected:" + e);
    	}
    	return connection;
    }

    public enum Table{
	USER,	//ユーザー
	GROUP,	//グループ
	GMEMBER, //グループメンバー
	ITEM,	//アイテム
	PFID;	//Profundus ID
    }
    
    public static Table stringToEnum(String str) {
    	switch(str) {
    	case "USER":
    		return Table.USER;
    	case "GROUP":
    		return Table.GROUP;
    	case "GMEMBER":
    		return Table.GMEMBER;
    	case "ITEM":
    		return Table.ITEM;
    	case "PFID":
    		return Table.PFID;
    	}
    	return null;
    }
    public static Boolean createTable(Table tableName, Boolean dropIfExists) {
    	StringBuilder sql = new StringBuilder();
    	sql.append("CREATE TABLE IF NOT EXISTS " + tableName.toString() +" (");

    	switch(tableName) {
    	case USER:
    		sql.append("""
    				seqID INTEGER PRIMARY KEY AUTOINCREMENT,
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
    				""");
    		break;
    	case PFID:
    		sql.append("""
    				seqID INTEGER PRIMARY KEY AUTOINCREMENT,
    				mostSignificantPFID BIGINT NOT NULL,
    				leastSignificantPFID BIGINT NOT NULL,
    				type VARCHAR NOT NULL,
    				timeStamp TIMESTAMP NOT NULL
    				""");
    		break;
    	case GROUP:
    		sql.append("""
    				seqID INTEGER PRIMARY KEY AUTOINCREMENT,
    				mostSignificantPFID BIGINT NOT NULL,
    				leastSignificantPFID BIGINT NOT NULL,
    				groupName VARCHAR NOT NULL,
    				timeStamp TIMESTAMP NOT NULL
    				""");
    		break;
    	case GMEMBER:
    		sql.append("""
    				seqID INTEGER PRIMARY KEY AUTOINCREMENT,
    				mostSignificantGroupPFID BIGINT NOT NULL,
    				leastSignificantGroupPFID BIGINT NOT NULL,
    				mostSignificantUserPFID BIGINT NOT NULL,
    				leastSignificantUserPFID BIGINT NOT NULL,
    				role VARCHAR,
    				timeStamp TIMESTAMP NOT NULL
    				""");
    		break;
    	case ITEM:
    		sql.append("""
    				seqID INTEGER PRIMARY KEY AUTOINCREMENT,
    				mostSignificantOwnerPFID BIGINT NOT NULL,
    				leastSignificantOwnerPFID BIGINT NOT NULL,
    				mostSignificantPFID BIGINT NOT NULL,
    				leastSignificantPFID BIGINT NOT NULL,
     				mostSignificantUUID BIGINT NOT NULL,
    				leastSignificantUUID BIGINT NOT NULL,
       				note VARCHAR,
       				type VARCHAR,
    				timeStamp TIMESTAMP NOT NULL
    				""");
    		break;

    	}
    	sql.append(");");
    	
    	Connection con = getConnected();
	    try {
	    	if(dropIfExists) {
		    	Statement st = con.createStatement();
		    	st.executeUpdate("DROP TABLE IF EXISTS " + tableName.toString());
		    	con.commit();
		        System.out.println("DropTable  : " + tableName.toString());
		    	st.close();
	    	}

	    	Statement st = con.createStatement();
	        st.executeUpdate(sql.toString());
	        con.commit();
	        st.close();
	        System.out.println("Table created/exists : " + tableName.toString());
	        return true;
	    } catch (SQLException e) {
	    	System.out.println(e);
	        try {
	            con.rollback();
	        } catch (SQLException e2) {
                throw new RuntimeException(e2);
	        }
	        return false;
	    }
    }

    static Boolean insertItemEntry(PFItem item, UUID newPFID, String note) {
    	StringBuilder sql = new StringBuilder();
    	sql.append("INSERT INTO " + Table.ITEM.toString());
    	sql.append("""
    			(
    				mostSignificantOwnerPFID,
    				leastSignificantOwnerPFID,
    				mostSignificantPFID,
    				leastSignificantPFID,
     				mostSignificantUUID,
    				leastSignificantUUID,
       				note,
       				type,
    				timeStamp
    			) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)
    			""");
    	
    	Connection con = getConnected();
	    try {
	    	PreparedStatement preparedStatement = con.prepareStatement(sql.toString());
	    	preparedStatement.setLong(1, item.getOwner().getMostSignificantBits());
	    	preparedStatement.setLong(2, item.getOwner().getLeastSignificantBits());
	    	preparedStatement.setLong(3, newPFID.getMostSignificantBits());
	    	preparedStatement.setLong(4, newPFID.getLeastSignificantBits());
	    	preparedStatement.setLong(5, item.getUniqueId().getMostSignificantBits());
	    	preparedStatement.setLong(6, item.getUniqueId().getLeastSignificantBits());
	    	preparedStatement.setString(7, note);
	    	preparedStatement.setString(8, item.getItemStack().getType().toString());
	    	preparedStatement.setTimestamp(9, Timestamp.from(Instant.now()) );
	        int res = preparedStatement.executeUpdate();
	        con.commit();
	        preparedStatement.close();
	        return true;
	    } catch (SQLException e) {
	    	System.out.println("insertItemEntry: " + e);

	        try {
	            con.rollback();
	        } catch (SQLException e2) {
                throw new RuntimeException(e2);
	        }
	        return null;
      }
    }
    
    static Boolean insertPFIDEntry(UUID pfid, Table type) {
    	StringBuilder sql = new StringBuilder();
    	sql.append("INSERT INTO " + Table.PFID.toString());
    	sql.append("""
    			(
    			mostSignificantPFID,
    			leastSignificantPFID,
    			type,
    			timeStamp
    			) VALUES(?, ?, ?, ?)
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
	    	System.out.println("insertPFIDEntry: " + e);

	        try {
	            con.rollback();
	        } catch (SQLException e2) {
                throw new RuntimeException(e2);
	        }
	        return null;
      }
    }
    static Boolean insertUserEntry(User user) {
    	StringBuilder sql = new StringBuilder();
    	sql.append("INSERT INTO " + Table.USER.toString());
    	sql.append(" (mostSignificantPFID, leastSignificantPFID, screenName, mostSignificantUUID, leastSignificantUUID, memberSince, lastLogin) VALUES (?,?,?,?,?,?,?)");
    	Connection con = getConnected();
    	try {
	    	PreparedStatement preparedStatement = con.prepareStatement(sql.toString());
	    	preparedStatement.setLong(1, user.pfid.getMostSignificantBits());
	    	preparedStatement.setLong(2, user.pfid.getLeastSignificantBits());
	    	preparedStatement.setString(3, user.screenName);
	    	preparedStatement.setLong(4, user.uuid.getMostSignificantBits());
	    	preparedStatement.setLong(5, user.uuid.getLeastSignificantBits());
	    	preparedStatement.setTimestamp(6, Timestamp.from(user.memberSince));
	    	preparedStatement.setTimestamp(7, Timestamp.from(user.getLastLogin()));
	    	int r = preparedStatement.executeUpdate();
	        con.commit();
	    	preparedStatement.close();
	    	System.out.println(r +" rows inserted to USER");
	    	return true;
    	}catch(SQLException e) {
	    	System.out.println("insertUserEntry:" + e);

	        try {
	            con.rollback();
	        } catch (SQLException e2) {
                throw new RuntimeException(e2);
	        }
	        return false;
    	}
    }

    static java.sql.ResultSet select(Table tableName, String query){
    	StringBuilder sql = new StringBuilder();
    	sql.append("SELECT * FROM ");
    	sql.append(tableName.toString());
    	if(query!=null) {sql.append(" WHERE " + query);}
    	sql.append(";");
       	Connection con = getConnected();
	    try {
	    	PreparedStatement preparedStatement = con.prepareStatement(sql.toString());
	        ResultSet rs = preparedStatement.executeQuery();
	        preparedStatement.close();
	        return rs;
	    } catch (SQLException e) {
	    	System.out.println(e);
	        return null;
	    }
    }

    //PFIDテーブルのみ検索高速化のために，専用のpreparedStatementを準備。
    private static PreparedStatement psPFID;
    private static PreparedStatement preparePsPFID() {
   		try {
   			if(psPFID==null || psPFID.isClosed()) {
   		       	Connection con = getConnected();
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
   			System.out.println("prepare-psPFID:" + e);
   		}
   		return psPFID;
    }
    
    /**
     * UUID / PFIDでtableNameテーブルを検索
     * @param tableName Table Enum
     * @param idName UUID or PFID
     * @param uuid
     * @return java.sql.ResultSet
     */
    static java.sql.ResultSet selectUUID(Table tableName, String idName, UUID uuid){
	    try {
	    	PreparedStatement preparedStatement;
	    	if(tableName == Table.PFID) {
	    		preparedStatement = preparePsPFID();
	    	}else {
	           	Connection con = getConnected();

	        	StringBuilder sql = new StringBuilder();
	        	sql.append("SELECT * FROM ");
	        	sql.append(tableName.toString());
	        	sql.append(" WHERE mostSignificant"+idName+" = ?");
	        	sql.append(" AND leastSignificant"+idName+" = ?");
		    	preparedStatement = con.prepareStatement(sql.toString());
	    	}
	    	preparedStatement.setLong(1, uuid.getMostSignificantBits());
	    	preparedStatement.setLong(2, uuid.getLeastSignificantBits());
	    	
	        ResultSet rs = preparedStatement.executeQuery();
	        return rs;
	    } catch (SQLException e) {
	    	System.out.println("selectUUID: " + e);
	        return null;
	    }
    }

    static Boolean updateUserEntry(User user) {
      StringBuilder sql = new StringBuilder();
      sql.append("UPDATE " + Table.USER.toString());
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
	Connection con = getConnected();
	try {
		PreparedStatement preparedStatement = con.prepareStatement(sql.toString());
		preparedStatement.setString(1,user.screenName);
		preparedStatement.setTimestamp(2, Timestamp.from(user.getLastLogin()));
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
    	System.out.println("updateUserEntry:" + e);

		try {
		    con.rollback();
		} catch (SQLException e2) {
	    	System.out.println("updateUserEntry:" + e2);
		}
		return false;
	}
	}
    static Boolean deleteByPFID(Table tableName, UUID pfid) {
    	//PFIDエントリーを削除
    	if(tableName != Table.PFID) {deleteByPFID(Table.PFID,pfid);}
    	
    	Connection con = getConnected();
    	try {
    		PreparedStatement preparedStatement = con.prepareStatement("DELETE FROM " + tableName.toString() + " WHERE mostSignificantPFID = ? AND leastSignificantPFID = ?;");
    		preparedStatement.setLong(1, pfid.getMostSignificantBits());
    		preparedStatement.setLong(2, pfid.getLeastSignificantBits());
    		con.commit();
    		preparedStatement.close();
    		return true;
    	}catch(SQLException e) {
	    	System.out.println(e);

    		try {
    		    con.rollback();
    		} catch (SQLException e2) {
                throw new RuntimeException(e2);
    		}
    		return false;
    	}
    }
}
