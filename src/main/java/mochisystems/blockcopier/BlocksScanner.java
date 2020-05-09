package mochisystems.blockcopier;

import mochisystems._mc.block.BlockRemoteController;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlocksScanner {

    private byte progressState = 0; // 0:no craft 1:isCrafting 2:OnComplete
    private BlocksCompressor compressor = new BlocksCompressor();;
    private IBLockCopyHandler handler;
    private boolean isDrawEntity;
    private NBTTagCompound nbt = new NBTTagCompound();

    protected int srcPosMinX, srcPosMinY, srcPosMinZ;
    protected int srcPosMaxX, srcPosMaxY, srcPosMaxZ;
    protected int sizeX, sizeY, sizeZ;
    protected int nowBlockArrayIndex;
    protected BlockPiece[][][] BlockArray;
    Map<String, Integer> nameMap;
    List<String> nameList;

    protected class BlockPiece{
        public Block type = Blocks.air;
        public int meta;
        public TileEntity tile = null;
        public boolean excluded = false;
    }

    public NBTTagCompound GetNbt(){ return nbt; }
    public int getAllBlockNum()
    {
        return sizeX * sizeY * sizeZ;
    }
    public int getNowBlockArrayIndex()
    {
        return nowBlockArrayIndex;
    }
    public boolean isCrafting()
    {
        return progressState == 1;
    }

    public float GetProgress()
    {
        return (float) getNowBlockArrayIndex() / (float) getAllBlockNum() * 9/10
                + compressor.zipCompressor.currentProgress() * 1/10;
    }

    public void StartCopy(IBLockCopyHandler handler, int x, int y, int z, LimitFrame limit, boolean isDrawEntity)
    {
        if(progressState == 0)
        {
            Init(handler, x, y, z, limit, isDrawEntity);
            new CopyThread().start();
        }
    }

    public void Init(IBLockCopyHandler handler, int x, int y, int z, LimitFrame limit, boolean isDrawEntity)
    {
        nbt = new NBTTagCompound();
        progressState = 1;
        this.handler = handler;
        this.isDrawEntity = isDrawEntity;
        setSrcPosition(
                x+limit.getmx(), y+limit.getmy(), z+limit.getmz(),
                x+limit.getxx(), y+limit.getxy(), z+limit.getxz());
    }

    public void setSrcPosition(int xmin, int ymin, int zmin, int xmax, int ymax, int zmax)
    {
        // expand 1 position because register connection from outside block
        srcPosMinX = xmin-1;
        srcPosMinY = ymin-1;
        srcPosMinZ = zmin-1;
        srcPosMaxX = xmax+1;
        srcPosMaxY = ymax+1;
        srcPosMaxZ = zmax+1;
        sizeX = srcPosMaxX - srcPosMinX + 1;
        sizeY = srcPosMaxY - srcPosMinY + 1;
        sizeZ = srcPosMaxZ - srcPosMinZ + 1;
        allocBlockArray(sizeX, sizeY, sizeZ);
    }

    protected void allocBlockArray(int x, int y, int z)
    {
        BlockArray = new BlockPiece[x][y][z];
        for(int i=0;i<x;++i)
            for(int j=0;j<y;++j)
                for(int k=0;k<z;++k)
                    BlockArray[i][j][k] = new BlockPiece();
    }

    public void UpdateProgressStatus()
    {
        if (this.progressState != 0)
        {
            if (this.progressState == 2)
            {
                this.progressState = 0;
                if (handler != null) handler.OnComplete(nbt);
            }
        }
    }

    private void progress() {
        try {
            Register(null);
            progressState = 2;
        }
        catch (Exception e) {
            progressState = 0;
            throw e;
        }
    }

    public void Register(NBTTagCompound model)
    {
        if(model == null) model = new NBTTagCompound();
        nameMap = new HashMap<>();
        nameList = new ArrayList<>();
        nameList.add(Block.blockRegistry.getNameForObject(Blocks.air));
        nameMap.put(Block.blockRegistry.getNameForObject(Blocks.air), 0);
        SearchBlocks();
        compressor.RegisterBlocks(model, handler, BlockArray, nameMap, nameList);
        if (isDrawEntity) RegisterEntities(model, handler);
        nbt.setTag("model", model);
        handler.registerExternalParam(model);
        makeTag(nbt, handler);
    }

    private void SearchBlocks()
    {
        nowBlockArrayIndex = 0;
        int nameIndex = 0;

        // search block
        for (int z = srcPosMinZ; z <= srcPosMaxZ; ++z) {
            for (int y = srcPosMinY; y <= srcPosMaxY; ++y) {
                for (int x = srcPosMinX; x <= srcPosMaxX; ++x) {
                    ++nowBlockArrayIndex;
                    Block block = handler.getBlock(x, y, z);
                    if (block == null) continue;
                    if (handler.isExceptBlock(block)) continue;

//                    Logger.debugInfo("blocksCopier : "+block.getLocalizedName()+"\t {"+x+"."+y+"."+z+"}");
                    String name = Block.blockRegistry.getNameForObject(block);
                    if (!nameMap.containsKey(name)) {
                        nameIndex++;
                        nameMap.put(name, nameIndex);
                        nameList.add(name);
                    }
                    int meta = handler.getBlockMetadata(x, y, z);
                    TileEntity tile = handler.getTileEntity(x, y, z);
                    setBlock(block, meta, x, y, z, tile);
                }
            }
        }
    }

    public void RegisterEntities(NBTTagCompound nbt, IBLockCopyHandler handler)
    {
        AxisAlignedBB aabbForEntity = AxisAlignedBB.getBoundingBox(srcPosMinX -1, srcPosMinY -3, srcPosMinZ -1, srcPosMaxX +1, srcPosMaxY +3, srcPosMaxZ +1);
        List<Entity> listentity = handler.getEntities(aabbForEntity);
        nbt.setInteger("mtybr:entitynum", listentity.size());
        int i = 0;
        for(Entity e : listentity)
        {
            NBTTagCompound nbtentity = new NBTTagCompound();
            e.writeToNBT(nbtentity);
            nbtentity.setString("entityname", e.getClass().getName());
            nbt.setTag("entity."+i, nbtentity);
            ++i;
        }
    }

    protected void setBlock(Block block, int meta, int x, int y, int z, TileEntity tile)
    {
        if(block instanceof BlockRemoteController)return;
        if(block instanceof BlockExcluder) cut(x, y, z, (BlockExcluder)tile);
        if(BlockArray[x - srcPosMinX][y - srcPosMinY][z - srcPosMinZ].excluded) return;

        BlockArray[x - srcPosMinX][y - srcPosMinY][z - srcPosMinZ].type = block;
        BlockArray[x - srcPosMinX][y - srcPosMinY][z - srcPosMinZ].meta = meta;
        BlockArray[x - srcPosMinX][y - srcPosMinY][z - srcPosMinZ].tile = tile;
    }

    private void cut(int x, int y, int z, BlockExcluder excluder)
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
