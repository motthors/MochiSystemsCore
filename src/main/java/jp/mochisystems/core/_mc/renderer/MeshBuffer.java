package jp.mochisystems.core._mc.renderer;

import jp.mochisystems.core._mc._core._Core;
import jp.mochisystems.core.bufferedRenderer.CachedBufferBase;
import jp.mochisystems.core.bufferedRenderer.RendererVbo;
import jp.mochisystems.core.util.HashMaker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.pipeline.LightUtil;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class MeshBuffer extends CachedBufferBase {

    private IBakedModel modelCoaster;
    private ResourceLocation TextureResource;
    private int hash;
    private boolean isInit = false;

    @SuppressWarnings("unused")
    private MeshBuffer(){}

    public MeshBuffer(IBakedModel Obj, ResourceLocation Tex, String id)
    {
        assert Obj != null;
        assert Tex != null;
        modelCoaster = Obj;
        TextureResource = Tex;
        hash = new HashMaker().Append("MESHBUFFER").Append(id).GetHash();
    }

    @Override
    public int GetHash() {
        return hash;
    }

    @Override
    public int GetDrawMode(){return GL11.GL_QUADS;}
    @Override
    protected VertexFormat GetVertexFormat(){return DefaultVertexFormats.POSITION_TEX_NORMAL;}
    @Override
    public void setupArrayPointers()
    {
        RendererVbo.SetupArrayPointersForPosTexNormal(GetVertexFormat());
    }


    @Override
    protected void Compile() {
//        _Core.BindBlocksTextureMap();
//        TileEntityRendererDispatcher.instance.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
//        TileEntityRendererDispatcher.instance.renderEngine.bindTexture(TextureResource);
        List<BakedQuad> quads = modelCoaster.getQuads(null, null, 0);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        for (BakedQuad quad : quads)
        {
            LightUtil.renderQuadColor(bufferBuilder, quad, -1);
        }
//        bufferBuilder.pos(0, 0, 1).tex(0, 1).normal(1, 0, 0).endVertex();
//        bufferBuilder.pos(1, 0, 1).tex(1, 1).normal(1, 0, 0).endVertex();
//        bufferBuilder.pos(1, 0, 0).tex(1, 0).normal(1, 0, 0).endVertex();
//        bufferBuilder.pos(0, 0, 0).tex(0, 0).normal(1, 0, 0).endVertex();
//        tessellator.draw();
    }

    @Override
    public void PreRender()
    {
        TileEntityRendererDispatcher.instance.renderEngine.bindTexture(TextureResource);
//        TileEntityRendererDispatcher.instance.renderEngine.bindTexture(new ResourceLocation("exrollercoaster", "textures/entities/sushi.jpg"));
//        _Core.BindBlocksTextureMap();
    }
}