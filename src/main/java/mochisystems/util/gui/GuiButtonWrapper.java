package mochisystems.util.gui;

import cpw.mods.fml.client.config.GuiButtonExt;
import net.minecraft.client.Minecraft;

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
        xPosition = x;
        yPosition = y;
    }
    public int GetPositionX()
    {
        return xPosition;
    }
    public int GetPositionY()
    {
        return yPosition;
    }
    public int GetWidth(){ return width; }
    public int GetHeight(){return height;}
    public void SetPositionY(int y)
    {
        yPosition = y;
    }
    public void MouseClicked(int x, int y, int buttonId)
    {
    }
    public void Update(){}
    public void Draw(int mouseX, int mouseY)
    {
        drawButton(Minecraft.getMinecraft(), mouseX, mouseY);
    }
    public void Clicked()
    {
        action.run();
    }
    @Override
    public void ClickReleased(){}
}
