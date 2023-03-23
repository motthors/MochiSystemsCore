package jp.mochisystems.core.util;

import jp.mochisystems.core.math.Vec3d;

public class Connector {
	private final Vec3d originalPos = new Vec3d();// コピーされたときのオリジナルの位置
	private final Vec3d fixedPos = new Vec3d(); // 平行移動され、rot1とrot2で回転された基準接続位置
    private final Vec3d currentPos = new Vec3d();// fixedPosが回転された、そのフレームでの最終位置
    private final Vec3d prevPos = new Vec3d();
//    private final Vec3d futurePos = new Vec3d();
	private String name;

	public Connector(String name)
	{
		this.name = name;
	}

	public void Reset(String name, Vec3d original)
    {
        this.name = name;
        originalPos.CopyFrom(original);
    }

	public void Reset()
    {
        currentPos.CopyFrom(fixedPos);
        prevPos.CopyFrom(fixedPos);
//        futurePos.CopyFrom(fixedPos);
    }

	public void SetOrigin(Vec3d pos)
	{
		originalPos.CopyFrom(pos);
		fixedPos.CopyFrom(pos);
        Reset();
	}

	public void copyFrom(Connector cp)
	{
        originalPos.CopyFrom(cp.originalPos);
        fixedPos.CopyFrom(cp.fixedPos);
        currentPos.CopyFrom(cp.currentPos);
        prevPos.CopyFrom(cp.prevPos);
//        futurePos.CopyFrom(cp.futurePos);
	}

	public String GetName()
    {
        return name;
    }

    public Vec3d Current()
    {
        return currentPos;
    }

    public Vec3d Base()
    {
        return fixedPos;
    }

    public Vec3d Prev()
    {
        return prevPos;
    }

    public Vec3d ResetBase()
    {
        fixedPos.CopyFrom(originalPos);
        return fixedPos;
    }

    public void UpdatePrev()
    {
        prevPos.CopyFrom(currentPos);
//        currentPos.CopyFrom(futurePos);
    }

    public static void Fix(Vec3d out, Connector source, float partialTick)
    {
        Vec3d.Lerp(out, partialTick, source.prevPos, source.currentPos);
    }
}
