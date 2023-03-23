package jp.mochisystems.core._mc.message;

import io.netty.buffer.ByteBuf;
import jp.mochisystems.core.blockcopier.ILimitFrameHolder;
import jp.mochisystems.core.blockcopier.LimitFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageChangeLimitLine implements IMessage {

	public static final int Reset = -1;

	private int x, y, z;
	private int mx, my, mz, xx, xy, xz;

    public MessageChangeLimitLine() {
    }
    public MessageChangeLimitLine(BlockPos pos, LimitFrame frame)
    {
        this(pos.getX(), pos.getY(), pos.getZ(), frame);
    }
    public MessageChangeLimitLine(int x, int y, int z, LimitFrame frame)
	{
        this.x = x;
        this.y = y;
        this.z = z;
        mx = frame.getmx();
        my = frame.getmy();
        mz = frame.getmz();
        xx = frame.getxx();
        xy = frame.getxy();
        xz = frame.getxz();
	}
	
	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(this.x);
		buf.writeInt(this.y);
        buf.writeInt(this.z);
        buf.writeInt(this.mx);
        buf.writeInt(this.my);
        buf.writeInt(this.mz);
        buf.writeInt(this.xx);
        buf.writeInt(this.xy);
        buf.writeInt(this.xz);
	}
	
	@Override
    public void fromBytes(ByteBuf buf)
    {
	    this.x = buf.readInt();
	    this.y = buf.readInt();
        this.z = buf.readInt();
        this.mx = buf.readInt();
        this.my = buf.readInt();
        this.mz = buf.readInt();
        this.xx = buf.readInt();
        this.xy = buf.readInt();
        this.xz = buf.readInt();
    }

    public static class Handler implements IMessageHandler<MessageChangeLimitLine, IMessage> {

	    @Override
        public IMessage onMessage(MessageChangeLimitLine message, MessageContext ctx) {
            BlockPos pos = new BlockPos(message.x, message.y, message.z);
            EntityPlayer player = ctx.getServerHandler().player;
            TileEntity tile = ctx.getServerHandler().player.world.getTileEntity(pos);
            if (tile == null) return null;
            ILimitFrameHolder controller = (tile instanceof ILimitFrameHolder) ? (ILimitFrameHolder) tile : null;
            assert controller != null;
            LimitFrame frame = controller.GetLimitFrame();

            frame.SetLengths(message.mx, message.my, message.mz, message.xx, message.xy, message.xz);
            player.world.markBlockRangeForRenderUpdate(message.x, message.y, message.z, message.x, message.y, message.z);
            return null;
        }
    }
}
