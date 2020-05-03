package mochisystems.blockcopier;

import mochisystems.math.Vec3d;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class LimitFrame {

	private int xMin, xMax;
	private int yMin, yMax;
	private int zMin, zMax;

	public int getmx(){ return xMin; }
	public int getxx(){ return xMax; }
	public int getmy(){ return yMin; }
	public int getxy(){ return yMax; }
	public int getmz(){ return zMin; }
	public int getxz(){ return zMax; }
    public int lenX() { return getxx() - getmx();}
    public int lenY() { return getxy() - getmy();}
    public int lenZ() { return getxz() - getmz();}

	private Vec3d posArray[] = new Vec3d[16];
	private Vec3d vertexArray[] = new Vec3d[38];
	
	public LimitFrame()
	{
		for(int i=0;i<16;++i) posArray[i] = new Vec3d(0, 0, 0);
		for(int i=0;i<38;++i) vertexArray[i] = new Vec3d(0, 0, 0);
	}

	public void SetLengths(int minx, int maxx, int miny, int maxy, int minz, int maxz, boolean isOdd, boolean isCreateVertex)
	{
		xMin = minx;
		xMax = maxx;
		yMin = miny;
		yMax = maxy;
		zMin = minz;
		zMax = maxz;
		if(isOdd) {
			xMax -= 1; yMax -= 1; zMax -= 1;
		}
        if(isCreateVertex) createVertex();
    }

    public void AddLengths(double minx, double maxx, double miny, double maxy, double minz, double maxz, boolean isCreateVertex)
    {
        xMin += minx;
        xMax += maxx;
        yMin += miny;
        yMax += maxy;
        zMin += minz;
        zMax += maxz;
        if(isCreateVertex) createVertex();
    }

	private void createVertex()
	{
		double minx = xMin + 0.1;
		double miny = yMin + 0.1;
		double minz = zMin + 0.1;
		double maxx = xMax - 0.1 + 1;
		double maxy = yMax - 0.1 + 1;
		double maxz = zMax - 0.1 + 1;
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
	
	public void render(Tessellator tess)
	{
		tess.startDrawing(GL11.GL_TRIANGLE_STRIP);
		tess.setNormal(0, 1, 0);
		for(Vec3d v : vertexArray)
		{
			tess.addVertexWithUV(v.x, v.y, v.z, 0.0d, 1d);
		}
		tess.draw();
	}
}
