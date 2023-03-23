package jp.mochisystems.core._mc.block;

import jp.mochisystems.core._mc.tileentity.TileEntityModelBase;
import jp.mochisystems.core.blockcopier.IItemBlockModelHolder;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public abstract class BlockModelCoreBase extends BlockContainer {

    protected abstract TileEntity createModelHolderTileEntity();


    public BlockModelCoreBase() {
        super(Material.GOURD, MapColor.BLACK);
        this.setLightOpacity(0);
        this.setHardness(0.4F);
        this.setResistance(2000.0F);
    }

    @Override
    public boolean hasTileEntity() {
        return true;
    }

    @Override
    @Nullable
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return createModelHolderTileEntity();
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack stack)
    {
        if(!(stack.getItem() instanceof IItemBlockModelHolder)) return;
        if(!stack.hasTagCompound())return;
        NBTTagCompound nbt = stack.getTagCompound();
        TileEntityModelBase tile = (TileEntityModelBase) world.getTileEntity(pos);

        tile.GetModel().Reset();
        tile.GetModel().readFromNBT(nbt);
    }


    public static class Tester extends BlockModelCoreBase {
        protected TileEntity createModelHolderTileEntity()
        {
            return new TileEntityModelBase.BlockModelTester();
        }
    }
}
