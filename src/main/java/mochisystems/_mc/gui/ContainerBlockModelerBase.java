package mochisystems._mc.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

import java.util.List;

public abstract class ContainerBlockModelerBase extends Container {

    protected int width, height;
    InventoryPlayer playerInventory;

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
        @SuppressWarnings("unchecked")
        List<Slot> list = (List<Slot>)inventorySlots;
        for (Slot slot : list)
        {
            int idx = slot.getSlotIndex();
            if (0 <= idx && idx < 9) {
                slot.xDisplayPosition = (idx % 9) * 18 + 2 + 18 * 9;
                slot.yDisplayPosition = (idx / 9) * 18 + height - 18;
            } else if (9 <= idx && idx < 9 * 4) {
                idx -= 9;
                slot.xDisplayPosition = (idx % 9) * 18 + 2;
                slot.yDisplayPosition = (idx / 9) * 18 + height - 18 * 3;
            }
        }
    }
}
