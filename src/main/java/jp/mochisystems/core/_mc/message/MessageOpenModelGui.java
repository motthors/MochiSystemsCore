package jp.mochisystems.core._mc.message;

import io.netty.buffer.ByteBuf;
import jp.mochisystems.core._mc._core._Core;
import jp.mochisystems.core._mc.gui.GUIHandler;
import jp.mochisystems.core.util.CommonAddress;
import jp.mochisystems.core.util.IModel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageOpenModelGui implements IMessage {

	protected CommonAddress address = new CommonAddress();

	@SuppressWarnings("unused")
	public MessageOpenModelGui(){}

	public MessageOpenModelGui(CommonAddress address) {
		this.address.CopyFrom(address);
	}


	
	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(address.x);
		buf.writeInt(address.y);
		buf.writeInt(address.z);
		buf.writeInt(address.TreeListIndex);
		buf.writeInt(address.entityId);
	}
	
	@Override
    public void fromBytes(ByteBuf buf)
    {
		address.x = buf.readInt();
		address.y = buf.readInt();
		address.z = buf.readInt();
		address.TreeListIndex = buf.readInt();
		address.entityId = buf.readInt();
    }

    public static class Handler implements IMessageHandler<MessageOpenModelGui, IMessage> {
		@Override
		public IMessage onMessage(MessageOpenModelGui m, MessageContext ctx) {
			EntityPlayer player = ctx.getServerHandler().player;

			if(m.address.entityId > -1 && m.address.y > -1) {
				player.openGui(_Core.Instance, _Core.GUIID_OpenWearingModel, player.world, m.address.y, m.address.TreeListIndex, m.address.z);
				return null;
			}
			IModel model = m.address.GetInstance(player.world);
			if (model == null) return null;

			GUIHandler.OpenBlockModelInServer(player, m.address);
			return null;
		}
	}
}
