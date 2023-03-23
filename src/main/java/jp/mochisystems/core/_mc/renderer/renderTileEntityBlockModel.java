package jp.mochisystems.core._mc.renderer;

import jp.mochisystems.core.util.IModelController;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;

public class renderTileEntityBlockModel extends TileEntitySpecialRenderer {

    public void render(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);

        ((IModelController)te).GetModel().RenderModel(0, partialTicks);
//        renderPass1Hook.add(t);

        GL11.glPopMatrix();
    }
}
