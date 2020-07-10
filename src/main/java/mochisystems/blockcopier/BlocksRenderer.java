package mochisystems.blockcopier;

import mochisystems.util.HashMaker;
import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.init.Blocks;

import java.util.LinkedList;

public class BlocksRenderer {

	protected RenderBlocks renderBlocks;
	protected MTYBlockAccess blockAccess;

    private final LinkedList<CopiedBlockRenderer> renderedList = new LinkedList<>();
    private final LinkedList<CopiedBlockRenderer> renderingList = new LinkedList<>();

    private final LinkedList<CopiedBlockRenderer> postRenderedList = new LinkedList<>();
    private final LinkedList<CopiedBlockRenderer> postRenderingList = new LinkedList<>();

	
	public BlocksRenderer(MTYBlockAccess ba)
	{
		renderBlocks = new RenderBlocks(ba);
		blockAccess = ba;
	}

	public void CompileRenderer()
	{
		CompileRneder(0);
		CompileRneder(1);
	}

    private void CompileRneder(int pass) {
        LinkedList<CopiedBlockRenderer> renderers = pass == 0 ? renderingList : postRenderingList;

        int csizeX = blockAccess.sizeX / 16 + 1;
        int csizeY = blockAccess.sizeY / 16 + 1;
        int csizeZ = blockAccess.sizeZ / 16 + 1;
        int startHash = 1;
        for (int cx = 0; cx < csizeX; ++cx) {
            for (int cz = 0; cz < csizeZ; ++cz) {
                for (int cy = 0; cy < csizeY; ++cy) {
                    HashMaker hasher = new HashMaker(startHash);
                    for (int _x = 0; _x < 16; ++_x) {
                        int x = _x + cx * 16 + 1;
                        if (x >= blockAccess.sizeX - 1 || x <= 0) continue;
                        for (int _z = 0; _z < 16; ++_z) {
                            int z = _z + cz * 16 + 1;
                            if (z >= blockAccess.sizeZ - 1 || z <= 0) continue;
                            for (int _y = 0; _y < 16; ++_y) {
                                int y = _y + cy * 16 + 1;
                                if (y >= blockAccess.sizeY - 1 || y <= 0) continue;
                                Block b = blockAccess.getBlockOrgPos(x, y, z);
                                if(b.getRenderBlockPass() != pass) continue;
                                if (b == Blocks.air) continue;
                                hasher.Append(Block.blockRegistry.getIDForObject(b));
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
//                    Logger.debugInfo("precheck:"+cx+"."+cy+"."+cz+":");
                    CopiedBlockRenderer renderer = new CopiedBlockRenderer(this, cx, cy, cz, pass, hash);
                    renderer.SetDirty();
                    renderers.add(renderer);
//                    Logger.debugInfo("set renderer : " + hash + " -- chunk:"+cx+"."+cy+"."+cz+":"+pass);//TODO けす
                }
            }
        }

	}

    protected void CalcExtHash(HashMaker hasher)
    {

    }

    void renderBlock(int pass, int cx, int cy, int cz)
    {
//        Tessellator.instance.setNormal(0, 1, 0);//TODO これなに
//        Logger.debugInfo("chunk:"+cx+"."+cy+"."+cz+":"+pass);
        for (int _x = 0; _x < 16; ++_x) {
            int x = _x + cx * 16 + 1;
            if (x >= blockAccess.sizeX - 1 || x <= 0) continue;
            for (int _z = 0; _z < 16; ++_z) {
                int z = _z + cz * 16 + 1;
                if (z >= blockAccess.sizeZ - 1 || z <= 0) continue;
                for (int _y = 0; _y < 16; ++_y) {
                    int y = _y + cy * 16 + 1;
                    if (y >= blockAccess.sizeY - 1 || y <= 0) continue;
                    Block b = blockAccess.getBlockOrgPos(x, y, z);
                    if(b.getRenderBlockPass() != pass) continue;
                    if (b == Blocks.air) continue;
//                    Logger.debugInfo(x+"."+y+"."+z+":"+blockAccess.getBLockMetadata_AbsolutePos(x,y,z)+" : "+b.getUnlocalizedName());
                    renderBlocks.renderBlockByRenderType(b,
                            x+blockAccess.originalCorePosX-blockAccess.localCorePosX,
                            y+blockAccess.originalCorePosY-blockAccess.localCorePosY,
                            z+blockAccess.originalCorePosZ-blockAccess.localCorePosZ);
        //            Logger.debugInfo(Block.blockRegistry.getNameForObject(piece.b));
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
	public void renderEntity_test()
	{
		//entity
//		if(isRenderEntity==1)GL11.glCallList(this.GLCallListEntity);
//		else CompileRenderEntity_test();
	}

	public void render()
	{
        GL11.glTranslatef(
            -blockAccess.originalCorePosX - 1.5f,
            -blockAccess.originalCorePosY - 1.5f,
            -blockAccess.originalCorePosZ - 1.5f);
//        _Core.BindBlocksTextureMap();

        GL11.glDisable(GL11.GL_LIGHTING);

        if(renderingList.size() > 0)
        {
            renderedList.add(renderingList.removeFirst());
        }
		for(CopiedBlockRenderer renderer : renderedList) renderer.Render();
		
		GL11.glEnable(GL11.GL_LIGHTING);
	}
	
	public void render2()
	{
        GL11.glTranslatef(
            -blockAccess.originalCorePosX - 1.5f,
            -blockAccess.originalCorePosY - 1.5f,
            -blockAccess.originalCorePosZ - 1.5f);
        TextureManager texturemanager = TileEntityRendererDispatcher.instance.field_147553_e;
        if (texturemanager != null) texturemanager.bindTexture(TextureMap.locationBlocksTexture);

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