package jp.mochisystems.core.blockcopier;

import jp.mochisystems.core.util.IModel;
import jp.mochisystems.core.util.IModelController;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IItemBlockModelHolder {
    IModel GetBlockModel(IModelController controller);
}
