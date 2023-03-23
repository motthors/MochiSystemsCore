package jp.mochisystems.core.blockcopier;

import jp.mochisystems.core._mc._core._Core;
import jp.mochisystems.core.bufferedRenderer.CachedBufferBase;
import jp.mochisystems.core._mc.renderer.BlocksRenderer;
import jp.mochisystems.core.bufferedRenderer.RendererVbo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.init.Blocks;
import org.lwjgl.opengl.GL11;


public class CopiedBlockRenderer extends CachedBufferBase {

    private final BlocksRenderer constructor;
    private int hash = 0;
    private final int cx, cy, cz;
    private boolean isTranslucent;

    public CopiedBlockRenderer(BlocksRenderer constructor, int cx, int cy, int cz, int hash, boolean isTranslucent)
    {
        this.constructor = constructor;
        this.cx = cx;
        this.cy = cy;
        this.cz = cz;
        this.hash = hash;
        this.isTranslucent = isTranslucent;
    }

    @Override
    public int GetDrawMode(){return GL11.GL_QUADS;}
    @Override
    protected VertexFormat GetVertexFormat(){return DefaultVertexFormats.BLOCK;}
    @Override
    public void setupArrayPointers()
    {
        RendererVbo.SetupArrayPointersForBlocks(GetVertexFormat());
    }

    @Override
    public int GetHash() {
        return hash;
    }

    @Override
    protected void Compile() {
        constructor.renderBlock(cx, cy, cz, isTranslucent);
    }

    @Override
    protected void PreRender()
    {
        _Core.BindBlocksTextureMap();
        GL11.glPushMatrix();
//        GL11.glTranslatef(cx*16, cy*16, cz*16);
    }

    @Override
    protected void PostRender()
    {
        GL11.glPopMatrix();
    }
}
