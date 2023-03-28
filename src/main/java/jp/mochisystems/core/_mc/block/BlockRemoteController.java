package jp.mochisystems.core._mc.block;

import jp.mochisystems.core._mc.item.ItemMultiTab;
import jp.mochisystems.core._mc.item.itemBlockRemoteController;
import jp.mochisystems.core._mc.tileentity.tileEntityRemoteController;
import jp.mochisystems.core._mc.tileentity.tileEntityRemoteReceiver;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockRemoteController extends BlockContainer {

	public static final String KeyRemotePosX = "remote_x";
	public static final String KeyRemotePosY = "remote_y";
	public static final String KeyRemotePosZ = "remote_z";

	public BlockRemoteController()
	{
		super(Material.GROUND);
		this.setHardness(0.3F);
		this.setResistance(2000.0F);
		this.setDefaultState(this.blockState.getBaseState());
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
	{
		return new tileEntityRemoteController();
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state)
	{
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack stack)
	{
		super.onBlockPlacedBy(world, pos, state, player, stack);

		tileEntityRemoteController tile = (tileEntityRemoteController) world.getTileEntity(pos);
		NBTTagCompound tag = stack.getTagCompound();
		tile.readMineFromNBT(tag);

		TileEntity receiver = world.getTileEntity(tile.remotePos);
		if(receiver instanceof tileEntityRemoteReceiver)
		{
			((tileEntityRemoteReceiver)receiver).SetPos(pos);
		}

		world.setTileEntity(pos, tile);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
									EnumHand hand, EnumFacing facing,
									float hitX, float hitY, float hitZ)
	{
//		ItemStack stack = player.getHeldItem(hand);
//		if(stack == null) return true;
//		boolean isRemoteController = stack.getItem() instanceof itemBlockRemoteController;
//		if(!isRemoteController) return true;
		tileEntityRemoteController tile = (tileEntityRemoteController) world.getTileEntity(pos);
		if(tile == null)return true;
		if(tile.remotePos.equals(pos)) return true;

		IBlockState remoteState = world.getBlockState(tile.remotePos);
		Block remoteBlock = remoteState.getBlock();
		return remoteBlock.onBlockActivated(world, tile.remotePos, remoteState, player, hand, facing, hitX, hitY, hitZ);
	}

	public static IBlockState TargetBlock(World world, NBTTagCompound nbt)
	{
		if(nbt == null) return null;
		int x = nbt.getInteger(KeyRemotePosX);
		int y = nbt.getInteger(KeyRemotePosY);
		int z = nbt.getInteger(KeyRemotePosZ);
		return world.getBlockState(new BlockPos(x, y, z));
	}

	public static TileEntity TargetTileEntity(World world, NBTTagCompound nbt)
	{
		if(nbt == null) return null;
		int x = nbt.getInteger(KeyRemotePosX);
		int y = nbt.getInteger(KeyRemotePosY);
		int z = nbt.getInteger(KeyRemotePosZ);
		return world.getTileEntity(new BlockPos(x, y, z));
	}


	///// for redstone

	@Override
	public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable EnumFacing side)
	{
		return true;
	}

	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state)
	{
		if (!world.isRemote)
		{
			tileEntityRemoteController tile = (tileEntityRemoteController) world.getTileEntity(pos);
			Block targetBlock = world.getBlockState(tile.remotePos).getBlock();
			targetBlock.onBlockAdded(world, pos, state);
		}
	}

	@Override
	public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor)
	{
		if (!((World) world).isRemote)
		{
			tileEntityRemoteController tile = (tileEntityRemoteController) world.getTileEntity(pos);
			Block targetBlock = world.getBlockState(tile.remotePos).getBlock();
			targetBlock.onNeighborChange(world, tile.remotePos, neighbor);
		}
	}
}
