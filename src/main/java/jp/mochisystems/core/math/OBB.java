//package jp.mochisystems.core.math;
//
//
//import net.minecraft.util.math.AxisAlignedBB;
//
//public class OBB extends AxisAlignedBB {
//
//    private final Vec3d centerPos;
//    private final Vec3d forward;
//    private final Vec3d up;
//    private final Vec3d left;
//    private final Vec3d length;
//
//    public OBB() {
//        super(0, 0, 0, 0, 0, 0);
//        centerPos = new Vec3d();
//        forward = new Vec3d();
//        up = new Vec3d();
//        left = new Vec3d();
//        length = new Vec3d();
//    }
//
//    public AxisAlignedBB setBounds(double mx, double my, double mz, double xx, double xy, double xz)
//    {
//        super.setBounds(mx, my, mz, xx, xy, xz);
//        length.x = xx - mx;
//        length.y = xy - my;
//        length.z = xz - mz;
//        length.mul(0.5);
//        return this;
//    }
//
//    public AxisAlignedBB addCoord(double x, double y, double z)
//    {
//        super.addCoord(x, y, z);
//        length.x += x;
//        length.y += y;
//        length.z += z;
//        return this;
//    }
//
//    public AxisAlignedBB expand(double x, double y, double z)
//    {
//        super.expand(x, y, z);
//        length.x += x;
//        length.y += y;
//        length.z += z;
//        return this;
//    }
//
//    public void SetPosition(Vec3d pos)
//    {
//        centerPos.CopyFrom(pos);
//    }
//
//    public void SetRotation(Quaternion q)
//    {
////        q.makeDirection();
//    }
//
//    public boolean intersectsWith(AxisAlignedBB that)
//    {
//        boolean r = super.intersectsWith(that);
//        if(r)Logger.debugInfo(""+r);
//        return r;
//    }
//
//    private final Vec3d offset = new Vec3d();
//    private final Vec3d center = new Vec3d();
//    private final Vec3d dir = new Vec3d();
//    public double calculateYOffset(AxisAlignedBB that, double posY)
//    {
//        center.SetFrom((that.minX+that.maxX)*0.5, that.minY, (that.minZ+that.maxZ) * 0.5);
//        dir.SetFrom(0, (that.maxY - that.minY)*0.5, 0).add(center);
//        calcOffset(offset, center, dir);
//        return posY - offset.y;
//    }
//
//    public double calculateXOffset(AxisAlignedBB that, double posX)
//    {
//        return posX - offset.x;
//    }
//
//    public double calculateZOffset(AxisAlignedBB that, double posZ)
//    {
//        return posZ - offset.z;
//    }
//
//    private Vec3d dOrg = new Vec3d();
//    private Vec3d dhDir = new Vec3d();
//    private Vec3d calcOffset(Vec3d out, Vec3d org, Vec3d up)
//    {
//        final float EPSILON = 1.175494e-37f;
//        dOrg.CopyFrom(org).add(up).mul(0.5f);
//        dhDir.CopyFrom(up).sub(dOrg);
//        dOrg.sub(centerPos);
//        dOrg.SetFrom(left.dot(dOrg), this.up.dot(dOrg), forward.dot(dOrg));
//        dhDir.SetFrom(left.dot(dhDir), this.up.dot(dhDir), forward.dot(dhDir));
//
//        float adx = java.lang.Math.abs((float) dhDir.x);
//        if(java.lang.Math.abs(dOrg.x) > length.x + adx) return Vec3d.Zero;
//        float ady = java.lang.Math.abs((float) dhDir.y);
//        if(java.lang.Math.abs(dOrg.y) > length.y + ady) return Vec3d.Zero;
//        float adz = java.lang.Math.abs((float) dhDir.z);
//        if(java.lang.Math.abs(dOrg.z) > length.z + adz) return Vec3d.Zero;
//        adx += EPSILON;
//        ady += EPSILON;
//        adz += EPSILON;
//
////        if(java.lang.Math.abs(dOrg.y * dhDir.z - dOrg.z * dhDir.y) > length.y * adz + length.z * ady ) return Vec3d.Zero;
////        if(java.lang.Math.abs(dOrg.z * dhDir.x - dOrg.x * dhDir.z) > length.x * adz + length.z * adx ) return Vec3d.Zero;
////        if(java.lang.Math.abs(dOrg.x * dhDir.y - dOrg.y * dhDir.x) > length.x * ady + length.y * adx ) return Vec3d.Zero;
//        out.x = (java.lang.Math.abs(dOrg.y * dhDir.z - dOrg.z * dhDir.y) - (length.y * adz + length.z * ady ));
//        out.y = (java.lang.Math.abs(dOrg.z * dhDir.x - dOrg.x * dhDir.z) - (length.x * adz + length.z * adx ));
//        out.z = (java.lang.Math.abs(dOrg.x * dhDir.y - dOrg.y * dhDir.x) - (length.x * ady + length.y * adx ));
//        Logger.debugInfo("x : " + out.x);
//        Logger.debugInfo("y : " + out.y);
//        Logger.debugInfo("z : " + out.z);
//        return out;
//    }
//}
