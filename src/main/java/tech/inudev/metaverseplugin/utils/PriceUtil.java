package tech.inudev.metaverseplugin.utils;

import tech.inudev.metaverseplugin.Metaverseplugin;

public class PriceUtil {
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

    public static void initPrices() {
        List<String> priceTypes = Metaverseplugin.getInstance().getConfigHandler().getPriceTypes();
        if (priceTypes != null && priceTypes.size() > 0) {
            DatabaseUtil.insertPriceValues(priceTypes);
        }
    }

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
