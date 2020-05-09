package mochisystems.blockcopier;

import mochisystems.util.byteZip;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;
import java.util.Map;

public class BlocksCompressor {

	byteZip zipCompressor = new byteZip();

	public void RegisterBlocks(NBTTagCompound nbt, IBLockCopyHandler handler, BlocksScanner.BlockPiece[][][] BlockArray,
                               Map<String, Integer> nameMap, List<String> nameList)
	{
		int sizeX = BlockArray.length;
		int sizeY = BlockArray[0].length;
		int sizeZ = BlockArray[0][0].length;
		zipCompressor.clear();

		// compress
		for(int z = 0; z< sizeZ; ++z)
			for(int y = 0; y< sizeY; ++y){
				for(int x = 0; x< sizeX; ++x){
				{
					BlocksScanner.BlockPiece p = BlockArray[x][y][z];
					boolean excluded = (BlockArray[x][y][z].excluded);
					if(excluded)
                    {
                        p.type = Blocks.air;
                        p.tile = null;
                    }

                    int id = nameMap.get(Block.blockRegistry.getNameForObject(p.type));
                    zipCompressor.setInt(id);
					if(id != 0) zipCompressor.setByte((byte) p.meta);

					if(p.tile != null)
					{
						NBTTagCompound nbttile = new NBTTagCompound();
						p.tile.writeToNBT(nbttile);
						nbt.setTag("tile."+(x)+"."+(y)+"."+(z), nbttile);
					}
				}
			}
		}
        zipCompressor.compress();
		nbt.setInteger("decompresssize", zipCompressor.getOrgSize());
		nbt.setByteArray("compressedbytearray", zipCompressor.getOutput());
		for(int i=0; i<nameList.size();++i)
		{
			nbt.setString("blockunlocalname"+i, nameList.get(i));
		}
		nbt.setInteger("blockunlocalnameNum", nameList.size());

//        zipCompressor.compress();
    }

}
