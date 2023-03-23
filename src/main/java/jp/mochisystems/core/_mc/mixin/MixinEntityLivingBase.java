package jp.mochisystems.core._mc.mixin;

import jp.mochisystems.core._mc._core.Logger;
import jp.mochisystems.core._mc._core._Core;
import jp.mochisystems.core.math.Vec3d;
import jp.mochisystems.core.util.IRollSeat;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase extends Entity {

    public MixinEntityLivingBase(World worldIn){
        super(worldIn);
    }

    @Inject(method = "dismountEntity", at = @At("HEAD"), cancellable = true)
    public void dismountEntityHead(Entity entityIn, CallbackInfo ci) {
        if(entityIn instanceof IRollSeat)
        {
            Vec3d pos = ((IRollSeat)entityIn).RemovePassenger(this);
            double x = pos.x;
            double y = pos.y;
            double z = pos.z;
            this.setPositionAndUpdate(x, y, z);
            ci.cancel();
        }
    }
}