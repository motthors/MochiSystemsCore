package jp.mochisystems.core._mc.block;

import jp.mochisystems.core._mc._core._Core;
import jp.mochisystems.core._mc.tileentity.TileEntityBlockModelCutter;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockModelCutter extends BlockContainer{

	public BlockModelCutter()
	{
		super(Material.GROUND);
		this.setHardness(1.0F);
		this.setResistance(2000.0F);
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) 
	{
		return new TileEntityBlockModelCutter();
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
		//OPEN GUI
		playerIn.openGui(_Core.Instance, _Core.GUIID_Cutter, worldIn, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state)
	{
		return EnumBlockRenderType.MODEL;
	}

}
