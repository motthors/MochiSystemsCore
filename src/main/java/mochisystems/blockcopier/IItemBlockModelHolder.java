package mochisystems.blockcopier;

import mochisystems.util.IModel;
import mochisystems.util.IModelController;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IItemBlockModelHolder {
    IModel GetBlockModel(IModelController controller);
    void OnSetInventory(IModel part, int slotidx, ItemStack itemStack, EntityPlayer player);
}
