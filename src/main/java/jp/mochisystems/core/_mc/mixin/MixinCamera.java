package jp.mochisystems.core._mc.mixin;

import jp.mochisystems.core.hook.OrientCameraHooker;
import net.minecraft.client.renderer.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class MixinCamera{

    @Inject(method = "orientCamera", at = @At("HEAD"), require = 1)
    private void orientCameraHead(float partialTicks, CallbackInfo ci) {
        OrientCameraHooker.HandleFirst(partialTicks);
    }

    @Inject(method = "orientCamera", at = @At("TAIL"), require = 1)
    private void orientCameraReturn(float partialTicks, CallbackInfo ci) {
        OrientCameraHooker.HandleLast(partialTicks);
    }
}