package tech.inudev.metaverseplugin.utils;

import tech.inudev.metaverseplugin.Metaverseplugin;
import tech.inudev.metaverseplugin.config.ConfigHandler;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
                CREATE TABLE IF NOT EXISTS money (
                    name VARCHAR(36) NOT NULL,
                    amount INT NOT NULL,
                    PRIMARY KEY (name))
                """);
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Databaseのnameに対応する金額データを取得する
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
            throw new RuntimeException(e);
        }
    }

    /**
     * Databaseのnameに対応する金額データを更新する
     * データが存在しなければ新規作成する
     *
     * @param name 金額データの名前
     * @param amount 金額データの金額
     */
    public static void updateMoneyAmount(String name, int amount) {
        try {
            createMoneyTable();

            PreparedStatement preparedStatement;
            String databaseType = Metaverseplugin.getInstance().getConfigHandler().getDatabaseType();

            if (databaseType.equals("mysql")) {
                preparedStatement = connection.prepareStatement("""
                    INSERT INTO money (name, amount) VALUES (?, ?)
                        ON DUPRICATE KEY UPDATE amount=VALUES(amount);
                    """);
            } else if (databaseType.equals("sqlite")){
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

    private static void createPriceTable() {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("""
                CREATE TABLE IF NOT EXISTS price (
                    type VARCHAR(100) NOT NULL,
                    count INT NOT NULL,
                    PRIMARY KEY (type))
                """);
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Databaseに存在しない商品データを新規保存する
     * 販売個数の初期値は0
     *
     * @param types 商品タイプのリスト
     */
    public static void insertPriceValues(List<String> types) {
        if (types == null || types.size() == 0){
            throw new IllegalArgumentException();
        }
        try {
            createPriceTable();

            StringBuilder sql = new StringBuilder("INSERT INTO price (type, count) VALUES");

            String databaseType = Metaverseplugin.getInstance().getConfigHandler().getDatabaseType();
            if (databaseType.equals("sqlite")) {
                // sqliteではINSERT ... ON DUPLICATE KEY UPDATEが使えないので対処
                // 既存のデータを検索
                PreparedStatement preparedStatement0 = connection.prepareStatement("""
                        Select * from price;
                        """);
                ResultSet resultSet = preparedStatement0.executeQuery();

                List<String> existingTypes = new ArrayList<>();
                while(resultSet.next()) {
                    existingTypes.add(resultSet.getString("type"));
                }
                preparedStatement0.close();

                // 既存のデータをINSERTする項目から外す
                types = types.stream()
                        .filter(t -> !existingTypes.contains(t))
                        .toList();

                if (types.size() == 0) {
                    return;
                }
            }

            for (String type : types) {
                sql.append("('$type', 0),".replace("$type", type));
            }
            sql = new StringBuilder(sql.substring(0, sql.length() - 1));

            if (databaseType.equals("mysql")) {
                sql.append("ON DUPLICATE KEY UPDATE count=count");
            }
            sql.append(";");

            PreparedStatement preparedStatement = connection.prepareStatement(sql.toString());
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Databaseのtypeに対応する商品の販売個数を取得する
     *
     * @param type 商品タイプ
     * @return 商品の販売個数
     */
    public static Integer loadPriceItemCount(String type) {
        try {
            createPriceTable();

            PreparedStatement preparedStatement = connection.prepareStatement("""
                SELECT * FROM price WHERE type=?
                """);
            preparedStatement.setString(1, type);
            ResultSet resultSet = preparedStatement.executeQuery();

            Integer result = resultSet.next() ? resultSet.getInt("count") : null;
            preparedStatement.close();
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Databaseのtypeに対応する商品の販売個数を更新する
     *
     * @param type 商品タイプ
     * @param count 商品の販売個数
     */
    public static void updatePriceItemCount(String type, int count) {
        try {
            createPriceTable();

            PreparedStatement preparedStatement;
            ConfigHandler configHandler = Metaverseplugin.getInstance().getConfigHandler();

            if (configHandler.getDatabaseType().equals("mysql")) {
                preparedStatement = connection.prepareStatement("""
                    INSERT INTO price (type, count) VALUES (?, ?)
                        ON DUPRICATE KEY UPDATE count=VALUES(count);
                    """);
            } else if (configHandler.getDatabaseType().equals("sqlite")){
                preparedStatement = connection.prepareStatement("""
                    INSERT OR REPLACE INTO price VALUES (?, ?);
                    """);
            } else {
                throw new SQLException();
            }
            preparedStatement.setString(1, type);
            preparedStatement.setInt(2, count);
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
