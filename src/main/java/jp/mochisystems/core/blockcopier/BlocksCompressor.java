package jp.mochisystems.core.blockcopier;

import jp.mochisystems.core._mc.tileentity.TileEntityBeaconWrapper;
import jp.mochisystems.core._mc.world.MTYBlockAccess;
import jp.mochisystems.core.util.HashMaker;
import jp.mochisystems.core.util.byteZip;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.math.BlockPos;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;

public class BlocksCompressor {

	byteZip zipCompressor = new byteZip();

	public void CompressAndWriteTo(NBTTagCompound nbt, BlocksScanner.BlockPiece[][][] BlockArray)
	{
		int sizeX = BlockArray.length;
		int sizeY = BlockArray[0].length;
		int sizeZ = BlockArray[0][0].length;
		zipCompressor.clear();

		List<TileEntity> tileEntityList = new ArrayList<>();
		// compress
		HashMaker hasher = new HashMaker(1);
		for(int z = 0; z< sizeZ; ++z)
			for(int y = 0; y< sizeY; ++y){
				for(int x = 0; x< sizeX; ++x){
				{
					BlocksScanner.BlockPiece p = BlockArray[x][y][z];
					boolean excluded = (BlockArray[x][y][z].excluded);
					if(excluded)
                    {
                        p.state = Blocks.AIR.getDefaultState();
                        p.tile = null;
                    }
					//TODO StateIDそのまま使ったらファイル書き込みとかで他へ移したらしぬのでは？
                    zipCompressor.setInt(Block.getStateId(p.state));

					if(!excluded && p.tile != null)
					{
						tileEntityList.add(p.tile);
//						NBTTagCompound nbttile = new NBTTagCompound();
//						p.tile.writeToNBT(nbttile);
//						nbt.setTag("tile."+(x)+"."+(y)+"."+(z), nbttile);
					}
					hasher.Append(Block.getIdFromBlock(p.state.getBlock()));
					hasher.Append(x);
					hasher.Append(y);
					hasher.Append(z);
				}
			}
		}
		int tileCount = 0;
		for(TileEntity te : tileEntityList)
		{
			NBTTagCompound nbttile = new NBTTagCompound();
			te.writeToNBT(nbttile);
			nbt.setTag("tile"+(tileCount++), nbttile);
		}
        zipCompressor.compress();
		nbt.setInteger("decompresssize", zipCompressor.getOrgSize());
		nbt.setByteArray("compressedbytearray", zipCompressor.getOutput());
		nbt.setInteger("tileentitycount", tileCount);
		nbt.setInteger("hash", hasher.GetHash());
    }

    public void LoadFromNbtAndDecompress(NBTTagCompound nbt, MTYBlockAccess blockAccess)
	{
		int orgSize = nbt.getInteger("decompresssize");
		if(orgSize == 0)return;

		byte[] compressedData = nbt.getByteArray("compressedbytearray");

		byte[] decompressedData = new byte[orgSize];

		zipCompressor.clear();
		try {
			byteZip.decompress(decompressedData, compressedData);
		} catch (DataFormatException e) {
			e.printStackTrace();
		}

		ByteBuffer buf = ByteBuffer.allocate(decompressedData.length);
		buf.put(decompressedData);
		buf.rewind();

		for(int z=0;z<blockAccess.getSizeZ();++z)
		{
			for(int y=0;y< blockAccess.getSizeY();++y)
			{
				for(int x=0;x< blockAccess.getSizeX();++x)
				{
					int stateId = buf.getInt();
					IBlockState state = Block.getStateById(stateId);

					if(state.getBlock() instanceof BlockAir) continue;
					blockAccess.setBlockAbsolute(state, x, y, z);
					blockAccess.setLightLevelLocal(state, x, y, z);
				}
			}
		}

		int tileCount = nbt.getInteger("tileentitycount");
		for(int i = 0; i < tileCount; ++i)
		{
			NBTTagCompound nbtTile = (NBTTagCompound) nbt.getTag("tile"+i);
			int x = nbtTile.getInteger("x");
			int y = nbtTile.getInteger("y");
			int z = nbtTile.getInteger("z");
			BlockPos pos = new BlockPos(x, y, z);
			IBlockState state = blockAccess.getBlockState(pos);
			TileEntity tile = state.getBlock().createTileEntity(blockAccess.getWorld(), state);
			assert tile != null;
			tile.readFromNBT(nbtTile);
			if(tile instanceof TileEntityBeacon) tile = new TileEntityBeaconWrapper(blockAccess, blockAccess.getBlockStateLocal(x, y-1, z).getBlock());
			tile.setWorld(blockAccess.getWorld());
			blockAccess.setTileEntity(tile, x, y, z);
		}
	}
}
