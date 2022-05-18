package tech.inudev.metaverseplugin.utils;

import tech.inudev.metaverseplugin.Metaverseplugin;
import tech.inudev.metaverseplugin.config.ConfigHandler;

import java.sql.*;

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
        ConfigHandler configHandler = Metaverseplugin.getInstance().getConfigHandler();
        if(configHandler.getDatabaseType().equals("mysql")) {
            databaseUrl = "jdbc:mysql://$address/$name?useUnicode=true&characterEncoding=utf8&autoReconnect=true&maxReconnects=10&useSSL=false"
                    .replace("$address", configHandler.getDatabaseAddress())
                    .replace("$name", configHandler.getDatabaseName());
        } else if (configHandler.getDatabaseType().equals("sqlite")) {
            databaseUrl = "jdbc:sqlite:$path".replace("$path", Metaverseplugin.getInstance().getDataFolder().getPath() + "/database.db");
        } else {
            throw new IllegalArgumentException("Invalid database type");
        }
    }

    /**
     * Databaseに接続する
     */
    public static void connect() {
        ConfigHandler configHandler = Metaverseplugin.getInstance().getConfigHandler();
        try {
            connection = DriverManager.getConnection(databaseUrl, configHandler.getDatabaseUsername(), configHandler.getDatabasePassword());
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
            throw new RuntimeException(e);
        }
    }

    /**
     * Databaseのnameに対応する金額データを取得する
     * @param name Databaseの検索に使用する金額データの名前
     * @return 金額。Database上にデータが存在しなければ0を返す
     */
    public static int loadMoneyAmount(String name) {
        try {
            createMoneyTable();

            PreparedStatement preparedStatement = connection.prepareStatement("""
                SELECT * FROM money WHERE name=?
                """);
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();

            int result = resultSet.isClosed() ? 0 : resultSet.getInt("amount");
            resultSet.close();
            preparedStatement.close();
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Databaseのnameに対応する金額データを更新する
     * データが存在しなければ新規作成する
     * @param name 金額データの名前
     * @param amount 金額データの金額
     */
    public static void updateMoneyAmount(String name, int amount) {
        try {
            createMoneyTable();

            PreparedStatement preparedStatement;
            ConfigHandler configHandler = Metaverseplugin.getInstance().getConfigHandler();

            if (configHandler.getDatabaseType().equals("mysql")) {
                preparedStatement = connection.prepareStatement("""
                    INSERT INTO money (name, amount) VALUES (?, ?)
                        ON DUPRICATE KEY UPDATE amount=VALUES(amount);
                    """);
            } else if (configHandler.getDatabaseType().equals("sqlite")){
                preparedStatement = connection.prepareStatement("""
                    INSERT OR REPLACE INTO money VALUES (?, ?);
                    """);
            } else {
                throw new SQLException();
            }
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, amount);
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
