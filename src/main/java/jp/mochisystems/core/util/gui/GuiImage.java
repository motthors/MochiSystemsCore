package jp.mochisystems.core.util.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

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
    public void Clicked()
    {

    }

    public void drawTexturedModalRect(int x, int y, int width, int height, int zLevel)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos((x),            (y + height), zLevel).tex(0, 1).endVertex();
        buffer.pos(x + width,   (y + height), zLevel).tex(1, 1).endVertex();
        buffer.pos(x + width,   (y)         , zLevel).tex(1, 0).endVertex();
        buffer.pos((x),            (y)         , zLevel).tex(0, 0).endVertex();
        Tessellator.getInstance().draw();
    }
    @Override
    public void ClickReleased(){}
}
