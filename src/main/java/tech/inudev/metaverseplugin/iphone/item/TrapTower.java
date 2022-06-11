package tech.inudev.metaverseplugin.iphone.item;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import tech.inudev.metaverseplugin.define.Money;
import tech.inudev.metaverseplugin.iphone.Iphone;

public class TrapTower {

    /**
     * 指定したプレイヤーをトラップタワーへ移動させる
     * @param player プレイヤーの指定
     */
    public TrapTower(Player player) {
        if (Iphone.location_traptower != null) {
            String[] Location = Iphone.location_traptower.split("/");
            Location GoLocation = new Location(Bukkit.getWorld(Location[0]),
                    Double.parseDouble(Location[1]), Double.parseDouble(Location[2]), Double.parseDouble(Location[3])
                    , (short) Double.parseDouble(Location[4]), (short) Double.parseDouble(Location[5]));
            Money money = new Money(player.getUniqueId());
            Integer bank = 0;

            player.sendMessage("現在プレイヤーの所持金取得方法が不明なため、全プレイヤーの所持金を0と仮定しています。");
            player.sendMessage("取得方法が判明次第、修正致します。(shinitaichan)");
            // ↑ 本当はプレイヤーの所持金が入る
            // コードがわからないため　0 と代わりに追いてます。

            if (0 >= Iphone.price_traptower) {
                player.teleport(GoLocation);
                player.sendMessage("トラップタワーへ移動しました！");
                money.remove(Iphone.price_traptower);
            } else {
                Integer Needs = Iphone.price_traptower - bank;
                player.sendMessage("お金が足りなかったため、トラップタワーへの移動ができませんでした。(足りなかった金額: "+Needs+"円)");
            }
        } else {
            player.sendMessage("トラップタワーへの移動先が不明なため、テレポートができませんでした。");
        }
    }
}
