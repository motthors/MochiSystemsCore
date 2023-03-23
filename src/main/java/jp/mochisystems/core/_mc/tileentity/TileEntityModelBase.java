package jp.mochisystems.core._mc.tileentity;

import jp.mochisystems.core._mc._core.Logger;
import jp.mochisystems.core._mc.world.MTYBlockAccess;
import jp.mochisystems.core.blockcopier.DefBlockModel;
import jp.mochisystems.core.blockcopier.IModelCollider;
import jp.mochisystems.core.util.IModel;
import jp.mochisystems.core.util.IModelController;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public abstract class TileEntityModelBase extends TileEntity implements IModelController, ITickable {

    public abstract IModel CreateModel();


    protected final IModel model;

    public TileEntityModelBase() {
        model = CreateModel();
        Logger.info("tile : instantiate");
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    @Override
    public double getMaxRenderDistanceSquared()
    {
        return 100000d;
    }

    @Override
    public void update() {
        model.Update();
    }

    @Override
    public NBTTagCompound getUpdateTag()
    {
        NBTTagCompound nbt = super.getUpdateTag();
        writeToNBT(nbt);
        return nbt;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        model.writeToNBT(compound);
        super.writeToNBT(compound);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        model.readFromNBT(compound);
        super.readFromNBT(compound);
    }

    @Override
    public double CorePosX() {
        return pos.getX();
    }

    @Override
    public double CorePosY() {
        return pos.getY();
    }

    @Override
    public double CorePosZ() {
        return pos.getZ();
    }

    @Override
    public EnumFacing CoreSide() {
        return EnumFacing.UP;
    }

    @Override
    public boolean IsInvalid() {
        return false;
    }

    @Override
    public boolean IsRemote() {
        return false;
    }

    @Override
    public void markBlockForUpdate() {
        markDirty();
        IBlockState state = World().getBlockState(pos);
        World().notifyBlockUpdate(pos, state, state, 3);
    }

    @Override
    public World World() {
        return world;
    }

    @Nonnull
    @Override
    public IModel GetModel() {
        return model;
    }




    public static class BlockModelTester extends TileEntityModelBase {
        @Override
        public IModel CreateModel()
        {
             return new DefBlockModel(this);
        }
    }
}
