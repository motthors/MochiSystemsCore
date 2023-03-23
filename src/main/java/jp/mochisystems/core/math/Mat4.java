package jp.mochisystems.core.math;


import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import org.lwjgl.BufferUtils;

import java.nio.DoubleBuffer;


public class Mat4 {

    private DoubleBuffer buf = BufferUtils.createDoubleBuffer(16); //DoubleBuffer.allocate(16);
    private final Vec3d dirX = new Vec3d(1, 0, 0);
    private final Vec3d dirY = new Vec3d(0, 1, 0);
    private final Vec3d dirZ = new Vec3d(0, 0, 1);
    private final Vec3d pos = new Vec3d(0, 0, 0);

    public Mat4()
    {
        Identifier();
        makeBuffer();
    }

    public void Identifier()
    {
        dirX.CopyFrom(new Vec3d(1, 0, 0));
        dirY.CopyFrom(new Vec3d(0, 1, 0));
        dirZ.CopyFrom(new Vec3d(0, 0, 1));
        pos.CopyFrom(new Vec3d(0, 0, 0));
        makeBuffer();
    }

    public void ResetRot()
    {
        dirX.CopyFrom(new Vec3d(1, 0, 0));
        dirY.CopyFrom(new Vec3d(0, 1, 0));
        dirZ.CopyFrom(new Vec3d(0, 0, 1));
        makeBuffer();
    }

    public boolean Equals(Mat4 other)
    {
        return this.dirX.Equals(other.dirX)
            && this.dirY.Equals(other.dirY)
            && this.dirZ.Equals(other.dirZ)
            && this.pos.Equals(other.pos);
    }

    public void CopyFrom(Mat4 src)
    {
        this.pos.CopyFrom(src.pos);
        this.dirZ.CopyFrom(src.dirZ);
        this.dirY.CopyFrom(src.dirY);
        this.dirX.CopyFrom(src.dirX);
        makeBuffer();
    }

    public void SetFrom(Vec3d pos, Vec3d dir, Vec3d up){
        this.pos.CopyFrom(pos);
        this.dirZ.CopyFrom(dir);
        this.dirY.CopyFrom(up);
        correctOrthogonal();
        makeBuffer();
    }

    public void setPos(Vec3d pos)
    {
        this.pos.CopyFrom(pos);
        makeBuffer();
    }

    public void setDir(Vec3d dir)
    {
        this.dirZ.CopyFrom(dir);
        makeBuffer();
    }

    public void setUp(Vec3d up)
    {
        this.dirY.CopyFrom(up);
        makeBuffer();
    }

    public void setSide(Vec3d side) {
        this.dirX.CopyFrom(side);
        makeBuffer();
    }

    public Vec3d Pos()
    {
        return pos;
    }

    public Vec3d Dir()
    {
        return dirZ;
    }

    public Vec3d Up()
    {
        return dirY;
    }

    public Vec3d Side()
    {
        return dirX;
    }

    public void rotation(double radian, Vec3d axis)
    {
        dirZ.Rotate(axis, radian).normalize();
        dirY.Rotate(axis, radian).normalize();
        correctOrthogonal();
        makeBuffer();
    }

    public DoubleBuffer makeBuffer()
    {
        buf.clear();
//        buf.put(0, dirX.x);
//        buf.put(1, dirY.x);
//        buf.put(2, dirZ.x);
//        buf.put(3, 0);
//        buf.put(4, dirX.y);
//        buf.put(5, dirY.y);
//        buf.put(6, dirZ.y);
//        buf.put(7, 0);
//        buf.put(8, dirX.z);
//        buf.put(9, dirY.z);
//        buf.put(10, dirZ.z);
//        buf.put(11, 0);
//        buf.put(12, pos.x);
//        buf.put(13, pos.y);
//        buf.put(11, pos.z);
//        buf.put(15, 1d);

//        buf.put(0, 1);
//        buf.put(1, 0);
//        buf.put(2, 0);
//        buf.put(3, 0);
//        buf.put(4, 0);
//        buf.put(5, 1);
//        buf.put(6, 0);
//        buf.put(7, 0);
//        buf.put(8, 0);
//        buf.put(9, 0);
//        buf.put(10, 1);
//        buf.put(11, 0);
//        buf.put(12, pos.x);
//        buf.put(13, pos.y);
//        buf.put(14, pos.z);
//        buf.put(15, 1d);

        buf.put(0, dirX.x);
        buf.put(1, dirX.y);
        buf.put(2, dirX.z);
        buf.put(3, 0);
        buf.put(4, dirY.x);
        buf.put(5, dirY.y);
        buf.put(6, dirY.z);
        buf.put(7, 0);
        buf.put(8, dirZ.x);
        buf.put(9, dirZ.y);
        buf.put(10, dirZ.z);
        buf.put(11, 0);
        buf.put(12, pos.x);
        buf.put(13, pos.y);
        buf.put(14, pos.z);
        buf.put(15, 1d);
        buf.rewind();
        return buf;
    }

    public DoubleBuffer getBuffer()
    {
        return buf;
    }

    private void correctOrthogonal()
    {
        dirX.CopyFrom(dirY).cross(dirZ).normalize();
        dirY.CopyFrom(dirZ).cross(dirX).normalize();
    }

    public void WriteToBytes(ByteBuf buf)
    {
        dirX.WriteBuf(buf);
        dirY.WriteBuf(buf);
        dirZ.WriteBuf(buf);
        pos.WriteBuf(buf);
    }

    public void ReadFromBytes(ByteBuf buf)
    {
        dirX.ReadBuf(buf);
        dirY.ReadBuf(buf);
        dirZ.ReadBuf(buf);
        pos.ReadBuf(buf);
    }

    public void WriteToNBT(String key, NBTTagCompound tag)
    {
        dirX.WriteToNBT(key+"dirx", tag);
        dirY.WriteToNBT(key+"diry", tag);
        dirZ.WriteToNBT(key+"dirz", tag);
        pos.WriteToNBT(key+"pos", tag);
    }

    public void ReadFromNBT(String key, NBTTagCompound tag)
    {
        dirX.ReadFromNBT(key+"dirx", tag);
        dirY.ReadFromNBT(key+"diry", tag);
        dirZ.ReadFromNBT(key+"dirz", tag);
        pos.ReadFromNBT(key+"pos", tag);
    }

    public void Lerp(Mat4 next, float t)
    {
        Vec3d.SLerp(dirZ, t, dirZ, next.dirZ);
        Vec3d.SLerp(dirY, t, dirY, next.dirY);
        dirZ.normalize();
        dirX.CopyFrom(dirY).cross(dirZ);
        dirX.normalize();
        dirY.CopyFrom(dirZ).cross(dirX);
        dirY.normalize();
//        Vec3d.Lerp(pos, t, pos, next.pos);
    }
}

