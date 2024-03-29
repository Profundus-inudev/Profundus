package tech.inudev.profundus.database;

import tech.inudev.profundus.Profundus;
import tech.inudev.profundus.profundusLib.database.BaseDatabaseUtil;

import java.sql.*;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Databaseを管理するためのクラス
 * 絶対にConfigHandler初期化後に使用すること
 *
 * @author tererun
 */

public class DatabaseUtil extends BaseDatabaseUtil {
    /**
     * テーブル作成。
     * 初回ぐらいしか呼ばないと思うのでまとめました。
     *
     * @param tableName    Table enum
     * @param dropIfExists true=>存在しても再作成(初期化)
     * @return success?
     * TODO リリース時には第二引数dropIfExistsは削除。
     */
    public static boolean createTable(Table tableName, boolean dropIfExists) {
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE IF NOT EXISTS ").append(tableName.name()).append(" (");

        switch (tableName) {
            case USER -> sql.append(DBUUser.createStr);
            case PFID -> sql.append(DBUPFID.createStr);
            case MONEY -> sql.append(DBUMoney.createStr);
            case GOODS -> sql.append(DBUGoods.createStr);
        }
        sql.append(");");

        Connection con = getConnection();
        try {
            //TODO リリース時にはこのIFブロック丸々削除
            if (dropIfExists) {
                Statement st = con.createStatement();
                st.executeUpdate("DROP TABLE IF EXISTS " + tableName.name());
                con.commit();
                Profundus.getInstance().getLogger().log(Level.INFO, "DropTable  : " + tableName.name());
                st.close();
            }

            Statement st = con.createStatement();
            st.executeUpdate(sql.toString());
            con.commit();
            st.close();
            Profundus.getInstance().getLogger().log(Level.INFO, "Table created/exists : " + tableName.name());
            return true;
        } catch (SQLException e) {
            Profundus.getInstance().getLogger().log(Level.WARNING, e.toString());
            try {
                con.rollback();
            } catch (SQLException e2) {
                throw new RuntimeException(e2);
            }
            return false;
        }
    }

    /**
     * 汎用SELECT文
     *
     * @param tableName
     * @param query
     * @return success?
     */
    static ResultSet select(Table tableName, String query) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM ");
        sql.append(tableName.name());
        if (query != null) {
            sql.append(" WHERE ").append(query);
        }
        Connection con = getConnection();
        try {
            PreparedStatement preparedStatement = con.prepareStatement(sql.toString());
            return preparedStatement.executeQuery();
        } catch (SQLException e) {
            Profundus.getInstance().getLogger().log(Level.WARNING, e.toString());
            return null;
        }
    }

    /**
     * UUID / PFIDでtableNameテーブルを検索
     *
     * @param tableName Table Enum
     * @param idName    UUID or PFID
     * @param uuid
     * @return java.sql.ResultSet
     */
    static java.sql.ResultSet selectUUID(Table tableName, String idName, UUID uuid) {
        try {
            PreparedStatement preparedStatement;
            if (tableName == Table.PFID) {
                preparedStatement = DBUPFID.preparePsPFID();
            } else {
                Connection con = getConnection();

                String sql = "SELECT * FROM " +
                        tableName.name() +
                        " WHERE mostSignificant" + idName + " = ?" +
                        " AND leastSignificant" + idName + " = ?";
                preparedStatement = con.prepareStatement(sql);
            }
            preparedStatement.setLong(1, uuid.getMostSignificantBits());
            preparedStatement.setLong(2, uuid.getLeastSignificantBits());

            return preparedStatement.executeQuery();
        } catch (SQLException e) {
            Profundus.getInstance().getLogger().log(Level.WARNING, "selectUUID: " + e);
            return null;
        }
    }

    /**
     * PFIDでデータ削除 DELETE
     *
     * @param tableName
     * @param pfid
     * @return
     */
    static boolean deleteByPFID(Table tableName, UUID pfid) {
        //PFIDエントリーを削除
        if (tableName != Table.PFID) {
            deleteByPFID(Table.PFID, pfid);
        }

        Connection con = getConnection();
        try {
            PreparedStatement preparedStatement = con.prepareStatement("DELETE FROM " + tableName.name() + " WHERE mostSignificantPFID = ? AND leastSignificantPFID = ?;");
            preparedStatement.setLong(1, pfid.getMostSignificantBits());
            preparedStatement.setLong(2, pfid.getLeastSignificantBits());
            con.commit();
            preparedStatement.close();
            return true;
        } catch (SQLException e) {
            Profundus.getInstance().getLogger().log(Level.WARNING, e.toString());

            try {
                con.rollback();
            } catch (SQLException e2) {
                throw new RuntimeException(e2);
            }
            return false;
        }
    }


    /**
     * SQLデータベースで扱うテーブル一覧
     *
     * @author kidocchy
     */
    public enum Table {
        /**
         * SQLテーブル名:USER
         */
        USER,    //ユーザー
        /**
         * SQLテーブル名:PFID
         */
        PFID,    //Profundus ID
        /**
         * SQLテーブル名:money
         */
        MONEY,
        /**
         * SQLテーブル名:goods
         */
        GOODS
    }
}
