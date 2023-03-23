package jp.mochisystems.core._mc.tileentity;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityBeaconWrapper extends TileEntityBeacon{

	float power = 0;
	
	public TileEntityBeaconWrapper(IBlockAccess ba, Block underOfThis)
	{
		if(underOfThis.isBeaconBase(ba, new BlockPos(0, 0, 0), new BlockPos(0, 0, 1))) power = 1;
	}
	
	@SideOnly(Side.CLIENT)
    public float func_146002_i()
    {
        return power;
    }
}
