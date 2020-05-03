package mochisystems.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public interface IModel {
    boolean IsLock();
    void Reset();
    void Update();
    void Unload();
    void Invalidate();
    void readFromNBT(NBTTagCompound nbt);
    void writeToNBT(NBTTagCompound nbt);
    void setRSPower(int power);
    void SetWorld(World world);
    void RenderModel(int pass, float partialTick);
    boolean HasChild();
    IModel[] GetChildren();
    String GetName();
    void SetActive(boolean active);
    void SetOffset(float x, float y, float z);
}

