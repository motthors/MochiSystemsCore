package jp.mochisystems.core.util;

import jp.mochisystems.core.manager.EntityWearingModelManager;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import javax.swing.event.TreeExpansionListener;


public class CommonAddress {

    public int x;
    public int y = -9999;
    public int z;
    public int TreeListIndex;
    public int entityId;

    public CommonAddress Init(int x, int y, int z, int TreeListIndex)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.TreeListIndex = TreeListIndex;
        this.entityId = -1;
        return this;
    }
    public CommonAddress Init(int entityId, int TreeListIndex)
    {
        this.entityId = entityId;
        this.TreeListIndex = TreeListIndex;
        this.y = -9999;
        return this;
    }

    public void Disconnect()
    {
        x = 0;
        y = -9999;
        z = 0;
        TreeListIndex = 0;
        entityId = -1;
    }

    public boolean isSyncing()
    {
        return y != -9999 || entityId >= 0;
    }

    public IModel GetInstance(IModelController controller)
    {
        return GetInstance(controller.World());
    }
    @Nullable
    public IModel GetInstance(World world)
    {
        if(entityId >= 0) {
            if(y > -1) {
                Entity entity = world.getEntityByID(entityId);
                if(entity == null) return null;
                return EntityWearingModelManager.GetCurrentModel(entity, y);
            }
            if(!(world.getEntityByID(entityId) instanceof IModelController)){
                return null;
            }
            IModelController c = (IModelController) world.getEntityByID(entityId);
            if(c == null) return null;
            return c.GetModel().GetModelFromTreeIndex(TreeListIndex);
        }
        IModelController c = ((IModelController) world
                .getTileEntity(new BlockPos(x, y, z)));
        if(c == null) return null;
        return c.GetModel().GetModelFromTreeIndex(TreeListIndex);
    }

    public IModelController GetController(World world)
    {
        if(entityId >= 0) {
            if(!(world.getEntityByID(entityId) instanceof IModelController)){
                return null;
            }
            return (IModelController) world.getEntityByID(entityId);
        }
        return ((IModelController) world.getTileEntity(new BlockPos(x, y, z)));
    }

    public void readFromNBT(NBTTagCompound nbt, String key)
    {
        TreeListIndex = nbt.getInteger("parentSyncTileIdx");
        entityId = nbt.getInteger("entityId");
        if(TreeListIndex != -1)
        {
            x = nbt.getInteger(key+"parentsyncx");
            if(nbt.hasKey(key+"parentsyncy")) y = nbt.getInteger(key+"parentsyncy");
            z = nbt.getInteger(key+"parentsyncz");
        }
    }

    public void writeToNBT(NBTTagCompound nbt, String key)
    {
        nbt.setInteger(key+"parentsyncx", x);
        nbt.setInteger(key+"parentsyncy", y);
        nbt.setInteger(key+"parentsyncz", z);
        nbt.setInteger("parentSyncTileIdx", TreeListIndex);
        nbt.setInteger("entityId", entityId);
    }

    public boolean Equals(CommonAddress other)
    {
        return (this.x == other.x && this.y == other.y && this.z == other.z
                && this.TreeListIndex == other.TreeListIndex)
                || (this.entityId!=-1 && this.entityId == other.entityId);
    }

    public void CopyFrom(CommonAddress other)
    {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
        this.TreeListIndex = other.TreeListIndex;
        this.entityId = other.entityId;
    }
}
