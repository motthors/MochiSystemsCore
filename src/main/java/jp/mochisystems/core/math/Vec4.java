package jp.mochisystems.core.math;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

public class Vec4{

    double x;
    double y;
    double z;
    double w;

    public Vec4()
    {
        x=0;
        y=0;
        z=0;
        w=0;
    }
    public Vec4(double x,double y,double z,double w)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public static double Dot(Vec4 a, Vec4 b)
    {
        return a.x * b.x
                + a.y * b.y
                + a.z * b.z
                + a.w * b.w;
    }

    public Vec4 WriteToNBT(String key, NBTTagCompound tag)
    {
        tag.setDouble(key + "vx", x);
        tag.setDouble(key + "vy", y);
        tag.setDouble(key + "vz", z);
        tag.setDouble(key + "vw", w);
        return this;
    }

    public Vec4 ReadFromNBT(String key, NBTTagCompound tag)
    {
        x = tag.getDouble(key + "vx");
        y = tag.getDouble(key + "vy");
        z = tag.getDouble(key + "vz");
        w = tag.getDouble(key + "vw");
        return this;
    }

    public void WriteToBytes(ByteBuf buf)
    {
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeDouble(w);
    }
    public void ReadFromBytes(ByteBuf buf)
    {
        x = buf.readDouble();
        y = buf.readDouble();
        z = buf.readDouble();
        w = buf.readDouble();
    }

    @Override
    public boolean equals(Object q)
    {
        if(q instanceof Quaternion) {
            Quaternion Q = (Quaternion) q;
            return this.x == Q.x && this.y == Q.y && this.z == Q.z && this.w == Q.w;
        }
        return false;
    }
}