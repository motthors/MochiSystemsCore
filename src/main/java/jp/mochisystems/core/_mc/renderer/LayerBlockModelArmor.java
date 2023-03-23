package jp.mochisystems.core._mc.renderer;

import jp.mochisystems.core._mc._core._Core;
import jp.mochisystems.core.blockcopier.IItemBlockModelHolder;
import jp.mochisystems.core.manager.EntityWearingModelManager;
import jp.mochisystems.core.util.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public class LayerBlockModelArmor extends LayerBipedArmor {
    private final RenderLivingBase<?> renderer;

    public LayerBlockModelArmor(RenderLivingBase<?> rendererIn) {
        super(rendererIn);
        this.renderer = rendererIn;
    }

    @Override
    public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
//        GlStateManager.disableLighting();
        this.renderArmorLayer(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, EntityEquipmentSlot.CHEST);
        this.renderArmorLayer(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, EntityEquipmentSlot.LEGS);
        this.renderArmorLayer(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, EntityEquipmentSlot.FEET);
        this.renderArmorLayer(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, EntityEquipmentSlot.HEAD);
    }

    private void renderArmorLayer(EntityLivingBase player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, EntityEquipmentSlot slotIn)
    {
        ItemStack itemstack = player.getItemStackFromSlot(slotIn);

        if (itemstack.isEmpty() || !( itemstack.getItem() instanceof IItemBlockModelHolder) )
        {
            return;
        }

        int slotIndex = slotIn.getIndex();
        ModelBiped model = EntityWearingModelManager.GetModelBiped(player, itemstack, slotIndex);

        if (model != null)
        {
            model.setModelAttributes(this.renderer.getMainModel());
            model.setLivingAnimations(player, limbSwing, limbSwingAmount, partialTicks);
            this.setModelSlotVisible(model, slotIn);
            _Core.BindBlocksTextureMap();
            model.render(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }
    }
}
