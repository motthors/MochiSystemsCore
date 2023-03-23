package jp.mochisystems.core._mc.gui;

import jp.mochisystems.core._mc._core._Core;
import jp.mochisystems.core._mc.gui.container.ContainerBlockScanner;
import jp.mochisystems.core._mc.message.MessageUpdateModelParameter;
import jp.mochisystems.core._mc.message.PacketHandler;
import jp.mochisystems.core._mc.tileentity.TileEntityFileManager;
import jp.mochisystems.core.math.Vec3d;
import jp.mochisystems.core.util.gui.GuiButtonWrapper;
import jp.mochisystems.core.util.gui.GuiFormattedTextField;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraft.entity.player.InventoryPlayer;

public class GUIFileManager extends GUICanvasGroupControl {

    private final TileEntityFileManager tile;
    protected ContainerBlockScanner container;

    public GUIFileManager(InventoryPlayer invPlayer, TileEntityFileManager tile)
    {
        super(new ContainerBlockScanner(invPlayer, tile));
        this.tile = tile;
        container = (ContainerBlockScanner) inventorySlots;

        NBTTagCompound nbt = new NBTTagCompound();
        tile.writeToNBT(nbt);
//        this.shadow = new NbtParamsShadow(nbt);
//        ModelName = shadow.Create("ModelName", nbt::setString, nbt::getString);

    }

    protected void UpdateCameraPosFromCore(Vec3d dest, float tick)
    {
        dest.SetFrom(tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ());
    }
    
    @Override
	public void initGui()
    {
        int gDef = -1;
        super.initGui();
        container.SetWindowSize(width, height);

        GuiFormattedTextField field = new GuiFormattedTextField(0, fontRenderer, (width-95)/2, 4, 95, 12, 0xffffff,40,
                () -> tile.reName,
                t -> true,
                t -> tile.reName=t );
        Canvas.Register(gDef, field);
		
        addButton1(55, 16, width-63, 48, _Core.I18n("gui.fileio.text.save"), tile::FileWrite);
        addButton1(55, 16, width-63, 70, _Core.I18n("gui.fileio.text.load"), tile::FileRead);
        addButton1(55, 16, width-63, 90, _Core.I18n("gui.fileio.text.rename"), () -> {
            NBTTagCompound nbt = new NBTTagCompound();
            NBTTagCompound model = new NBTTagCompound();
            model.setString("ModelName", tile.reName);
            nbt.setTag("model", model);
            MessageUpdateModelParameter packet = new MessageUpdateModelParameter(tile.getPos(), nbt);
            PacketHandler.INSTANCE.sendToServer(packet);
        });
    }
    
    public void addButton1(int lenx, int leny, int posx, int posy, String str, Runnable run)
    {
        Canvas.Register(-1, new GuiButtonWrapper(0, posx, posy, lenx, leny, str, run));
    }

    @Override
    public void drawWorldBackground(int p_146270_1_)
    {
        super.drawWorldBackground(p_146270_1_);
        int offset = 2;
        int mx = offset, my = height - 19 * 3 + offset;
        int xx = 164 - offset, xy = height - offset;
        int color = 0x80777777;
        drawRect(mx, my, xx, xy, color);
        mx = 164;
        my = height - 20 + offset;
        xx = 164*2 - offset;
        drawRect(mx, my, xx, xy, color);

        mx = width - 92; my = height - 42;
        xx = mx + 20; xy = my + 20;
        drawRect(mx, my, xx, xy, color);
    }
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseZ) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseZ);
        this.fontRenderer.drawString("Name:", width / 2 - 76, 4, 0xB0B0B0);
    }

        @Override
   	public void onGuiClosed()
    {
   		super.onGuiClosed();
    }

    
}
