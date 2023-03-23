package jp.mochisystems.core._mc.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

import static jp.mochisystems.core._mc.block.BlockRemoteController.*;

public class tileEntityRemoteController extends TileEntity 
{
	public BlockPos remotePos = new BlockPos(-1, -1, -1);

	public void SetPos(BlockPos pos)
	{
		remotePos = pos;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		readMineFromNBT(nbt);
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger(KeyRemotePosX, remotePos.getX());
		nbt.setInteger(KeyRemotePosY, remotePos.getY());
		nbt.setInteger(KeyRemotePosZ, remotePos.getZ());
		return nbt;
	}

	public void readMineFromNBT(NBTTagCompound nbt)
	{
		remotePos = new BlockPos(
				nbt.getInteger(KeyRemotePosX),
				nbt.getInteger(KeyRemotePosY),
				nbt.getInteger(KeyRemotePosZ));
	}

	@Nullable
	public SPacketUpdateTileEntity getUpdatePacket()
	{
		return new SPacketUpdateTileEntity(this.pos, 9, this.getUpdateTag());
	}

	public NBTTagCompound getUpdateTag()
	{
		return this.writeToNBT(new NBTTagCompound());
	}
	
}