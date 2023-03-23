package jp.mochisystems.core._mc.proxy;

import jp.mochisystems.core._mc.renderer.BlocksRenderer;
import jp.mochisystems.core._mc.world.MTYBlockAccess;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public interface IProxy{

	void PreInit();
	EntityPlayer GetPlayer(MessageContext ctx);

	BlocksRenderer GetConstructorBlocksVertex(MTYBlockAccess ba);
	Side checkSide();
	EntityPlayer getClientPlayer();
	void PlayContinuousSound(MovingSound sound);
	void PlaySoundOnce(World world, double x, double y, double z, SoundEvent event, float f1, float f2);
}