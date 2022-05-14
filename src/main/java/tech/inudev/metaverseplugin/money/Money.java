package tech.inudev.metaverseplugin.money;

import lombok.Getter;
import tech.inudev.metaverseplugin.Metaverseplugin;

/**
 * お金の取引を処理するためのクラス
 *
 * @author toru-toruto
 */
public class Money {
    @Getter
    private long amount;
    private int playerUUID;
    private String bankName;
    private boolean isBankMoney;

    /**
     * プレイヤーの所持金を使用する場合のコンストラクタ
     * @param playerUUID プレイヤーのUUID
     */
    public Money(int playerUUID) {
        // データベースよりプレイヤ(playerUUIDの所持金の取得
        long mock = 1000;

        init(mock, playerUUID, "", false);
    }

    /**
     * プレイヤーの口座上のお金を使用する場合のコンストラクタ
     * @param playerUUID プレイヤーのUUID
     * @param bankName 口座の名前
     */
    public Money(int playerUUID, String bankName, boolean isBankMoney) {
        // データベースよりプレイヤ(playerUUID)の口座(bankName)上の金額の取得
        long mock = 100000;

        init(mock, playerUUID, bankName, true);
    }


    private void init (long amount, int playerUUID, String bankName, boolean isBankMoney) {
        this.amount = amount;
        this.playerUUID = playerUUID;
        this.bankName = bankName;
        this.isBankMoney = isBankMoney;
    }

    /**
     * 金額への加算
     * @param value 加算する金額
     */
    public void add(long value) {
        this.amount += value;
    }

    /**
     * 金額への減算
     * @param value 減算する金額
     */
    public void remove(long value) {
        if (this.amount < value) {
            logging("取引するためのお金が足りません");
        } else {
            this.amount -= value;
        }
    }

    /**
     * 取引後の金額をDatabaseへ反映する
     */
    public void push() {
        if (this.isBankMoney) {
            logging("取引後の口座の金額をDatabaseへ反映します");
        } else {
            logging("取引後の所持金をDatabaseへ反映します");
        }
    }

    private void logging(String message) {
        Metaverseplugin.getInstance().getLogger().info(message);
    }
}
