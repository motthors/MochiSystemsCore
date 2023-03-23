package jp.mochisystems.core._mc.block;

import jp.mochisystems.core._mc.tileentity.tileEntityRemoteReceiver;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockRemoteReceiver extends BlockContainer {

	public BlockRemoteReceiver()
	{
		super(Material.GROUND);
		this.setHardness(0.4F);
		this.setResistance(2000.0F);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta)
	{
		return new tileEntityRemoteReceiver();
	}

	///// for redstone

	@Override
	public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable EnumFacing side)
	{
		return true;
	}

//	@Override
//	public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
//	{
//		world.notifyBlocksOfNeighborChange(x, y, z, this);
//	}
//
//	public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int side)
//	{
//		tileEntityRemoteReceiver tile = (tileEntityRemoteReceiver) world.getTileEntity(x, y, z);
//		if(tile==null)return 0;
//		Block remotedblock = world.getBlock(tile.remotex, tile.remotey, tile.remotez);
//		if(remotedblock == null)return 0;
//		if(tile.remotex	== x && tile.remotey == y && tile.remotez == z) return 0;
//		return remotedblock.isProvidingStrongPower(world, tile.remotex, tile.remotey, tile.remotez, side);
//	}
//
//	public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side)
//	{
//		tileEntityRemoteReceiver tile = (tileEntityRemoteReceiver) world.getTileEntity(x, y, z);
//		if (tile == null) return 0;
//		Block remotedblock = world.getBlock(tile.remotex, tile.remotey, tile.remotez);
//		if (remotedblock == null) return 0;
//		if (tile.remotex == x && tile.remotey == y && tile.remotez == z) return 0;
//		return remotedblock.isProvidingWeakPower(world, tile.remotex, tile.remotey, tile.remotez, side);
//	}
}
