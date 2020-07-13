package mochisystems.message;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import mochisystems.blockcopier.IBLockCopyHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

public class MessageSendModelData implements IMessage, IMessageHandler<MessageSendModelData, IMessage>{


	private int x, y, z;
	private int idx, total;
    private byte[] arrayByte;

	public MessageSendModelData(){}

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
	    if(arrayByte.length>0)buf.readBytes(this.arrayByte);
    }
	
	@Override
    public IMessage onMessage(MessageSendModelData message, MessageContext ctx)
    {
		EntityPlayer player = ctx.getServerHandler().playerEntity;
		TileEntity tile = ctx.getServerHandler().playerEntity.worldObj.getTileEntity(message.x, message.y, message.z);
		if (tile==null)return null;
		if(!(tile instanceof IBLockCopyHandler)) return null;
		IBLockCopyHandler controller = (IBLockCopyHandler) tile;


        controller.RecieveBlockData(player, message.idx, message.total, message.arrayByte);
		player.worldObj.markBlockForUpdate(message.x, message.y, message.z);
        return null;
    }
    
}
