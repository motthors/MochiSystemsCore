package jp.mochisystems.core._mc.renderer;

import jp.mochisystems.core._mc.block.BlockSeatPositionMarker;
import jp.mochisystems.core._mc.world.MTYBlockAccess;

import jp.mochisystems.core.blockcopier.CopiedBlockRenderer;
import jp.mochisystems.core.util.HashMaker;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;

import java.util.LinkedList;

public class BlocksRenderer {

    protected BlockRendererDispatcher renderBlocks;
    protected MTYBlockAccess blockAccess;

    private final LinkedList<CopiedBlockRenderer> renderedList = new LinkedList<>();
    private final LinkedList<CopiedBlockRenderer> renderingList = new LinkedList<>();

    private final LinkedList<CopiedBlockRenderer> postRenderedList = new LinkedList<>();
    private final LinkedList<CopiedBlockRenderer> postRenderingList = new LinkedList<>();

    public float renderOffsetX = 0, renderOffsetY = 0, renderOffsetZ = 0;

//	private final RegionRenderCacheBuilder regionRenderCacheBuilder = new RegionRenderCacheBuilder();

	public BlocksRenderer(MTYBlockAccess ba)
	{
        renderBlocks = Minecraft.getMinecraft().getBlockRendererDispatcher();
		blockAccess = ba;
//        builder = new RegionRenderCacheBuilder();
//        ((IReloadableResourceManager)Minecraft.getMinecraft().getResourceManager()).registerReloadListener(this);
	}

	public void CompileRenderer()
	{
        CompileRender(BlockRenderLayer.SOLID);
		CompileRender(BlockRenderLayer.TRANSLUCENT);
        renderOffsetX = -blockAccess.originalCorePosX - (blockAccess.IsTrueCopy() ? 1.5f : 0.5f);
        renderOffsetY = -blockAccess.originalCorePosY - (blockAccess.IsTrueCopy() ? 1.5f : 0.5f);
        renderOffsetZ = -blockAccess.originalCorePosZ - (blockAccess.IsTrueCopy() ? 1.5f : 0.5f);
    }

    private void CompileRender(BlockRenderLayer layer) {
        LinkedList<CopiedBlockRenderer> renderers = layer == BlockRenderLayer.TRANSLUCENT ? postRenderingList : renderingList;
        int d = blockAccess.IsTrueCopy() ? 1 : 0;

        int csizeX = blockAccess.getSizeX() / 16 + 1;
        int csizeY = blockAccess.getSizeY() / 16 + 1;
        int csizeZ = blockAccess.getSizeZ() / 16 + 1;
        int startHash = 1;
        for (int cx = 0; cx < csizeX; ++cx) {
            for (int cz = 0; cz < csizeZ; ++cz) {
                for (int cy = 0; cy < csizeY; ++cy) {
                    HashMaker hasher = new HashMaker(startHash);
                    for (int _x = 0; _x < 16; ++_x) {
                        int x = _x + cx * 16;
                        if (x >= blockAccess.getSizeX() - d || x < d) continue;
                        for (int _z = 0; _z < 16; ++_z) {
                            int z = _z + cz * 16;
                            if (z >= blockAccess.getSizeZ() - d || z < d) continue;
                            for (int _y = 0; _y < 16; ++_y) {
                                int y = _y + cy * 16;
                                if (y >= blockAccess.getSizeY() - d || y < d) continue;
                                if (blockAccess.isAirBlockLocal(x, y, z)) continue;
                                IBlockState state = blockAccess.getBlockStateLocal(x, y, z);
                                if(state.getBlock().getBlockLayer() != layer) continue;
                                hasher.Append(Block.getIdFromBlock(state.getBlock()));
                                hasher.Append(state.getBlock().getMetaFromState(state));
                                hasher.Append(_x);
                                hasher.Append(_y);
                                hasher.Append(_z);
                            }
                        }
                    }
//
                    CalcExtHash(hasher);
                    int hash = hasher.GetHash();
                    if(hash == startHash) continue;
                    CopiedBlockRenderer renderer = new CopiedBlockRenderer(this, cx, cy, cz, hash, layer == BlockRenderLayer.TRANSLUCENT);
                    renderer.SetDirty();
                    renderers.add(renderer);
                }
            }
        }

	}

    protected void CalcExtHash(HashMaker hasher)
    {

    }

    public void renderBlock(int cx, int cy, int cz, boolean isTranslucent)
    {
        int d = blockAccess.IsTrueCopy() ? 1 : 0;
        for (int _z = 0; _z < 16; ++_z) {
            int z = _z + cz * 16 + d;
            if (z > blockAccess.getSizeZ() - d || z < d) continue;
            for (int _y = 0; _y < 16; ++_y) {
                int y = _y + cy * 16 + d;
                if (y > blockAccess.getSizeY() - d || y < d) continue;
                for (int _x = 0; _x < 16; ++_x) {
                    int x = _x + cx * 16 + d;
                    if (x > blockAccess.getSizeX() - d || x < d) continue;
                    IBlockState state = blockAccess.getBlockStateLocal(x, y, z);
                    if(state.getBlock() instanceof BlockSeatPositionMarker) continue;
                    if(isTranslucent ^ state.getBlock().getBlockLayer() == BlockRenderLayer.TRANSLUCENT) continue;

                    renderBlocks.renderBlock(state,
                        new BlockPos(
                            x+blockAccess.originalCorePosX-blockAccess.GetLocalCorePosX(),
                            y+blockAccess.originalCorePosY-blockAccess.GetLocalCorePosY(),
                            z+blockAccess.originalCorePosZ-blockAccess.GetLocalCorePosZ()),
                        blockAccess,
//                            builder.getWorldRendererByLayer(state.getBlock().getBlockLayer())
                        Tessellator.getInstance().getBuffer() //RegionRenderCacheBuilderを使う？TODO
//                        _Core.BlockModelBuffer.getWorldRendererByLayer(BlockRenderLayer.SOLID)
                    );

//                    TileEntity tile = blockAccess.GetTileEntityLocal(x, y, z);
//                    if(tile != null) {
//                        TileEntityRendererDispatcher.instance.render(tile, x, y, z, 1);
//                    }
//                    Logger.debugInfo("renderBlocks : "+state.getBlock().getLocalizedName());
                }
            }
        }
    }
	
	int GLCallListEntity;
	int isRenderEntity = -2;
	public void CompileRenderEntity_test()
	{
//		if(blockAccess.getEntityNum() <= 0)return;
//		GLCallListEntity = GLAllocation.generateDisplayLists(1);
//		GL11.glNewList(this.GLCallListEntity, GL11.GL_COMPILE);
//		blockAccess.renderEntity();
//		GL11.glEndList();
//		isRenderEntity = 1;
	}
	public void RenderEntity()
	{
		//entity
//		if(isRenderEntity==1)GL11.glCallList(this.GLCallListEntity);
//		else CompileRenderEntity_test();
        for(int i = 0; i < blockAccess.getEntityNum(); ++i)
        {
            Entity e = blockAccess.getEntity(i);
            Minecraft.getMinecraft().getRenderManager().getEntityClassRenderObject(e.getClass()).doRender(e, e.posX, e.posY, e.posZ, 0, 1);
        }
	}
    public void RenderTileEntity()
    {
        for(int i = 0; i < blockAccess.listTileEntity.size(); ++i)
        {
            TileEntity tile = blockAccess.listTileEntity.get(i);
            TileEntityRendererDispatcher.instance.render(tile, tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ(), 1);
        }
    }

	public void render()
	{
        GL11.glTranslatef(renderOffsetX, renderOffsetY, renderOffsetZ);

        GL11.glDisable(GL11.GL_LIGHTING);

        if(renderingList.size() > 0)
        {
            renderedList.add(renderingList.removeFirst());
        }
		for(CopiedBlockRenderer renderer : renderedList) renderer.Render();
		
		GL11.glEnable(GL11.GL_LIGHTING);

        RenderEntity();
        RenderTileEntity();

    }
	
	public void render2()
	{
        GL11.glTranslatef(renderOffsetX, renderOffsetY, renderOffsetZ);

        if(postRenderingList.size() > 0)
        {
            postRenderedList.add(postRenderingList.removeFirst());
        }
        for(CopiedBlockRenderer renderer : postRenderedList) renderer.Render();

        GL11.glEnable(GL11.GL_LIGHTING);
	}

	public void SetDirty()
    {
        for(CopiedBlockRenderer renderer : renderedList) renderer.SetDirty();
        for(CopiedBlockRenderer renderer : postRenderedList) renderer.SetDirty();
    }

	public void delete()
	{
        for(CopiedBlockRenderer renderer : renderedList) renderer.DeleteBuffer();
        for(CopiedBlockRenderer renderer : postRenderedList) renderer.DeleteBuffer();
        renderedList.clear();
        postRenderedList.clear();
        renderingList.clear();
        postRenderingList.clear();
//		if(GLCallList>0)renderEventCompileWheel.setDeleteList(GLCallList,GLCallListNum);
//		if(GLCallListPost>0)renderEventCompileWheel.setDeleteList(GLCallListPost,GLCallListPostNum);
//		if(GLCallListCore>0)renderEventCompileWheel.setDeleteList(GLCallListCore,1);
	}



}