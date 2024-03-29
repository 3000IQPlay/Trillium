package dev._3000IQPlay.trillium.mixin.mixins;

import org.spongepowered.asm.mixin.*;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.entity.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import dev._3000IQPlay.trillium.Trillium;
import net.minecraft.entity.player.EntityPlayer;
import dev._3000IQPlay.trillium.modules.render.*;
import org.spongepowered.asm.mixin.injection.*;
import net.minecraft.inventory.*;

import static dev._3000IQPlay.trillium.util.Util.mc;

@Mixin({ LayerArmorBase.class })
public class MixinLayerArmorBase
{
    @Inject(method = { "doRenderLayer" },  at = { @At("HEAD") },  cancellable = true)
    public void doRenderLayer(final EntityLivingBase entitylivingbaseIn,  final float limbSwing,  final float limbSwingAmount,  final float partialTicks,  final float ageInTicks,  final float netHeadYaw,  final float headPitch,  final float scale,  final CallbackInfo ci) {
        if (NoRender.getInstance().isEnabled() && NoRender.getInstance().noArmor.getValue() == NoRender.NoArmor.ALL) {
            ci.cancel();
        }
		if (Trillium.moduleManager == null){
            return;
        }
        if (Trillium.friendManager == null) {
            return;
        }
        if (Trillium.moduleManager.getModuleByClass(Models.class).isOn() && Trillium.moduleManager.getModuleByClass(Models.class).onlySelf.getValue() && entitylivingbaseIn == mc.player ){
            ci.cancel();
        } else if (Trillium.moduleManager.getModuleByClass(Models.class).isOn() && !Trillium.moduleManager.getModuleByClass(Models.class).onlySelf.getValue()){
            ci.cancel();
        }
    }

    @Inject(method = { "renderArmorLayer" },  at = { @At("HEAD") },  cancellable = true)
    public void renderArmorLayer(final EntityLivingBase entityLivingBaseIn,  final float limbSwing,  final float limbSwingAmount,  final float partialTicks,  final float ageInTicks,  final float netHeadYaw,  final float headPitch,  final float scale,  final EntityEquipmentSlot slotIn,  final CallbackInfo ci) {
        if (NoRender.getInstance().isEnabled() && NoRender.getInstance().noArmor.getValue() == NoRender.NoArmor.HELMET && slotIn == EntityEquipmentSlot.HEAD) {
            ci.cancel();
        }
    }
    
}