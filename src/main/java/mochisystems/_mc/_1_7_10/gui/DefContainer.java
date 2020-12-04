package mochisystems._mc._1_7_10.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class DefContainer extends Container {

    public DefContainer() { }
 
    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }
}
