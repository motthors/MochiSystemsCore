//package jp.mochisystems.core._mc.block;
//
//import net.minecraft.block.Block;
//import net.minecraft.block.material.Material;
//import net.minecraft.entity.EntityLivingBase;
//import net.minecraft.item.ItemStack;
//import net.minecraft.util.math.AxisAlignedBB;
//import net.minecraft.util.math.MathHelper;
//import net.minecraft.world.World;
//
//public class BlockSeatToSitEx extends Block {
//
//	public BlockSeatToSitEx()
//	{
//		super(Material.GROUND);
//	}
//
//
//	@Override
//	public boolean canPlaceBlockOnSide(World world, int x, int y, int z, int side)
//	{
//		return side == 1;
//	}
//
//	@Override
//	public boolean isOpaqueCube()
//	{
//		return false;
//	}
//	@Override
//	public boolean renderAsNormalBlock()
//	{
//		return false;
//	}
//
//
//	public void onBlockPlacedBy(World world, BlockPos pos, EntityLivingBase player, ItemStack itemstack)
//	{
//		int l = MathHelper.Floor((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
//		int meta=l;
////		switch(l)
////		{
////        case 0: coreSide = 2; break;
////        case 1: coreSide = 5; break;
////        case 2: coreSide = 3; break;
////        case 3: coreSide = 4; break;
////        }
////		MFW_Logger.debugInfo("blockseatex : "+coreSide);
//		world.notifyBlockUpdate(x, y, z, meta, 2);
//	}
//}
