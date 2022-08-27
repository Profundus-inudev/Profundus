package tech.inudev.profundus.database;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PFItemStack extends ItemStack {

	public PFItemStack() {
		// TODO Auto-generated constructor stub
	}

	public PFItemStack(@NotNull Material type) {
		super(type);
		// TODO Auto-generated constructor stub
	}

	public PFItemStack(@NotNull ItemStack stack) throws IllegalArgumentException {
		super(stack);
		// TODO Auto-generated constructor stub
	}

	public PFItemStack(@NotNull Material type, int amount) {
		super(type, amount);
		// TODO Auto-generated constructor stub
	}

}
