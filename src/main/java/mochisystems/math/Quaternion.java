package mochisystems.math;

import org.lwjgl.BufferUtils;

import java.nio.DoubleBuffer;

public class Quaternion extends Vec4 {

    private DoubleBuffer buf = BufferUtils.createDoubleBuffer(16);
    public final Vec3d left = new Vec3d();
    public final Vec3d up = new Vec3d();
    public final Vec3d dir = new Vec3d();

    public Quaternion() {
        this.Identity();
    }

    public Quaternion(double x, double y, double z, double w) {
        this.w = w;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Quaternion CopyFrom(Quaternion that) {
        this.w = that.w;
        this.x = that.x;
        this.y = that.y;
        this.z = that.z;
        return this;
    }

    public void Make(Vec3d axis, double radian) {
        radian *= 0.5;
        double sin = java.lang.Math.sin(radian);
        w = java.lang.Math.cos(radian);
        x = axis.x * sin;
        y = axis.y * sin;
        z = axis.z * sin;
    }

    public Quaternion mul(Quaternion that) {
        mul(this, this, that);
        return this;
    }

    public Quaternion mulLeft(Quaternion that)
    {
        mul(this, that, this);
        return this;
    }
    
    public void mul(Quaternion out, Quaternion left, Quaternion right)
    {
        double w = left.w*right.w - left.x*right.x - left.y*right.y - left.z*right.z;
        double x = left.x*right.w + left.w*right.x - left.z*right.y + left.y*right.z;
        double y = left.y*right.w + left.z*right.x + left.w*right.y - left.x*right.z;
        double z = left.z*right.w - left.y*right.x + left.x*right.y + left.w*right.z;
        out.x = x;
        out.y = y;
        out.z = z;
        out.w = w;
    }


    public void Identity()
    {
        w = 1;
        x = y = z = 0;
    }

    public void Inverse()
    {
        x *= -1;
        y *= -1;
        z *= -1;
    }

    public Quaternion Make(Vec3d baseDir, Vec3d baseUp, Vec3d toDir, Vec3d toUp)
    {
        double dirAngle = Math.AngleBetweenTwoVec(baseDir, toDir);

        return this;
    }

    public void Lerp(Quaternion next, double t)
    {
        double w = (next.w - this.w) * t + this.w;
        double x = (next.x - this.x) * t + this.x;
        double y = (next.y - this.y) * t + this.y;
        double z = (next.z - this.z) * t + this.z;
        this.w = w;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double dot(Quaternion that)
    {
        return this.x * that.x + this.y * that.y + this.z * that.z + this.w * that.w;
    }

    public static void Lerp(Quaternion out, Quaternion q0, Quaternion q1, double t)
    {
        double s = (q0.dot(q1) < 0d) ? -1d : 1d;
        double w = (s * q1.w - q0.w) * t + q0.w;
        double x = (s * q1.x - q0.x) * t + q0.x;
        double y = (s * q1.y - q0.y) * t + q0.y;
        double z = (s * q1.z - q0.z) * t + q0.z;
        double l = w*w+x*x+y*y+z*z;
        l = 1d / java.lang.Math.sqrt(l);
        out.w = w * l;
        out.x = x * l;
        out.y = y * l;
        out.z = z * l;
    }

    public Quaternion normalized()
    {
        double l = w*w+x*x+y*y+z*z;
        l = 1d / java.lang.Math.sqrt(l);
        w = w * l;
        x = x * l;
        y = y * l;
        z = z * l;
        return this;
    }

    public void makeDirection()
    {
        double x2 = x * x * 2;
        double y2 = y * y * 2;
        double z2 = z * z * 2;
        double xy = x * y * 2;
        double yz = y * z * 2;
        double xz = x * z * 2;
        double wx = w * x * 2;
        double wy = w * y * 2;
        double wz = w * z * 2;
        left.x = 1-y2-z2;
        left.y = xy+wz;
        left.z = xz-wy;
        up.x = xy-wz;
        up.y = 1-x2-z2;
        up.z = yz+wx;
        dir.x = xz + wy;
        dir.y = yz-wx;
        dir.z =  1-x2-y2;
    }

    public DoubleBuffer makeMatrixBuffer()
    {
        double x2 = x * x * 2;
        double y2 = y * y * 2;
        double z2 = z * z * 2;
        double xy = x * y * 2;
        double yz = y * z * 2;
        double xz = x * z * 2;
        double wx = w * x * 2;
        double wy = w * y * 2;
        double wz = w * z * 2;
        buf.clear();
        buf.put(0, 1-y2-z2);
        buf.put(4, xy-wz);
        buf.put(8, xz + wy);
        buf.put(3, 0);

        buf.put(1, xy+wz);
        buf.put(5, 1-x2-z2);
        buf.put(9, yz-wx);
        buf.put(7, 0);

        buf.put(2, xz-wy);
        buf.put(6, yz+wx);
        buf.put(10, 1-x2-y2);
        buf.put(11, 0);

        buf.put(12, 0);
        buf.put(13, 0);
        buf.put(14, 0);
        buf.put(15, 1d);
        buf.rewind();
        return buf;
    }

    public DoubleBuffer GetBuffer()
    {
        return buf;
    }

    @Override
    public String toString()
    {
        return String.format("[%.2f.%.2f.%.2f:%.2f]", x, y, z, w);
    }
}
