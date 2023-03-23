package jp.mochisystems.core._mc.world;

import jp.mochisystems.core._mc._core._Core;
import jp.mochisystems.core._mc.block.BlockSeatPositionMarker;
import jp.mochisystems.core.blockcopier.BlocksCompressor;
import jp.mochisystems.core.math.Math;
import jp.mochisystems.core.math.Quaternion;
import jp.mochisystems.core.math.Vec3d;
import jp.mochisystems.core.util.Connector;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MTYBlockAccess implements IBlockAccess{

	public static class BlockPiece{
		IBlockState state = Blocks.AIR.getDefaultState();
		int lightLevel = 0;
		TileEntity tile;
	}

	private World worldObj;
	private BlockPiece[][][] BlockArray;

	protected int sizeX, sizeY, sizeZ;
	protected BlockPos corePos;
	protected int localCorePosX, localCorePosY, localCorePosZ;
	private boolean TrueCopy;
	public boolean CompletedConstructing;
	public boolean IsTrueCopy(){return TrueCopy;}

	// for biomes or ctmRandom
	public int originalCorePosX =0, originalCorePosY =0, originalCorePosZ =0;


    ArrayList<ITickable> tickableList = new ArrayList<>();
    ArrayList<Entity> listEntity = new ArrayList<>();
	public List<Connector> listSeat = new ArrayList<>();
	public List<Quaternion> listSeatRot = new ArrayList<>();
	public ArrayList<TileEntity> listTileEntity = new ArrayList<>();


	static ExecutorService pool = Executors.newFixedThreadPool(2);
	public void constructFromTag(NBTTagCompound nbt, int corex, int corey, int corez, boolean isMultiThread, Runnable postInit)
	{
		corePos = new BlockPos(corex, corey, corez);
        if(nbt==null) return;
        if(isMultiThread)
        {
			pool.submit(new multiThread_BlockCopy(nbt, postInit));
        }
        else _constructFromTag(nbt, postInit);
	}

    private class multiThread_BlockCopy extends Thread{

	    NBTTagCompound nbt;
        Runnable postInit;
        multiThread_BlockCopy(NBTTagCompound nbt, Runnable postInit)
        {
            this.nbt = nbt;
            this.postInit = postInit;
        }

        public void run()
        {
            _constructFromTag(nbt, postInit);
        }
    }


    private void _constructFromTag(NBTTagCompound nbt, Runnable postInitAction)
    {
        SetData(nbt);
        if(worldObj.isRemote)
        {
//            diffLight();
			postInitAction.run();
		}
        postInit();
		CompletedConstructing = true;
    }
    private void SetData(NBTTagCompound nbt)
    {
        // <- Constructor
        int side = nbt.getByte("constructorside");
        TrueCopy = nbt.getBoolean("TrueCopy");
        originalCorePosX = nbt.getInteger("copiedPosX");
        originalCorePosY = nbt.getInteger("copiedPosY");
        originalCorePosZ = nbt.getInteger("copiedPosZ");
        localCorePosX = nbt.getInteger("originlocalx");
        localCorePosY = nbt.getInteger("originlocaly");
        localCorePosZ = nbt.getInteger("originlocalz");
        // <- BlocksCompressor
        // "size" value include outline pos
        int sizeX = nbt.getInteger("mtybr:sizex");
        int sizeY = nbt.getInteger("mtybr:sizey");
        int sizeZ = nbt.getInteger("mtybr:sizez");
		allocBlockArray(sizeX, sizeY, sizeZ);

//        int entityNum = nbt.getInteger("mtybr:entitynum");


        new BlocksCompressor().LoadFromNbtAndDecompress(nbt, this);

        //entity
		NBTTagList entitiesList = nbt.getTagList("entities", 10);
		for (int i = 0; i < entitiesList.tagCount(); ++i)
        {
			NBTTagCompound nbtentity = entitiesList.getCompoundTagAt(i);
//			NBTTagCompound nbtentity = (NBTTagCompound) nbt.getTag("entity."+i);
//            if(nbtentity==null) continue;
            String entityName = nbtentity.getString("entityname");
            try{
                Class<?> clazz;
                Constructor<?> constructor = null;
                Entity e = null;
                if(entityName.equals(EntityPlayerMP.class.getName()))
                {
//		    			clazz = Class.forName(EntityOtherPlayerMP.class.getName());
//		    			constructor = clazz.getConstructor(World.class, GameProfile.class);
//		    			e = (Entity)constructor.newInstance(
//		    					worldObj,
//		    					Minecraft.getMinecraft().getSession().func_148256_e());
                }
                else
                {
                    clazz = Class.forName(entityName);
                    constructor = clazz.getConstructor(World.class);
                    e = (Entity)constructor.newInstance(worldObj);
                }
                if(e == null)continue;
                e.readFromNBT(nbtentity);
                setEntity(e);
            }catch(Exception ignored)
            {
            }
        }
    }

    public void postInit() {}


	public void allocBlockArray(int x, int y, int z)
	{
		sizeX = x; sizeY = y; sizeZ = z;
		tickableList.clear();
		BlockArray = new BlockPiece[x][y][z];
		for(int i=0;i<x;++i)
	    	for(int j=0;j<y;++j)
	    		for(int k=0;k<z;++k)
	    			BlockArray[i][j][k] = new BlockPiece();
	}


	public void setWorld(World world)
	{
		worldObj = world;
		setWorldToTileEntities(world);
	}
	public World getWorld()
	{
		return worldObj;
	}

    public int LocalFromWorldX(int worldX){ return worldX + localCorePosX - originalCorePosX; }
    public int LocalFromWorldY(int worldY){ return worldY + localCorePosY - originalCorePosY; }
    public int LocalFromWorldZ(int worldZ){ return worldZ + localCorePosZ - originalCorePosZ; }
    public int WorldFromLocalX(int localX){ return localX - localCorePosX + originalCorePosX; }
    public int WorldFromLocalY(int localY){ return localY - localCorePosY + originalCorePosY; }
    public int WorldFromLocalZ(int localZ){ return localZ - localCorePosZ + originalCorePosZ; }

    public void setBlock(IBlockState state, int worldX, int worldY, int worldZ)
    {
        setBlockAbsolute(state, LocalFromWorldX(worldX), LocalFromWorldY(worldY), LocalFromWorldZ(worldZ));
    }
	public void setBlockAbsolute(IBlockState state, int x, int y, int z)
	{
		if(state.getBlock() instanceof BlockSeatPositionMarker)
		{
			setSeat(state.getValue(BlockHorizontal.FACING), x, y, z);
		}
        BlockArray[x][y][z].state = state;
	}


	public void setTileEntity(TileEntity tile, int worldX, int worldY, int worldZ)
	{
		BlockArray[LocalFromWorldX(worldX)][LocalFromWorldY(worldY)][LocalFromWorldZ(worldZ)].tile = tile;
		listTileEntity.add(tile);
		if(tile instanceof ITickable) tickableList.add((ITickable)tile);
	}

	public void setEntity(Entity entity)
	{
		listEntity.add(entity);
	}
	private void setWorldToTileEntities(World world)
	{
		for(ITickable te : tickableList)
		{
			((TileEntity)te).setWorld(world);
		}
	}
	
	public void updateTileEntity()
	{
		for(ITickable t : tickableList)
		{
			t.update();
		}
	}


	public void setSeat(EnumFacing face, float x, float y, float z)
	{
//		if(MFW_Core.proxy.checkSide().isClient())return;
//		if(getWorld()==null)return;
//		if(getWorld().isRemote)return;
//		entityPartSitEx e = new entityPartSitEx(getWorld(), loopHead, -1000, CorePosX, y, z, angle);
		Connector c = new Connector("");
		c.SetOrigin(new Vec3d(x-localCorePosX, y-localCorePosY-1, z-localCorePosZ));
		listSeat.add(c);
		listSeatRot.add(new Quaternion().Make(Vec3d.Up, -Math.toRadians(face.getHorizontalAngle())));
	}
	

	@Override
	public IBlockState getBlockState(BlockPos pos)
	{
		int localX = LocalFromWorldX(pos.getX());
		int localY = LocalFromWorldY(pos.getY());
		int localZ = LocalFromWorldZ(pos.getZ());
		return getBlockStateLocal(localX, localY, localZ);
	}
	public IBlockState getBlockStateLocal(int x, int y, int z)
	{
		int limit = 0;//TrueCopy ? 0 : 1;
		if(x < limit)
			return _Core.AIR.getDefaultState();
		else if(x >= sizeX - limit)
			return _Core.AIR.getDefaultState();
		if(y < limit)
			return _Core.AIR.getDefaultState();
		else if(y >= sizeY - limit)
			return _Core.AIR.getDefaultState();
		if(z < limit)
			return _Core.AIR.getDefaultState();
		else if(z >= sizeZ - limit)
			return _Core.AIR.getDefaultState();
		BlockPiece p = BlockArray[x][y][z];
		if(p == null) return _Core.AIR.getDefaultState();
		return p.state;
	}

	@Override
	public TileEntity getTileEntity(BlockPos pos)
	{
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		try{
            int localX = LocalFromWorldX(x);
            int localY = LocalFromWorldY(y);
            int localZ = LocalFromWorldZ(z);
			return BlockArray[localX][localY][localZ].tile;
		}catch(ArrayIndexOutOfBoundsException e){
			return null;
		}
	}
	public TileEntity GetTileEntityLocal(int x, int y, int z)
	{
		try{
			return BlockArray[x][y][z].tile;
		}catch(ArrayIndexOutOfBoundsException e){
			return null;
		}
	}


//	@Override
	public int getLightBrightnessForSkyBlocks(int x, int y, int z, int meta)
	{
//		int i1 = worldObj.getSkyBlockTypeBrightness(EnumSkyBlock.Sky, CorePosX, CorePosY, CorePosZ);
//		int j1 = worldObj.getSkyBlockTypeBrightness(EnumSkyBlock.Block, CorePosX, CorePosY, CorePosZ);
//		int j2 = getBlightness(x/*-originalCorePosX*/, y/*-originalCorePosY*/, z/*-originalCorePosZ*/);
//		j1 += (int)((15-j1)*j2/15f);
//		return i1 << 20 | j1 << 4;
		return 0;
	}

	@SideOnly(Side.CLIENT)
	public int getCombinedLight(BlockPos pos, int lightValue)
	{
		int i1 = worldObj.getLightFromNeighborsFor(EnumSkyBlock.SKY, corePos);
		int j = getBlightness(pos);
		if (j < lightValue)
		{
			j = lightValue;
		}

		return i1 << 20 | j << 4;
	}

	public IBlockState getBLockMetadata_AbsolutePos(int x, int y, int z)
	{
		try{
			return BlockArray[x][y][z].state;
		}catch(ArrayIndexOutOfBoundsException e){
			return null;
		}
	}


	public int isBlockProvidingPowerTo(int p_72879_1_, int p_72879_2_, int p_72879_3_, int p_72879_4_)
	{
		return 0;
	}

	public boolean isAirBlockLocal(int localX, int localY, int localZ)
	{
		if(localX<0)return false;
		else if(localX>= sizeX)return false;
		if(localY<0)return false;
		else if(localY>= sizeY)return false;
		if(localZ<0)return false;
		else if(localZ>= sizeZ)return false;
		BlockPiece p = BlockArray[localX][localY][localZ];
		Block b = p.state.getBlock();
		return b.isAir(p.state, this, BlockPos.ORIGIN);

	}

	@Override
	public boolean isAirBlock(BlockPos pos)
	{
        int localX = LocalFromWorldX(pos.getX());
        int localY = LocalFromWorldY(pos.getY());
        int localZ = LocalFromWorldZ(pos.getZ());
		return isAirBlockLocal(localX, localY, localZ);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public Biome getBiome(BlockPos pos)
	{
		return worldObj.getBiome(corePos);
	}


	public int getSizeX()
	{
		return sizeX;
	}
	public int getSizeY()
	{
		return sizeY;
	}
	public int getSizeZ()
	{
		return sizeZ;
	}
	public int getActualSizeX()
	{
		return TrueCopy ? sizeX-2 : sizeX;
	}
	public int getActualSizeY()
	{
		return TrueCopy ? sizeY-2 : sizeY;
	}
	public int getActualSizeZ()
	{
		return TrueCopy ? sizeZ-2 : sizeZ;
	}

	public int GetLocalCorePos(int flag)
	{
		switch(flag)
		{
		case 0:return localCorePosX;
		case 1:return localCorePosY;
		case 2:return localCorePosZ;
		}
		return -10000;
	}

	public int GetLocalCorePosX()
	{
		return localCorePosX;
	}
	public int GetLocalCorePosY()
	{
		return localCorePosY;
	}
	public int GetLocalCorePosZ()
	{
		return localCorePosZ;
	}



	public void setLightLevelLocal(IBlockState state, int localX, int localY, int localZ)
	{
		BlockPos pos = new BlockPos(localX,localY,localZ);
		int level = state.getLightValue(this, pos);
		setLightLevelLocal(level, pos.getX(), pos.getY(), pos.getZ());
	}

	private void setLightLevelLocal(int lightlevel, int x, int y, int z)
	{
		if(x < 0 || sizeX <= x
				|| y < 0 || sizeY <= y
				|| z < 0 || sizeZ <= z) return;
		if(BlockArray[x][y][z].lightLevel < lightlevel)
		{
			BlockArray[x][y][z].lightLevel = lightlevel;
		}
		int level = lightlevel - 1; //- BlockArray[x][y][z].type.getLightOpacity();
		if(level <= 0)return;
		if(isAirBlockLocal(x, y, z)) return;
		setLightLevelLocal(level, x-1, y, z);
		setLightLevelLocal(level, x+1, y, z);
		setLightLevelLocal(level, x, y-1, z);
		setLightLevelLocal(level, x, y+1, z);
		setLightLevelLocal(level, x, y, z-1);
		setLightLevelLocal(level, x, y, z+1);
	}

	public int getLightLevel(BlockPos pos)
	{
		try{
			int ax = LocalFromWorldX(pos.getX());
			int ay = LocalFromWorldY(pos.getY());
			int az = LocalFromWorldZ(pos.getZ());
			return BlockArray[ax][ay][az].lightLevel;
		}catch(ArrayIndexOutOfBoundsException e){
			return 0; 
		} 
	}
	
	public int getBlightness(BlockPos pos)
	{
//        if (this.getBlock(x, y, z).getUseNeighborBrightness()) 1.7.10
        if (getBlockState(pos).useNeighborBrightness())
        {
            int j2 = this.getLightLevel(pos.up());
            int j1 = this.getLightLevel(pos.east());
            int k1 = this.getLightLevel(pos.west());
            int l1 = this.getLightLevel(pos.south());
            int i2 = this.getLightLevel(pos.north());

            if (j1 > j2)
            {
                j2 = j1;
            }

            if (k1 > j2)
            {
                j2 = k1;
            }

            if (l1 > j2)
            {
                j2 = l1;
            }

            if (i2 > j2)
            {
                j2 = i2;
            }

            return j2;
        }
        else
        {
            return getLightLevel(pos);
        }
    }

	public int getEntityNum()
	{
		return listEntity.size();
	}
	public Entity getEntity(int idx) { return listEntity.get(idx);}

	public int getStrongPower(BlockPos pos, EnumFacing direction)
	{
		return 0; //  int getStrongPower(BlockPos pos, EnumFacing direction);
	}

	public WorldType getWorldType()
	{
		return this.worldObj.getWorldType();
	}

	public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default)
	{
		if (!this.worldObj.isValid(pos)) return _default;

		Chunk chunk = worldObj.getChunkFromBlockCoords(pos);
		if (chunk == null || chunk.isEmpty()) return _default;
		return getBlockState(pos).isSideSolid(this, pos, side);
	}
}
