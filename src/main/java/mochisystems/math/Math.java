package mochisystems.math;


import mochisystems._mc._1_7_10._core.Logger;
import net.minecraft.util.MathHelper;

public class Math {

	static double ToRad = 1 / 360d * 2 * java.lang.Math.PI;
	static double ToDeg = 1 * 360d / (2 * java.lang.Math.PI);

	public static int floor(double d)
	{
		return MathHelper.floor_double(d);
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
		double x = 2*t3-3*t2+1;
		double y = -2*t3+3*t2;
		double z = t3-2*t2+t;
		double w = t3-t2;

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

			// w
//			1 * x+
//			1 * y );
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
	
	public static Vec3d Lerp(float t, Vec3d base, Vec3d next)
	{
		return new Vec3d(
				base.x * (1-t) + next.x * t,
				base.y * (1-t) + next.y * t,
				base.z * (1-t) + next.z * t
				);
	}
	
	public static float CalcSmoothRailPower(Vec3d dirBase, Vec3d dirNext, Vec3d vecBase, Vec3d vecNext)
	{
		double dot = dirBase.dot(dirNext);
    	double len = vecBase.distanceTo(vecNext);
    	float f = (float) ((-dot+1d)*3d+len*0.8);
    	//ERC_Logger.info("math helper = "+f+", distance = "+len+", dot = "+dot);
    	return f;
	}
	
//	public static void CalcCoasterRollMatrix(ERC_ReturnCoasterRot out, Vec3d Pos, Vec3d Dir, Vec3d Up)
//	{
//		Vec3d zaxis = Dir.normalize();//Pos.subtract(At).normalize();
//		Vec3d xaxis = zaxis.crossProduct(Up).normalize();
//		Vec3d yaxis = xaxis.crossProduct(zaxis);
//
////		out.rotmat.clear();
//////		out.rotmat.put(xaxis.CorePosX).put(yaxis.CorePosX).put(-zaxis.CorePosX).put(0);
//////		out.rotmat.put(xaxis.CorePosY).put(yaxis.CorePosY).put(-zaxis.CorePosY).put(0);
//////		out.rotmat.put(xaxis.CorePosZ).put(yaxis.CorePosZ).put(-zaxis.CorePosZ).put(0);
//////		out.rotmat.put(0).put(0).put(0).put(1);
////		out.rotmat.put(xaxis.xCoord).put(xaxis.CorePosY).put(xaxis.CorePosZ).put(0);
////		out.rotmat.put(yaxis.xCoord).put(yaxis.CorePosY).put(yaxis.CorePosZ).put(0);
////		out.rotmat.put(zaxis.xCoord).put(zaxis.CorePosY).put(zaxis.CorePosZ).put(0);
////		out.rotmat.put(0).put(0).put(0).put(1);
////		out.rotmat.flip();
//	}
	
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
		return src > max ? max : (src < min ? min : src);
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

    public static Quaternion MakeQuaternionFromDirUp(Quaternion outQ, Vec3d Dir, Vec3d _Up)
	{
	    Vec3d left = _Up.New().cross(Dir);
	    Vec3d Up = Dir.New().cross(left);
		double[] elem = new double[4];
//        elem[0] = m.m00 - m.m11 - m.m22 + 1.0f;
//        elem[1] = -m.m00 + m.m11 - m.m22 + 1.0f;
//        elem[2] = -m.m00 - m.m11 + m.m22 + 1.0f;
//        elem[3] = m.m00 + m.m11 + m.m22 + 1.0f;
        elem[0] = left.x - Up.y - Dir.z + 1.0f;
        elem[1] = -left.x + Up.y - Dir.z + 1.0f;
        elem[2] = -left.x - Up.y + Dir.z + 1.0f;
        elem[3] = left.x + Up.y + Dir.z + 1.0f;

		int biggestIdx = 0;
		for (int i = 0; i < elem.length; i++)
		{
			if (elem[i] > elem[biggestIdx])
			{
				biggestIdx = i;
			}
		}

		if (elem[biggestIdx] < 0)
		{
            Logger.warn("Wrong matrix.");
			return new Quaternion();
		}

		double[] q = new double[4];
		float v = (float)java.lang.Math.sqrt(elem[biggestIdx]) * 0.5f;
		q[biggestIdx] = v;
		float mult = 0.25f / v;

		switch (biggestIdx)
		{
			case 0:
//				q[1] = (m.m10 + m.m01) * mult;
//				q[2] = (m.m02 + m.m20) * mult;
//				q[3] = (m.m21 - m.m12) * mult;
                q[1] = (left.y + Up.x) * mult;
                q[2] = (Dir.x + left.z) * mult;
                q[3] = (Up.z - Dir.y) * mult;
				break;
			case 1:
//				q[0] = (m.m10 + m.m01) * mult;
//				q[2] = (m.m21 + m.m12) * mult;
//				q[3] = (m.m02 - m.m20) * mult;
                q[0] = (left.y + Up.x) * mult;
                q[2] = (Up.z + Dir.y) * mult;
                q[3] = (Dir.x - left.z) * mult;
				break;
			case 2:
//				q[0] = (m.m02 + m.m20) * mult;
//				q[1] = (m.m21 + m.m12) * mult;
//				q[3] = (m.m10 - m.m01) * mult;
                q[0] = (Dir.x + left.z) * mult;
                q[1] = (Up.z + Dir.y) * mult;
                q[3] = (left.y - Up.x) * mult;
				break;
			case 3:
//				q[0] = (m.m21 - m.m12) * mult;
//				q[1] = (m.m02 - m.m20) * mult;
//				q[2] = (m.m10 - m.m01) * mult;
                q[0] = (Up.z - Dir.y) * mult;
                q[1] = (Dir.x - left.z) * mult;
                q[2] = (left.y - Up.x) * mult;
				break;
		}

//		return new Quaternion(q[0], q[1], q[2], q[3]);
		outQ.x = q[0];
		outQ.y = q[1];
		outQ.z = q[2];
		outQ.w = q[3];
		return outQ;
	}
}
