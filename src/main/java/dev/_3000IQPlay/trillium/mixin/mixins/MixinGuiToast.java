package dev._3000IQPlay.trillium.mixin.mixins;

import org.spongepowered.asm.mixin.*;
import net.minecraft.client.gui.toasts.*;
import net.minecraft.client.gui.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import dev._3000IQPlay.trillium.modules.render.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin({GuiToast.class})
public class MixinGuiToast
{
    @Inject(method = { "drawToast" },  at = { @At("HEAD") },  cancellable = true)
    public void drawToastHook(final ScaledResolution resolution,  final CallbackInfo info) {
        if (NoRender.getInstance().isOn() && (boolean)NoRender.getInstance().advancements.getValue()) {
            info.cancel();
        }
    }
}