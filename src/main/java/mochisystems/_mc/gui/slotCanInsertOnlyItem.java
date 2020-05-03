package mochisystems._mc.gui;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import java.util.function.Predicate;


public class slotCanInsertOnlyItem extends Slot{
	private Predicate<ItemStack> predicate;
	public slotCanInsertOnlyItem(Predicate<ItemStack> predicate, IInventory inventory, int x, int y, int z) {
		super(inventory, x, y, z);
		this.predicate = predicate;
	}
	public boolean isItemValid(ItemStack itemStack)
	{
		return itemStack !=null && predicate.test(itemStack);
	}
}

