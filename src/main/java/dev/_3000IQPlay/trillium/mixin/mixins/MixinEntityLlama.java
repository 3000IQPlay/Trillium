package dev._3000IQPlay.trillium.mixin.mixins;

import dev._3000IQPlay.trillium.modules.exploit.EntityControl;
import net.minecraft.entity.passive.EntityLlama;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityLlama.class)
public class MixinEntityLlama {
    @Inject(method = "canBeSteered", at = @At("HEAD"), cancellable = true)
    public void canBeSteered(final CallbackInfoReturnable<Boolean> info) {
        if (EntityControl.instance.isEnabled()) info.setReturnValue(true);
    }
}