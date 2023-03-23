package jp.mochisystems.core.blockcopier;

import jp.mochisystems.core._mc._core.Logger;
import jp.mochisystems.core._mc.entity.EntityBlockModelCollider;
import jp.mochisystems.core._mc.entity.EntityCollisionParts;
import jp.mochisystems.core._mc.world.MTYBlockAccess;
import jp.mochisystems.core.math.Quaternion;
import jp.mochisystems.core.math.Vec3d;
import jp.mochisystems.core.util.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class BlockModelColliderModule {

    public BlockModelColliderModule(MTYBlockAccess blockAccess, IBlockModel target, IModelController controller)
    {
        this.blockAccess = blockAccess;
        this.controller = controller;
        this.target = target;
    }

    public boolean IsEnableCollider()
    {
        return isEnableCollider;
    }

    public void ToggleActive()
    {
        isEnableCollider = !isEnableCollider;
        if(isEnableCollider)
        {
            isMade = false;
        }
    }

    public void AddEntity(EntityBlockModelCollider entity)
    {
        entityList.add(entity);
    }

    public void Update(Quaternion rotation)
    {
        DisconnectIfEntityDead();
        SpawnOrDeleteIfNeed();
        for(EntityBlockModelCollider e : entityList) e.LateUpdate(rotation);
    }

    public void ReadFromNBT(NBTTagCompound nbt)
    {
        isEnableCollider = nbt.getBoolean("isenablecollider");
        isMade = nbt.getBoolean("isMade");
    }
    public void WriteToNBT(NBTTagCompound nbt)
    {
        nbt.setBoolean("isenablecollider", isEnableCollider);
        nbt.setBoolean("isMade", isMade);
    }

    public void SetWorld(World world)
    {
        this.world = world;
    }

    public void SetScale(double scale)
    {
        for(EntityBlockModelCollider e : entityList) e.SetScale((float)scale);
    }

    private World world;
    private boolean isEnableCollider;
    private boolean isMade;
    private final IBlockModel target;
    private final IModelController controller;
    private final MTYBlockAccess blockAccess;
    private final List<EntityBlockModelCollider> entityList = new ArrayList<>();

    private void _Spawn()
    {
        int len = 5;
        int sizeX = blockAccess.getSizeX();
        int sizeY = blockAccess.getSizeY();
        int sizeZ = blockAccess.getSizeZ();
        int partialIdxX = (int)Math.ceil(sizeX / (float)len);
        int partialIdxY = (int)Math.ceil(sizeY / (float)len);
        int partialIdxZ = (int)Math.ceil(sizeZ / (float)len);
        for(int parIX = 0; parIX < partialIdxX; ++parIX)
        {
            for(int parIY = 0; parIY < partialIdxY; ++parIY)
            {
                for(int parIZ = 0; parIZ < partialIdxZ; ++parIZ)
                {
                    RegisterInPartialBox(len,
                            parIX * len,
                            parIY * len,
                            parIZ * len);
                }
            }
        }
    }

    private void RegisterInPartialBox(int len, int startX, int startY, int startZ)
    {
        EntityBlockModelCollider collider = null;
        for(int x = startX; x < startX + len; ++x)
            for(int y = startY; y < startY + len; ++y)
                for(int z = startZ; z < startZ + len; ++z){
                    IBlockState state = blockAccess.getBlockStateLocal(x, y, z);
                    AxisAlignedBB aabb = state.getCollisionBoundingBox(blockAccess, new BlockPos(x, y, z));
                    if(aabb != null){
                        double center = len*0.5;
                        collider = new EntityBlockModelCollider(world, target, controller, startX, startY, startZ);
                        collider.setLocationAndAngles(
                                target.ModelPosX() + center - blockAccess.GetLocalCorePosX(),
                                target.ModelPosY() + center - blockAccess.GetLocalCorePosY(),
                                target.ModelPosZ() + center - blockAccess.GetLocalCorePosZ(), 0f, 0f);

                        controller.MakeAndSpawnCollider(collider);
                        entityList.add(collider);
//                        Logger.debugInfo("collider spawn : "+startX+"."+startY+"."+startZ);
//                        Logger.debugInfo("collider position : "+collider.posX+"."+collider.posY+"."+collider.posZ);
//                        target.AddLateUpdater((ILateUpdater)collider);
                        return;
                    }
                }
    }

    private void DisconnectIfEntityDead()
    {
        for(int i = 0; i < entityList.size(); ++i) {
            EntityBlockModelCollider e = entityList.get(i);
            if(e.IsDead()) entityList.remove(e);
        }
    }
    private void SpawnOrDeleteIfNeed()
    {
        if(isEnableCollider && !isMade && blockAccess.CompletedConstructing)
        {
            if(!controller.IsRemote()) {
                _Spawn();
                isMade = true;
//                collider = controller.MakeAndSpawnCollider(this, blockAccess);
            }
        }
        else if(!isEnableCollider && isMade)
        {
            for(EntityBlockModelCollider e : entityList) {
                e.Delete();
            }
            entityList.clear();
            isMade = false;
        }
    }


}
