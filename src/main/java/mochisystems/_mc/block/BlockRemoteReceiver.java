package mochisystems._mc.block;

import mochisystems._mc.tileentity.tileEntityRemoteController;
import mochisystems._mc.tileentity.tileEntityRemoteReceiver;
import mochisystems._mc.tileentity.tileEntityRemoteReceiver;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRemoteReceiver extends BlockContainer {

	public BlockRemoteReceiver()
	{
		super(Material.ground);
		this.setHardness(0.4F);
		this.setResistance(2000.0F);
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
	{
		return new tileEntityRemoteReceiver();
	}
	
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack is)
	{
		super.onBlockPlacedBy(world, x, y, z, player, is); 
		world.setTileEntity(x, y, z, createNewTileEntity(world, 0));
	}

	///// for redstone

	@Override
	public boolean canProvidePower()
	{
		return true;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
	{
		world.notifyBlocksOfNeighborChange(x, y, z, this);
	}

	public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int side)
	{
		tileEntityRemoteReceiver tile = (tileEntityRemoteReceiver) world.getTileEntity(x, y, z);
		if(tile==null)return 0;
		Block remotedblock = world.getBlock(tile.remotex, tile.remotey, tile.remotez);
		if(remotedblock == null)return 0;
		if(tile.remotex	== x && tile.remotey == y && tile.remotez == z) return 0;
		return remotedblock.isProvidingStrongPower(world, tile.remotex, tile.remotey, tile.remotez, side);
	}

	public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side)
	{
		tileEntityRemoteReceiver tile = (tileEntityRemoteReceiver) world.getTileEntity(x, y, z);
		if (tile == null) return 0;
		Block remotedblock = world.getBlock(tile.remotex, tile.remotey, tile.remotez);
		if (remotedblock == null) return 0;
		if (tile.remotex == x && tile.remotey == y && tile.remotez == z) return 0;
		return remotedblock.isProvidingWeakPower(world, tile.remotex, tile.remotey, tile.remotez, side);
	}
}
