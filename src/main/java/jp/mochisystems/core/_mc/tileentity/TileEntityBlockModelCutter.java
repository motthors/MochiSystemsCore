package jp.mochisystems.core._mc.tileentity;

import jp.mochisystems.core.blockcopier.BlockExcluder;
import jp.mochisystems.core.blockcopier.ILimitFrameHolder;
import jp.mochisystems.core.blockcopier.LimitFrame;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;

import javax.annotation.Nullable;

public class TileEntityBlockModelCutter extends TileEntity implements BlockExcluder, ILimitFrameHolder {

	LimitFrame limitFrame = new LimitFrame();
	
	public TileEntityBlockModelCutter() {
		super();
		limitFrame.SetLengths(0, 0, 0, 0, 0, 0);
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() 
	{
		return INFINITE_EXTENT_AABB;
	}



	///////////////// override BlockExcluder
    @Override
    public int getMinX()
    {
        return pos.getX();
    }
    @Override
    public int getMinY()
    {
        return pos.getY();
    }
    @Override
    public int getMinZ()
    {
        return pos.getZ();
    }

    @Override
	public int getMaxX() {
		return pos.getX() + limitFrame.lenX();
	}

	@Override
	public int getMaxY() {
		return pos.getY() + limitFrame.lenY();
	}

	@Override
	public int getMaxZ() {
		return pos.getZ() + limitFrame.lenZ();
	}

	////////////////////////////////////////////


//	public void setFrame(int l, int FLAG)
//	{
//		int value = 0;
//		switch(l)
//		{
//		case 0 : value = -100;break;
//		case 1 : value = -10; break;
//		case 2 : value = -1;  break;
//		case 3 : value = 1;   break;
//		case 4 : value = 10;  break;
//		case 5 : value = 100; break;
//		}
//		FLAG -= MessageFerrisMisc.GUIFerrisCutterX;
//		switch(FLAG)
//		{
//		case MessageFerrisMisc.GUIFerrisCutterX : _setFrameX(value);break;
//		case MessageFerrisMisc.GUIFerrisCutterY : _setFrameY(value);break;
//		case MessageFerrisMisc.GUIFerrisCutterZ : _setFrameZ(value);break;
//		}
//	}

    public void readFromNBT(NBTTagCompound nbt)
    {
    	super.readFromNBT(nbt);
    	loadFromNBT(nbt);
    }
    private void loadFromNBT(NBTTagCompound nbt)
    {
    	limitFrame.ReadFromNBT(nbt);
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
		super.writeToNBT(nbt);
		saveToNBT(nbt);
		return nbt;
	}
    private void saveToNBT(NBTTagCompound nbt)
    {
		limitFrame.WriteToNBT(nbt);
	}

//	@Override
//	public Packet getDescriptionPacket() {
//		NBTTagCompound nbtTagCompound = new NBTTagCompound();
//		this.writeToNBT(nbtTagCompound);
//		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, nbtTagCompound);
//	}
//
//	@Override
//	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
//		this.readFromNBT(pkt.func_148857_g());
//		limitFrame.SetLengths(0, LimitFrameX-1,0, LimitFrameY-1,0, LimitFrameZ-1, false,true);
//	}

	@Nullable
	public SPacketUpdateTileEntity getUpdatePacket()
	{
		return new SPacketUpdateTileEntity(this.pos, 9, this.getUpdateTag());
	}

	public NBTTagCompound getUpdateTag()
	{
		return this.writeToNBT(new NBTTagCompound());
	}

	@Override
	public LimitFrame GetLimitFrame() {
		return limitFrame;
	}
}
