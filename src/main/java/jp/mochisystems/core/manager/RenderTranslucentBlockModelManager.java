package jp.mochisystems.core.manager;

import java.util.ArrayDeque;

import jp.mochisystems.core.util.IModel;
import jp.mochisystems.core.util.IModelController;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;

public class RenderTranslucentBlockModelManager {

	public static ArrayDeque<IModel> deque = new ArrayDeque<>();

	public static void add(IModel model)
	{
		deque.add(model);
	}
	
	// from renderEntities
	public static void draw(float f)
    {
		Minecraft mc = Minecraft.getMinecraft();
        {
            GL11.glMatrixMode(GL11.GL_MODELVIEW);

//            mc.entityRenderer.enableLightmap((double)f);


            RenderHelper.enableStandardItemLighting();
			mc.entityRenderer.enableLightmap();

            
            for(IModel model : deque)
            {
            	// same TileEntityRenderDispatcher
//             //TODO ライト？
//            	TileEntityRendererDispatcher terd = TileEntityRendererDispatcher.instance;
//            	int i = terd.world.getLightBrightnessForSkyBlocks(tile.xCoord, tile.yCoord, tile.zCoord, 0);
//            	int j = i % 65536;
//            	int k = i / 65536;
//            	OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j / 1.0F, (float)k / 1.0F);
            	GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            	
            	// rendererTileEntity
//            	Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            	GL11.glPushMatrix();
            	double x = model.ModelPosX() - TileEntityRendererDispatcher.staticPlayerX;
            	double y = model.ModelPosY() - TileEntityRendererDispatcher.staticPlayerY;
            	double z = model.ModelPosZ() - TileEntityRendererDispatcher.staticPlayerZ;
            	GL11.glTranslated(x, y, z);
				model.RenderModel(1, f);
            	GL11.glPopMatrix();
            }		
            deque.clear();
            
//            mc.entityRenderer.disableLightmap((double)f);
			mc.entityRenderer.disableLightmap();
        }
        
    }
}
