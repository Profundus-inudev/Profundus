package tech.inudev.profundus.define;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import tech.inudev.profundus.Profundus;
import tech.inudev.profundus.utils.HelpUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

public class Metazon {
    Gui gui;
    private int sellPrice = 1;
    private static final String METAZON_TITLE = "Metazon";
    private static final int EMERALD_X = 5;
    private static final int EMERALD_Y = 1;
    private static final int GOODS_X = 5;
    private static final int GOODS_Y = 3;

    public void open(Player player) {
        this.gui = new Gui(METAZON_TITLE);
        this.sellPrice = 1;
        this.gui.setMenuItems(initTopMenu());
        this.gui.open(player);
    }

    private List<Gui.PosMenuItem> initTopMenu() {
        List<Gui.PosMenuItem> result = new ArrayList<>();

        MenuItem paper = new MenuItem(
                "購入モード",
                null,
                (menuItem, player) -> openBuyMode(player),
                new ItemStack(Material.GREEN_WOOL));
        result.add(new Gui.PosMenuItem(paper, 4, 1));

        MenuItem sellMode = new MenuItem(
                "販売モード",
                null,
                (menuItem, player) -> openSellMode(player, null),
                new ItemStack(Material.ORANGE_WOOL));
        result.add(new Gui.PosMenuItem(sellMode, 6, 1));

        int[][] filledArray = {{4, 6}};
        result.addAll(generateDisuses(filledArray));
        return result;
    }

    private void openBuyMode(Player player) {

    }

    private void openSellMode(Player player, ItemStack sellItem) {
        this.gui = new Gui(METAZON_TITLE + " 販売モード");
        List<Gui.PosMenuItem> menuItemList = initSellMenu(sellItem);
        this.gui.setMenuItems(menuItemList);
        this.gui.open(player);
    }

    private List<Gui.PosMenuItem> initSellMenu(ItemStack sellItem) {
        List<Gui.PosMenuItem> result = new ArrayList<>();

        // 金額変動用アイテム
        for (int i = 0; i < 3; i++) {
            int x = 5;
            result.add(generatePriceChanger(
                    (int) Math.pow(10, i), x + (i + 1)));
            result.add(generatePriceChanger(
                    (int) -Math.pow(10, i), x - (i + 1)));
        }

        // 金額表示用アイテム
        MenuItem emerald = new MenuItem(
                Component.text("金額"),
                List.of(Component.text(this.sellPrice)),
                null,
                new ItemStack(Material.EMERALD),
                null,
                false,
                false,
                false);
        result.add(new Gui.PosMenuItem(emerald, EMERALD_X, EMERALD_Y));

        // 販売ボタン用アイテム
        MenuItem paper = new MenuItem(
                Component.text("販売ボタン"),
                List.of(Component.text("下に売りたいアイテムをセットしてください")),
                (menuItem, player) -> onSellPaperClick(player),
                new ItemStack(Material.PAPER),
                null,
                false,
                false,
                false);
        result.add(new Gui.PosMenuItem(paper, EMERALD_X, EMERALD_Y + 1));

        // 販売アイテムセット用の空欄
        MenuItem itemBox = MenuItem.generateDraggable(null, sellItem);
        result.add(new Gui.PosMenuItem(itemBox, EMERALD_X, EMERALD_Y + 2));

        // 戻るボタン用アイテム
        MenuItem backPage = new MenuItem(
                Component.text("前に戻る"),
                null,
                (menuItem, player) -> open(player),
                new ItemStack(Material.PAPER),
                null,
                false,
                false,
                false);
        result.add(new Gui.PosMenuItem(backPage, 1, 3));

        // ヘルプ用アイテム
        MenuItem help = new MenuItem(
                Component.text("ヘルプ"),
                List.of(Component.text("Metazonのつかいかた")),
                (menuItem, player) -> HelpUtil.openHelp(player.getUniqueId(), HelpUtil.HelpType.Sample),
                new ItemStack(Material.WRITTEN_BOOK),
                null,
                true,
                false,
                false);
        result.add(new Gui.PosMenuItem(help, 9, EMERALD_Y + 2));

        int[][] filledArray = {{2, 3, 4, 5, 6, 7, 8}, {5}, {1, 5, 9}};
        result.addAll(generateDisuses(filledArray));
        return result;
    }

    private void onSellPaperClick(Player player) {
        Gui.PosMenuItem goodsMenuItem = gui.cloneMenuItems().stream()
                .filter(v -> v.x() == GOODS_X && v.y() == GOODS_Y)
                .findFirst().orElse(null);
        if (goodsMenuItem == null) {
            throw new IllegalStateException();
        }
        ItemStack goods = goodsMenuItem.menuItem().getIcon();
        if (goods == null) {
            return;
        }
        openSellConfirm(player, goods);
//        List<Gui.PosMenuItem> menuItemList = gui.cloneMenuItems().stream().peek(v -> {
//            if (v.x() == GOODS_X && v.y() == GOODS_Y) {
//                v.menuItem().setIcon(null);
//            }
//        }).toList();
//        gui.setMenuItems(menuItemList);
//        gui.open(player);
    }

    private void openSellConfirm(Player player, ItemStack sellItem) {
        this.gui = new Gui(METAZON_TITLE + " 販売確認");
        List<Gui.PosMenuItem> menuItemList = initSellConfirmMenu(sellItem);
        this.gui.setMenuItems(menuItemList);
        this.gui.open(player);
    }

    private List<Gui.PosMenuItem> initSellConfirmMenu(ItemStack sellItem) {
        List<Gui.PosMenuItem> result = new ArrayList<>();

//        MenuItem paper = new MenuItem(
//                Component.text("確認"),
//                List.of(Component.text("こちらのアイテムを販売します。"),
//                        Component.text("左：戻る 右：確定")),
//                null,
//                new ItemStack(Material.EMERALD),
//                null,
//                false,
//                false,
//                false);
//        result.add(new Gui.PosMenuItem(paper, 5, 1));

        // 金額表示用アイテム
        MenuItem emerald = new MenuItem(
                Component.text("金額"),
                List.of(Component.text(this.sellPrice)),
                null,
                new ItemStack(Material.EMERALD),
                null,
                false,
                false,
                false);
        result.add(new Gui.PosMenuItem(emerald, 5, 1));

        MenuItem goods = new MenuItem(
                null,
                null,
                null,
                sellItem,
                null,
                sellItem.getItemMeta().hasEnchants(),
                false,
                false);
        result.add(new Gui.PosMenuItem(goods, 5, 2));

        MenuItem backPage = new MenuItem(
                Component.text("前に戻る"),
                null,
                (menuItem, player) -> openSellMode(player, sellItem),
                new ItemStack(Material.PAPER),
                null,
                false,
                false,
                false);
        result.add(new Gui.PosMenuItem(backPage, 4, 3));

        MenuItem confirm = new MenuItem(
                Component.text("販売を確定").color(TextColor.color(0x55FF55)),
                null,
                (menuItem, player) -> mockRegisterItem(sellItem, this.sellPrice, player),
                new ItemStack(Material.PAPER),
                null,
                false,
                true,
                false);
        result.add(new Gui.PosMenuItem(confirm, 6, 3));

        int[][] filledArray = {{5}, {5}, {4, 6}};
        result.addAll(generateDisuses(filledArray));

        return result;
    }

    private void mockRegisterItem(ItemStack sellItem, int sellPrice, Player player) {
        // 本来は外部の販売アイテム登録用メソッドを呼び出す
    }

    private Gui.PosMenuItem generatePriceChanger(int value, int x) {
        BiConsumer<MenuItem, Player> onClick = (menuItem, player) -> {
            this.sellPrice += (int) menuItem.getCustomData();
            if (this.sellPrice < 1) {
                this.sellPrice = 1;
            }
            List<Gui.PosMenuItem> menuItemList = gui.cloneMenuItems().stream().peek(v -> {
                if (v.x() == EMERALD_X && v.y() == EMERALD_Y) {
                    v.menuItem().setLore(List.of(Component.text(this.sellPrice)));
                }
            }).toList();
            gui.setMenuItems(menuItemList);
            gui.open(player);
        };
        MenuItem newItem = new MenuItem(
                Component.text(String.valueOf(value)),
                List.of(Component.text(value)),
                onClick,
                new ItemStack(value > 0 ? Material.BLUE_WOOL : Material.RED_WOOL),
                value,
                false,
                false,
                false);
        return new Gui.PosMenuItem(newItem, x, 1);
    }

    // 不使用スロットを埋める
    private List<Gui.PosMenuItem> generateDisuses(int[][] filledArray) {
        List<Gui.PosMenuItem> result = new ArrayList<>();
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < filledArray.length; y++) {
                int finalX = x;
                if (Arrays.stream(filledArray[y]).filter(v -> v == finalX + 1).findFirst().isEmpty()) {
                    result.add(new Gui.PosMenuItem(
                            MenuItem.generateDisuse(), x + 1, y + 1));
                }
            }
        }
        return result;
    }
}
