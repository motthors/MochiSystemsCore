package jp.mochisystems.core._mc.message;

import jp.mochisystems.core._mc._core._Core;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler
{
	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(_Core.MODID);
  
  	public static void init()
  	{
  		int i=0;
		INSTANCE.registerMessage(MessageSendModelData.Handler.class, MessageSendModelData.class, i++, Side.SERVER);
		INSTANCE.registerMessage(MessageChangeLimitLine.Handler.class, MessageChangeLimitLine.class, i++, Side.SERVER);
		INSTANCE.registerMessage(MessageUpdateModelParameter.Handler.class, MessageUpdateModelParameter.class, i++, Side.SERVER);
  		INSTANCE.registerMessage(MessageSyncNbtCtS.Handler.class, MessageSyncNbtCtS.class, i++, Side.SERVER);
		INSTANCE.registerMessage(MessageOpenModelGui.Handler.class, MessageOpenModelGui.class, i++, Side.SERVER);
	}
}
