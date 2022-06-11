package tech.inudev.metaverseplugin.iphone.item;

import org.bukkit.entity.Player;

public class AutoMine {

    /**
     * 自動採掘~~
     * @param player プレイヤーの指定
     */
    public AutoMine(Player player) {
        player.sendMessage("現在自動採掘の使用はできません。");
    }
}
