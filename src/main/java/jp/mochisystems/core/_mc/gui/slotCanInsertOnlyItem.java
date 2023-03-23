package jp.mochisystems.core._mc.gui;

import jp.mochisystems.core.blockcopier.IItemBlockModelHolder;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import java.util.function.Predicate;


public class slotCanInsertOnlyItem extends Slot{
	private Predicate<ItemStack> predicate;
	public slotCanInsertOnlyItem(Predicate<ItemStack> predicate, IInventory inventory, int idx, int x, int y) {
		super(inventory, idx, x, y);
		this.predicate = predicate;
	}
	public boolean isItemValid(ItemStack itemStack)
	{
		return !itemStack.isEmpty() && predicate.test(itemStack);
	}

	public static class forModelHolder extends slotCanInsertOnlyItem{
		public forModelHolder(IInventory inventory, int x, int y, int z)
		{
			super(stack -> stack.getItem() instanceof IItemBlockModelHolder, inventory, x, y, z);
		}
	}
}

