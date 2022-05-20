package tech.inudev.metaverseplugin.utils;

import tech.inudev.metaverseplugin.Metaverseplugin;

import java.util.ArrayList;
import java.util.List;

/**
 * 物価を管理するためのクラス
 *
 * @author toru-toruto
 */
public class PriceUtil {

    /**
     * 商品価格を取得する
     *
     * @param type 商品タイプ
     * @return 商品価格
     */
    public static int getPrice(String type) {
        Integer count = DatabaseUtil.loadPriceItemCount(type);
        Integer basicPrice = Metaverseplugin.getInstance().getConfigHandler().getBasicPrice(type);
        if (count == null || basicPrice == null) {
            throw new IllegalArgumentException("指定された商品情報が存在しません。");
        }
        if (count <= 0) {
            throw new IllegalArgumentException("1つも販売されていない商品です。");
        }

        return 1000 / count + basicPrice;
    }

    /**
     * 販売個数を取得する
     *
     * @param type 商品タイプ
     * @return 販売個数
     */
    public static int getItemCount(String type) {
        Integer count = DatabaseUtil.loadPriceItemCount(type);
        if (count == null) {
            throw new IllegalArgumentException("指定された商品情報が存在しません。");
        }
        return count;
    }

    /**
     * 新規価格情報を起動時にDatabaseへ追加する
     */
    public static void initPrices() {
        List<String> priceTypes = new ArrayList<>(
                Metaverseplugin.getInstance().getConfigHandler().getPriceMap().keySet());

        if (priceTypes.size() > 0) {
            DatabaseUtil.insertPriceValues(priceTypes);
        }
    }

    /**
     * 販売個数を増加させる
     *
     * @param type 商品タイプ
     * @param count 増加させる販売個数
     */
    public static void addPrice(String type, int count) {
        if (count < 0) {
            throw new IllegalArgumentException("第2引数に負の値は指定できません。");
        }

        Integer preCount = DatabaseUtil.loadPriceItemCount(type);
        if (preCount == null) {
            throw new IllegalArgumentException("指定された商品情報が存在しません。");
        }
        DatabaseUtil.updatePriceItemCount(type, preCount + count);
    }

    /**
     * 販売個数を減少させる
     *
     * @param type 商品タイプ
     * @param count 減少させる販売個数
     */
    public static void substructPrice(String type, int count) {
        if (count < 0) {
            throw new IllegalArgumentException("第2引数に負の値は指定できません。");
        }

        Integer preCount = DatabaseUtil.loadPriceItemCount(type);
        if (preCount == null) {
            throw new IllegalArgumentException("指定された商品情報が存在しません。");
        }
        if (preCount < count) {
            throw new IllegalArgumentException("販売個数は負の値にできません。");
        }
        DatabaseUtil.updatePriceItemCount(type, preCount-count);
    }
}
