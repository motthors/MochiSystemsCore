package jp.mochisystems.core.blockcopier;

import jp.mochisystems.core._mc.message.MessageSendModelData;
import jp.mochisystems.core.math.Vec3d;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public interface IBLockCopyHandler extends BlockModelSender.IHandler {

	// for scan
	BlockPos GetHandlerPos();
	EnumFacing GetSide();
	IBlockState GetBlockState(BlockPos pos);
	TileEntity getTileEntity(BlockPos pos);
    List<Entity> getEntities(float mx, float my, float mz, float xx, float xy, float xz);
	void registerExternalParam(NBTTagCompound model, NBTTagCompound nbt);

	//for message handler
	BlocksScanner GetScanner();
	BlockModelSender GetSender();
	default void OnCompleteScan()
	{
		GetSender().SendNbtToServer(GetScanner().GetTag(), GetHandlerPos());
	}
}
