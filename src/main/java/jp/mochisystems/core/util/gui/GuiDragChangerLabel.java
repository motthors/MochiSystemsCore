package jp.mochisystems.core.util.gui;

import net.minecraft.client.gui.FontRenderer;
import org.lwjgl.input.Mouse;

import java.util.function.Consumer;

public class GuiDragChangerLabel extends GuiLabel implements IGuiDraggable {

    protected final int defX, defY;
    protected final Consumer<Integer> updateParam;
    private final Runnable onRelease;
    public GuiDragChangerLabel(String Text, FontRenderer fontRenderer, int posX, int posY, int color,
                               Consumer<Integer> updateParam,
                               Runnable onRelease
                               ) {
        super("{ "+Text+" }", fontRenderer, posX, posY, color);
        defX = posX;
        defY = posY;
        this.updateParam = updateParam;
        this.onRelease = onRelease;
    }

    @Override
    public void Dragged(int dx, int dy)
    {
        this.updateParam.accept(Mouse.getEventDX());
        this.color = 0xffffff00;
        //SetPosition(GetPositionX()+dx, GetPositionY());
    }

    @Override
    public int GetStartPos()
    {
        return 0;
    }

    @Override
    public void SetStartPos(int x) {

    }

    @Override
    public void ClickReleased()
    {
        this.color = -1;
        super.Clicked();
    }

    @Override
    public void DragReleased()
    {
        this.color = -1;
        SetPosition(defX, defY);
        if(onRelease != null) onRelease.run();
    }

    public static class Vert extends GuiDragChangerLabel{

        public Vert(String Text, FontRenderer fontRenderer, int posX, int posY, int color, Consumer<Integer> updateParam, Runnable onRelease) {
            super(Text, fontRenderer, posX, posY, color, updateParam, onRelease);
        }

        @Override
        public void SetStartPos(int y) {

        }
        @Override
        public void Dragged(int dx, int dy)
        {
            this.updateParam.accept(Mouse.getEventDY());
            this.color = 0xffffff00;
//            SetPosition(GetPositionX(), GetPositionY()+dy);
        }
    }
}
