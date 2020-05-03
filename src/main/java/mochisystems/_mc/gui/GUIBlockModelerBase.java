package mochisystems._mc.gui;

import mochisystems.math.Vec3d;
import mochisystems.util.gui.GuiDragController;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import org.lwjgl.input.Mouse;

// - Model Position Origin View
public abstract class GUIBlockModelerBase extends GuiContainer {

    private int blockposX;
    private int blockposY;
    private int blockposZ;
    ContainerBlockModelerBase container;

    GuiDragController dragController = new GuiDragController() {
        @Override
        public void RegisterCamPosFromCore(Vec3d dest, float tick) {
            UpdateCameraPosFromCore(dest, tick);
        }
    };

    protected abstract void UpdateCameraPosFromCore(Vec3d dest, float tick);

    protected float GetCameraDistance(){ return dragController.CurrentCameraDistance(); }

    public GUIBlockModelerBase(int x, int y, int z, ContainerBlockModelerBase container)
    {
        super(container!=null?container:new DefContainer());
        blockposX = x;
        blockposY = y;
        blockposZ = z;
        this.container = container;
        dragController.Init(blockposX, blockposY, blockposZ);
    }

    protected boolean CanDrag(int x, int y, int buttonid)
    {
        return 80 < x && x < width-80 && 16 < y && y < height-57;
    }

    @Override
    public void initGui()
    {
        super.initGui();

        xSize = width;
        ySize = height;
        guiLeft = 0;//-width / 2;
        guiTop = 0;//-height / 2;
        if(container!=null) container.SetWindowSize(width, height);
    }

    @Override
    public void handleMouseInput()
    {
        super.handleMouseInput();
        int i = Mouse.getEventDWheel();
        dragController.handleMouseWheel(i);
    }

    @Override
    public void mouseClicked(int x, int y, int buttonid)
    {
        if(CanDrag(x, y, buttonid))
        {
            dragController.StartDrag();
        }
        super.mouseClicked(x, y, buttonid);
    }

    @Override
    protected void mouseMovedOrUp(int x, int y, int buttonid)
    {
        super.mouseMovedOrUp(x, y, buttonid);
        boolean onUp = buttonid == 0 || buttonid == 1;
        if(onUp) dragController.StopMove();
    }

    @Override
    protected void mouseClickMove(int x, int y, int event, long time)
    {
        dragController.Drag(Mouse.getDX(), Mouse.getDY());
        super.mouseClickMove(x, y, event, time);
    }

    @Override
    protected void keyTyped(char keyChar, int keycode)
    {
        if (keycode == this.mc.gameSettings.keyBindInventory.getKeyCode())
        {
            GuiDragController.ResetSpecialCamera();
        }
        super.keyTyped(keyChar, keycode);
    }

    @Override
    public void onGuiClosed()
    {
        super.onGuiClosed();
        Close();
    }

    public void drawRightedString(FontRenderer renderer, String text, int x, int y, int color)
    {
        renderer.drawStringWithShadow(text, x - renderer.getStringWidth(text), y, color);
    }

    protected void Close()
    {
        dragController.EndDragCamera();
    }

    @Override
    public void drawWorldBackground(int p_146270_1_)
    {
        int mx = 0, my = height - 20 * 3;
        int xx = width, xy = height;
        int color = 0xD0101010;
        drawRect(mx, my, xx, xy, color); // bottom
        xx = 20 * 4;
        my = 0;
        xy = height - 20 * 3;
        drawRect(mx, my, xx, xy, color); // left
        mx = width - 20 * 4;
        xx = width;
        drawRect(mx, my, xx, xy, color); // right
        mx = width/2 - 80;
        xx = width/2 + 80;
        xy = 16;
        drawRect(mx+2, my+2, xx-2, xy-2, color); // name
        drawRect(mx, my, xx, xy, color); // name
    }
}
