package jp.mochisystems.core._mc.gui.container;

import jp.mochisystems.core._mc._core.Logger;
import jp.mochisystems.core._mc.tileentity.TileEntityBlocksScannerBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public abstract class ContainerBlockModelerBase extends Container {

    protected int width, height;
    protected InventoryPlayer playerInventory;

    public ContainerBlockModelerBase(InventoryPlayer playerInventory)
    {
        this.playerInventory = playerInventory;
        AddSlots();
    }

    // have to call from GuiContainer::initGui()
    public void SetWindowSize(int x, int y)
    {
        width = x;
        height = y;
        slideSlotPos();
    }

    private void AddSlots()
    {
        for (int vert = 0; vert < 3; ++vert)
        {
            for (int horz = 0; horz < 9; ++horz)
            {
                int index = vert * 9 + horz + 9;
                this.addSlotToContainer(new Slot(playerInventory, index, 0, 0));
            }
        }
        // player inventory
        for (int i = 0; i < 9; ++i)
        {
            this.addSlotToContainer(new Slot(playerInventory, i, 0, 0));
        }
    }


    protected void slideSlotPos()
    {
        for (Slot slot : inventorySlots)
        {
            int idx = slot.getSlotIndex();
            if (0 <= idx && idx < 9) {
                slot.xPos = (idx % 9) * 18 + 2 + 18 * 9;
                slot.yPos = (idx / 9) * 18 + height - 18;
            } else if (9 <= idx && idx < 9 * 4) {
                idx -= 9;
                slot.xPos = (idx % 9) * 18 + 2;
                slot.yPos = (idx / 9) * 18 + height - 18 * 3;
            }
        }
    }
}
