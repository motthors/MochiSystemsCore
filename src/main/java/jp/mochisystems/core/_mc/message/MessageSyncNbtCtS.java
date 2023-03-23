package jp.mochisystems.core._mc.message;

import io.netty.buffer.ByteBuf;
import jp.mochisystems.core.blockcopier.IBLockCopyHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MessageSyncNbtCtS implements IMessage {
	private int type;
	private int id;
	private int x, y, z;
	private NBTTagCompound nbt;

	public MessageSyncNbtCtS() {
	}

	public MessageSyncNbtCtS(Entity entity, NBTTagCompound nbt)
	{
		type = 1;
		id = entity.getEntityId();
		this.nbt = nbt;
	}

	public MessageSyncNbtCtS(TileEntity tile, NBTTagCompound nbt)
	{
		type = 0;
		BlockPos pos = tile.getPos();
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
		this.nbt = nbt;
	}
	
	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(this.type);
		buf.writeInt(this.id);
		buf.writeInt(this.x);
		buf.writeInt(this.y);
        buf.writeInt(this.z);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			CompressedStreamTools.writeCompressed(nbt, os);
			buf.writeInt(os.size());
			buf.writeBytes(os.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
    public void fromBytes(ByteBuf buf)
    {
		this.type = buf.readInt();
		this.id = buf.readInt();
		this.x = buf.readInt();
	    this.y = buf.readInt();
        this.z = buf.readInt();
	    int len = buf.readInt();
	    if(len <= 0) return;
	    byte[] arrayByte = new byte[len];
	    buf.readBytes(arrayByte);
		try {
			this.nbt = CompressedStreamTools.readCompressed(new ByteArrayInputStream(arrayByte));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    public static class Handler implements IMessageHandler<MessageSyncNbtCtS, IMessage> {

		@Override
		public IMessage onMessage(MessageSyncNbtCtS message, MessageContext ctx) {
			EntityPlayer player = ctx.getServerHandler().player;
			switch(message.type){
				case 0: //TileEntity
					BlockPos pos = new BlockPos(message.x, message.y, message.z);
					TileEntity tile = player.world.getTileEntity(pos);
					if (tile == null) return null;
					NBTTagCompound n = tile.writeToNBT(new NBTTagCompound());
					n.merge(message.nbt);
					tile.readFromNBT(n);
					tile.markDirty();
					IBlockState state = player.world.getBlockState(pos);
					player.world.notifyBlockUpdate(pos, state, state, 3);
					break;
				case 1: //Entity
					Entity entity = player.world.getEntityByID(message.id);
					assert entity != null;
					n = entity.writeToNBT(new NBTTagCompound());
					n.merge(message.nbt);
					entity.readFromNBT(n);
//					Entityに使うようになったなら、ちゃんと送り返すほうがよさそう？
			}

			return null;
		}
	}
}
