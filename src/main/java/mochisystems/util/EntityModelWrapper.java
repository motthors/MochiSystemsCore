package mochisystems.util;

import mochisystems.blockcopier.IModelCollider;
import mochisystems.blockcopier.MTYBlockAccess;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class EntityModelWrapper implements IModelController{

    Entity entity;
    IModel model;

    public EntityModelWrapper(Entity entity)
    {
        this.entity = entity;
    }

    public void SetModel(IModel model)
    {
        this.model = model;
    }

    @Override
    public int CorePosX() {
        return (int)entity.posX;
    }

    @Override
    public int CorePosY() {
        return (int)entity.posY;
    }

    @Override
    public int CorePosZ() {
        return (int)entity.posZ;
    }

    @Override
    public int CoreSide() {
        return 2;
    }

    @Override
    public boolean IsInvalid() {
        return entity.isDead;
    }

    @Override
    public boolean IsRemote() {
        return entity.worldObj.isRemote;
    }

    @Override
    public void markBlockForUpdate() {

    }

    @Override
    public World World() {
        return entity.worldObj;
    }

    @Override
    public IModel GetBlockModel(int x, int y, int z) {
        return null;
    }

    @Override
    public IModelCollider MakeAndSpawnCollider(IModel parent, MTYBlockAccess blockAccess) {
        return null;
    }
}
