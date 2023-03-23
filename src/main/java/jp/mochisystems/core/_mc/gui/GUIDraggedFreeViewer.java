package jp.mochisystems.core._mc.gui;

import jp.mochisystems.core.math.Vec3d;
import jp.mochisystems.core.util.gui.GuiDragController;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import org.lwjgl.input.Mouse;

import java.io.IOException;

// - Model Position Origin View
public abstract class GUIDraggedFreeViewer extends GuiContainer {

    public GuiDragController dragController = new GuiDragController() {
        @Override
        public void RegisterCamPosFromCore(Vec3d dest, float tick) {
            UpdateCameraPosFromCore(dest, tick);
        }
    };

    protected abstract void UpdateCameraPosFromCore(Vec3d dest, float tick);

    protected float GetCameraDistance(){ return dragController.CurrentCameraDistance(); }

    public GUIDraggedFreeViewer(Container container)
    {
        super(container);
        dragController.Init();
    }

    protected boolean CanDrag(int x, int y, int buttonId)
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
    }

    @Override
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        int i = Mouse.getEventDWheel();
        dragController.handleMouseWheel(i);
    }

    @Override
    public void mouseClicked(int x, int y, int buttonid) throws IOException
    {
        if(CanDrag(x, y, buttonid))
        {
            dragController.StartDrag();
        }
        super.mouseClicked(x, y, buttonid);
    }

    @Override
    protected void mouseReleased(int x, int y, int state)
    {
        super.mouseReleased(x, y, state);
        boolean onUp = state == 0 || state == 1;
        if(onUp) dragController.StopMove();
    }

    @Override
    protected void mouseClickMove(int x, int y, int event, long time)
    {
        int dx = Mouse.getEventDX();
        int dy = Mouse.getEventDY();
        dragController.Drag(dx, dy);
        super.mouseClickMove(x, y, event, time);
    }

    @Override
    protected void keyTyped(char keyChar, int keycode) throws IOException
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
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    public void drawWorldBackground(int tint)
    {
        int mx = 0, my = height - 18 * 3 - 6;
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
