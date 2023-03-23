package jp.mochisystems.core._mc.eventhandler;

import jp.mochisystems.core._mc.message.MessageChangeLimitLine;
import jp.mochisystems.core._mc.message.PacketHandler;
import jp.mochisystems.core.blockcopier.LimitFrame;
import jp.mochisystems.core.manager.ChangingLimitLineManager;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;

public class KeyHandlerClient {
	
	public static final KeyBinding keyEditX2 = new KeyBinding("Change Position-", Keyboard.KEY_NUMPAD2, "mfw : Change Frame Lnegth");
	public static final KeyBinding keyEditX3 = new KeyBinding("Change Position+", Keyboard.KEY_NUMPAD3, "mfw : Change Frame Lnegth");
	public static final KeyBinding keyEditY4 = new KeyBinding("Change height-", Keyboard.KEY_NUMPAD4, "mfw : Change Frame Lnegth");
	public static final KeyBinding keyEditY7 = new KeyBinding("Change height+", Keyboard.KEY_NUMPAD7, "mfw : Change Frame Lnegth");
	public static final KeyBinding keyEditZ5 = new KeyBinding("Change Depth-", Keyboard.KEY_NUMPAD5, "mfw : Change Frame Lnegth");
	public static final KeyBinding keyEditZ9 = new KeyBinding("Change Depth+", Keyboard.KEY_NUMPAD9, "mfw : Change Frame Lnegth");
	public static final KeyBinding keyReset = new KeyBinding("LimitLine Reset", Keyboard.KEY_NUMPAD0, "mfw : Change Frame Lnegth");
	public static final KeyBinding[] keyArray = new KeyBinding[]{keyEditX2,keyEditX3,keyEditY4,keyEditY7,keyEditZ5,keyEditZ9, keyReset};
	
	public static void init() {
		for(KeyBinding k : keyArray)
			ClientRegistry.registerKeyBinding(k);

	}

	@SubscribeEvent
	public void keyDown(InputEvent.KeyInputEvent event)
	{
		int idx = -1;
		for(int i = 0; i< keyArray.length; ++i)
		{
			if(keyArray[i].isPressed())
			{
				idx = i;
				break;
			}
		}
		LimitFrame limitFrame = ChangingLimitLineManager.INSTANCE.getSaveLimitFrame();
		if(limitFrame == null)return;
		int len = 0;
		int sign = idx%2 == 0 ? -1 : 1;
		boolean alt = Keyboard.isKeyDown(Keyboard.KEY_LMENU);
		boolean ctrl = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL);
		if(alt && ctrl) sign *= 100;
		else if(alt) sign *= 10;
		len += sign;
		switch(idx)
		{
			case 0 : case 1 :
				limitFrame.AddLengths(-len, 0, 0, len, 0, 0);
				break;
			case 2 : case 3 :
				limitFrame.AddLengths(0, -len, 0, 0, len, 0);
				break;
			case 4 : case 5 :
				limitFrame.AddLengths(0, 0, -len, 0, 0, len);
				break;
			case 6 :
				limitFrame.Reset();
				break;
			default : return;
		}

		int x = ChangingLimitLineManager.INSTANCE.x;
		int y = ChangingLimitLineManager.INSTANCE.y;
		int z = ChangingLimitLineManager.INSTANCE.z;
		MessageChangeLimitLine m = new MessageChangeLimitLine(x, y, z, limitFrame);
		PacketHandler.INSTANCE.sendToServer(m);
	}
}
