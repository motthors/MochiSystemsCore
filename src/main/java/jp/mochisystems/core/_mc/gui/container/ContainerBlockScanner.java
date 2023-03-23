package jp.mochisystems.core._mc.gui.container;

import jp.mochisystems.core._mc.gui.slotCanInsertOnlyItem;
import jp.mochisystems.core._mc.tileentity.TileEntityBlocksScannerBase;
import jp.mochisystems.core.blockcopier.IItemBlockModelHolder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerBlockScanner extends ContainerBlockModelerBase
{
    private final Slot coreSlot;
    private final TileEntityBlocksScannerBase tileScanner;

    public ContainerBlockScanner(InventoryPlayer playerInventory, TileEntityBlocksScannerBase tileScanner)
    {
        super(playerInventory);
        this.tileScanner = tileScanner;
        coreSlot = new slotCanInsertOnlyItem((itemStack -> itemStack.getItem() instanceof IItemBlockModelHolder), tileScanner, 0, 257, 57);
        this.addSlotToContainer(coreSlot);
    }

    @Override
    protected void slideSlotPos()
    {
        super.slideSlotPos();
        coreSlot.yPos = height - 40;
        coreSlot.xPos = width - 90;
    }

    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data)
    {
        this.tileScanner.setField(id, data); //TODOこれの適用
    }

    public boolean canInteractWith(EntityPlayer player)
    {
        return true;
    }

    /**
     * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
     */
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if(!(itemstack1.getItem() instanceof IItemBlockModelHolder))
                return ItemStack.EMPTY;
            
            if (index >= 36)
            {
                if (!this.mergeItemStack(itemstack1, 0, 36, true))
                {
                    return ItemStack.EMPTY;
                }
            }
            // mergeItemStack(入れたいItemStack、移動先のスロットの先頭ID、移動先のスロットの最後尾ID、昇順につめるか)
            else if (!this.mergeItemStack(itemstack1, 36, 37, false))
            {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty())
            {
                slot.putStack(ItemStack.EMPTY);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount())
            {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, itemstack1);
        }

        return itemstack;
    }


    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player)
    {
        TileEntityBlocksScannerBase.ChangeUser = player;
        return super.slotClick(slotId, dragType, clickTypeIn, player);
    }
//    /**
//     * used by the Anvil GUI to update the Item Name being typed by the player
//     */
//    public void updateItemName(String str)
//    {
//        this.ItemName = str;
//
//        if (this.getSlot(0).getHasStack())
//        {
//            ItemStack itemstack = this.getSlot(0).getStack();
//
//            if (StringUtils.isBlank(str))
//            {
//                itemstack.func_135074_t();
//            }
//            else
//            {
//                itemstack.setStackDisplayName(this.ItemName);
//            }
//        }
//
////        this.updateRepairOutput();
//    }
}