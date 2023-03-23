package jp.mochisystems.core.blockcopier;

import jp.mochisystems.core.math.Vec3d;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3i;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;

import java.util.function.Consumer;

public class LimitFrame {

	private int xMin, xMax;
	private int yMin, yMax;
	private int zMin, zMax;

	private Vec3i limitMin = new Vec3i(0, 0, 0);
	private Vec3i limitMax = new Vec3i(0, 0, 0);

	private Vec3i DefaultMin = new Vec3i(0, 0, 0);
	private Vec3i DefaultMax = new Vec3i(0, 0, 0);

	public int getmx(){ return xMin; }
	public int getxx(){ return xMax; }
	public int getmy(){ return yMin; }
	public int getxy(){ return yMax; }
	public int getmz(){ return zMin; }
	public int getxz(){ return zMax; }
    public int lenX() { return getxx() - getmx() + 1;}
    public int lenY() { return getxy() - getmy() + 1;}
    public int lenZ() { return getxz() - getmz() + 1;}

	private final Vec3d[] posArray = new Vec3d[16];
	private final Vec3d[] vertexArray = new Vec3d[38];
	
	public LimitFrame()
	{
		for(int i=0;i<16;++i) posArray[i] = new Vec3d(0, 0, 0);
		for(int i=0;i<38;++i) vertexArray[i] = new Vec3d(0, 0, 0);
	}

	public void Reset(){
		SetLengths(DefaultMin.getX(), DefaultMin.getY(), DefaultMin.getZ(),
				DefaultMax.getX(), DefaultMax.getY(), DefaultMax.getZ());
		createVertex();
	}

	public void SetLimit(Vec3i min, Vec3i max)
	{
		limitMin = min;
		limitMax = max;
	}

	public void SetReset(Vec3i min, Vec3i max)
	{
		DefaultMin = min;
		DefaultMax = max;
	}

	public void SetX(int min, int max)
	{
		yMin = min;
		yMax = max;
		FixByLimit();
		createVertex();
	}
	public void SetY(int min, int max)
	{
		yMin = min;
		yMax = max;
		FixByLimit();
		createVertex();
	}
	public void SetZ(int min, int max)
	{
		yMin = min;
		yMax = max;
		FixByLimit();
		createVertex();
	}

	public void SetLengths(int minx, int miny, int minz, int maxx, int maxy, int maxz)
	{
		xMin = minx;
		xMax = maxx;
		yMin = miny;
		yMax = maxy;
		zMin = minz;
		zMax = maxz;
		FixByLimit();
		createVertex();
    }

    public void AddLengths(int minx, int miny, int minz, int maxx, int maxy, int maxz)
    {
        int xMin = Math.min(this.xMin + minx, this.xMax);
        int xMax = Math.max(this.xMax + maxx, this.xMin);
        int yMin = Math.min(this.yMin + miny, this.yMax);
        int yMax = Math.max(this.yMax + maxy, this.yMin);
        int zMin = Math.min(this.zMin + minz, this.zMax);
        int zMax = Math.max(this.zMax + maxz, this.zMin);
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
		this.zMin = zMin;
		this.zMax = zMax;
		FixByLimit();
        createVertex();
    }

    private void FixByLimit()
	{
		xMin = Math.min(xMin, limitMin.getX());
		yMin = Math.min(yMin, limitMin.getY());
		zMin = Math.min(zMin, limitMin.getZ());
		xMax = Math.max(xMax, limitMax.getX());
		yMax = Math.max(yMax, limitMax.getY());
		zMax = Math.max(zMax, limitMax.getZ());
	}

	public void AddByDirection(EnumFacing facing, int addMin, int addMax)
	{
		switch(facing){
			case SOUTH: zMin += addMin; zMax += addMax; break;
			case NORTH: zMax -= addMin; zMin -= addMax; break;
			case UP: yMin += addMin; yMax += addMax; break;
			case DOWN: yMin -= addMax; yMax -= addMin; break;
			case EAST: xMin += addMin; xMax += addMax; break;
			case WEST: xMin -= addMax; xMax -= addMin; break;
		}
		FixByLimit();
		createVertex();
	}

	public void SetByDirection(EnumFacing facing, int min, int max)
	{
		switch(facing){
			case SOUTH: zMin = min; zMax = max; break;
			case NORTH: zMin = max; zMax = min; break;
			case UP: yMin = min; yMax = max; break;
			case DOWN: yMin = max; yMax = min; break;
			case EAST: xMin = min; xMax = max; break;
			case WEST: xMin = max; xMax = min; break;
		}
		FixByLimit();
		createVertex();
	}

	public int GetLenByDirection(EnumFacing facing)
	{
		switch(facing){
			case SOUTH:
			case NORTH: return lenZ();
			case UP:
			case DOWN: return lenY();
			case EAST:
			case WEST: return lenX();
		}
		return 0;
	}

	private void createVertex()
	{
		double minx = xMin + 0.1;
		double miny = yMin + 0.1;
		double minz = zMin + 0.1;
		double maxx = xMax - 0.1+1;
		double maxy = yMax - 0.1+1;
		double maxz = zMax - 0.1+1;
		for(int i=0;i<2;++i)
		{
			posArray[  i*8].SetFrom(maxx, maxy, maxz);
			posArray[1+i*8].SetFrom(maxx, maxy, minz);
			posArray[2+i*8].SetFrom(minx, maxy, minz);
			posArray[3+i*8].SetFrom(minx, maxy, maxz);
			posArray[4+i*8].SetFrom(maxx, miny, maxz);
			posArray[5+i*8].SetFrom(maxx, miny, minz);
			posArray[6+i*8].SetFrom(minx, miny, minz);
			posArray[7+i*8].SetFrom(minx, miny, maxz);
			minx -= 0.2;
			miny -= 0.2;
			minz -= 0.2;
			maxx += 0.2;
			maxy += 0.2;
			maxz += 0.2;
		}
		int j=0;
		// �㑤�l�p1��
		vertexArray[j++] = posArray[0];
		vertexArray[j++] = posArray[  8];
		vertexArray[j++] = posArray[1];
		vertexArray[j++] = posArray[1+8];
		vertexArray[j++] = posArray[2];
		vertexArray[j++] = posArray[2+8];
		vertexArray[j++] = posArray[3];
		vertexArray[j++] = posArray[3+8];
		vertexArray[j++] = posArray[0];
		vertexArray[j++] = posArray[  8];
		// �����ǉ�
		vertexArray[j++] = posArray[4];
		vertexArray[j++] = posArray[4+8];
		vertexArray[j++] = posArray[5];
		vertexArray[j++] = posArray[5+8];
		vertexArray[j++] = posArray[1];
		vertexArray[j++] = posArray[1+8];
		// �W�����v
		vertexArray[j++] = posArray[1+8];
		vertexArray[j++] = posArray[2];
		// L��1���
		vertexArray[j++] = posArray[2];
		vertexArray[j++] = posArray[2+8];
		vertexArray[j++] = posArray[6];
		vertexArray[j++] = posArray[6+8];
		vertexArray[j++] = posArray[5];
		vertexArray[j++] = posArray[5+8];
		// �W�����v
		vertexArray[j++] = posArray[5+8];
		vertexArray[j++] = posArray[3];
		// L��1���
		vertexArray[j++] = posArray[3];
		vertexArray[j++] = posArray[3+8];
		vertexArray[j++] = posArray[7];
		vertexArray[j++] = posArray[7+8];
		vertexArray[j++] = posArray[4];
		vertexArray[j++] = posArray[4+8];
		// �W�����v
		vertexArray[j++] = posArray[4+8];
		vertexArray[j++] = posArray[7];
		// ���X�g�P��
		vertexArray[j++] = posArray[7];
		vertexArray[j++] = posArray[7+8];
		vertexArray[j++] = posArray[6];
		vertexArray[j  ] = posArray[6+8];
	}
	
	public void render()
	{
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder buf = tess.getBuffer();
		buf.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_TEX);
		for(Vec3d v : vertexArray)
		{
			buf.pos(v.x, v.y, v.z).tex(0.0d, 1d).endVertex();
		}
		tess.draw();
	}

	public void ReadFromNBT(NBTTagCompound nbt)
	{
		limitMin = ReadVec3iFromNBT("limitmin", nbt);
		limitMax = ReadVec3iFromNBT("limitmax", nbt);
		DefaultMin = ReadVec3iFromNBT("defaultmin", nbt);
		DefaultMax = ReadVec3iFromNBT("defaultmax", nbt);

		SetLengths(
			nbt.getInteger("frameminx"),
			nbt.getInteger("frameminy"),
			nbt.getInteger("frameminz"),
			nbt.getInteger("framemaxx"),
			nbt.getInteger("framemaxy"),
			nbt.getInteger("framemaxz")
		);
	}

	public void WriteToNBT(NBTTagCompound nbt)
	{
		WriteVec3iToNBT("limitmin", limitMin, nbt);
		WriteVec3iToNBT("limitmax", limitMax, nbt);
		WriteVec3iToNBT("defaultmin", DefaultMin, nbt);
		WriteVec3iToNBT("defaultmax", DefaultMax, nbt);

		nbt.setInteger("frameminx", getmx());
		nbt.setInteger("frameminy", getmy());
		nbt.setInteger("frameminz", getmz());
		nbt.setInteger("framemaxx", getxx());
		nbt.setInteger("framemaxy", getxy());
		nbt.setInteger("framemaxz", getxz());
	}

	private Vec3i ReadVec3iFromNBT(String key, NBTTagCompound nbt)
	{
		return new Vec3i(nbt.getInteger(key+"x"),
		nbt.getInteger(key+"y"),
		nbt.getInteger(key+"z"));
	}

	private void WriteVec3iToNBT(String key, Vec3i v, NBTTagCompound nbt)
	{
		nbt.setInteger(key+"x", v.getX());
		nbt.setInteger(key+"y", v.getY());
		nbt.setInteger(key+"z", v.getZ());
	}

	@Override
	public String toString()
	{
		return "["+xMin+"."+yMin+"."+zMin+"].["+xMax+"."+yMax+"."+zMax+"]";
	}
}
