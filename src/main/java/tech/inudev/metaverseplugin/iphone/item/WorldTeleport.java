package tech.inudev.metaverseplugin.iphone.item;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import tech.inudev.metaverseplugin.iphone.AddItem;
import tech.inudev.metaverseplugin.iphone.Iphone;

import java.util.Arrays;
import java.util.List;

public class WorldTeleport {

    /**
     * 指定したプレイヤーをトラップタワーへ移動させる
     *
     * @param player プレイヤーの指定
     */
    public WorldTeleport(Player player) {
        if (Iphone.location_survivalserver.equalsIgnoreCase("WORLD")) {
            Menu(player, "Location");
        } else if (Iphone.location_survivalserver.equalsIgnoreCase("SERVER")) {
            Menu(player, "Select");
        } else {
            player.sendMessage("エラーが発生しました。運営等へ報告してください。");
            player.sendMessage("エラー：テレポート先の設定が不明(WORLD or SERVER)");
        }
    }

    public void Menu(Player player, String type) {
        Inventory inventory = Bukkit.createInventory(null, 9, "§eWorld Teleport");

        // 全アイテムの取得
        Integer slot = 0;
        List<String> stringList = Iphone.select_worldteleport;
        if (type.equalsIgnoreCase("Location")) {
            stringList = Iphone.location_worldteleport;
            for (String alls : stringList) {
                String[] all = alls.split("<=>"); // "Name<=>Material<=>Server"
                new AddItem(inventory, 2, Material.getMaterial(all[1]), all[0], Arrays.asList(
                        "",
                        "&f移動ワールド先: &a" + all[2],
                        "",
                        "&eクリックで移動する",
                        ""));
                slot++;
            }
        } else {
            for (String alls : stringList) {
                String[] all = alls.split("<=>"); // "Name<=>Material<=>Server"
                new AddItem(inventory, 2, Material.getMaterial(all[1]), all[0], Arrays.asList(
                        "",
                        "&f移動サーバー先: &a" + all[2],
                        "",
                        "&eクリックで移動する",
                        ""));
                slot++;
            }
        }

        player.openInventory(inventory);
    }
}