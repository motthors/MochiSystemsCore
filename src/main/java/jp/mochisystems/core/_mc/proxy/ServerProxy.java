package jp.mochisystems.core._mc.proxy;

import jp.mochisystems.core._mc._core._Core;
import jp.mochisystems.core.bufferedRenderer.SmartBufferManager;
import jp.mochisystems.core._mc.renderer.BlocksRenderer;
import jp.mochisystems.core._mc.world.MTYBlockAccess;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class ServerProxy implements IProxy {

	public void PreInit()
	{
		_Core.Instance.smartBufferManager = new SmartBufferManager.Dummy();
//        TickEventHandler.AddServerTickPostListener(EntityWearingModelManager::UpdateModel);
	}

	public EntityPlayer GetPlayer(MessageContext ctx)
	{
		return ctx.getServerHandler().player;
	}

	public BlocksRenderer GetConstructorBlocksVertex(MTYBlockAccess ba)
	{
		return null;
	}

	public Side checkSide()
	{
		return Side.SERVER;
	}
	
	public EntityPlayer getClientPlayer()
	{
		return null;
	}

	public void PlayContinuousSound(MovingSound sound){}
	public void PlaySoundOnce(World world, double x, double y, double z, SoundEvent event, float f1, float f2){}
}
