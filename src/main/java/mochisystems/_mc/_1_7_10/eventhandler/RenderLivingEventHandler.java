package mochisystems._mc._1_7_10.eventhandler;

import mochisystems._mc._1_7_10.entity.EntityRollingSeat;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.Timer;
import net.minecraftforge.client.event.RenderLivingEvent;

public class RenderLivingEventHandler {

	EntityRollingSeat getSeat(EntityLivingBase target)
	{
		if (!target.isRiding()) {
			return null;
		}
		if (!(target.ridingEntity instanceof EntityRollingSeat)) {
			return null;
		}
		return (EntityRollingSeat)target.ridingEntity;
	}
	
	@SideOnly(Side.CLIENT)
  	@SubscribeEvent(priority=EventPriority.LOWEST)
	public void renderPre(RenderLivingEvent.Pre event)
	{
		if (event.isCanceled()) return;
		EntityRollingSeat seat = getSeat(event.entity);
		if (seat == null) return;

		GL11.glPushMatrix();
	    
		Timer timer = (Timer)ReflectionHelper.getPrivateValue(Minecraft.class, Minecraft.getMinecraft(), new String[] { "field_71428_T", "timer" });
	    float partialTicks = timer.renderPartialTicks;
	    
//	    double yaw = seat.renderrotyPrev + (seat.renderroty - seat.renderrotyPrev)*partialTicks;
//	    double pitch = seat.renderrotpPrev + (seat.renderrotp - seat.renderrotpPrev)*partialTicks;
//	    double roll = seat.renderrotrPrev + (seat.renderrotr - seat.renderrotrPrev)*partialTicks;
//
//	    GL11.glRotated(yaw, 0.0F, -1.0F, 0.0F);
//	    GL11.glRotated(pitch, 1.0F, 0.0F, 0.0F);
//	    GL11.glRotated(roll, 0.0F, 0.0F, 1.0F);
//	    GL11.glRotated(yaw, 0.0F, 1.0F, 0.0F);
//	    GL11.glTranslated(x,y,z);

		seat.GetRotation(); // TODO
	}
	  
	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority=EventPriority.HIGHEST)
	public void renderPost(RenderLivingEvent.Post event)
	{
		if (getSeat(event.entity) == null) return;
		GL11.glPopMatrix();
	}
}
