package jp.mochisystems.core.manager;

import jp.mochisystems.core._mc._core.Logger;
import jp.mochisystems.core._mc.message.MessageOpenModelGui;
import jp.mochisystems.core._mc.message.PacketHandler;
import jp.mochisystems.core.blockcopier.IItemBlockModelHolder;
import jp.mochisystems.core.util.CommonAddress;
import jp.mochisystems.core.util.EntityModelWrapper;
import jp.mochisystems.core.util.IModel;
import jp.mochisystems.core.util.ModelBiped;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EntityWearingModelManager {

    private static class ModelSet{
        EntityPlayer entity;
        IModel[] models;
        ModelBiped[] biped;
    }

    private static HashMap<Integer, ModelSet> entityIdToModels = new HashMap<>();
    private static List<Integer> deleteList = new ArrayList<>();

    public static void OnWear(Entity entity, IModel model, int slotIdx)
    {
        ModelSet set = null;
        if(entityIdToModels.containsKey(entity.getEntityId()))
        {
            set = entityIdToModels.get(entity.getEntityId());
        }
        else
        {
            set = new ModelSet();
            set.entity = (EntityPlayer) entity;
            set.models = new IModel[4];
            set.biped = new ModelBiped[4];
            for(int i = 0; i < set.biped.length; ++i) set.biped[i] = new ModelBiped();
            entityIdToModels.put(entity.getEntityId(), set);
        }
        if(set.models[slotIdx] != null){
            set.biped[slotIdx].Invalidate();
//            set.models[slotIdx].Invalidate();
        }
        set.models[slotIdx] = model;
        set.biped[slotIdx].SetModel(model, 3 - slotIdx);
    }

    public static void UpdateModel()
    {
        for(ModelSet set : entityIdToModels.values())
        {
            for(ModelBiped biped : set.biped)
            {
                if(biped != null) biped.Update();
            }

            for(int i = 0; i < 4; ++i)
            {
                if(set.models[i] != null && set.entity.inventory.armorInventory.get(i).isEmpty())
                {
                    set.biped[i].Invalidate();
//                    set.models[i].Invalidate();
                    set.models[i] = null;
                }

            }
            if(Minecraft.getMinecraft().world != null && Minecraft.getMinecraft().world.getEntityByID(set.entity.getEntityId())==null)
            {
                deleteList.add(set.entity.getEntityId());
                set.entity = null;
            }
        }
        for(int i : deleteList){
            entityIdToModels.remove(i);
        }
    }

    public static ModelBiped GetModelBiped(Entity entity, ItemStack stack, int slot) {
        try {
            if(!entityIdToModels.containsKey(entity.getEntityId())) {
                OnWear(entity, GetModel(entity, stack, slot), slot);
            }
            return entityIdToModels.get(entity.getEntityId()).biped[slot];
        } catch (NullPointerException e){
            throw e;
        }
    }


    public static IModel GetModel(Entity entity, ItemStack stack, int slot)
    {
        EntityModelWrapper wrapper = new EntityModelWrapper(entity, slot);
        IModel model = ((IItemBlockModelHolder)stack.getItem()).GetBlockModel(wrapper);
        model.SetWorld(Minecraft.getMinecraft().world);
        model.readFromNBT(stack.getTagCompound());
        wrapper.SetModel(model);
        return model;
    }

    public static IModel GetCurrentModel(Entity entity, int idx)
    {
        return entityIdToModels.get(entity.getEntityId()).models[idx];
    }

    public static IModel GetCurrentModel(Entity entity, int idx, int treeIdx, int separateId)
    {
        ModelSet set = entityIdToModels.get(entity.getEntityId());
        if(idx == 2 && separateId == 1) return set.biped[idx].modelArmLeft;
        if(idx == 2 && separateId == 2) return set.biped[idx].modelArmRight;
        if(idx <= 1 && separateId == 1) return set.biped[idx].modelLegLeft;
        if(idx <= 1 && separateId == 2) return set.biped[idx].modelLegRight;
        return set.models[idx].GetModelFromTreeIndex(treeIdx);
    }


    GuiScreen currentGui;

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void AddModelGuiOpenButtonToPlayerInventory(GuiOpenEvent e)
    {
        if(e.getGui() == null) {
            currentGui = null;
            return;
        }
        EntityPlayer player = Minecraft.getMinecraft().player;
        if(player == null) {
            currentGui = null;
            return;
        }
        boolean isCreative = player.capabilities.isCreativeMode;
        if(!isCreative && e.getGui() instanceof GuiInventory){
            currentGui = e.getGui();

        }
        else if(e.getGui() instanceof GuiContainerCreative){
            currentGui = e.getGui();

        }
        else {
            currentGui = null;
        }
    }

    GuiButton button1;
    GuiButton button2, button2L, button2R;
    GuiButton button3, button3L, button3R;
    GuiButton button4, button4L, button4R;

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void AddButton(GuiScreenEvent.InitGuiEvent.Post e)
    {
        if(currentGui == null) return;
        if(currentGui != e.getGui()) return;

        if(currentGui instanceof GuiInventory) {
            int guiLeft = ((GuiContainer) e.getGui()).getGuiLeft();
            int guiTop = ((GuiContainer) e.getGui()).getGuiTop();
            button1 = new GuiButton(10001, guiLeft - 30, guiTop +  8, 30, 12, "Edit");
            button2 = new GuiButton(10002, guiLeft - 30, guiTop + 26, 30, 12, "Edit");
            button2L = new GuiButton(10012, guiLeft - 45, guiTop + 26, 12, 12, "L");
            button2R = new GuiButton(10022, guiLeft - 60, guiTop + 26, 12, 12, "R");
            button3 = new GuiButton(10003, guiLeft - 30, guiTop + 44, 30, 12, "Edit");
            button3L = new GuiButton(10013, guiLeft - 45, guiTop + 44, 12, 12, "L");
            button3R = new GuiButton(10023, guiLeft - 60, guiTop + 44, 12, 12, "R");
            button4 = new GuiButton(10004, guiLeft - 30, guiTop + 62, 30, 12, "Edit");
            button4L = new GuiButton(10014, guiLeft - 45, guiTop + 62, 12, 12, "L");
            button4R = new GuiButton(10024, guiLeft - 60, guiTop + 62, 12, 12, "R");
            e.getButtonList().add(button1);
            e.getButtonList().add(button2);
            e.getButtonList().add(button2L);
            e.getButtonList().add(button2R);
            e.getButtonList().add(button3);
            e.getButtonList().add(button3L);
            e.getButtonList().add(button3R);
            e.getButtonList().add(button4);
            e.getButtonList().add(button4L);
            e.getButtonList().add(button4R);
        }
        else if(currentGui instanceof GuiContainerCreative) {
            int guiLeft = ((GuiContainer) e.getGui()).getGuiLeft();
            int guiTop = ((GuiContainer) e.getGui()).getGuiTop();
            button1 = new GuiButton(10001, -1000, guiTop +  5, 30, 12, "Edit");
            button2 = new GuiButton(10002, -1000, guiTop + 37, 30, 12, "Edit");
            button2L = new GuiButton(10012, -1000, guiTop + 37, 12, 12, "L");
            button2R = new GuiButton(10022, -1000, guiTop + 37, 12, 12, "R");
            button3 = new GuiButton(10003, -1000, guiTop +  5, 30, 12, "Edit");
            button3L = new GuiButton(10013, -1000, guiTop +  5, 12, 12, "L");
            button3R = new GuiButton(10023, -1000, guiTop +  5, 12, 12, "R");
            button4 = new GuiButton(10004, -1000, guiTop + 37, 30, 12, "Edit");
            button4L = new GuiButton(10014, -1000, guiTop + 37, 12, 12, "L");
            button4R = new GuiButton(10024, -1000, guiTop + 37, 12, 12, "R");
            e.getButtonList().add(button1);
            e.getButtonList().add(button2);
            e.getButtonList().add(button2L);
            e.getButtonList().add(button2R);
            e.getButtonList().add(button3);
            e.getButtonList().add(button3L);
            e.getButtonList().add(button3R);
            e.getButtonList().add(button4);
            e.getButtonList().add(button4L);
            e.getButtonList().add(button4R);
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void UpdateButton(GuiScreenEvent.MouseInputEvent.Post e)
    {
        if(currentGui instanceof GuiInventory)
        {
            if(!entityIdToModels.containsKey(Minecraft.getMinecraft().player.getEntityId())) {
                button1.x = -1000;
                button2.x = -1000;
                button3.x = -1000;
                button4.x = -1000;
                button2L.x = -1000;
                button2R.x = -1000;
                button3L.x = -1000;
                button3R.x = -1000;
                button4L.x = -1000;
                button4R.x = -1000;
                return;
            }
            ModelSet set = entityIdToModels.get(Minecraft.getMinecraft().player.getEntityId());
            InventoryPlayer inv = Minecraft.getMinecraft().player.inventory;
            int guiLeft = ((GuiContainer) e.getGui()).getGuiLeft();
            if(!inv.armorInventory.get(3).isEmpty()) button1.x = guiLeft - 30;
            else button1.x = -1000;
            if(!inv.armorInventory.get(2).isEmpty()) {
                button2.x = guiLeft - 30;
                if(set.biped[2].isSeparateArmL) button2L.x = guiLeft - 45;
                else button2L.x =- 1000;
                if(set.biped[2].isSeparateArmR) button2R.x = guiLeft - 60;
                else button2R.x =- 1000;
            }
            else button2.x = -1000;
            if(!inv.armorInventory.get(1).isEmpty()) {
                button3.x = guiLeft - 30;
                if(set.biped[1].isSeparateLegL) button3L.x = guiLeft - 45;
                else button3L.x =- 1000;
                if(set.biped[1].isSeparateLegR) button3R.x = guiLeft - 60;
                else button3R.x =- 1000;
            }
            else button3.x = -1000;
            if(!inv.armorInventory.get(0).isEmpty()) {
                button4.x = guiLeft - 30;
                if(set.biped[0].isSeparateLegL) button4L.x = guiLeft - 45;
                else button4L.x =- 1000;
                if(set.biped[0].isSeparateLegR) button4R.x = guiLeft - 60;
                else button4R.x =- 1000;
            }
            else button4.x = -1000;
        }
        else if(currentGui instanceof GuiContainerCreative)
        {
            GuiContainerCreative gui = (GuiContainerCreative) currentGui;
//            Logger.debugInfo("CHECK : "+gui.getSelectedTabIndex());
            if(gui.getSelectedTabIndex() == CreativeTabs.INVENTORY.getTabIndex())
            {
                if(!entityIdToModels.containsKey(Minecraft.getMinecraft().player.getEntityId())){
                    button1.x = -1000;
                    button2.x = -1000;
                    button3.x = -1000;
                    button4.x = -1000;
                    button2L.x = -1000;
                    button2R.x = -1000;
                    button3L.x = -1000;
                    button3R.x = -1000;
                    button4L.x = -1000;
                    button4R.x = -1000;
                    return;
                }
                ModelSet set = entityIdToModels.get(Minecraft.getMinecraft().player.getEntityId());
                InventoryPlayer inv = Minecraft.getMinecraft().player.inventory;
                int guiLeft = ((GuiContainer) e.getGui()).getGuiLeft();
                if(!inv.armorInventory.get(3).isEmpty()) button1.x = guiLeft +  20;
                else button1.x = -1000;

                if(!inv.armorInventory.get(2).isEmpty()) {
                    button2.x = guiLeft + 20;
                    if(set.biped[2].isSeparateArmL) button2L.x = guiLeft + 20 - 15;
                    else button2L.x = -1000;
                    if(set.biped[2].isSeparateArmR) button2R.x = guiLeft + 20 - 30;
                    else button2R.x = -1000;
                }
                else {
                    button2.x = -1000; button2L.x = -1000; button2R.x = -1000;
                }

                if(!inv.armorInventory.get(1).isEmpty()) {
                    button3.x = guiLeft + 130;
                    if(set.biped[1].isSeparateArmL) button3L.x = guiLeft + 130 + 33;
                    else button3L.x = -1000;
                    if(set.biped[1].isSeparateArmR) button3R.x = guiLeft + 130 + 33+15;
                    else button3R.x = -1000;
                }
                else {
                    button3.x = -1000; button3L.x = -1000; button3R.x = -1000;
                }

                if(!inv.armorInventory.get(0).isEmpty()) {
                    button4.x = guiLeft + 130;
                    if(set.biped[0].isSeparateLegL) button4L.x = guiLeft + 130 + 33;
                    else button4L.x = -1000;
                    if(set.biped[0].isSeparateLegR) button4R.x = guiLeft + 130 + 33+15;
                    else button4R.x = -1000;
                }
                else {
                    button4.x = -1000; button4L.x = -1000; button4R.x = -1000;
                }
            }
            else
            {
                button1.x = -1000;
                button2.x = -1000;
                button3.x = -1000;
                button4.x = -1000;
                button2L.x = -1000;
                button2R.x = -1000;
                button3L.x = -1000;
                button3R.x = -1000;
                button4L.x = -1000;
                button4R.x = -1000;
            }
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void ActionPerformedEvent(GuiScreenEvent.ActionPerformedEvent e)
    {
        if(currentGui == null) return;
        EntityPlayer player = Minecraft.getMinecraft().player;
        if(!entityIdToModels.containsKey(player.getEntityId())) return;
        switch(e.getButton().id){
            case 10001:
                IModel model = entityIdToModels.get(player.getEntityId()).models[3];
                if(model == null) return;
                PacketHandler.INSTANCE.sendToServer(new MessageOpenModelGui(model.GetCommonAddress()));
                break;
            case 10002:
                model = entityIdToModels.get(player.getEntityId()).models[2];
                if(model == null) return;
                PacketHandler.INSTANCE.sendToServer(new MessageOpenModelGui(model.GetCommonAddress()));
                break;
            case 10003:
                model = entityIdToModels.get(player.getEntityId()).models[1];
                if(model == null) return;
                PacketHandler.INSTANCE.sendToServer(new MessageOpenModelGui(model.GetCommonAddress()));
                break;
            case 10004:
                model = entityIdToModels.get(player.getEntityId()).models[0];
                if(model == null) return;
                PacketHandler.INSTANCE.sendToServer(new MessageOpenModelGui(model.GetCommonAddress()));
                break;

            case 10012:
                model = entityIdToModels.get(player.getEntityId()).biped[2].modelArmLeft;
                if(model == null) return;
                CommonAddress ca = model.GetCommonAddress();
                ca.z =  1;
                PacketHandler.INSTANCE.sendToServer(new MessageOpenModelGui(ca));
                break;
            case 10022:
                model = entityIdToModels.get(player.getEntityId()).biped[2].modelArmRight;
                if(model == null) return;
                ca = model.GetCommonAddress();
                ca.z =  2;
                PacketHandler.INSTANCE.sendToServer(new MessageOpenModelGui(ca));
                break;


            case 10013:
                model = entityIdToModels.get(player.getEntityId()).biped[1].modelLegLeft;
                if(model == null) return;
                ca = model.GetCommonAddress();
                ca.z =  1;
                PacketHandler.INSTANCE.sendToServer(new MessageOpenModelGui(ca));
                break;
            case 10023:
                model = entityIdToModels.get(player.getEntityId()).biped[1].modelLegRight;
                if(model == null) return;
                ca = model.GetCommonAddress();
                ca.z =  2;
                PacketHandler.INSTANCE.sendToServer(new MessageOpenModelGui(ca));
                break;

            case 10014:
                model = entityIdToModels.get(player.getEntityId()).biped[0].modelLegLeft;
                if(model == null) return;
                ca = model.GetCommonAddress();
                ca.z =  1;
                PacketHandler.INSTANCE.sendToServer(new MessageOpenModelGui(ca));
                break;
            case 10024:
                model = entityIdToModels.get(player.getEntityId()).biped[0].modelLegRight;
                if(model == null) return;
                ca = model.GetCommonAddress();
                ca.z =  2;
                PacketHandler.INSTANCE.sendToServer(new MessageOpenModelGui(ca));
                break;
        }
    }
}
