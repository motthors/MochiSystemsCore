package jp.mochisystems.core.util.gui;

import net.minecraft.client.gui.FontRenderer;

public class GuiLabel implements IGuiElement {

    private String text;
    private int posX, posY;
    private int id;
    protected int color;
    private FontRenderer fontRenderer;

    public GuiLabel(String Text, FontRenderer fontRenderer, int posX, int posY, int color)
    {
        this.text = Text;
        this.posX = posX;
        this.posY = posY;
        this.color = color;
        this.fontRenderer = fontRenderer;
    }

    public void Update()
    {

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
    public int GetWidth(){ return text.length() * 5 + 4; }
    public int GetHeight(){return 8;}
    public void Clicked() { }
    @Override
    public void ClickReleased(){}
}
