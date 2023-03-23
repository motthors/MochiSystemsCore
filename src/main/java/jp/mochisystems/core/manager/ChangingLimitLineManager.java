package jp.mochisystems.core.manager;


import jp.mochisystems.core.blockcopier.ILimitFrameHolder;
import jp.mochisystems.core.blockcopier.LimitFrame;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ChangingLimitLineManager {
	
	private ChangingLimitLineManager(){}
	
	public static ChangingLimitLineManager INSTANCE = new ChangingLimitLineManager();

	LimitFrame savedTile;
	public int x, y, z;

	public void saveTileIfHoldLimitFrame(TileEntity tile, int x, int y, int z)
	{
		if(!(tile instanceof ILimitFrameHolder)) {
			reset();
			return;
		}
		savedTile = ((ILimitFrameHolder)tile).GetLimitFrame();
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public void saveTile(LimitFrame tile, int x, int y, int z)
	{
		savedTile = tile;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public LimitFrame getSaveLimitFrame()
	{
		return savedTile;
	}
	
	public void reset()
	{
		savedTile = null;
	}
}
