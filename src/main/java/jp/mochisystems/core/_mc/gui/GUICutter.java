package jp.mochisystems.core._mc.gui;

import java.util.HashMap;
import java.util.Map;

import jp.mochisystems.core._mc.gui.container.DefContainer;
import jp.mochisystems.core._mc.message.MessageChangeLimitLine;
import jp.mochisystems.core._mc.message.PacketHandler;
import jp.mochisystems.core._mc.tileentity.TileEntityBlockModelCutter;
import jp.mochisystems.core.blockcopier.LimitFrame;
import jp.mochisystems.core.math.Vec3d;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class GUICutter extends GUIDraggedFreeViewer {
	
//	private static final ResourceLocation TEXTURE = new ResourceLocation(MFW_Core.MODID, "textures/gui/ferriscore.png");
	TileEntityBlockModelCutter tile;
	private int buttonid;
	protected final LimitFrame frame;

	class GUIName{
		String name;
		int x;
		int y;
		Axis axis;
		int baseID;
		GUIName(String str,int x, int y, Axis axis, int base){name=str; this.x=x; this.y=y; this.axis = axis; this.baseID = base;}
	}
    Map<Integer, GUIName> GUINameMap = new HashMap<Integer, GUIName>();

	enum Axis{X, Y, Z}
    
    public GUICutter(int x, int y, int z, InventoryPlayer invPlayer, TileEntityBlockModelCutter tile)
    {
        super(new DefContainer());
        this.tile = tile;
		this.frame = tile.GetLimitFrame();
	}

	@Override
	protected void UpdateCameraPosFromCore(Vec3d dest, float tick) {
		float x = (frame.getxx()+frame.getmx())/2f;
		float y = (frame.getxy()+frame.getmy())/2f;
		float z = (frame.getxz()+frame.getmz())/2f;
		dest.SetFrom(x+0.5, y+0.5, z+0.5);
		dest.add(tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ());

	}

	@Override
	public void initGui()
    {
		super.initGui();
		buttonid = 0;
//		this.guiTop = 0;//this.height*7/8 - ySize/2;
		// �{�^���o�^
//		offsetx = this.guiLeft + 20;
//		offsety = this.guiTop;
//		int offset = 4;
//        int length = 27;

		addButton6(width/5, height*9/10, "", Axis.X);
		addButton6(width/2, height*9/10, "", Axis.Y);
		addButton6(width*4/5, height*9/10, "", Axis.Z);
    }
	
    @SuppressWarnings("unchecked")
    public void addButton6(int posx, int posy, String str, Axis axis)
    {
    	this.buttonList.add(new GuiButtonExt(buttonid++, posx-41, posy, 14, 10, ""));
    	this.buttonList.add(new GuiButtonExt(buttonid++, posx-26, posy, 12, 10, ""));
    	this.buttonList.add(new GuiButtonExt(buttonid++, posx-13, posy, 10, 10, ""));
		this.buttonList.add(new GuiButtonExt(buttonid++, posx   , posy, 10, 10, ""));
		this.buttonList.add(new GuiButtonExt(buttonid++, posx+11, posy, 12, 10, ""));
		this.buttonList.add(new GuiButtonExt(buttonid++, posx+24, posy, 14, 10, ""));
		GUIName data = new GUIName(str,posx-53,posy+2, axis,buttonid-6);
		GUINameMap.put(buttonid-6, data);
    	GUINameMap.put(buttonid-5, data);
		GUINameMap.put(buttonid-4, data);
    	GUINameMap.put(buttonid-3, data);
    	GUINameMap.put(buttonid-2, data);
    	GUINameMap.put(buttonid-1, data);
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseZ)
    {
    	super.drawGuiContainerForegroundLayer(mouseX, mouseZ);
//    	this.fontRendererObj.drawString("drawScreen",width/5,0,0xffffff);
    	drawString(this.fontRenderer, "Position X",         xSize/2-25-width*3/10, ySize/2+height*3/10, 0xffffff);
    	drawString(this.fontRenderer, "Position Y(Height)", xSize/2-40,            ySize/2+height*3/10, 0xffffff);
    	drawString(this.fontRenderer, "Lenght Z",         xSize/2-25+width*3/10, ySize/2+height*3/10, 0xffffff);
    	drawString(this.fontRenderer, ""+tile.getMaxX(), xSize/2-4-width*3/10, ySize/2+height*5/14, 0xffffff);
    	drawString(this.fontRenderer, ""+tile.getMaxY(), xSize/2-4,            ySize/2+height*5/14, 0xffffff);
    	drawString(this.fontRenderer, ""+tile.getMaxZ(), xSize/2-4+width*3/10, ySize/2+height*5/14, 0xffffff);
    }
    
    @Override
    public void drawWorldBackground(int p_146270_1_)
    {
    	this.drawGradientRect(0, this.height*7/10, this.width, this.height, -1072689136, -804253680);
    }
	
    public boolean doesGuiPauseGame()
    {
    	return false;
    }
    
	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) 
	{
	}
//	/*GUI�̕������̕`�揈��*/
//    @Override
//    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseZ)
//    {
//        super.drawGuiContainerForegroundLayer(mouseX, mouseZ);   
//        
//        this.fontRendererObj.drawString(tile.,10,147,0x0000A);
//        for(GUIName g :  GUINameMap.values())
//        {
//        	this.fontRendererObj.drawString(g.name,g.CorePosX,g.y,0x404040);
//        }
//        
//        drawString(this.fontRendererObj, "Regist :"+String.format("% 4.2f",tile.rotAccel), 270, 5, 0xffffAFF);
//        drawString(this.fontRendererObj, "Speed :"+String.format("% 4.2f",tile.rotSpeed), 270, 15, 0xffffff);
//        drawString(this.fontRendererObj, "Resist :"+String.format("% 4.1f",tile.rotResist*100f), 270, 32, 0xffffff);
//        drawString(this.fontRendererObj, "Size :"+String.format("% 4.2f",tile.wheelSize), 270, 48, 0xffffff);
//        
//        drawString(this.fontRendererObj, "rot1 :"+String.format("% 4.2f",tile.pitch), 270, 68, 0xffffff);
//        drawString(this.fontRendererObj, "rot2 :"+String.format("% 4.2f",tile.yaw), 270, 80, 0xffffff);
//        this.fontRendererObj.drawString(""+(container.getPageNum()),70,5,0x404040);
////        drawString(this.fontRendererObj, String.format("% 2.1f",ERC_BlockRailManager.clickedTileForGUI.BaseRail.Power), 37, 56, 0xffffff);
////        drawString(this.fontRendererObj, ERC_BlockRailManager.clickedTileForGUI.SpecialGUIDrawString(), 42, 199, 0xffffff);
//    }
	@Override
	protected void actionPerformed(GuiButton button)
	{
		GUIName obj = GUINameMap.get(button.id);
		int data = (button.id - obj.baseID);
		Axis axis = this.GUINameMap.get(button.id).axis;
		LimitFrame frame = tile.GetLimitFrame();
		switch(axis){
			case X: frame.AddLengths(-1, 0, 0, 1, 0, 0); break;
			case Y: frame.AddLengths(0, -1, 0, 0, 1, 0); break;
			case Z: frame.AddLengths(0, 0, -1, 0, 0, 1); break;
		}
		BlockPos pos = tile.getPos();
		MessageChangeLimitLine packet = new MessageChangeLimitLine(pos.getX(), pos.getY(), pos.getZ(), frame);
	    PacketHandler.INSTANCE.sendToServer(packet);
	}


}
