package jp.mochisystems.core.blockcopier;

import jp.mochisystems.core._mc.block.*;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class BlocksScanner {

//    public byte progressState = 0; // 0:no craft 1:isCrafting 2:OnComplete
    private final BlocksCompressor compressor = new BlocksCompressor();
    protected IBLockCopyHandler handler;
    private boolean isDrawEntity;
    private NBTTagCompound nbt;

    protected int srcPosMinX, srcPosMinY, srcPosMinZ;
    protected int srcPosMaxX, srcPosMaxY, srcPosMaxZ;
    protected int sizeX, sizeY, sizeZ;
    protected int nowBlockArrayIndex;
    protected BlockPiece[][][] BlockArray;

    public int blockCount;

    protected static class BlockPiece{
        public IBlockState state = Blocks.AIR.getDefaultState();
        public TileEntity tile = null;
        public boolean excluded = false;
    }

    public int SizeX(){return sizeX;}
    public int SizeY(){return sizeY;}
    public int SizeZ(){return sizeZ;}
    public NBTTagCompound GetTag()
    {
        return nbt;
    }
    public int getAllBlockNum()
    {
        return sizeX * sizeY * sizeZ;
    }
    public int getNowBlockArrayIndex()
    {
        return nowBlockArrayIndex;
    }
//    public boolean isCrafting()
//    {
//        return progressState == 1;
//    }

    public float GetProgress()
    {
        return (float) getNowBlockArrayIndex() / (float) getAllBlockNum() * 9/10
                + compressor.zipCompressor.currentProgress() * 1/10;
    }


    public BlocksScanner(IBLockCopyHandler handler)
    {
        nbt = new NBTTagCompound();
        this.handler = handler;
    }

    private void SetSrcPosition(boolean trueCopy, int xmin, int ymin, int zmin, int xmax, int ymax, int zmax)
    {
        // expand 1 position because register connection from outside block
        int d = trueCopy ? 1 : 0;
        srcPosMinX = xmin-d;
        srcPosMinY = ymin-d;
        srcPosMinZ = zmin-d;
        srcPosMaxX = xmax+d;
        srcPosMaxY = ymax+d;
        srcPosMaxZ = zmax+d;
        sizeX = srcPosMaxX - srcPosMinX + 1;
        sizeY = srcPosMaxY - srcPosMinY + 1;
        sizeZ = srcPosMaxZ - srcPosMinZ + 1;
        allocBlockArray(sizeX, sizeY, sizeZ);
//        Logger.debugInfo("set scan pos : "+srcPosMinX+"."+srcPosMinY+"."+srcPosMinZ+" -> "+srcPosMaxX+"."+srcPosMaxY+"."+srcPosMaxZ);
    }

    public void RegisterSettings(int x, int y, int z, LimitFrame limit, boolean isDrawEntity, boolean TrueCopy)
    {
        this.isDrawEntity = isDrawEntity;
        SetSrcPosition(TrueCopy,
                x+limit.getmx(), y+limit.getmy(), z+limit.getmz(),
                x+limit.getxx(), y+limit.getxy(), z+limit.getxz());
//        StartParallelScan();
    }
    public void StartParallelScan()
    {
        new CopyThread().start();
    }



    protected void allocBlockArray(int x, int y, int z)
    {
        blockCount = 0;
        BlockArray = new BlockPiece[x][y][z];
        for(int i=0;i<x;++i)
            for(int j=0;j<y;++j)
                for(int k=0;k<z;++k)
                    BlockArray[i][j][k] = new BlockPiece();
    }

//    public void UpdateProgressStatus()
//    {
//        if (this.progressState != 0)
//        {
//            if (this.progressState == 2)
//            {
//                this.progressState = 0;
//                handler.OnCompleteScan();
//            }
//        }
//    }

    private void progress() {
        try {
            Register();
            handler.OnCompleteScan();
//            onCompleteScan.run();
//            progressState = 2;
        }
        catch (Exception e) {
//            progressState = 0;
            throw e;
        }
    }

    public void Register()
    {
        NBTTagCompound model = new NBTTagCompound();
        SearchBlocks();
        compressor.CompressAndWriteTo(model, BlockArray);
        if (isDrawEntity) RegisterEntities(model, handler);
        nbt.setTag("model", model);
        handler.registerExternalParam(model, nbt);
        makeTag(nbt, handler);
    }

    private void SearchBlocks()
    {
        nowBlockArrayIndex = 0;

        // search block
        for (BlockPos pos : BlockPos.getAllInBox(srcPosMinX, srcPosMinY, srcPosMinZ, srcPosMaxX, srcPosMaxY, srcPosMaxZ))
        {
            ++nowBlockArrayIndex;

            IBlockState state = handler.GetBlockState(pos);
            TileEntity tile = handler.getTileEntity(pos);
            Block block = state.getBlock();
//            if(!(block instanceof BlockAir)) Logger.debugInfo(block.getLocalizedName()+":"+state.getBlock().getMetaFromState(state));
            if(block instanceof BlockExcluder)
            {
                cut((BlockExcluder)tile);
                continue;
            }
            if (isExcludedBlock(block))
                continue;
            setBlock(state, pos, tile);
        }
    }

    public void RegisterEntities(NBTTagCompound nbt, IBLockCopyHandler handler)
    {
        List<Entity> listentity = handler.getEntities(srcPosMinX -1, srcPosMinY -3, srcPosMinZ -1, srcPosMaxX +1, srcPosMaxY +3, srcPosMaxZ +1);
//        nbt.setInteger("mtybr:entitynum", listentity.size());
        int i = 0;
        NBTTagList nbttaglist = new NBTTagList();
        for(Entity e : listentity)
        {
            NBTTagCompound nbtentity = new NBTTagCompound();
            e.writeToNBT(nbtentity);
            nbtentity.setString("entityname", e.getClass().getName());
            nbttaglist.appendTag(nbtentity);
            ++i;
        }
        nbt.setTag("entities", nbttaglist);
    }

    protected boolean isExcludedBlock(Block block)
    {
        if(block instanceof BlockRemoteController) return true;
        if(block instanceof BlockBlockScannerBase) return true;
        if(block instanceof BlockModelCoreBase) return true;
        if(block instanceof BlockFileManager)return true;
        if(block instanceof BlockModelCutter)return true;
        return false;
    }

    protected void setBlock(IBlockState state, BlockPos pos, TileEntity tile)
    {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        if(BlockArray[x - srcPosMinX][y - srcPosMinY][z - srcPosMinZ].excluded) return;

        BlockArray[x - srcPosMinX][y - srcPosMinY][z - srcPosMinZ].state = state;
        BlockArray[x - srcPosMinX][y - srcPosMinY][z - srcPosMinZ].tile = tile;

        if(state.getBlock() != Blocks.AIR) blockCount++;
    }

    private void cut(BlockExcluder excluder)
    {
        for(int X = excluder.getMinX(); X < excluder.getMaxX(); ++X)
            for(int Y = excluder.getMinY(); Y < excluder.getMaxY(); ++Y)
                for(int Z = excluder.getMinZ(); Z < excluder.getMaxZ(); ++Z)
                {
                    if( X - srcPosMinX < 0 || sizeX <= X - srcPosMinX) continue;
                    if( Y - srcPosMinY < 0 || sizeY <= Y - srcPosMinY) continue;
                    if( Z - srcPosMinZ < 0 || sizeZ <= Z - srcPosMinZ) continue;
                    BlockArray[X - srcPosMinX][Y - srcPosMinY][Z - srcPosMinZ].excluded = true;
                }
    }

    protected void makeTag(NBTTagCompound nbt, IBLockCopyHandler handler)
    {
        NBTTagCompound model = (NBTTagCompound) nbt.getTag("model");
        model.setInteger("mtybr:sizex", sizeX);
        model.setInteger("mtybr:sizey", sizeY);
        model.setInteger("mtybr:sizez", sizeZ);
    }




    //////////// Thread ///////////

    private class CopyThread extends Thread {
        @Override
        public void run() {
            progress();
        }
    }

}
