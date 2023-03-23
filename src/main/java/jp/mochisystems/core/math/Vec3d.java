package jp.mochisystems.core.math;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3i;

public class Vec3d {

	public static final Vec3d Up = new ConstVec3d(0, 1, 0);
	public static final Vec3d Down = new ConstVec3d(0, -1, 0);
	public static final Vec3d Left = new ConstVec3d(1, 0, 0);
	public static final Vec3d Right = new ConstVec3d(-1, 0, 0);
	public static final Vec3d Front = new ConstVec3d(0, 0, 1);
	public static final Vec3d Back = new ConstVec3d(0, 0, -1);
	public static final Vec3d Zero = new ConstVec3d(0, 0, 0);
	public static final Vec3d One = new ConstVec3d(1, 1, 1);

	public double x;
	public double y;
	public double z;

	public Vec3d()
	{
		x = 0; y = 0; z = 0;
	}

	public Vec3d(double x, double y, double z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vec3d(Vec3i vi)
	{
		this.x = vi.getX();
		this.y = vi.getY();
		this.z = vi.getZ();
	}

	public Vec3d(Vec3d v)
	{
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
	}

	public Vec3d CopyFrom(Vec3d that)
	{
		this.x = that.x;
		this.y = that.y;
		this.z = that.z;
		return this;
	}

	public Vec3d SetFrom(double x, double y, double z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}

	public Vec3d New()
	{
		return new Vec3d(this);
	}

	public Vec3d add(Vec3d v)
	{
		this.x += v.x;
		this.y += v.y;
		this.z += v.z;
		return this;
	}

	public Vec3d add(double n)
	{
		this.x += n;
		this.y += n;
		this.z += n;
		return this;
	}

	public Vec3d add(double x, double y, double z)
	{
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}

	public Vec3d sub(Vec3d that)
	{
		return sub(that.x, that.y, that.z);
	}

	public Vec3d sub(double x, double y, double z)
	{
		this.x -= x;
		this.y -= y;
		this.z -= z;
		return this;
	}

	public Vec3d mul(Vec3d v)
	{
		this.x *= v.x;
		this.y *= v.y;
		this.z *= v.z;
		return this;
	}

	public Vec3d mul(double x, double y, double z)
	{
		this.x *= x;
		this.y *= y;
		this.z *= z;
		return this;
	}

	public Vec3d mul(double p)
	{
		this.x *= p;
		this.y *= p;
		this.z *= p;
		return this;
	}

	public Vec3d normalize()
	{
		double q = java.lang.Math.sqrt(x*x + y*y + z*z);
		double norm = 1.0 / q;
		this.x *= norm;
		this.y *= norm;
		this.z *= norm;
		return this;
	}

	public static Vec3d Lerp(Vec3d out, float t, Vec3d base, Vec3d next)
	{
        double x = (next.x - base.x) * t + base.x;
        double y = (next.y - base.y) * t + base.y;
        double z = (next.z - base.z) * t + base.z;
        out.x = x;
        out.y = y;
        out.z = z;
        return out;
	}

	public static Vec3d SLerp(Vec3d out, float t, Vec3d base, Vec3d next)
	{
		double angle = Math.AngleBetweenTwoVec(base, next);
        if(angle == 0){
			out.x = base.x;
			out.y = base.y;
			out.z = base.z;
			return out;
		}
		double PerSinTh = 1d / java.lang.Math.sin(angle);
        double Ps = java.lang.Math.sin( angle * ( 1 - t ) );
        double Pe = java.lang.Math.sin( angle * t );
        out.x = ( Ps * base.x + Pe * next.x ) * PerSinTh;
        out.y = ( Ps * base.y + Pe * next.y ) * PerSinTh;
        out.z = ( Ps * base.z + Pe * next.z ) * PerSinTh;
        out.normalize();
        return out;
	}

	public double dot(Vec3d that)
	{
		return this.x * that.x + this.y * that.y + this.z * that.z;
	}

	public Vec3d cross(Vec3d that)
	{
		double x = this.y * that.z - this.z * that.y;
		double y = that.x * this.z - that.z * this.x;
		this.z = this.x * that.y - this.y * that.x;
		this.x = x;
		this.y = y;
		return this;
	}

	public double length()
	{
		return java.lang.Math.sqrt(x * x + y * y + z * z);
	}

	public double distanceTo(Vec3d that)
	{
		double x = that.x - this.x;
		double y = that.y - this.y;
		double z = that.z - this.z;
		return java.lang.Math.sqrt(x*x + y*y + z*z);
	}

	public Vec3d Rotate(Vec3d axis, double radian)
	{
		radian *= 0.5;
		double rw = java.lang.Math.cos(radian);
		double rx = axis.x * java.lang.Math.sin(radian);
		double ry = axis.y * java.lang.Math.sin(radian);
		double rz = axis.z * java.lang.Math.sin(radian);
		double w = Math.MulQuaternion(this,
				rw, rx, ry, rz,
				0, this.x, this.y, this.z
		);
		Math.MulQuaternion(
				this,
				w, this.x, this.y, this.z,
				rw, -rx, -ry, -rz);
		return this;
	}

	public Vec3d Rotate(Quaternion rotation)
	{
        double w = Math.MulQuaternion(this,
                rotation.w, rotation.x, rotation.y, rotation.z,
				0, this.x, this.y, this.z);
		Math.MulQuaternion(this,
				w, this.x, this.y, this.z,
                rotation.w, -rotation.x, -rotation.y, -rotation.z);
		return this;
	}

	public void WriteBuf(ByteBuf buf)
	{
		buf.writeDouble(x);
		buf.writeDouble(y);
		buf.writeDouble(z);
	}

	public void ReadBuf(ByteBuf buf)
	{
		x = buf.readDouble();
		y = buf.readDouble();
		z = buf.readDouble();
	}

	public void WriteToNBT(String key, NBTTagCompound tag)
	{
		tag.setDouble(key + "vx", x);
		tag.setDouble(key + "vy", y);
		tag.setDouble(key + "vz", z);
	}

	public void ReadFromNBT(String key, NBTTagCompound tag)
	{
		x = tag.getDouble(key + "vx");
		y = tag.getDouble(key + "vy");
		z = tag.getDouble(key + "vz");
	}

	@Override
	public String toString()
	{
		return String.format("[%.4f, %.4f, %.4f]", x, y, z);
	}

    public boolean Equals(Vec3d other)
    {
        return this.x == other.x && this.y == other.y && this.z == other.z;
    }



    public static class ConstVec3d extends Vec3d{
		public ConstVec3d(double x, double y, double z)
		{
			super(x, y, z);
		}
		public Vec3d CopyFrom(Vec3d that) { return this; }
		public Vec3d SetFrom(double x, double y, double z) { return this; }
		public Vec3d add(Vec3d v) { return this; }
		public Vec3d add(double n) { return this; }
		public Vec3d sub(Vec3d that) { return this; }
		public Vec3d mul(Vec3d v) { return this; }
		public Vec3d mul(double p) { return this; }
		public Vec3d normalize() { return this; }
		public Vec3d cross(Vec3d that) { return this; }
		public Vec3d Rotate(Vec3d axis, double radian) { return this; }
	}
}
