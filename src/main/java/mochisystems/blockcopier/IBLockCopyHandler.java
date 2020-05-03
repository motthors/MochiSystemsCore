package mochisystems.blockcopier;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import java.util.List;

public interface IBLockCopyHandler {

	boolean isExceptBlock(Block b);
	Block getBlock(int x, int y, int z);
	int getBlockMetadata(int x, int y, int z);
	TileEntity getTileEntity(int x, int y, int z);
    List<Entity> getEntities(AxisAlignedBB aabb);

	void OnComplete(NBTTagCompound nbt);
	void OnFailure();

    ItemStack InstantiateModelItem();
	void registerExternalParam(NBTTagCompound nbt);
    void RecieveBlockData(EntityPlayer player, int idx, int total, byte[] arraybyte);
}
