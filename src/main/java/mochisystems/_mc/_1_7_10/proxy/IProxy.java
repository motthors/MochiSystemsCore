package mochisystems._mc._1_7_10.proxy;

import cpw.mods.fml.relauncher.Side;
import mochisystems.blockcopier.BlocksRenderer;
import mochisystems._mc._1_7_10.world.MTYBlockAccess;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.player.EntityPlayer;

public interface IProxy{

	void PreInit();

	BlocksRenderer GetConstructorBlocksVertex(MTYBlockAccess ba);
	Side checkSide();
	EntityPlayer getClientPlayer();
	void PlayContinuousSound(MovingSound sound);
	void PlaySoundOnce(double x, double y, double z, String domain, float f1, float f2);
}