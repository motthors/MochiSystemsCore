package jp.mochisystems.core.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public interface IModelController {
    double CorePosX();
    double CorePosY();
    double CorePosZ();
    EnumFacing CoreSide();
    boolean IsInvalid();
    boolean IsRemote();
    void markBlockForUpdate();
    World World();
    @Nonnull IModel GetModel();
    default void MakeAndSpawnCollider(Entity entity){
        World().spawnEntity(entity);
    }
    default CommonAddress GetCommonAddress() {
        return new CommonAddress().Init(
                MathHelper.floor(CorePosX()),
                MathHelper.floor(CorePosY()),
                MathHelper.floor(CorePosZ()), 0);
    }
}
