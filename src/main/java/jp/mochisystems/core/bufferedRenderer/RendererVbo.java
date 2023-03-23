package jp.mochisystems.core.bufferedRenderer;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import org.lwjgl.opengl.GL11;

public class RendererVbo implements ICachedBufferRenderer {
    //ref -> net.minecraft.client.renderer.VboRenderList
    private int glBufferID = -1;
    private VertexBuffer vbo;
    private CachedBufferBase base;

    public RendererVbo(int id, CachedBufferBase base){
        glBufferID = id;
        this.base = base;
    }

    public boolean IsBuilt()
    {
        return vbo != null;
    }

    public void Build()
    {
//        Logger.debugInfo("rendererVbo::Build : "+base);
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buffer = tess.getBuffer();

        vbo = new VertexBuffer(base.GetVertexFormat());

        buffer.begin(base.GetDrawMode(), base.GetVertexFormat());
        base.Compile();

        buffer.finishDrawing();
        buffer.reset();
        vbo.bufferData(buffer.getByteBuffer());
    }

    public void Render(){
        if(vbo==null)
            return;
        vbo.bindBuffer();

//        int stride = base.GetVertexFormat().getNextOffset();

//        if (DefaultVertexFormats.POSITION_TEX_NORMAL.equals(base.GetVertexFormat())) {
//            GlStateManager.glVertexPointer(3, GL11.GL_FLOAT, stride, 0);
//            GlStateManager.glEnableClientState(GL11.GL_VERTEX_ARRAY);
//            GlStateManager.glTexCoordPointer(2, GL11.GL_FLOAT, stride, 12);
//            GlStateManager.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
//            GL11.glNormalPointer(GL11.GL_BYTE, stride, 20);
//            GlStateManager.glEnableClientState(GL11.GL_NORMAL_ARRAY);
//        }
        base.setupArrayPointers();
        vbo.drawArrays(base.GetDrawMode());
        vbo.unbindBuffer();
    }

    public void Delete()
    {
        if (vbo != null)
        {
//            Logger.debugInfo("rendererVbo::Delete : "+base);
            vbo.deleteGlBuffers();
            vbo = null;
        }
    }



    public static void SetupArrayPointersForPosTexNormal(VertexFormat format)
    {
        int stride = format.getNextOffset();
        GlStateManager.glVertexPointer(3, GL11.GL_FLOAT, stride, 0);
        GlStateManager.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        GlStateManager.glTexCoordPointer(2, GL11.GL_FLOAT, stride, 12);
        GlStateManager.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        GL11.glNormalPointer(GL11.GL_BYTE, stride, 20);
        GlStateManager.glEnableClientState(GL11.GL_NORMAL_ARRAY);

        GlStateManager.glDisableClientState(GL11.GL_COLOR_ARRAY);
    }

    public static void SetupArrayPointersForBlocks(VertexFormat format)
    {
        int stride = format.getNextOffset();
        GlStateManager.glVertexPointer(3, GL11.GL_FLOAT, stride, 0);
        GlStateManager.glColorPointer(4, GL11.GL_UNSIGNED_BYTE, stride, 12);
        GlStateManager.glTexCoordPointer(2, GL11.GL_FLOAT, stride, 16);
        OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.glTexCoordPointer(2, GL11.GL_SHORT, stride, 24);
        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        GlStateManager.glEnableClientState(GL11.GL_COLOR_ARRAY);
        GlStateManager.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
    }
}
