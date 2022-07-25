package tech.inudev.profundus.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;

import tech.inudev.profundus.Profundus;

public class DBUGoods extends DatabaseUtil{

	final static Table table = Table.GOODS;
	
	static final String createStr = """
            'id' INT AUTO_INCREMENT,
            'item' VARBINARY(511) NOT NULL,
            'price' INT NOT NULL,
            'seller' VARCHAR(36) NOT NULL,
            PRIMARY KEY ('id')
            """;
	
    /**
     * Metazonの商品を新たに登録する。
     *
     * @param item   商品アイテム
     * @param price  商品の価格
     * @param seller 出品者の名前（プレイヤーであればUUIDの文字列)
     */
    public static void createGoodsRecord(ItemStack item, int price, String seller) {
        if (item == null) {
            throw new IllegalArgumentException();
        }
        try {

            PreparedStatement preparedStatement = getConnection().prepareStatement("""
                    INSERT INTO goods (item, price, seller)
                    VALUES (?, ?, ?)
                    """);
            preparedStatement.setBytes(1, item.serializeAsBytes());
            preparedStatement.setInt(2, price);
            preparedStatement.setString(3, seller);
            preparedStatement.execute();
            preparedStatement.close();

            commitTransaction();
        } catch (SQLException e) {
            try {
            	getConnection().rollback();
                Profundus.getInstance().getLogger().info("rollback");
            } catch (SQLException e2) {
                throw new RuntimeException(e2);
            }
        }
    }

    /**
     * Metazonの商品データ（商品アイテム、価格、出品者）をまとめたもの。
     *
     * @param item   商品アイテム
     * @param price  価格
     * @param seller 出品者
     */
    public record GoodsData(ItemStack item, int price, String seller) {

    }

    /**
     * Metazonの商品データのリストを取得する。
     *
     * @return 商品データのリスト
     */
    public static List<GoodsData> loadGoodsList() {
        try {

            PreparedStatement preparedStatement = getConnection().prepareStatement("""
                    SELECT * FROM goods
                    """);
            ResultSet resultSet = preparedStatement.executeQuery();

            List<GoodsData> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(new GoodsData(
                        ItemStack.deserializeBytes(resultSet.getBytes("item")),
                        resultSet.getInt("price"),
                        resultSet.getString("seller")));
            }
            preparedStatement.close();
            return result;
        } catch (SQLException e) {
            try {
            	getConnection().rollback();
                Profundus.getInstance().getLogger().info("rollback");
                return null;
            } catch (SQLException e2) {
                throw new RuntimeException(e2);
            }
        }
    }
	

}
