package mochisystems.blockcopier;

import mochisystems._core._Core;
import mochisystems.bufferedrenderer.IBufferedRenderer;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.GL11;


public class CopiedBlockRenderer extends IBufferedRenderer {

    private final BlocksRenderer constructor;
    private int hash = 0;
    private int pass;
    private int cx, cy, cz;

    CopiedBlockRenderer(BlocksRenderer constructor, int cx, int cy, int cz, int pass, int hash)
    {
        this.constructor = constructor;
        this.pass = pass;
        this.cx = cx;
        this.cy = cy;
        this.cz = cz;
        this.hash = hash;
    }

    @Override
    public int GetHash() {
        return hash;
    }

    @Override
    protected void Draw() {
        GL11.glTranslatef(-cx*16, -cy*16, -cz*16);
        Tessellator.instance.startDrawingQuads();
        constructor.renderBlock(pass, cx, cy, cz);
        Tessellator.instance.draw();
//        GL11.glTranslatef(cx*16, cy*16, cz*16);
    }

    @Override
    protected void PreRender()
    {
        _Core.BindBlocksTextureMap();
        GL11.glPushMatrix();
        GL11.glTranslatef(cx*16, cy*16, cz*16);
    }

    @Override
    protected void PostRender()
    {
        GL11.glPopMatrix();
    }
}
