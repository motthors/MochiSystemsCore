package mochisystems.util.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

public class GuiImage implements IGuiElement{
    private ResourceLocation resource;
    private int posX, posY;
    private int id;
    private int width, height;

    public GuiImage(ResourceLocation resource, int posX, int posY, int width, int height)
    {
        this.resource = resource;
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
    }

    public void Update()
    {

    }

    @Override
    public void Draw(int mouseX, int mouseY)
    {
        Minecraft.getMinecraft().renderEngine.bindTexture(resource);
        this.drawTexturedModalRect(posX, posY, width, height, 0);
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
    public void MouseClicked(int x, int y, int buttonId){}
    public void Clicked()
    {

    }

    public void drawTexturedModalRect(int x, int y, int width, int height, int zLevel)
    {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double)(x + 0), (double)(y + height), (double)zLevel, 0, 1);
        tessellator.addVertexWithUV((double)(x + width), (double)(y + height), (double)zLevel, 1, 1);
        tessellator.addVertexWithUV((double)(x + width), (double)(y + 0), (double)zLevel, 1, 0);
        tessellator.addVertexWithUV((double)(x + 0), (double)(y + 0), (double)zLevel, 0, 0);
        tessellator.draw();
    }
    @Override
    public void ClickReleased(){}
}
