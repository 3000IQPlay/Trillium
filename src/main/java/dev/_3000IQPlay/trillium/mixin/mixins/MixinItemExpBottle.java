package dev._3000IQPlay.trillium.mixin.mixins;

import dev._3000IQPlay.trillium.Trillium;
import dev._3000IQPlay.trillium.modules.misc.MiddleClick;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemExpBottle.class)
public abstract class MixinItemExpBottle
{


    @Redirect(
            method = "onItemRightClick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;shrink(I)V"))
    public void onItemRightClickHook(ItemStack stack, int quantity)
    {
        if (!Trillium.moduleManager.getModuleByClass(MiddleClick.class).isOn() &&Trillium.moduleManager.getModuleByClass(MiddleClick.class).cancelShrink() )
        {
            stack.shrink(quantity);
        }
    }

}