package mochisystems.util.gui;

import net.minecraft.client.gui.FontRenderer;

public class GuiLabel implements IGuiElement {

    private String text;
    private int posX, posY;
    private int id;
    private int color;
    private FontRenderer fontRenderer;

    public GuiLabel(String Text, FontRenderer fontRenderer, int posX, int posY, int color)
    {
        this.text = Text;
        this.posX = posX;
        this.posY = posY;
        this.color = color;
        this.fontRenderer = fontRenderer;
    }

    public void Draw(int mouseX, int mouseY)
    {
        fontRenderer.drawStringWithShadow(text, posX, posY, color);
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
        posX = x;
        posY = y;
    }
    public void SetPositionY(int y)
    {
        posY = y;
    }
    public int GetPositionX()
    {
        return posX;
    }
    public int GetPositionY()
    {
        return posY;
    }
    public int GetWidth(){ return 0; }
    public int GetHeight(){return 0;}
    public void MouseClicked(int x, int y, int buttonId){}
    public void Clicked() { }
    @Override
    public void ClickReleased(){}
}
