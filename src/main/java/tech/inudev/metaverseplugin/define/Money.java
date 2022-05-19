package tech.inudev.metaverseplugin.define;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import tech.inudev.metaverseplugin.utils.DatabaseUtil;

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
    private final boolean isBankMoney;

    /**
     * プレイヤーの所持金を使用する場合のコンストラクタ
     * @param playerUUID プレイヤーのUUID
     */
    public Money(UUID playerUUID) {
        Integer amount = DatabaseUtil.loadMoneyAmount(playerUUID.toString());
        this.amount = amount != null ? amount : 0;
        this.playerUUID = playerUUID;
        this.isBankMoney = false;
    }

    /**
     * プレイヤーの口座上のお金を使用する場合のコンストラクタ
     * @param bankName 口座の名前
     */
    public Money(String bankName) {
        String regex = "[a-f0-9]{8}-[a-f0-9]{4}-[0-9][a-f0-9]{3}-[89ab][a-f0-9]{3}-[a-f0-9]{12}";
        if (bankName.toLowerCase().matches(regex)) {
            throw new IllegalArgumentException("UUID形式の文字列は引数に指定できません。");
        }

        Integer amount = DatabaseUtil.loadMoneyAmount(bankName);
        this.amount = amount != null ? amount : 0;
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
     * 減算するお金が足りない場合、プレイヤーへ通知する
     * @param value 減算する金額
     */
    public boolean remove(int value) {
        if (this.amount >= value) {
            this.amount -= value;
            return true;
        } else {
            if (this.isBankMoney || playerUUID == null) {
                return false;
            }
            // 所持金による取引の場合、プレイヤーへお金不足を通知
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null && player.isOnline()) {
                player.sendMessage(Component.text(
                    "取引するためのお金が足りません"));
            }
            return false;
        }
    }

    /**
     * 取引後の金額をDatabaseへ反映する
     */
    public boolean push() {
        if (this.isBankMoney) {
            if (this.bankName.isEmpty()) {
                return false;
            }
            DatabaseUtil.updateMoneyAmount(this.bankName, this.amount);
        } else {
            if (this.playerUUID == null) {
                return false;
            }
            DatabaseUtil.updateMoneyAmount(this.playerUUID.toString(), this.amount);
        }
        return true;
    }
}
