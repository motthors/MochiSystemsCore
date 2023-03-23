package jp.mochisystems.core.bufferedRenderer;

import jp.mochisystems.core._mc._core.Logger;
import jp.mochisystems.core._mc._core._Core;
import jp.mochisystems.core.math.Vec3d;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;

public abstract class CachedBufferBase {

    private ICachedBufferRenderer renderer;
    public void SetRenderer(ICachedBufferRenderer renderer){ this.renderer = renderer; }
    public ICachedBufferRenderer GetRenderer(){return renderer;}

    private boolean isDirty = true;
    public void SetDirty(){isDirty = true;}
    public boolean IsDirty(){ return isDirty; }
    protected void ClearDirty() {isDirty = false;}

    public abstract int GetHash();

    public abstract int GetDrawMode();
    protected abstract VertexFormat GetVertexFormat();
    public abstract void setupArrayPointers();

    protected abstract void Compile();

    protected void RegisterVertex(Vec3d pos, Vec3d normal, float u, float v)
    {
        Tessellator tess = Tessellator.getInstance();
        tess.getBuffer()
                .pos(pos.x, pos.y, pos.z)
                .tex(u, v)
                .normal((float)normal.x, (float)normal.y, (float)normal.z)
                .endVertex();
    }

    public void DeleteBuffer()
    {
        if(renderer != null && renderer.IsBuilt()){
            _Core.Instance.smartBufferManager.Delete(this);
        }
    }

    protected void PreRender(){}
    protected void PostRender(){}

    public final void Render()
    {
        if(IsDirty()){
//            Logger.debugInfo("rebuild");
            ClearDirty();
            DeleteBuffer();
            _Core.Instance.smartBufferManager.Construct(this);
        }
        PreRender();
        renderer.Render();
        PostRender();
    }
}
