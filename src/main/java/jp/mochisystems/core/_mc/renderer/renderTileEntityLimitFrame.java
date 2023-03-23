package jp.mochisystems.core._mc.renderer;

import jp.mochisystems.core._mc._core._Core;
import jp.mochisystems.core._mc.tileentity.TileEntityBlocksScannerBase;
import jp.mochisystems.core.blockcopier.ILimitFrameHolder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class renderTileEntityLimitFrame extends TileEntitySpecialRenderer<TileEntity> {
	
	private static final ResourceLocation TEXTURE = new ResourceLocation(_Core.MODID,"textures/limitline.png");
	
	@Override
	public void render(TileEntity t, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
	{
		this.bindTexture(TEXTURE);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);

		GlStateManager.color(1f, 1f, 1f);
		((ILimitFrameHolder)t).GetLimitFrame().render();

		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glPopMatrix();
	}
}
