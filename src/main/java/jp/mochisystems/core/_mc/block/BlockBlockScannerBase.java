package jp.mochisystems.core._mc.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class BlockBlockScannerBase extends BlockContainer {

    protected abstract int GetGuiId();
    protected abstract Object GetModCore();
    protected abstract TileEntity CreateTileEntityScanner();

    public BlockBlockScannerBase()
    {
        super(Material.GROUND);
        this.setHardness(1.0F);
        this.setResistance(2000.0F);
        this.setLightOpacity(1);
        this.setLightLevel(0.0F);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        player.openGui(GetModCore(), GetGuiId(), world, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return CreateTileEntityScanner();
    }


//	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemstack)
//	{
//		TileEntityRailModelConstructor tile = (TileEntityRailModelConstructor) world.getTileEntity(x, y, z);
//		tile.createVertex();
//		tile.SetSide(world.getBlockMetadata(x, y, z));
//	}

    public void breakBlock(World world, BlockPos pos, IBlockState state)
    {
        TileEntity tileentity = world.getTileEntity(pos);
        InventoryHelper.dropInventoryItems(world, pos, (IInventory)tileentity);

        super.breakBlock(world, pos, state);
    }

}
