package mochisystems._mc._1_7_10.proxy;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import mochisystems._mc._1_7_10._core._Core;
import mochisystems.blockcopier.BlocksRenderer;
import mochisystems._mc._1_7_10.world.MTYBlockAccess;
import mochisystems._mc._1_7_10.bufferedrenderer.SmartBufferManager;
import mochisystems._mc._1_7_10.eventhandler.TickEventHandler;
import mochisystems.manager.EntityWearingModelManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy implements IProxy{
	
	public void PreInit()
	{
		_Core.Instance.smartBufferManager = new SmartBufferManager();
        FMLCommonHandler.instance().bus().register(_Core.Instance.smartBufferManager);
		TickEventHandler.AddClientTickPostListener(EntityWearingModelManager::UpdateModel);
		MinecraftForge.EVENT_BUS.register(_Core.ItemStick);
	}


	public BlocksRenderer GetConstructorBlocksVertex(MTYBlockAccess ba)
	{
		return new BlocksRenderer(ba);
	}

	public Side checkSide()
	{
		return Side.CLIENT;
	}
	
	public EntityPlayer getClientPlayer()
	{
		return Minecraft.getMinecraft().thePlayer;
	}

	public void PlayContinuousSound(MovingSound sound)
	{
		Minecraft.getMinecraft().getSoundHandler().playSound(sound);
	}

	public void PlaySoundOnce(MovingSound sound)
	{
		Minecraft.getMinecraft().getSoundHandler().playSound(sound);
	}

	public void PlaySoundOnce(double x, double y, double z, String domain, float f1, float f2)
	{
		Minecraft.getMinecraft().theWorld.playSoundEffect(x, y, z, domain, f1, f2);
	}
}