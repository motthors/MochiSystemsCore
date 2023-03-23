package jp.mochisystems.core.util.gui;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class GuiButtonWrapper extends GuiButtonExt implements IGuiElement {

    private Runnable action;

    public GuiButtonWrapper(int id, int xPos, int yPos, int width, int height, String displayString, Runnable action)
    {
        super(id, xPos, yPos, width, height, displayString);
        this.action = action;
    }

    public void SetAction(Runnable action)
    {
        this.action = action;
    }

    public void SetId(int id)
    {
        this.id = id;
    }

    public int GetId()
    {
        return id;
    }

    public void SetPosition(int x, int y)
    {
        this.x = x;
        this.y = y;
    }
    public int GetPositionX()
    {
        return x;
    }
    public int GetPositionY()
    {
        return y;
    }
    public int GetWidth(){ return width; }
    public int GetHeight(){return height;}
    public void SetPositionY(int y)
    {
        this.y = y;
    }
    public void Update(){}

    public void Draw(int mouseX, int mouseY)
    {
        drawButton(Minecraft.getMinecraft(), mouseX, mouseY, 0f);
    }
    public void Clicked()
    {
        action.run();
    }
    @Override
    public void ClickReleased(){}
}
