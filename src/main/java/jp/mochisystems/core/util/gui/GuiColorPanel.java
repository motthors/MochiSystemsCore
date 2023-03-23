package jp.mochisystems.core.util.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

public class GuiColorPanel implements IGuiElement {

    private int color;
    private int posX, posY;
    private int width, height;
    private int id;


    public GuiColorPanel(int color, int posX, int posY, int width, int height)
    {
        this.color = color;
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
    }

    public void SetColor(int color)
    {
        this.color = color;
    }


    @Override
    public void Update()
    {

    }
    @Override
    public void Draw(int mouseX, int mouseY)
    {
        Gui.drawRect(posX, posY, width, height, color);
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
    public int GetWidth(){ return width; }
    public int GetHeight(){return height;}
    public void Clicked() {

    }
    @Override
    public void ClickReleased(){}
}
