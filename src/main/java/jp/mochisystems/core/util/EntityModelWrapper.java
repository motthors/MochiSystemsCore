package jp.mochisystems.core.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class EntityModelWrapper implements IModelController{

    public Entity entity;
    public IModel model;
    int slot;

    @Override
    public CommonAddress GetCommonAddress()
    {
        CommonAddress ca = new CommonAddress();
        ca.Init(entity.getEntityId(), 0);
        ca.y = slot;
        return ca;
    }

    public EntityModelWrapper(Entity entity, int slot)
    {
        this.entity = entity;
        this.slot = slot;
    }

    public void SetModel(IModel model)
    {
        this.model = model;
    }

    @Override
    public double CorePosX() {
        return entity.posX;
    }

    @Override
    public double CorePosY() {
        return entity.posY;
    }

    @Override
    public double CorePosZ() {
        return entity.posZ;
    }

    @Override
    public EnumFacing CoreSide() {
        return EnumFacing.NORTH;
    }

    @Override
    public boolean IsInvalid() {
        return entity.isDead;
    }

    @Override
    public boolean IsRemote() {
        return entity.world.isRemote;
    }

    @Override
    public void markBlockForUpdate() {

    }

    @Override
    public World World() {
        return entity.world;
    }

    @Nonnull
    @Override
    public IModel GetModel() {
        return model;
    }

}
