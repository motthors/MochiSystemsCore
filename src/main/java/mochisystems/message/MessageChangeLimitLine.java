package mochisystems.message;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import mochisystems.blockcopier.ILimitLine;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

public class MessageChangeLimitLine implements IMessage, IMessageHandler<MessageChangeLimitLine, IMessage>{

	public static final int Length = 51;
	public static final int Width = 52;
	public static final int Height = 53;
	public static final int Reset = -1;

	private int x, y, z;
	private int type, value;

	public MessageChangeLimitLine(){}

	public MessageChangeLimitLine(int x, int y, int z, int DirType, int value)
	{
        this.x = x;
        this.y = y;
        this.z = z;
        this.type = DirType;
        this.value = value;
	}
	
	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(this.x);
		buf.writeInt(this.y);
        buf.writeInt(this.z);
        buf.writeInt(this.type);
        buf.writeInt(this.value);
	}
	
	@Override
    public void fromBytes(ByteBuf buf)
    {
	    this.x = buf.readInt();
	    this.y = buf.readInt();
        this.z = buf.readInt();
        this.type = buf.readInt();
        this.value = buf.readInt();
    }
	
	@Override
    public IMessage onMessage(MessageChangeLimitLine message, MessageContext ctx)
    {
		EntityPlayer player = ctx.getServerHandler().playerEntity;
		TileEntity tile = ctx.getServerHandler().playerEntity.worldObj.getTileEntity(message.x, message.y, message.z);
		if (tile==null)return null;
        ILimitLine controller = (tile instanceof ILimitLine) ? (ILimitLine) tile : null;
        if(controller == null) return null;

        switch(message.type)
        {
            case Length:
                controller.setFrameLength(message.value);
                break;
            case Width:
                controller.setFrameWidth(message.value);
                break;
            case Height:
                controller.setFrameHeight(message.value);
                break;
            case Reset:
                controller.resetFrameLength();
                break;
        }
		player.worldObj.markBlockForUpdate(message.x, message.y, message.z);
        return null;
    }
    
}
