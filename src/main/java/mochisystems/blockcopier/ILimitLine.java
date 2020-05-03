package mochisystems.blockcopier;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;

public interface ILimitLine {
	void setFrameLength(int value);
	void setFrameHeight(int value);
	void setFrameWidth(int value);
	int getFrameLength();
	int getFrameHeight();
	int getFrameWidth();
	void resetFrameLength();
	void render(Tessellator tess);
}
