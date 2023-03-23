package jp.mochisystems.core.math;

import jp.mochisystems.core._mc._core.Logger;
import org.lwjgl.BufferUtils;

import java.nio.DoubleBuffer;

public class Quaternion extends Vec4 {

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

    public Quaternion Make(Vec3d axis, double radian) {
        radian *= 0.5;
        double sin = java.lang.Math.sin(radian);
        w = java.lang.Math.cos(radian);
        x = axis.x * sin;
        y = axis.y * sin;
        z = axis.z * sin;
        return this;
    }

    public Quaternion Make(Vec3d start, Vec3d end) {
        Vec3d cross = new Vec3d(start).cross(end);
        if (cross.length() < 0.0001) cross.CopyFrom(Vec3d.Up);
        else cross.normalize();
        double angle = Math.AngleBetweenTwoVec(start, end);
        if(angle == 0.0) this.Identity();
        else Make(cross, angle);
        return this;
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
    
    public static void mul(Quaternion out, Quaternion left, Quaternion right)
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

    public void Wrap()
    {
        this.x *= -1;
        this.y *= -1;
        this.z *= -1;
        this.w *= -1;
    }

    public boolean IsSameDir(Quaternion that)
    {
        return this.dot(that) >= 0;
//        return this.w * that.w >= 0;
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

    public void Slerp(Quaternion next, double t)
    {
        Slerp(this, this, next, t);
    }
    public static void Slerp(Quaternion out, Quaternion base, Quaternion next, double t)
    {
        //https://swkagami.hatenablog.com/entry/lie_08slerp
        double dot = Vec4.Dot(base, next);
        dot = Math.Clamp(dot, -1, 1);
        double theta = java.lang.Math.acos(dot);
        double sin = java.lang.Math.sin(theta);
        if(sin == 0) return;
        double sin_r = java.lang.Math.sin((1-t) * theta);
        double sin_t = java.lang.Math.sin(t * theta);
        double l = sin_r / sin;
        double r = sin_t / sin;
        out.x = l * out.x + r * next.x;
        out.y = l * out.y + r * next.y;
        out.z = l * out.z + r * next.z;
        out.w = l * out.w + r * next.w;
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


    @Override
    public String toString()
    {
        return String.format("[%.2f.%.2f.%.2f:%.2f]", x, y, z, w);
    }

    public Vec3d Euler()
    {
//        makeDirection();
//        double ty = java.lang.Math.asin(-dir.x);
//        double tx, tz;
//        if(java.lang.Math.cos(ty) == 0){
//            tx = 0;
//            tz = java.lang.Math.atan(-left.y/up.y);
//        }
//        else {
//            tx = java.lang.Math.atan(dir.y/dir.z);
//            tz = java.lang.Math.atan(up.x/left.x);
//        }
//        return new Vec3d(tx, ty, tz);
        Quaternion q = this;
        double sinr_cosp = 2 * (q.w * q.x + q.y * q.z);
        double cosr_cosp = 1 - 2 * (q.x * q.x + q.y * q.y);
        double tx = java.lang.Math.atan2(sinr_cosp, cosr_cosp);

        double sinp = java.lang.Math.sqrt(1 + 2 * (q.w * q.y - q.x * q.z));
        double cosp = java.lang.Math.sqrt(1 - 2 * (q.w * q.y - q.x * q.z));
        double ty = 2 * java.lang.Math.atan2(sinp, cosp) - java.lang.Math.PI / 2;

        double siny_cosp = 2 * (q.w * q.z + q.x * q.y);
        double cosy_cosp = 1 - 2 * (q.y * q.y + q.z * q.z);
        double tz = java.lang.Math.atan2(siny_cosp, cosy_cosp);

        return new Vec3d(tx, ty, tz);
    }

    @Deprecated
    public static Quaternion MakeQuaternionFromDirUp(Quaternion outQ, Vec3d Dir, Vec3d _Up)
    {
        Vec3d left = _Up.New().cross(Dir);
        Vec3d Up = Dir.New().cross(left);
//        double[] elem = new double[4];
        double elem0 = left.x - Up.y - Dir.z + 1.0f;
        double elem1 = -left.x + Up.y - Dir.z + 1.0f;
        double elem2 = -left.x - Up.y + Dir.z + 1.0f;
        double elem3 = left.x + Up.y + Dir.z + 1.0f;

        int biggestIdx = 0;
        double biggest = elem0;
        if(elem1 > biggest) { biggestIdx = 1; biggest = elem1; }
        if(elem2 > biggest) { biggestIdx = 2; biggest = elem2; }
        if(elem3 > biggest) { biggestIdx = 3; biggest = elem3; }

        if (biggest < 0)
        {
            Logger.warn("Wrong matrix.");
            return new Quaternion();
        }

//        double[] q = new double[4];
        double q0 = 0, q1 = 0, q2 = 0, q3 = 0;
        float v = (float)java.lang.Math.sqrt(biggest) * 0.5f;
//        q[biggestIdx] = v;
        switch(biggestIdx){
            case 0 : q0 = v; break;
            case 1 : q1 = v; break;
            case 2 : q2 = v; break;
            case 3 : q3 = v; break;
        }
        float mult = 0.25f / v;

        switch (biggestIdx)
        {
            case 0:
//				q[1] = (m.m10 + m.m01) * mult;
//				q[2] = (m.m02 + m.m20) * mult;
//				q[3] = (m.m21 - m.m12) * mult;
                q1 = (left.y + Up.x) * mult;
                q2 = (Dir.x + left.z) * mult;
                q3 = (Up.z - Dir.y) * mult;
                break;
            case 1:
//				q[0] = (m.m10 + m.m01) * mult;
//				q[2] = (m.m21 + m.m12) * mult;
//				q[3] = (m.m02 - m.m20) * mult;
                q0 = (left.y + Up.x) * mult;
                q2 = (Up.z + Dir.y) * mult;
                q3 = (Dir.x - left.z) * mult;
                break;
            case 2:
//				q[0] = (m.m02 + m.m20) * mult;
//				q[1] = (m.m21 + m.m12) * mult;
//				q[3] = (m.m10 - m.m01) * mult;
                q0 = (Dir.x + left.z) * mult;
                q1 = (Up.z + Dir.y) * mult;
                q3 = (left.y - Up.x) * mult;
                break;
            case 3:
//				q[0] = (m.m21 - m.m12) * mult;
//				q[1] = (m.m02 - m.m20) * mult;
//				q[2] = (m.m10 - m.m01) * mult;
                q0 = (Up.z - Dir.y) * mult;
                q1 = (Dir.x - left.z) * mult;
                q2 = (left.y - Up.x) * mult;
                break;
        }

//		return new Quaternion(q[0], q[1], q[2], q[3]);
        outQ.x = q0;
        outQ.y = q1;
        outQ.z = q2;
        outQ.w = q3;
        outQ.normalized();
        return outQ;
    }

    public static class V3Mat {
        public final Vec3d left = new Vec3d();
        public final Vec3d up = new Vec3d();
        public final Vec3d dir = new Vec3d();
        public V3Mat() { }
        public V3Mat(Quaternion q) {
            Fix(q);
        }
        public void Fix(Quaternion q) {
            double x2 = q.x * q.x * 2;
            double y2 = q.y * q.y * 2;
            double z2 = q.z * q.z * 2;
            double xy = q.x * q.y * 2;
            double yz = q.y * q.z * 2;
            double xz = q.x * q.z * 2;
            double wx = q.w * q.x * 2;
            double wy = q.w * q.y * 2;
            double wz = q.w * q.z * 2;
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
    }
    public static class MatBuffer {
        private final DoubleBuffer buf = BufferUtils.createDoubleBuffer(16);
        public MatBuffer() {}
        public MatBuffer(Quaternion q)
        {
            buf.clear();
            Fix(q);
        }
        public DoubleBuffer Fix(Quaternion q)
        {
            double x2 = q.x * q.x * 2;
            double y2 = q.y * q.y * 2;
            double z2 = q.z * q.z * 2;
            double xy = q.x * q.y * 2;
            double yz = q.y * q.z * 2;
            double xz = q.x * q.z * 2;
            double wx = q.w * q.x * 2;
            double wy = q.w * q.y * 2;
            double wz = q.w * q.z * 2;
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
    }
}
