//package jp.mochisystems.core._mc.gui.container;
//
//import jp.mochisystems.core._mc.gui.slotCanInsertOnlyItem;
//import jp.mochisystems.core._mc.tileentity.TileEntityFileManager;
//import net.minecraft.entity.player.EntityPlayer;
//import net.minecraft.entity.player.InventoryPlayer;
//import net.minecraft.inventory.Container;
//import net.minecraft.inventory.Slot;
//
//public class ContainerFileManager extends Container
//{
//    private TileEntityFileManager tile;
//
//    public ContainerFileManager(InventoryPlayer invPlayer, TileEntityFileManager tile)
//    {
//        this.tile = tile;
//        this.addSlotToContainer(new slotCanInsertOnlyItem.forModelHolder( tile, 0, 148, 57));
//
//        for (int i = 0; i < 3; ++i)
//        {
//        	for (int j = 0; j < 9; ++j)
//        	{
//        		this.addSlotToContainer(new Slot(invPlayer, 9+j+i*9, 8+j*18, 84+i*18));
//        	}
//        }
//        for (int i = 0; i < 9; ++i)
//        {
//            this.addSlotToContainer(new Slot(invPlayer, i, 8 + i * 18, 142));
//        }
//    }
//
//    public boolean canInteractWith(EntityPlayer player)
//    {
//        return this.tile.isUsableByPlayer(player);
//    }
//
//}