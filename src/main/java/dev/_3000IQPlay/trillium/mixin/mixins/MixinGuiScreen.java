package dev._3000IQPlay.trillium.mixin.mixins;

import dev._3000IQPlay.trillium.modules.misc.ToolTips;
import dev._3000IQPlay.trillium.modules.render.NoRender;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {GuiScreen.class})
public class MixinGuiScreen
        extends Gui {
    @Inject(method = {"renderToolTip"}, at = {@At(value = "HEAD")}, cancellable = true)
    public void renderToolTipHook(ItemStack stack, int x, int y, CallbackInfo info) {
        if (ToolTips.getInstance().isOn() && stack.getItem() instanceof ItemShulkerBox) {
            ToolTips.getInstance().renderShulkerToolTip(stack, x, y, null);
            info.cancel();
        }
    }
	
	@Inject(method = "drawDefaultBackground", at = @At("HEAD"), cancellable = true)
    public void drawDefaultBackground(CallbackInfo info) {
        if (NoRender.getInstance().isOn() && NoRender.getInstance().containerBackground.getValue().booleanValue()){
            info.cancel();
        }
    }
}