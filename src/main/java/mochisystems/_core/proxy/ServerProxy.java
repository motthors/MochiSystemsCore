package mochisystems._core.proxy;

import cpw.mods.fml.relauncher.Side;
import mochisystems._core._Core;
import mochisystems.blockcopier.BlocksRenderer;
import mochisystems.blockcopier.MTYBlockAccess;
import mochisystems.bufferedrenderer.SmartBufferManager;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.player.EntityPlayer;

public class ServerProxy implements IProxy{

	public void PreInit()
	{
		_Core.Instance.smartBufferManager = new SmartBufferManager.Dummy();
//        TickEventHandler.AddServerTickPostListener(EntityWearingModelManager::UpdateModel);
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
	public void PlaySoundOnce(double x, double y, double z, String domain, float f1, float f2){}
}
