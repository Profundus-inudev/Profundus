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
    List<Gui.PosMenuItem> menuItemList = new ArrayList<>();
    private int price = 0;
    private static final String METAZON_TITLE = "Metazon";

    public void open(Player player) {
        this.gui = new Gui(METAZON_TITLE);
        initializeMenuItems();
        this.gui.open(player);
    }

    private void initializeMenuItems() {
        menuItemList = new ArrayList<>();
        menuItemList.add(createPriceUpdater(-100, 2));
        menuItemList.add(createPriceUpdater(-10, 3));
        menuItemList.add(createPriceUpdater(-1, 4));
        menuItemList.add(createPriceUpdater(1, 6));
        menuItemList.add(createPriceUpdater(10, 7));
        menuItemList.add(createPriceUpdater(100, 8));

        MenuItem emerald = new MenuItem(
                Component.text("金額"),
                List.of(Component.text(this.price)),
                null,
                new ItemStack(Material.EMERALD),
                null,
                false,
                false
        );
        menuItemList.add(new Gui.PosMenuItem(emerald, 5, 1));

        BiConsumer<MenuItem, Player> onPaperClick = (menuItem, player)
                -> Profundus.getInstance().getLogger().info("on paper click");
        MenuItem paper = new MenuItem(
                Component.text("売却ボタン"),
                List.of(Component.text("下に売りたいアイテムをセットしてください")),
                onPaperClick,
                new ItemStack(Material.PAPER),
                null,
                false,
                false);
        menuItemList.add(new Gui.PosMenuItem(paper, 5, 2));

        BiConsumer<MenuItem, Player> onHelpClick = (menuItem, player)
                -> HelpUtil.openHelp(player.getUniqueId(), HelpUtil.HelpType.Sample);
        MenuItem help = new MenuItem(
                Component.text("ヘルプ"),
                List.of(Component.text("Metazonのつかいかた")),
                onHelpClick,
                new ItemStack(Material.WRITTEN_BOOK),
                null,
                true,
                false);
        menuItemList.add(new Gui.PosMenuItem(help, 9, 3));

        MenuItem a = new MenuItem(
                Component.text(""),
                null,
                null,
                new ItemStack(Material.GRAY_STAINED_GLASS_PANE),
                null,
                false,
                false);
        int[][] filledArray = {{2, 3, 4, 5, 6, 7, 8}, {5}, {5, 9}};
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 3; y++) {
                int finalX = x;
                if (Arrays.stream(filledArray[y]).filter(v -> v == finalX + 1).findFirst().isEmpty()) {
                    menuItemList.add(new Gui.PosMenuItem(a, x + 1, y + 1));
                }
            }
        }

        gui.setMenuItems(menuItemList);
    }

    private Gui.PosMenuItem createPriceUpdater(int value, int x) {
        BiConsumer<MenuItem, Player> onClick = (menuItem, player) -> {
            this.price += (int) menuItem.getCustomData();
            initializeMenuItems();
            gui.open(player);
        };
        MenuItem newItem = new MenuItem(
                Component.text(String.valueOf(value)),
                List.of(Component.text(value)),
                onClick,
                new ItemStack(value > 0 ? Material.BLUE_WOOL : Material.RED_WOOL),
                value,
                false,
                false);
        return new Gui.PosMenuItem(newItem, x, 1);
    }


}
