package mochisystems.blockcopier;

import mochisystems._core._Core;
import mochisystems.bufferedrenderer.IBufferedRenderer;
import net.minecraft.client.renderer.Tessellator;


public class CopiedBlockRenderer extends IBufferedRenderer {

    private final BlocksRenderer constructor;
    private int hash = 0;
    private int pass, start, end;

    CopiedBlockRenderer(BlocksRenderer constructor, int pass, int start, int end, int hash)
    {
        this.constructor = constructor;
        this.pass = pass;
        this.start = start;
        this.end = end;
        this.hash = hash;
    }

    @Override
    public int GetHash() {
        return hash;
    }

    @Override
    protected void Draw() {
        Tessellator.instance.startDrawingQuads();
        constructor.renderBlock(pass, start, end);
        Tessellator.instance.draw();
    }

    @Override
    protected void PreRender()
    {
        _Core.BindBlocksTextureMap();
    }
}
