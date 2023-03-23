package jp.mochisystems.core.math;

import net.minecraft.util.math.MathHelper;

public class Math {

	static final double ToRad = 1 / 360d * 2 * java.lang.Math.PI;
	static final double ToDeg = 1 * 360d / (2 * java.lang.Math.PI);

	public static int floor(double d)
	{
		return MathHelper.floor(d);
	}

	public static double toRadians(double deg)
	{
		return deg * ToRad;
	}

	public static double toDegrees(double rad)
	{
		return rad * ToDeg;
	}

	public static void Spline(Vec3d outPos, double t, Vec3d base, Vec3d next, Vec3d dir1, Vec3d dir2)
	{
		double t2 = t*t;
		double t3 = t2*t;

		double x =  2*t3 -3*t2    +1;
		double y = -2*t3 +3*t2;
		double z =    t3 -2*t2 +t;
		double w =    t3   -t2;

		outPos.SetFrom(
			base.x * x +
				next.x * y +
				dir1.x * z +
				dir2.x * w,
			base.y * x +
				next.y * y +
				dir1.y * z +
				dir2.y * w,
			base.z * x +
				next.z * y +
				dir1.z * z +
				dir2.z * w);
	}

	public static void SplineNormal(Vec3d out, double t, Vec3d base, Vec3d next, Vec3d dir1, Vec3d dir2)
	{
		double t2 = t*t;
		double t3 = t2*t;
		double x = 2*t3-3*t2+1;
		double y = -2*t3+3*t2;
		double z = t3-2*t2+t;
		double w = t3-t2;

		out.SetFrom(
				base.x * x +
					next.x * y +
					-dir1.x * z * 0.2 +
					-dir2.x * w * 0.2,
				base.y * x +
					next.y * y +
					-dir1.y * z * 0.2 +
					-dir2.y * w * 0.2,
				base.z * x +
					next.z * y +
					-dir1.z * z * 0.2 +
					-dir2.z * w * 0.2)
				.normalize();
	}

	public static void SplineDirection(Vec3d outDir, double t, Vec3d base, Vec3d next, Vec3d dir1, Vec3d dir2)
	{
		double t2 = t*t;
		double x = 6 * t2 -6 * t;
		double y = -6 * t2 + 6 * t;
		double z = 3 * t2 -4 * t + 1;
		double w = 3 * t2 - 2 * t;

		outDir.SetFrom(
				base.x * x +
				next.x * y +
				dir1.x * z +
				dir2.x * w,
				base.y * x +
				next.y * y +
				dir1.y * z +
				dir2.y * w,
				base.z * x +
				next.z * y +
				dir1.z * z +
				dir2.z * w);
	}

	public static void SplineAngularVel(Vec3d outDir, double t, Vec3d base, Vec3d next, Vec3d dir1, Vec3d dir2)
	{
		double x = 12 * t -6;
		double y = -12 * t + 6;
		double z = 6 * t -4;
		double w = 6 * t - 2;

		outDir.SetFrom(
				base.x * x +
						next.x * y +
						dir1.x * z +
						dir2.x * w,
				base.y * x +
						next.y * y +
						dir1.y * z +
						dir2.y * w,
				base.z * x +
						next.z * y +
						dir1.z * z +
						dir2.z * w);
	}

	public static double CatmullRom1(double t, double prev, double start, double end, double next) {
		double t2 = t * t;
		double t3 = t2 * t;
		double x = -1 * t3 + 2 * t2 - 1 * t;
		double y = 1.5 * t3 - 2.5 * t2 + 1;
		double z = -1.5 * t3 + 2 * t2 + 0.5 * t;
		double w = 0.5 * t3 - 0.5 * t2;
		return prev * x + start * y + end * z + next * w;
	}
	public static void CatmullRom(Vec3d outPos, double t, Vec3d prev, Vec3d start, Vec3d end, Vec3d next)
	{
		double t2 = t*t;
		double t3 = t2*t;

		double x = -1*t3 +2*t2 -1*t;
		double y =  1.5*t3 -2.5*t2 + 1;
		double z = -1.5*t3 +2*t2 +0.5*t;
		double w =  0.5*t3 -0.5*t2;

		outPos.SetFrom(
				prev.x * x +
						start.x * y +
						end.x * z +
						next.x * w,
				prev.y * x +
						start.y * y +
						end.y * z +
						next.y * w,
				prev.z * x +
						start.z * y +
						end.z * z +
						next.z * w);
	}
	public static void CatmullRomDir(Vec3d outPos, double t, Vec3d prev, Vec3d start, Vec3d end, Vec3d next)
	{
		double t2 = t*t;
		double x = -1.5*t2 +2*t -0.5;
		double y =  4.5*t2 -5*t ;
		double z = -4.5*t2 +4*t +0.5;
		double w =  1.5*t2 -1*t;

		outPos.SetFrom(
				prev.x * x +
						start.x * y +
						end.x * z +
						next.x * w,
				prev.y * x +
						start.y * y +
						end.y * z +
						next.y * w,
				prev.z * x +
						start.z * y +
						end.z * z +
						next.z * w);
	}

	public static Vec3d B_Spline(Vec3d outPos, double t, Vec3d prev, Vec3d start, Vec3d end, Vec3d next)
	{
		double t2 = t*t;
		double t3 = t2*t;

		double x = -1/6f*t3 +3/6f*t2 -3/6f*t +1/6f;
		double y =  3/6f*t3 -6/6f*t2  		 +4/6f;
		double z = -3/6f*t3 +3/6f*t2 +3/6f*t +1/6f;
		double w =  1/6f*t3;

		outPos.SetFrom(
				prev.x * x +
						start.x * y +
						end.x * z +
						next.x * w,
				prev.y * x +
						start.y * y +
						end.y * z +
						next.y * w,
				prev.z * x +
						start.z * y +
						end.z * z +
						next.z * w);
		return outPos;
	}
	public static Vec3d B_SplineDir(Vec3d out, double t, Vec3d prev, Vec3d start, Vec3d end, Vec3d next)
	{
		double t2 = t*t;
		double x = -1/2f*t2 +3/3f*t -3/6f;
		double y =  3/2f*t2 -6/3f*t;
		double z = -3/2f*t2 +3/3f*t +3/6f;
		double w =  1/2f*t2;

		out.SetFrom(
				prev.x * x +
					start.x * y +
					end.x * z +
					next.x * w,
				prev.y * x +
					start.y * y +
					end.y * z +
					next.y * w,
				prev.z * x +
					start.z * y +
					end.z * z +
					next.z * w);
		return out;
	}




	public static Vec3d Lerp(float t, Vec3d base, Vec3d next)
	{
		return new Vec3d(
				base.x * (1-t) + next.x * t,
				base.y * (1-t) + next.y * t,
				base.z * (1-t) + next.z * t
				);
	}
	

	public static double AngleBetweenTwoVec(Vec3d a, Vec3d b)
	{
		return java.lang.Math.acos( Clamp(a.New().normalize().dot(b.New().normalize())) );
	}
	
	public static double Clamp(double a)
	{
		return Clamp(a, -1, 1);
	}

	public static double Clamp(double src, double min, double max)
	{
		return src > max ? max : (java.lang.Math.max(src, min));
	}
	public static float Clamp(float src, float min, float max)
	{
		return src > max ? max : (src < min ? min : src);
	}
	public static int Clamp(int src, int min, int max)
	{
		return src > max ? max : (src < min ? min : src);
	}

	public static float wrap(float a)
	{
		if(a >  java.lang.Math.PI)a -= java.lang.Math.PI*2;
		if(a < -java.lang.Math.PI)a += java.lang.Math.PI*2;
		return a;
	}
	
	public static double Lerp(double t, double a, double b)
	{
		return a + (b - a) * t;
	}


	public static Vec3d RotateVecByQuaternion(Vec3d inout, Quaternion q)
	{
		Quaternion in = new Quaternion(inout.x, inout.y, inout.z, 0);
		MulQuaternion(in,
				q.w, q.x, q.y, q.z,
				0, in.x,in.y,in.z
		);
		MulQuaternion(
				in,
				in.w, in.x, in.y, in.z,
				q.w, -q.x, -q.y, -q.z);
		inout.SetFrom(in.x, in.y, in.z);
		return inout;
	}

	public static void rotateAroundVector(Vec3d out, Vec3d rotpos, Vec3d axis, double radian)
	{
		radian *= 0.5;
		Quaternion ans = new Quaternion(rotpos.x, rotpos.y, rotpos.z, 0);
		double rw = java.lang.Math.cos(radian);
		double rx = -axis.x*java.lang.Math.sin(radian);
		double ry = -axis.y*java.lang.Math.sin(radian);
		double rz = -axis.z*java.lang.Math.sin(radian);
		MulQuaternion(ans,
				rw, rx, ry, rz,
				0,rotpos.x,rotpos.y,rotpos.z
		);
		MulQuaternion(
				ans,
				ans.w, ans.x, ans.y, ans.z,
				rw, -rx, -ry, -rz);
		out.SetFrom(ans.x, ans.y, ans.z);
	}

	public static void MulQuaternion(Quaternion out,
									 double q1w, double q1x, double q1y, double q1z,
										double q2w, double q2x, double q2y, double q2z)
	{
		double w = q1w*q2w - q1x*q2x - q1y*q2y - q1z*q2z;
		double x = q1x*q2w + q1w*q2x - q1z*q2y + q1y*q2z;
		double y = q1y*q2w + q1z*q2x + q1w*q2y - q1x*q2z;
		double z = q1z*q2w - q1y*q2x + q1x*q2y + q1w*q2z;
		out.w = w;
		out.x = x;
		out.y = y;
		out.z = z;
	}

	// return w
    public static double MulQuaternion(Vec3d out,
                                     double q1w, double q1x, double q1y, double q1z,
                                     double q2w, double q2x, double q2y, double q2z)
    {
        double w = q1w*q2w - q1x*q2x - q1y*q2y - q1z*q2z;
        double x = q1x*q2w + q1w*q2x - q1z*q2y + q1y*q2z;
        double y = q1y*q2w + q1z*q2x + q1w*q2y - q1x*q2z;
        double z = q1z*q2w - q1y*q2x + q1x*q2y + q1w*q2z;
        out.x = x;
        out.y = y;
        out.z = z;
        return w;
    }
	
	// 球面線形保管
	public static void Slerp(Vec3d out, double t, Vec3d Base, Vec3d Goal)
	{
		double theta = java.lang.Math.acos(Clamp(out.CopyFrom(Base).dot(Goal)));
		if(theta == 0 || theta == 1d)return;
		double perSinTh = 1d / java.lang.Math.sin(theta);
		double Pb = java.lang.Math.sin(theta*(1-t));
		double Pg = java.lang.Math.sin(theta*t);
		out.SetFrom((Base.x * Pb + Goal.x * Pg)*perSinTh,
				(Base.y * Pb + Goal.y * Pg)*perSinTh,
				(Base.z * Pb + Goal.z * Pg)*perSinTh);
	}
	
	public static float fixrot(float rot, float prevrot)
    {
    	if(rot - prevrot>180f)prevrot += 360f;
        else if(rot - prevrot<-180f)prevrot -= 360f;
    	return prevrot;
    }

}
