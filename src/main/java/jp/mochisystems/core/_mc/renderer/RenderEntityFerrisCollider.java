package jp.mochisystems.core._mc.renderer;

import jp.mochisystems.core._mc.entity.EntityBlockModelCollider;
import jp.mochisystems.core.math.Vec3d;
import jp.mochisystems.core.util.Connector;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.RenderEntity;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import org.lwjgl.opengl.GL11;

public class RenderEntityFerrisCollider extends RenderEntity{
	public RenderEntityFerrisCollider(RenderManager renderManagerIn) {
		super(renderManagerIn);
	}

	public void doRender(EntityBlockModelCollider collider, double x, double y, double z, float f, float partialtick)
	{
//		GL11.glLineWidth(2.0F);
//		GL11.glDisable(GL11.GL_TEXTURE_2D);
//		GL11.glDepthMask(false);
//		RenderGlobal.drawSelectionBoundingBox(collider.getEntityBoundingBox().offset(x-collider.lastTickPosX, y-collider.lastTickPosY, z-collider.lastTickPosZ), 0, 0, 0, 0.4f);
//		GL11.glDepthMask(true);
//		GL11.glEnable(GL11.GL_TEXTURE_2D);
//		GL11.glDisable(GL11.GL_BLEND);
//
////		renderOffsetAABB(collider.getEntityBoundingBox(),  x-collider.lastTickPosX, y-collider.lastTickPosY, z-collider.lastTickPosZ);
//
//		Entity[] ea = collider.getParts();
//		if(ea != null)for(int i=0; i<ea.length; i++)
//		{
//			Entity e = ea[i];
//			renderOffsetAABB(e.getEntityBoundingBox(),  x - collider.posX, y - collider.posY, z - collider.posZ);
//		}
//
//		if(collider.target!=null)for(Connector c : collider.target.GetBlockAccess().listSeat)
//		{
//			Vec3d p = c.Current();
//			double _x = p.x + collider.target.ModelPosX();
//			double _y = p.y + collider.target.ModelPosY();
//			double _z = p.z + collider.target.ModelPosZ();
//			AxisAlignedBB aabb = new AxisAlignedBB(_x - 0.5, _y - 0.5, _z - 0.5, _x + 0.5, _y + 0.5, _z + 0.5);
//			renderOffsetAABB(aabb,  x - collider.posX, y - collider.posY, z - collider.posZ);
//		}
	}
	
	@Override
	public void doRender(Entity entity, double x, double y, double z, float f, float partialtick) {
		doRender((EntityBlockModelCollider)entity, x, y, z, f, partialtick);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity e) {
		return null;
	}
}
