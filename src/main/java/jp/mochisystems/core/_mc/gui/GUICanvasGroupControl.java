package jp.mochisystems.core._mc.gui;

import jp.mochisystems.core.util.gui.GuiGroupCanvas;
import net.minecraft.inventory.Container;

import java.io.IOException;

public abstract class GUICanvasGroupControl extends GUIDraggedFreeViewer {

    protected GuiGroupCanvas Canvas = new GuiGroupCanvas();


    public GUICanvasGroupControl(Container container) {
        super(container);
    }

    @Override
    public void initGui()
    {
        super.initGui();
        Canvas.Init();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY)
    {
        Canvas.Update();
        Canvas.DrawContents(mouseX, mouseY);
    }


    /**
     * KEY
     */
    @Override
    protected void keyTyped(char c, int keyCode) throws IOException {
        if (Canvas.KeyTyped(c, keyCode)) return;
        super.keyTyped(c, keyCode);
    }


    /**
     * MOUSE
     */
    @Override
    public void mouseClicked(int x, int y, int buttonId) throws IOException
    {
        if(!Canvas.MouseClicked(x, y, buttonId)) {
            super.mouseClicked(x, y, buttonId);
        }
    }
    @Override
    protected void mouseClickMove(int x, int y, int event, long time)
    {
        if(Canvas.mouseClickMove(x, y, event, time)) return;
        super.mouseClickMove(x, y, event, time);
    }
    @Override
    protected void mouseReleased(int x, int y, int state)
    {
        super.mouseReleased(x, y, state);
        Canvas.mouseReleased(x, y, state);
    }
}
