package jp.mochisystems.core.util;

import jp.mochisystems.core.math.Quaternion;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IModel {
    void SetWorld(World world);
    boolean HasChild();
    boolean IsLock();
    default void SetLock(boolean lock){}
    boolean IsInvalid();
    void Reset();
    void Update();
    void Unload();
    void Invalidate();
    void readFromNBT(NBTTagCompound nbt);
    void writeToNBT(NBTTagCompound nbt);
    void setRSPower(int power);
    void SetVisible(boolean active);
    void SetOffset(float x, float y, float z);
    void SetRotation(Quaternion attitude);
    IModel[] GetChildren();
    double ModelPosX();
    double ModelPosY();
    double ModelPosZ();
    String GetName();
    CommonAddress GetCommonAddress();

    @SideOnly(Side.CLIENT)
    void RenderModel(int pass, float partialTick);

    default IModel GetModelFromTreeIndex(int index){
        return this;
    }
}
