package dev._3000IQPlay.trillium.mixin.mixins;

import net.minecraft.client.gui.GuiMerchant;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GuiMerchant.class)
public interface IGuiMerchant {
    @Accessor("selectedMerchantRecipe")
    int getSelectedMerchantRecipe();
}