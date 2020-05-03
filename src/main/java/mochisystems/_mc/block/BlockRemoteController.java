package mochisystems._mc.block;

import mochisystems._core.Logger;
import mochisystems._mc.tileentity.tileEntityRemoteController;
import mochisystems._mc.tileentity.tileEntityRemoteReceiver;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Facing;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.ArrayList;

public class BlockRemoteController extends BlockContainer {

	public BlockRemoteController()
	{
		super(Material.ground);
		this.setHardness(0.4F);
		this.setResistance(2000.0F);
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
	{
		return new tileEntityRemoteController();
	}
	
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack is)
	{
		super.onBlockPlacedBy(world, x, y, z, player, is); 

		tileEntityRemoteController tile=null;
		tile = (tileEntityRemoteController) world.getTileEntity(x, y, z);
		if(tile==null) tile = (tileEntityRemoteController) createNewTileEntity(world, 0);
		if(!is.hasTagCompound())return;
		NBTTagCompound tag = is.getTagCompound();
		if(!tag.hasKey("mfwcontrollerX"))return;
		tile.remotex = tag.getInteger("mfwcontrollerX");
		tile.remotey = tag.getInteger("mfwcontrollerY");
		tile.remotez = tag.getInteger("mfwcontrollerZ");

		TileEntity receiver = world.getTileEntity(tile.remotex, tile.remotey, tile.remotez);
		if(receiver instanceof tileEntityRemoteReceiver)
		{
			((tileEntityRemoteReceiver)receiver).SetPos(x, y, z);
		}

		world.setTileEntity(x, y, z, tile);
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z,
			EntityPlayer player, int side, float xOffset, float yOffset, float zOffset)
	{
		if(player.getHeldItem()!=null && player.getHeldItem().getItem() instanceof itemBlockRemoteController)return true;
		tileEntityRemoteController tile = (tileEntityRemoteController) world.getTileEntity(x, y, z);
		if(tile==null)return true;
		Block remotedblock = world.getBlock(tile.remotex, tile.remotey, tile.remotez);
		if(remotedblock == null)return true;
		if(tile.remotex	== x && tile.remotey == y && tile.remotez == z) return true;
		return remotedblock.onBlockActivated(world, tile.remotex, tile.remotey, tile.remotez, player, side, xOffset, yOffset, zOffset);
	}

	public static Block TargetBlock(World world, NBTTagCompound nbt)
	{
		if(nbt == null) return null;
		int x = nbt.getInteger("mfwcontrollerX");
		int y = nbt.getInteger("mfwcontrollerY");
		int z = nbt.getInteger("mfwcontrollerZ");
		return world.getBlock(x, y, z);
	}

	public static TileEntity TargetTileEntity(World world, NBTTagCompound nbt)
	{
		if(nbt == null) return null;
		int x = nbt.getInteger("mfwcontrollerX");
		int y = nbt.getInteger("mfwcontrollerY");
		int z = nbt.getInteger("mfwcontrollerZ");
		return world.getTileEntity(x, y, z);
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
		int meta = world.getBlockMetadata(x, y, z);
		int power = world.getStrongestIndirectPower(x, y, z);
		if(power > 0) --power;
		if(meta != power)
		{
			world.setBlockMetadataWithNotify(x, y, z, power, 2);
		}


		tileEntityRemoteController tile = (tileEntityRemoteController) world.getTileEntity(x, y, z);
		if(tile==null)return;
		Block remotedblock = world.getBlock(tile.remotex, tile.remotey, tile.remotez);
		if(remotedblock == null)return;
		if(tile.remotex	== x && tile.remotey == y && tile.remotez == z) return;
		world.setBlockMetadataWithNotify(tile.remotex, tile.remotey, tile.remotez, 15, 2);
//		world.notifyBlocksOfNeighborChange(tile.remotex, tile.remotey, tile.remotez, this);
		remotedblock.onNeighborBlockChange(world, tile.remotex, tile.remotey, tile.remotez, this);
	}

	public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side)
	{
		return world.getBlockMetadata(x, y, z);
	}

	private int func_150178_a(World world, int x, int y, int z, int d)
	{
		if (world.getBlock(x, y, z) != this)
		{
			return d;
		}
		else
		{
			int i1 = world.getBlockMetadata(x, y, z);
			return i1 > d ? i1 : d;
		}
	}
}
