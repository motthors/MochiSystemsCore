package mochisystems._mc._1_7_10.bufferedrenderer;

import mochisystems._mc._1_7_10._core._Core;
import mochisystems.math.Vec3d;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.GL11;

public abstract class IBufferedRenderer {
    private int glBufferID = PreInitBufferId;
    public static final int PreInitBufferId = -1;
    public void SetBufferID(int id){ glBufferID = id; }
    public int GetBufferID(){ return glBufferID; }

//    private boolean invalidate = false;
//    public void SetInvalidate(){ invalidate = true; }
//    public boolean IsInvalidate(){ return invalidate; }

    private boolean isDirty = false;
    public void SetDirty(){isDirty = true;}
    public boolean IsDirty(){ return isDirty; }
    protected void ClearDirty() {isDirty = false;}

    public abstract int GetHash();

    public void Construct(int bufferID)
    {
        SetBufferID(bufferID);

        GL11.glNewList(GetBufferID(), GL11.GL_COMPILE);
        Draw();
        GL11.glEndList();
    }

    protected abstract void Draw();

    protected void RegisterVertex(Vec3d pos, Vec3d normal, float u, float v)
    {
        Tessellator tess = Tessellator.instance;
        tess.setNormal((float)normal.x, (float)normal.y, (float)normal.z);
        tess.addVertexWithUV(pos.x, pos.y, pos.z, u, v);
    }

    public void DeleteBuffer()
    {
        _Core.Instance.smartBufferManager.Delete(this);
    }

    protected void PreRender(){}
    protected void PostRender(){}

    public final void Render()
    {
        if(IsDirty()){
            ClearDirty();
            DeleteBuffer();
            _Core.Instance.smartBufferManager.Construct(this);
        }
        PreRender();
        GL11.glCallList(this.GetBufferID());
        PostRender();
    }
}
