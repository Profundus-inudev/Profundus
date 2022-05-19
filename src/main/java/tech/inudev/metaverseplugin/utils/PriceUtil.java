package tech.inudev.metaverseplugin.utils;

import tech.inudev.metaverseplugin.Metaverseplugin;

public class PriceUtil {
    public static int getPrice(String type) {
        Integer count = DatabaseUtil.loadPriceItemCount(type);
        Integer basicPrice = Metaverseplugin.getInstance().getConfigHandler().getBasicPrice(type);
        if (count == null) {
            throw new IllegalArgumentException("販売個数が設定されていません。");
        }
        if (basicPrice == null) {
            throw new IllegalArgumentException("基本価格が設定されていません。");
        }
        if (count <= 0) {
            throw new IllegalArgumentException("1つも販売されていないtypeです。");
        }

        return 1000 / count + basicPrice;
    }

    public static void setPrice(String type, int count) {
        DatabaseUtil.updatePriceItemCount(type, count);
    }

    public static void addPrice(String type, int count) {
        if (count < 0) {
            throw new IllegalArgumentException("第2引数に負の値は指定できません。");
        }

        int preCount = DatabaseUtil.loadPriceItemCount(type);
        DatabaseUtil.updatePriceItemCount(type, preCount + count);
    }

    public static void substructPrice(String type, int count) {
        if (count < 0) {
            throw new IllegalArgumentException("第2引数に負の値は指定できません。");
        }

        int preCount = DatabaseUtil.loadPriceItemCount(type);
        if (preCount <= count) {
            throw new IllegalArgumentException("販売個数は0以下にはできません。");
        }
        DatabaseUtil.updatePriceItemCount(type, preCount-count);
    }
}
