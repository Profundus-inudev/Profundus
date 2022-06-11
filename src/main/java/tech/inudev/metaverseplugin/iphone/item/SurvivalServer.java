package tech.inudev.metaverseplugin.iphone.item;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import tech.inudev.metaverseplugin.Metaverseplugin;
import tech.inudev.metaverseplugin.iphone.Iphone;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public class SurvivalServer {

    /**
     * サバイバルサーバーへの移動
     * @param player プレイヤーの指定
     */
    public SurvivalServer(Player player) {
        if (Iphone.location_survivalserver.equalsIgnoreCase("WORLD")) {
            SendWorld(player);
        } else if (Iphone.location_survivalserver.equalsIgnoreCase("SERVER")) {
            SendServer(player, Iphone.select_survivalserver);
        } else {
            player.sendMessage("エラーが発生しました。運営等へ報告してください。");
            player.sendMessage("エラー：テレポート先の設定が不明(WORLD or SERVER)");
        }
    }

    /**
     * 転送先が他鯖だった場合
     * @param player プレイヤーの指定
     * @param server 移動させるサーバーの名前を指定 (BungeeCord等の場合はBungeeCordで設定されているサーバー名とする)
     */
    public void SendServer(Player player, String server) {
        try {
            player.sendMessage("サーバーに接続中...");
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DataOutputStream outputStream = new DataOutputStream(byteArrayOutputStream);
            outputStream.writeUTF("Connect");
            outputStream.writeUTF(server);
            player.sendPluginMessage(Metaverseplugin.getPlugin(Metaverseplugin.class), "BungeeCord", byteArrayOutputStream.toByteArray());
            byteArrayOutputStream.close();
            outputStream.close();
        } catch (Exception e) {
            player.sendMessage("サーバー接続時にエラーがでました。");
            Bukkit.getLogger().info("Debug Error: " + e.getMessage());
        }
    }

    /**
     * 転送先が同鯖だった場合
     * @param player   プレイヤーの指定
     */
    public void SendWorld(Player player) {
        if (Iphone.location_survivalserver != null) {
            String[] Location = Iphone.location_survivalserver.split("/");
            Location GoLocation = new Location(Bukkit.getWorld(Location[0]),
                    Double.parseDouble(Location[1]), Double.parseDouble(Location[2]), Double.parseDouble(Location[3])
                    , (short) Double.parseDouble(Location[4]), (short) Double.parseDouble(Location[5]));

            player.teleport(GoLocation);
            player.sendMessage("サバイバルワールドへ移動しました！");
        } else {
            player.sendMessage("サバイバルワールドへの移動先が不明なため、テレポートができませんでした。");
        }
    }
}