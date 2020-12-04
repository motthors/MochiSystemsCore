package mochisystems._mc._1_7_10.message;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import mochisystems._mc._1_7_10._core._Core;

public class PacketHandler
{
	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(_Core.MODID);
  
  	public static void init()
  	{
  		int i=0;
		INSTANCE.registerMessage(MessageSendModelData.class, MessageSendModelData.class, i++, Side.SERVER);
		INSTANCE.registerMessage(MessageChangeLimitLine.class, MessageChangeLimitLine.class, i++, Side.SERVER);
  	}
}
