//package mochisystems.message;
//
//import cpw.mods.fml.common.network.simpleimpl.IMessage;
//import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
//import cpw.mods.fml.common.network.simpleimpl.MessageContext;
//import io.netty.buffer.ByteBuf;
//import mochisystems.blockcopier.ILimitLine;
//import net.minecraft.entity.player.EntityPlayer;
//import net.minecraft.tileentity.TileEntity;
//
//public class MessageChangeLimitFrame implements IMessage, IMessageHandler<MessageChangeLimitFrame, IMessage>{
//
//	public static final int Position = 1;
//	public static final int Width = 4;
//	public static final int Height = 3;
//	public static final int Reset = -1;
//
//	public int x, y, z;
//	public int type;
//	public int add;
//
//	public MessageChangeLimitFrame(){}
//
//	public MessageChangeLimitFrame(int x, int y, int z, int type, int add)
//	{
//	    this.x = x;
//	    this.y = y;
//	    this.z = z;
//        this.type = type;
//        this.add = add;
//  	}
//
//	@Override
//	public void toBytes(ByteBuf buf)
//	{
//		buf.writeInt(this.x);
//		buf.writeInt(this.y);
//		buf.writeInt(this.z);
//        buf.writeInt(this.type);
//        buf.writeInt(this.add);
//	}
//
//	@Override
//    public void fromBytes(ByteBuf buf)
//    {
//	    this.x = buf.readInt();
//	    this.y = buf.readInt();
//	    this.z = buf.readInt();
//        this.type = buf.readInt();
//        this.add = buf.readInt();
//
//    }
//
//	@Override
//    public IMessage onMessage(MessageChangeLimitFrame message, MessageContext ctx)
//    {
//		EntityPlayer player = ctx.getServerHandler().playerEntity;
//		TileEntity tile = ctx.getServerHandler().playerEntity.worldObj.getTileEntity(message.x, message.y, message.z);
//		if (tile==null)return null;
//		ILimitLine limitLine = (tile instanceof ILimitLine) ? (ILimitLine) tile : null;
//
//		switch(message.type)
//    	{
//            case Position:
//                ((ILimitLine) tile).setFrameLength(message.add);
//                break;
//            case Width:
//                ((ILimitLine) tile).setFrameWidth(message.add);
//                break;
//            case Height:
//                ((ILimitLine) tile).setFrameHeight(message.add);
//                break;
//			case Reset:
//				((ILimitLine) tile).resetFrameLength();
//    	}
//		player.worldObj.markBlockForUpdate(message.x, message.y, message.z);
//        return null;
//    }
//
//}
