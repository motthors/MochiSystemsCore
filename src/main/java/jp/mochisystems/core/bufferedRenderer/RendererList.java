package jp.mochisystems.core.bufferedRenderer;

import net.minecraft.client.renderer.*;
import org.lwjgl.opengl.GL11;

public class RendererList implements ICachedBufferRenderer {
    //ref -> net.minecraft.client.renderer.RenderList
    private int displayList = -1;
    private CachedBufferBase base;

    public RendererList(CachedBufferBase base) {
        this.base = base;
        this.displayList = GL11.glGenLists(1);
    }

    public boolean IsBuilt()
    {
        return displayList >= 0;
    }

    public void Build()
    {
        GlStateManager.glNewList(displayList, GL11.GL_COMPILE);

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buffer = tess.getBuffer();
        buffer.begin(base.GetDrawMode(), base.GetVertexFormat());

        base.Compile();
//       base.Draw()の中で tessellator.draw(); を呼ばないように
        Tessellator.getInstance().draw();
        GlStateManager.glEndList();
    }

    public void Render(){
        GlStateManager.callList(displayList);
    }

    public void Delete()
    {
        if (displayList >= 0)
        {
            GLAllocation.deleteDisplayLists(displayList);
            displayList = -1;
        }
    }
}
