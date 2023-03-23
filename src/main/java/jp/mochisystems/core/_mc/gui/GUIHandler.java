
package jp.mochisystems.core._mc.gui;

import jp.mochisystems.core._mc._core.Logger;
import jp.mochisystems.core._mc._core._Core;
import jp.mochisystems.core._mc.gui.container.ContainerBlockScanner;
import jp.mochisystems.core._mc.gui.container.DefContainer;
import jp.mochisystems.core._mc.message.MessageOpenModelGui;
import jp.mochisystems.core._mc.message.PacketHandler;
import jp.mochisystems.core._mc.tileentity.TileEntityBlockModelCutter;
import jp.mochisystems.core._mc.tileentity.TileEntityFileManager;
import jp.mochisystems.core.blockcopier.IItemBlockModelHolder;
import jp.mochisystems.core.blockcopier.ILimitFrameHolder;
import jp.mochisystems.core.manager.ChangingLimitLineManager;
import jp.mochisystems.core.manager.EntityWearingModelManager;
import jp.mochisystems.core.util.CommonAddress;
import jp.mochisystems.core.util.IModel;
import jp.mochisystems.core.util.IModelController;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;


public class GUIHandler implements IGuiHandler {
	
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
//		if(tile == null)return null;
		switch(ID) {
			case _Core.GUIID_OpenWearingModel:
				return ContainerInstance(player.inventory, EntityWearingModelManager.GetCurrentModel(player, x, y, z));
			case _Core.GUIID_BlockModel:
				return ContainerInstance(player.inventory, GetModel(player, world, x, y, z));
			case _Core.GUIID_FileManager:
				return new ContainerBlockScanner(player.inventory, (TileEntityFileManager) tile);
			case _Core.GUIID_Cutter:
				return new DefContainer();
		}
		return null;
	}
    
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
//		if(tile==null) return null;
		if(tile instanceof ILimitFrameHolder) ChangingLimitLineManager.INSTANCE.saveTile(((ILimitFrameHolder) tile).GetLimitFrame(), x, y, z);
		else ChangingLimitLineManager.INSTANCE.reset();
		switch(ID) {
			case _Core.GUIID_OpenWearingModel:
				return GuiInstance(EntityWearingModelManager.GetCurrentModel(player, x, y, z), player.inventory);
			case _Core.GUIID_BlockModel:
				return GuiInstance(GetModel(player, world, x, y, z), player.inventory);
			case _Core.GUIID_FileManager:
				return new GUIFileManager(player.inventory, (TileEntityFileManager) tile);
			case _Core.GUIID_Cutter:
				return new GUICutter(x, y, z, player.inventory, (TileEntityBlockModelCutter) tile);
		}
		return null;
	}


	public static IModel GetModel(EntityPlayer player, World world, int x, int y, int z) {
		if(reservedGuiAddress.isSyncing()){
			IModel model = reservedGuiAddress.GetInstance(world);
//			Logger.debugInfo("GetModel ... "+reservedGuiAddress.x+" : "+reservedGuiAddress.y+" : "+reservedGuiAddress.z+" _ "+reservedGuiAddress.TreeListIndex);
			return model;
		}else{
//			Logger.error("yava"); //TODO dummyGUIで防ぐ？ 前面黒半透明に赤字ERROR表示　怖すぎ？
			return null;
		}

//		TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
//		ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
//		Item heldItem = stack.isEmpty() ? null : stack.getItem();
//		Entity entity = null;
//		if(tile != null){
//			ChangingLimitLineManager.INSTANCE.saveTileIfHoldLimitFrame(tile, x, y, z);
//		}
//		if(y <= -9999)
//			entity = world.getEntityByID(x);
//
//		IModelController modelHolder = null;
//		if(tile != null) modelHolder = (IModelController) tile;
//		if(entity != null) modelHolder = (IModelController) entity;
//		IModel model = null;
//		if(modelHolder != null){
//			model = modelHolder.GetModelForGui();
//		}
//		else if(heldItem instanceof IItemBlockModelHolder){
//			model = ((IItemBlockModelHolder)heldItem).GetBlockModel(null);
//		}
//		return model;
	}





	static class GuiData{
		public Object mod;
		public Class guiClass;
		public Class containerClass;
	}
	private static Map<Class, GuiData> GuiMap = new HashMap<>();
	public static void RegisterBlockModelGui(Class clazz, Object modInstance, Class guiClass,
											 Class containerClass)
	{
		if(GuiMap.containsKey(clazz)) return;
		GuiData data = new GuiData();
		data.mod = modInstance;
		data.guiClass = guiClass;
		data.containerClass = containerClass;
		GuiMap.put(clazz, data);
	}


	static CommonAddress reservedGuiAddress = new CommonAddress();
	public static void OpenBlockModelGuiInClient(IModel model)
	{
		if(!GuiMap.containsKey(model.getClass())) return;
		reservedGuiAddress.CopyFrom(model.GetCommonAddress());
		PacketHandler.INSTANCE.sendToServer(new MessageOpenModelGui(reservedGuiAddress));
//		GuiData data = GuiMap.get(model.getClass());
//		player.openGui(_Core.Instance, _Core.GUIID_BlockModel, player.world, x, y, z);
	}
	public static void OpenCustomGuiInClient(CommonAddress address)
	{
		reservedGuiAddress.CopyFrom(address);
	}
	public static void OpenBlockModelInServer(EntityPlayer player, CommonAddress address)
	{
		reservedGuiAddress = address;
		player.openGui(_Core.Instance, _Core.GUIID_BlockModel, player.world, address.x, address.y, address.z);
	}
	public static void OpenForCustomGuiInServer(CommonAddress address)
	{
		reservedGuiAddress = address;
	}


	public Object GuiInstance(IModel model, InventoryPlayer inv) {
		if (!GuiMap.containsKey(model.getClass())) return null;
		GuiData data = GuiMap.get(model.getClass());
		try {
			Constructor<?> init = data.guiClass.getDeclaredConstructor(InventoryPlayer.class, IModel.class);
			return init.newInstance(inv, model);
		}
		catch (Exception e) {
			return null;
		}
	}
	public Object ContainerInstance(InventoryPlayer inv, IModel model) {
		if (!GuiMap.containsKey(model.getClass())) return null;
		GuiData data = GuiMap.get(model.getClass());
		try {
			Constructor<?> init = data.containerClass.getDeclaredConstructor(InventoryPlayer.class, IModel.class);
			return init.newInstance(inv, model);
		}
		catch (Exception e) {
			return null;
		}
	}
}
