package mochisystems.blockcopier;

import mochisystems._core.Logger;
import mochisystems.util.HashMaker;
import org.lwjgl.opengl.GL11;

import mochisystems.blockcopier.MTYBlockAccess.renderPiece;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.init.Blocks;

import java.util.*;
import java.util.LinkedList;

public class BlocksRenderer {

	protected RenderBlocks renderBlocks;
	protected MTYBlockAccess blockAccess;
	
//	protected int GLCallList = -1;
//	protected int GLCallListPost = -1;
//	protected int GLCallListNum = 0;
//	protected int GLCallListPostNum = 0;
//	protected int isCompile = -2;		//flags  -2:before Blockaccess done copy -1:compiled  0:init compile  1~:compiling
//	protected int isCompilePost = -2;	//flags  -2:before Blockaccess done copy -1:compiled  0:init compile  1~:compiling
	protected final int blockNumPer1List = 5000;
//	protected int GLCallListCore = -1;

	protected float offsetX = 0;
	protected float offsetY = 0;
	protected float offsetZ = 0;

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

    private void CompileRneder(int pass)
	{
	    LinkedList<CopiedBlockRenderer> renderers = pass == 0 ? renderingList : postRenderingList;
        ArrayList<renderPiece> blocks = pass == 0 ? blockAccess.listPrePass : blockAccess.listPostPass;
        int size = blocks.size();
        int start = 0;
        while(start < size)
        {
            int end = start + blockNumPer1List;
            end = (end < blocks.size()) ? end : blocks.size() - 1;
            int hash = CalcBlocksHash(pass, start, end, blockAccess.sizeX, blockAccess.sizeY, blockAccess.sizeZ);
            CopiedBlockRenderer renderer = new CopiedBlockRenderer(this, pass, start, end, hash);
            renderer.SetDirty();
            renderers.add(renderer);
            Logger.debugInfo("set renderer : " + hash);//TODO けす
            start = end + 1;
        }
	}

    protected int CalcBlocksHash(int pass, int start, int end, int widthX, int widthY, int widthZ)
    {
        ArrayList<renderPiece> blocks = pass == 0 ? blockAccess.listPrePass : blockAccess.listPostPass;
        HashMaker hasher = new HashMaker();

        for (int i = start; i <= end; i++)
            hasher.Append(Block.blockRegistry.getIDForObject(blocks.get(i).b));
        hasher.Append(widthX);
        hasher.Append(widthY);
        hasher.Append(widthZ);
        return hasher.GetHash();
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
//        TextureManager texturemanager = TileEntityRendererDispatcher.instance.field_147553_e;
//        texturemanager.bindTexture(TextureMap.locationBlocksTexture);

        GL11.glDisable(GL11.GL_LIGHTING);

        if(renderingList.size() > 0)
        {
            renderedList.add(renderingList.removeFirst());
            Logger.debugInfo("render");
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

    void renderBlock(int pass, int start, int end)
    {
        Tessellator.instance.setNormal(0, 1, 0);
        ArrayList<renderPiece> blocks = pass == 0 ? blockAccess.listPrePass : blockAccess.listPostPass;
        for(int i = start; i <= end; ++i)
        {
            renderPiece piece = blocks.get(i);
            if( piece.b == Blocks.air) continue;
            renderBlocks.renderBlockByRenderType(piece.b, piece.x, piece.y, piece.z);
//            Logger.debugInfo(Block.blockRegistry.getNameForObject(piece.b));
        }
    }


}