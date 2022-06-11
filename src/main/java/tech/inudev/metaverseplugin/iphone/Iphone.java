package tech.inudev.metaverseplugin.iphone;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import tech.inudev.metaverseplugin.utils.DatabaseUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Iphone extends JavaPlugin {

    public static String location_traptower = null; //"World/X/Y/Z/Yaw/Pitch"
    public static Integer price_traptower = 200; //"200 = 200円"

    public static String type_survivalserver = null; //"WORLD or SERVER"
    public static String location_survivalserver = null; //"World/X/Y/Z/Yaw/Pitch"
    public static String select_survivalserver = null; //"World/X/Y/Z/Yaw/Pitch"

    public static String type_worldteleport = null; //"WORLD or SERVER"
    public static List<String> location_worldteleport = new ArrayList<>(); // "Name<=>Material<=>Location"
    public static List<String> select_worldteleport = new ArrayList<>(); // "Name<=>Material<=>Server"

    @Override
    public void onEnable() {

        getServer().getPluginManager().registerEvents(new ClickManager(), this);

        Configuration config = getProvidingPlugin(MetadataValue.class).getConfig();
        location_traptower = config.getString("Location.TrapTower");
        price_traptower = config.getInt("Price.TrapTower");

        type_survivalserver = config.getString("Type.SurvivalServer");
        location_survivalserver = config.getString("Location.SurvivalServer");
        select_survivalserver = config.getString("Select.SurvivalServer");

        type_worldteleport = config.getString("Type.WorldTeleport");
        List<String> location_worldteleport_list = new ArrayList<>();
        for (Integer slot = 1; slot < 8; slot++) {
            if (config.getString("Location.WorldTeleport.Server"+slot) != null) {
                String Name = config.getString("Location.WorldTeleport.Server"+slot+".Name");
                String Type = config.getString("Location.WorldTeleport.Server"+slot+".Type");
                String Location = config.getString("Location.WorldTeleport.Server"+slot+".Location");
                location_worldteleport_list.add(Name+"<=>"+Type+"<=>"+Location);
            }
        }
        location_worldteleport = location_worldteleport_list;
        List<String> select_worldteleport_list = new ArrayList<>();
        for (Integer slot = 1; slot < 8; slot++) {
            if (config.getString("Select.WorldTeleport.Server"+slot) != null) {
                String Name = config.getString("Select.WorldTeleport.Server"+slot+".Name");
                String Type = config.getString("Select.WorldTeleport.Server"+slot+".Type");
                String Location = config.getString("Select.WorldTeleport.Server"+slot+".Location");
                select_worldteleport_list.add(Name+"<=>"+Type+"<=>"+Location);
            }
        }
        select_worldteleport = select_worldteleport_list;
    }

    /**
     * 指定したプレイヤーにGUIを開く
     * @param player プレイヤーの指定
     *
     *  0  1  2  3  4  5  6  7  8
     *  9 10 11 12 13 14 15 16 17
     * 18 19 20 21 22 23 24 25 26
     * 27 28 29 30 31 32 33 34 35
     * 36 37 38 39 40 41 42 43 44
     * 45 46 47 48 49 50 51 52 53
     */
    public Iphone(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 18, "§eIphone");
        String InventoryName = "§eIphone";

        // トラップタワー ihpone/item/TrapTower.java
        new AddItem(inventory, InventoryName,"TrapTower",1, Material.CRACKED_STONE_BRICKS, "&aトラップタワー", Arrays.asList(
                "",
                "&fコスト: &c200円",
                "",
                "&eクリックでトラップタワーに行く",
                ""));

        // 自動採掘 ihpone/item/AutoMine.java
        new AddItem(inventory, InventoryName,"AutoMine", 2, Material.BRICKS, "&a自動採掘", null);

        // サバイバルサーバー ihpone/item/SurvivalServer.java
        new AddItem(inventory, InventoryName,"SurvivalServer", 3, Material.GRASS_BLOCK, "&aサバイバルサーバー", Arrays.asList(
                "",
                "&eクリックでサバイバルサーバーに戻る",
                ""));

        // 自治体
        new AddItem(inventory, InventoryName,"null", 4, Material.END_STONE, "&a自治体", Arrays.asList(
                "",
                "&eクリックで自治体メニューを開く",
                ""));

        // Discordと連携
        new AddItem(inventory, InventoryName,"null", 5, Material.PAPER, "&aDiscordと連携", Arrays.asList(
                "",
                "&eクリックでDiscordと連携する",
                ""));

        // ワールドテレポート ihpone/item/WorldTeleport.java
        new AddItem(inventory, InventoryName,"WorldTeleport", 6, Material.EMERALD_BLOCK, "&aネット銀行", Arrays.asList(
                "",
                "&eクリックでネット銀行を開く",
                ""));

        // 初期スポーン
        new AddItem(inventory, InventoryName,"null", 7, Material.ENDER_PEARL, "&a初期スポーン", Arrays.asList(
                "",
                "&fコスト: &c200円",
                "",
                "&eクリックで初期スポーンへ移動",
                ""));

        // エンドラ復活
        new AddItem(inventory, InventoryName,"null", 8, Material.ENDER_PEARL, "&aエンダードラゴン復活", Arrays.asList(
                "",
                "&fコスト: &c500円",
                "",
                "&eクリックでエンドラ復活",
                ""));

        // ワールドテレポート
        new AddItem(inventory, InventoryName,"null", 9, Material.STONE, "&aワールドテレポート", Arrays.asList(
                "&c==運営限定==",
                "",
                "&eクリックでワールドテレポートメニューを開く",
                ""));

        player.openInventory(inventory);
    }
}