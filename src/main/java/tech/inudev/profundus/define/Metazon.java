package tech.inudev.profundus.define;

import net.kyori.adventure.text.Component;
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
    private int price = 0;
    private static final String METAZON_TITLE = "Metazon";
    private static final int EMERALD_X = 5;
    private static final int EMERALD_Y = 1;

    public void open(Player player) {
        this.gui = new Gui(METAZON_TITLE);
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
                (menuItem, player) -> openSellMode(player),
                new ItemStack(Material.ORANGE_WOOL));
        result.add(new Gui.PosMenuItem(sellMode, 6, 1));

        int[][] filledArray = {{4, 6}};
        result.addAll(generateDisuses(filledArray));
        return result;
    }

    private void openBuyMode(Player player) {

    }

    private void openSellMode(Player player) {
        this.gui = new Gui(METAZON_TITLE);
        List<Gui.PosMenuItem> menuItemList = initSellMenu();
        this.gui.setMenuItems(menuItemList);
        this.gui.open(player);
    }

    private List<Gui.PosMenuItem> initSellMenu() {
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
                List.of(Component.text(this.price)),
                null,
                new ItemStack(Material.EMERALD),
                null,
                false,
                false,
                false
        );
        result.add(new Gui.PosMenuItem(emerald, EMERALD_X, EMERALD_Y));

        // 売却ボタン用アイテム
        BiConsumer<MenuItem, Player> onPaperClick = (menuItem, player)
                -> Profundus.getInstance().getLogger().info("on paper click");
        MenuItem paper = new MenuItem(
                Component.text("売却ボタン"),
                List.of(Component.text("下に売りたいアイテムをセットしてください")),
                onPaperClick,
                new ItemStack(Material.PAPER),
                null,
                false,
                false,
                false);
        result.add(new Gui.PosMenuItem(paper, EMERALD_X, EMERALD_Y + 1));

        // 販売アイテムセット用の空欄
        MenuItem itemBox = new MenuItem(
                "",
                null,
                null,
                false,
                true);
        result.add(new Gui.PosMenuItem(itemBox, EMERALD_X, EMERALD_Y + 2));

        // ヘルプ用アイテム
        BiConsumer<MenuItem, Player> onHelpClick = (menuItem, player) ->
                HelpUtil.openHelp(player.getUniqueId(), HelpUtil.HelpType.Sample);
        MenuItem help = new MenuItem(
                Component.text("ヘルプ"),
                List.of(Component.text("Metazonのつかいかた")),
                onHelpClick,
                new ItemStack(Material.WRITTEN_BOOK),
                null,
                true,
                false,
                false);
        result.add(new Gui.PosMenuItem(help, 9, EMERALD_Y + 2));

        int[][] filledArray = {{2, 3, 4, 5, 6, 7, 8}, {5}, {5, 9}};
//        addDisuses(filledArray);
        result.addAll(generateDraggableBlanks(filledArray));

        return result;
    }

    private Gui.PosMenuItem generatePriceChanger(int value, int x) {
        BiConsumer<MenuItem, Player> onClick = (menuItem, player) -> {
            this.price += (int) menuItem.getCustomData();
            List<Gui.PosMenuItem> menuItemList = gui.cloneMenuItems().stream().peek(v -> {
                if (v.x() == EMERALD_X && v.y() == EMERALD_Y) {
                    v.menuItem().setLore(List.of(Component.text(this.price)));
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

    private List<Gui.PosMenuItem> generateDraggableBlanks(int[][] filledArray) {
        List<Gui.PosMenuItem> result = new ArrayList<>();
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < filledArray.length; y++) {
                int finalX = x;
                if (Arrays.stream(filledArray[y]).filter(v -> v == finalX + 1).findFirst().isEmpty()) {
                    result.add(new Gui.PosMenuItem(new MenuItem(
                            "",
                            null,
                            null,
                            false,
                            true), x + 1, y + 1));
                }
            }
        }
        return result;
    }
}
