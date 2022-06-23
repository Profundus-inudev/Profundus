package tech.inudev.profundus.utils;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class InventoryUtil {

    public static void addItems(Inventory inventory, List<ItemStack> itemStacks) {
        ItemStack[] arrayStack = new ItemStack[itemStacks.size()];
        itemStacks.toArray(arrayStack);
        inventory.addItem(arrayStack);
    }

}
