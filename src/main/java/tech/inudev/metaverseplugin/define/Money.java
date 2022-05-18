package tech.inudev.metaverseplugin.define;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import tech.inudev.metaverseplugin.Metaverseplugin;

import java.util.UUID;

/**
 * お金の取引を処理するためのクラス
 *
 * @author toru-toruto
 */
public class Money {
    @Getter
    private int amount;
    private UUID playerUUID;
    private String bankName;
    private boolean isBankMoney;

    /**
     * プレイヤーの所持金を使用する場合のコンストラクタ
     * @param playerUUID プレイヤーのUUID
     */
    public Money(UUID playerUUID) {
        // データベースよりプレイヤ(playerUUIDの所持金の取得
        int mock = 1000;

        this.amount = mock;
        this.playerUUID = playerUUID;
        this.isBankMoney = false;
    }

    /**
     * プレイヤーの口座上のお金を使用する場合のコンストラクタ
     * @param bankName 口座の名前
     */
    public Money(String bankName) {
        // データベースよりプレイヤ(playerUUID)の口座(bankName)上の金額の取得
        int mock = 100000;

        this.amount = mock;
        this.bankName = bankName;
        this.isBankMoney = true;
    }

    /**
     * 金額への加算
     * @param value 加算する金額
     */
    public void add(int value) {
        this.amount += value;
    }

    /**
     * 金額への減算
     * @param value 減算する金額
     */
    public void remove(int value) {
        if (this.amount >= value) {
            this.amount -= value;
        } else {
            String mes = "取引するためのお金が足りません";
            logging(mes);
            // 所持金による取引の場合プレイヤーにも通知
            if (!this.isBankMoney && playerUUID != null) {
                Player player = Bukkit.getPlayer(playerUUID);
                if (player.isOnline()) {
                    player.sendMessage(Component.text(mes));
                }
            }
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
