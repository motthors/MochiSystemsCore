package jp.mochisystems.core._mc.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
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

public class MessageUpdateModelParameter implements IMessage {

	public static final int Reset = -1;

	private int x, y, z;
    NBTTagCompound nbt;

    public MessageUpdateModelParameter() { }

    public MessageUpdateModelParameter(BlockPos pos, NBTTagCompound nbt)
	{
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
	    this.nbt = nbt;
    }
	
	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(this.x);
		buf.writeInt(this.y);
        buf.writeInt(this.z);
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            CompressedStreamTools.writeCompressed(nbt, os);
            byte[] bytes = os.toByteArray();
            buf.writeInt(bytes.length);
            buf.writeBytes(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	@Override
    public void fromBytes(ByteBuf buf)
    {
	    this.x = buf.readInt();
	    this.y = buf.readInt();
        this.z = buf.readInt();
        byte[] bytes = new byte[buf.readInt()];
        if(bytes.length > 0) buf.readBytes(bytes);
        ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
        this.nbt = null;
        try {
            this.nbt = CompressedStreamTools.readCompressed(stream);
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static class Handler implements IMessageHandler<MessageUpdateModelParameter, IMessage> {

	    @Override
        public IMessage onMessage(MessageUpdateModelParameter message, MessageContext ctx) {
            BlockPos pos = new BlockPos(message.x, message.y, message.z);
            EntityPlayer player = ctx.getServerHandler().player;
            TileEntity tile = ctx.getServerHandler().player.world.getTileEntity(pos);
            if (tile == null) return null;

            NBTTagCompound modelNbt = ((IInventory)tile).getStackInSlot(0).getTagCompound();
            modelNbt.merge(message.nbt);

            player.world.markBlockRangeForRenderUpdate(message.x, message.y, message.z, message.x, message.y, message.z);
            return null;
        }
    }
}
