package jp.mochisystems.core._mc.gui.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class DefContainer extends Container {

    public DefContainer() { }
 
    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }
}
