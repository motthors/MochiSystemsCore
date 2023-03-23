package jp.mochisystems.core._mc.mixin;

import jp.mochisystems.core._mc.renderer.LayerBlockModelArmor;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderPlayer.class)
public abstract class MixinPlayerRenderer extends RenderLivingBase<AbstractClientPlayer> {

    public MixinPlayerRenderer(RenderManager renderManagerIn, ModelBase modelBaseIn, float shadowSizeIn) {
        super(renderManagerIn, modelBaseIn, shadowSizeIn);
    }

    @Inject(method = "<init>(Lnet/minecraft/client/renderer/entity/RenderManager;Z)V", at = @At("TAIL"), require = 1)
    public void RenderPlayerEnd(RenderManager renderManager, boolean useSmallArms, CallbackInfo ci) {
        this.addLayer(new LayerBlockModelArmor(this));
    }

}