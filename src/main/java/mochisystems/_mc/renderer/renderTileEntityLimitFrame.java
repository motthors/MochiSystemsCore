package mochisystems._mc.renderer;

import mochisystems._core._Core;
import mochisystems.blockcopier.ILimitLine;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

@SideOnly(Side.CLIENT)
public class renderTileEntityLimitFrame extends TileEntitySpecialRenderer{
	
	private static final ResourceLocation TEXTURE = new ResourceLocation(_Core.MODID,"textures/limitline.png");
	
	@Override
	public void renderTileEntityAt(TileEntity t, double x, double y, double z, float f)
	{
		this.bindTexture(TEXTURE);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);

		Tessellator tessellator = Tessellator.instance;
		tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
		((ILimitLine)t).render(tessellator);

		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glPopMatrix();
	}
}
