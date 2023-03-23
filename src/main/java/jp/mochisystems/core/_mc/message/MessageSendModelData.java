package jp.mochisystems.core._mc.message;

import io.netty.buffer.ByteBuf;
import jp.mochisystems.core.blockcopier.BlockModelSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageSendModelData implements IMessage {

	private int x, y, z;
	private int idx, total;
    private byte[] arrayByte;

	public MessageSendModelData() {
	}

	public MessageSendModelData(int x, int y, int z, int idx, int divTotal, byte[] abyte)
	{
        this.x = x;
        this.y = y;
        this.z = z;
        this.idx = idx;
        this.total = divTotal;
		this.arrayByte = abyte;
	}
	
	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(this.x);
		buf.writeInt(this.y);
        buf.writeInt(this.z);
        buf.writeInt(this.idx);
        buf.writeInt(this.total);
		if(arrayByte!=null)buf.writeInt(arrayByte.length);
		else buf.writeInt(0);
		if(arrayByte!=null)buf.writeBytes(arrayByte);
	}
	
	@Override
    public void fromBytes(ByteBuf buf)
    {
	    this.x = buf.readInt();
	    this.y = buf.readInt();
        this.z = buf.readInt();
        this.idx = buf.readInt();
        this.total = buf.readInt();
	    int arraylen = buf.readInt();
	    if(arraylen<=0)return;
	    arrayByte = new byte[arraylen];
	    buf.readBytes(this.arrayByte);
    }

    public static class Handler implements IMessageHandler<MessageSendModelData, IMessage> {

		@Override
		public IMessage onMessage(MessageSendModelData message, MessageContext ctx) {
			BlockPos pos = new BlockPos(message.x, message.y, message.z);
			TileEntity tile = ctx.getServerHandler().player.world.getTileEntity(pos);
			if (tile == null) return null;
			if (!(tile instanceof BlockModelSender.IHandler)) return null;
			BlockModelSender.IHandler controller = (BlockModelSender.IHandler) tile;

			controller.GetSender().ReceivePartialBlockData(message.idx, message.total, message.arrayByte, ctx.getServerHandler().player);
			return null;
		}
	}
}
