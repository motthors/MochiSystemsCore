package jp.mochisystems.core._mc.proxy;

import jp.mochisystems.core._mc._core._Core;
import jp.mochisystems.core.bufferedRenderer.SmartBufferManager;
import jp.mochisystems.core._mc.eventhandler.RenderLivingEventHandler;
import jp.mochisystems.core._mc.eventhandler.TickEventHandler;
import jp.mochisystems.core._mc.renderer.BlocksRenderer;
import jp.mochisystems.core._mc.world.MTYBlockAccess;
import jp.mochisystems.core.manager.EntityWearingModelManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class ClientProxy implements IProxy {
	
	public void PreInit()
	{
		_Core.Instance.smartBufferManager = new SmartBufferManager();
        FMLCommonHandler.instance().bus().register(_Core.Instance.smartBufferManager);
		TickEventHandler.AddClientTickPostListener(EntityWearingModelManager::UpdateModel);
		MinecraftForge.EVENT_BUS.register(_Core.ItemStick);
		MinecraftForge.EVENT_BUS.register(_Core.ItemRemoteController);
		MinecraftForge.EVENT_BUS.register(new RenderLivingEventHandler());
		MinecraftForge.EVENT_BUS.register(new EntityWearingModelManager());
	}

	public EntityPlayer GetPlayer(MessageContext ctx)
	{
		return Minecraft.getMinecraft().player;
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
		return Minecraft.getMinecraft().player;
	}

	public void PlayContinuousSound(MovingSound sound)
	{
		Minecraft.getMinecraft().getSoundHandler().playSound(sound);
	}

	public void PlaySoundOnce(World world, double x, double y, double z, SoundEvent event, float f1, float f2)
	{
		world.playSound(null, x, y, z, event, SoundCategory.BLOCKS, 1.0F, 0.9F);
//		Minecraft.getMinecraft().getSoundHandler().playSoun
	}
}