package tech.inudev.metaverseplugin.iphone;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AddItem {

    /**
     * 指定したGUIにアイテムを設置する
     * @param Inventory インべとりーの指定
     * @param Index アイテムの設置場所
     * @param Material アイテムの種類
     * @param Name アイテムの名前
     * @param Lore アイテムの説明欄
     */
    public AddItem(Inventory Inventory, String InventoryName, String ClassName, Integer Index, Material Material, String Name, List<String> Lore) {
        ItemStack itemStack = new ItemStack(Material);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(Name.replace("&","§"));
        if (Lore != null) {
            List<String> successLore = new ArrayList<>();
            for (String lores : Lore) {
                successLore.add(lores.replace("&","§"));
            }
            itemMeta.setLore(successLore);
        }
        itemStack.setItemMeta(itemMeta);

        Inventory.setItem(Index, itemStack);
        if (InventoryName.equalsIgnoreCase("§eIphone")) {
            ClickManager.Iphone_ItemManager.put(Index, ClassName);
        } else if (InventoryName.equalsIgnoreCase("§eWorld Teleport")) {
            ClickManager.WorldTeleport_ItemManager.put(Index, ClassName);
        }
    }
}
