package jp.mochisystems.core._mc.eventhandler;

import jp.mochisystems.core.math.Quaternion;
import jp.mochisystems.core.math.Vec3d;
import jp.mochisystems.core.util.IRollSeat;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

public class RenderLivingEventHandler {

	private boolean check;
	private final Quaternion q = new Quaternion();
	private final Quaternion.MatBuffer buf = new Quaternion.MatBuffer();

	@SideOnly(Side.CLIENT)
  	@SubscribeEvent(priority= EventPriority.LOWEST)
	public void renderPre(RenderLivingEvent.Pre event)
	{
		check = false;
		if (event.isCanceled()) return;
		Entity entity = event.getEntity().getRidingEntity();
		if(!(entity instanceof IRollSeat)) return;

		check = true;
		IRollSeat seat = (IRollSeat) entity;
		GL11.glPushMatrix();
	    float partialTicks = event.getPartialRenderTick();

		GL11.glTranslated(event.getX(), event.getY(), event.getZ());

		seat.Attitude(q, partialTicks);
		GL11.glMultMatrix(buf.Fix(q));

		GL11.glMultMatrix(buf.Fix(seat.GetSeatRotation(event.getEntity())));
		GL11.glTranslated(-event.getX(), -event.getY(), -event.getZ());
	}
	  
	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority=EventPriority.HIGHEST)
	public void renderPost(RenderLivingEvent.Post event)
	{
		if (check) GL11.glPopMatrix();
	}
}
