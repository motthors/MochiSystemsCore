package mochisystems.blockcopier;

import java.lang.reflect.Constructor;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.zip.DataFormatException;

import mochisystems._core.Logger;
import mochisystems._core._Core;
import mochisystems.util.byteZip;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityBeacon;
import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;

public class MTYBlockAccess implements IBlockAccess{

	public class BlockPiece{
		Block type = Blocks.air;
		int meta;
		int lightLevel = 0;
		TileEntity tile;
	}
	public class TileEntityPiece{
		int x,y,z;
		TileEntity tile;
	}
		
	private World worldObj;
	private BlockPiece[][][] BlockArray;

	private int blockNum = 0;
	private int blockNumPost = 0;
	protected int sizeX, sizeY, sizeZ;
	protected int CorePosX, CorePosY, CorePosZ;
	protected int localCorePosX, localCorePosY, localCorePosZ;
//	private int constructSide,CoreSide;

	// for biome or ctmRandom
	public int originalCorePosX =0, originalCorePosY =0, originalCorePosZ =0;

	public class renderPiece{
		public int x,y,z;
		public Block b;
		private renderPiece(int _x,int _y,int _z,Block _b){x=_x;y=_y;z=_z;b=_b;}
	}
//	ArrayList<renderPiece> listPrePass = new ArrayList<>();
//	ArrayList<renderPiece> listPostPass = new ArrayList<>();
//	Block[][][] listPrePass = new Block[][][][][][];
    ArrayList<TileEntityPiece> listTileEntity = new ArrayList<>();
    ArrayList<Entity> listEntity = new ArrayList<>();

	public MTYBlockAccess(World world)
	{
		worldObj = world;
	}

//	public void SetPos(int x, int y, int z)
//    {
//        this.CorePosX = x;
//        this.CorePosY = y;
//        this.CorePosZ = z;
//    }

	public void constructFromTag(NBTTagCompound nbt, int corex, int corey, int corez, boolean isMultiThread, Runnable postInit)
	{
        CorePosX = corex;
        CorePosY = corey;
        CorePosZ = corez;
        if(nbt==null) return;
        if(isMultiThread)
        {
            Thread thread = new multiThread_BlockCopy(nbt, postInit);
            thread.start();
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
            diffLight();
			postInitAction.run();
		}
        postInit();
    }
    private void SetData(NBTTagCompound nbt)
    {
        // <- Constructor
        int side = nbt.getByte("constructorside");
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
        int orgSize = nbt.getInteger("decompresssize");

        int blockNameNum = nbt.getInteger("blockunlocalnameNum");
        ArrayList<String> nameList = new ArrayList<>();
        for(int i=0; i<blockNameNum; ++i)
        {
            nameList.add(nbt.getString("blockunlocalname"+i));
        }
        int entitynum = nbt.getInteger("mtybr:entitynum");

        //////////////��/////////
        if(orgSize == 0)return;
        byte[] blocksArray = nbt.getByteArray("compressedbytearray");
        byte[] decompressedData = new byte[orgSize];
        if(blocksArray==null)return;
        try {
            byteZip.decompress(decompressedData, blocksArray);
        } catch (DataFormatException e) {
            e.printStackTrace();
        }
        ByteBuffer buf = ByteBuffer.allocate(decompressedData.length);
        buf.put(decompressedData);
        buf.rewind();

        allocBlockArray(sizeX, sizeY, sizeZ);


//	        MFW_Logger.debugInfo("thread construct checker 6 start set block to ba");
		for(int z=0;z<sizeZ;++z)
        {
            for(int y=0;y<sizeY;++y)
            {
				for(int x=0;x<sizeX;++x)
				{
                    int blockid = (int)buf.getInt();
                    int blockmeta = 0;
                    if(blockid!=0)blockmeta = (int)buf.get() & 0xff;

                    if(blockid >= nameList.size())continue;
                    Block type = Block.getBlockFromName(nameList.get(blockid));
//					Logger.debugInfo("MTYBlockAccess : "+type.getLocalizedName()+"\t {"+x+"."+y+"."+z+"}");
                    if(!isExistBlock(type))continue;
					setBlockAbsolute(type, blockmeta, x, y, z);
					setLightLevel(type, x, y, z);
					String tileName = new StringBuilder("tile.").append(x).append(".").append(y).append(".").append(z).toString();
                    if(nbt.hasKey(tileName))
                    {
                        TileEntity tile = type.createTileEntity(worldObj, blockmeta);
                        if(tile!=null)
                        {
                            NBTTagCompound nbttile = (NBTTagCompound) nbt.getTag(tileName);
                            tile.readFromNBT(nbttile);
                            if(tile instanceof TileEntityBeacon)tile = new W_TileEntityFerrisBeacon(this, getBlockOrgPos(x, y-1, z));
                            tile.setWorldObj(worldObj);
                            setTileEntityAbsolute(tile, x, y, z);
                        }
                    }
                }
            }
        }

//		boolean isremote = _Core.proxy.checkSide().isClient();
//		if(isremote)
//		{
//			SetRenderingList();
//		}

        //entity
        for(int i=0; i<entitynum; ++i)
        {
            NBTTagCompound nbtentity = (NBTTagCompound) nbt.getTag("entity."+i);
            if(nbtentity==null)continue;
            String entityName = nbtentity.getString("entityname");
            try{
                // �����񂩂�N���X���擾
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
            }catch(Exception e)
            {
                continue;
            }
        }
    }

    protected boolean isExistBlock(Block b)
    {
        if(b == null)return false;
        if(b instanceof BlockAir)return false;
        return true;
    }

    public void postInit() {}


	public void allocBlockArray(int x, int y, int z)
	{
		blockNum = 0;
		blockNumPost = 0;
		sizeX = x; sizeY = y; sizeZ = z;
		listTileEntity.clear();
		BlockArray = new BlockPiece[x][y][z];
		for(int i=0;i<x;++i)
	    	for(int j=0;j<y;++j)
	    		for(int k=0;k<z;++k)
	    			BlockArray[i][j][k] = new BlockPiece();
	}


	public void setWorld(World world)
	{
		worldObj = world;
	}
	public World getWorld()
	{
		return worldObj;
	}

    private int LocalFromWorldX(int worldX){ return worldX + localCorePosX - originalCorePosX; }
    private int LocalFromWorldY(int worldY){ return worldY + localCorePosY - originalCorePosY; }
    private int LocalFromWorldZ(int worldZ){ return worldZ + localCorePosZ - originalCorePosZ; }
    private int WorldFromLocalX(int localX){ return localX - localCorePosX + originalCorePosX; }
    private int WorldFromLocalY(int localY){ return localY - localCorePosY + originalCorePosY; }
    private int WorldFromLocalZ(int localZ){ return localZ - localCorePosZ + originalCorePosZ; }

    public void setBlock(Block block, int meta, int worldX, int worldY, int worldZ)
    {
        setBlockAbsolute(block, meta, LocalFromWorldX(worldX), LocalFromWorldY(worldY), LocalFromWorldZ(worldZ));
    }

    void setBlockAbsolute(Block block, int meta, int x, int y, int z)
	{
        BlockArray[x][y][z].type = block;
        BlockArray[x][y][z].meta = meta;
//        if(meta!=0)Logger.debugInfo(""+meta);
	}

//	private void SetRenderingList()
//    {
//		int csizeX = sizeX / 16 + 1;
//		int csizeY = sizeY / 16 + 1;
//		int csizeZ = sizeZ / 16 + 1;
//		for(int cx=0;cx<csizeX;++cx) {
//			for(int cz=0;cz<csizeZ;++cz){
//				for(int cy=0;cy<csizeY;++cy){
//					for (int _x = 0; _x < 16; ++_x) {
//						int x = _x + cx * 16;
//						for (int _z = 0; _z < 16; ++_z) {
//							int z = _z + cz * 16;
//							for (int _y = 0; _y < 16; ++_y) {
//								int y = _y + cy * 16;
//								if(x >= sizeX-1 || x <= 0) return;
//								if(y >= sizeY-1 || y <= 0) return;
//								if(z >= sizeZ-1 || z <= 0) return;
//
//								Block block = getBlock(x, y, z);
//								if(block.getRenderBlockPass()==0) blockNum += 1;
//								else blockNumPost += 1;
//
//								setLightLevel(block, x, y, z);
//
//								ArrayList<renderPiece> renderList = (block.getRenderBlockPass() == 0) ? listPrePass : listPostPass;
//								int worldX = WorldFromLocalX(x);
//								int worldY = WorldFromLocalY(y);
//								int worldZ = WorldFromLocalZ(z);
//								renderList.add(new renderPiece(worldX, worldY, worldZ, block));
//							}
//						}
//					}
//				}
//			}
//		}
//    }

	public void setTileEntity(TileEntity tile, int worldX, int worldY, int worldZ)
	{
		BlockArray[LocalFromWorldX(worldX)][LocalFromWorldY(worldY)][LocalFromWorldZ(worldZ)].tile = tile;
		TileEntityPiece t = new TileEntityPiece();
		t.tile = tile;
		t.x = worldX;
		t.y = worldY;
		t.z = worldZ;
		listTileEntity.add(t);
	}

	public void setTileEntityAbsolute(TileEntity tile, int x, int y, int z)
	{
		setTileEntity(tile, WorldFromLocalX(x), WorldFromLocalY(y), WorldFromLocalZ(z));
	}

	public void setEntity(Entity entity)
	{
		listEntity.add(entity);
	}
	public void setWorldToTileEntities(World world)
	{
		for(TileEntityPiece te : listTileEntity)te.tile.setWorldObj(world);
	}
	
	public void updateTileEntity()
	{
		for(TileEntityPiece t :listTileEntity)
		{
			t.tile.updateEntity();
		}
	}
	


	@Override
	public Block getBlock(int x, int y, int z)
	{
		int localX = LocalFromWorldX(x);
		int localY = LocalFromWorldY(y);
		int localZ = LocalFromWorldZ(z);
		if(localX < 0)
			return Blocks.air;
		else if(localX >= sizeX)
			return Blocks.air;
		if(localY < 0)
			return Blocks.air;
		else if(localY >= sizeY)
			return Blocks.air;
		if(localZ < 0)
			return Blocks.air;
		else if(localZ >= sizeZ)
			return Blocks.air;
		return BlockArray[localX][localY][localZ].type;
	}
	public Block getBlockOrgPos(int x, int y, int z)
	{
		try{
			return BlockArray[x][y][z].type;
		}catch(java.lang.ArrayIndexOutOfBoundsException e){
			return Blocks.air;
		}
	}
	
	@Override
	public TileEntity getTileEntity(int x, int y, int z)
	{
		try{
            int localX = LocalFromWorldX(x);
            int localY = LocalFromWorldY(y);
            int localZ = LocalFromWorldZ(z);
			return BlockArray[localX][localY][localZ].tile;
		}catch(java.lang.ArrayIndexOutOfBoundsException e){
			return null;
		}
	}
	
	
	@Override
	public int getLightBrightnessForSkyBlocks(int x, int y, int z, int meta)
	{
		int i1 = worldObj.getSkyBlockTypeBrightness(EnumSkyBlock.Sky, CorePosX, CorePosY, CorePosZ);
		int j1 = worldObj.getSkyBlockTypeBrightness(EnumSkyBlock.Block, CorePosX, CorePosY, CorePosZ);
		int j2 = getBlightness(x/*-originalCorePosX*/, y/*-originalCorePosY*/, z/*-originalCorePosZ*/);
		j1 += (int)((15-j1)*j2/15f);
//		if (j1 < meta)
//		{
//			j1 = meta;
//		}
//		MFW_Logger.debugInfo("lightLevel:"+j1);
		return i1 << 20 | j1 << 4;
//		return 15 << 20 | 15 << 4;
	}
	
	public int getBLockMetadata_AbsolutePos(int x, int y, int z)
	{
		try{
			return BlockArray[x][y][z].meta;
		}catch(java.lang.ArrayIndexOutOfBoundsException e){
			return 0;
		}
	}
	@Override
	public int getBlockMetadata(int x, int y, int z)
	{
		int tx = LocalFromWorldX(x);
		int ty = LocalFromWorldY(y);
		int tz = LocalFromWorldZ(z);
		if(tx<0)return 0;
		else if(tx>= sizeX)return 0;
		if(ty<0)return 0;
		else if(ty>= sizeY)return 0;
		if(tz<0)return 0;
		else if(tz>= sizeZ)return 0;
		return BlockArray[tx][ty][tz].meta;
	}
	
	
	@Override
	public int isBlockProvidingPowerTo(int p_72879_1_, int p_72879_2_, int p_72879_3_, int p_72879_4_)
	{
		return 0;
	}
	
	
	@Override
	public boolean isAirBlock(int x, int y, int z)
	{
        int localX = LocalFromWorldX(x);
        int localY = LocalFromWorldY(y);
        int localZ = LocalFromWorldZ(z);
		if(localX<0)return false;
		else if(localX>= sizeX)return false;
		if(localY<0)return false;
		else if(localY>= sizeY)return false;
		if(localZ<0)return false;
		else if(localZ>= sizeZ)return false;
		return BlockArray[localX][localY][localZ].type instanceof BlockAir;
	}
	
	
	@Override
	public BiomeGenBase getBiomeGenForCoords(int p_72807_1_, int p_72807_2_)
	{
		return worldObj.getBiomeGenForCoords(CorePosX, CorePosZ);
	}
	
	
	@Override
	public int getHeight()
	{
		return worldObj.getHeight();
	}
	
	
	@Override
	public boolean extendedLevelsInChunkCache()
	{
		return worldObj.extendedLevelsInChunkCache();
	}
	
	@Override
	public boolean isSideSolid(int x, int y, int z, ForgeDirection side, boolean _default)
	{
		return getBlock(x, y, z).isSideSolid(this, x, y, z, side);
	}
	
	
	public int getBlockNum(int pass)
	{
		return pass==0 ? blockNum : blockNumPost;
	}
	
	public int getSize(int flag)
	{
		switch(flag)
		{
		case 0:return sizeX;
		case 1:return sizeY;
		case 2:return sizeZ;
		}
		return -1;
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
	
	public int GetOriginalCorePos(int flag)
	{
		switch(flag)
		{
		case 0:return originalCorePosX;
		case 1:return originalCorePosY;
		case 2:return originalCorePosZ;
		}
		return -10000;
	}

	
	public void setLightLevel(Block b, int localX, int localY, int localZ)
	{
		BlockArray[localX][localY][localZ].lightLevel = b.getLightValue();
	}
	
	public void diffLight()
	{
		for(int x = 0; x< sizeX; ++x)for(int y = 0; y< sizeY; ++y)for(int z = 0; z< sizeZ; ++z)
		{
			int level = BlockArray[x][y][z].lightLevel -1;
			if(level <= 0)continue;
			setLightLevel(level, x-1, y, z);
			setLightLevel(level, x+1, y, z);
			setLightLevel(level, x, y-1, z);
			setLightLevel(level, x, y+1, z);
			setLightLevel(level, x, y, z-1);
			setLightLevel(level, x, y, z+1);
		}
	}
	// �ċN�Ăяo���֐�
	private void setLightLevel(int lightlevel, int x, int y, int z)
	{
		try{
			if(BlockArray[x][y][z].lightLevel < lightlevel)
			{
				BlockArray[x][y][z].lightLevel = lightlevel;
			}
		}catch(java.lang.ArrayIndexOutOfBoundsException e){ 
			return; 
		}
		int level = lightlevel - 4; //- BlockArray[x][y][z].type.getLightOpacity();
		if(level <= 0)return;
		if(BlockArray[x][y][z].type != Blocks.air) return;
		setLightLevel(level, x-1, y, z);
		setLightLevel(level, x+1, y, z);
		setLightLevel(level, x, y-1, z);
		setLightLevel(level, x, y+1, z);
		setLightLevel(level, x, y, z-1);
		setLightLevel(level, x, y, z+1);
	}
	
	public int getLightLevel(int x, int y, int z)
	{
		try{
			int ax = LocalFromWorldX(x);
			int ay = LocalFromWorldY(y);
			int az = LocalFromWorldZ(z);
			return BlockArray[ax][ay][az].lightLevel;
		}catch(java.lang.ArrayIndexOutOfBoundsException e){ 
			return 0; 
		} 
	}
	
	public int getBlightness(int x, int y, int z)
	{
        if (this.getBlock(x, y, z).getUseNeighborBrightness())
        {
            int j2 = this.getLightLevel( x, y + 1, z);
            int j1 = this.getLightLevel( x + 1, y, z);
            int k1 = this.getLightLevel( x - 1, y, z);
            int l1 = this.getLightLevel( x, y, z + 1);
            int i2 = this.getLightLevel( x, y, z - 1);

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
            return getLightLevel(x, y, z);
        }
    }
	
	
	public int getEntityNum()
	{
		return listEntity.size();
	}

//	public void updateFirstAfterConstruct(){}
}
