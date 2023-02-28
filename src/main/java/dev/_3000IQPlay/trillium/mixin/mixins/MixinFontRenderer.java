package dev._3000IQPlay.trillium.mixin.mixins;

import dev._3000IQPlay.trillium.Trillium;
import dev._3000IQPlay.trillium.modules.misc.PasswordHider;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static dev._3000IQPlay.trillium.util.Util.mc;

@Mixin(value={FontRenderer.class})
public abstract class MixinFontRenderer {
    @Shadow
    protected abstract void renderStringAtPos(String var1, boolean var2);
	
    @Redirect(method = {"renderString(Ljava/lang/String;FFIZ)I"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;renderStringAtPos(Ljava/lang/String;Z)V"))
    public void renderStringAtPosHook(FontRenderer fontRenderer, String string, boolean bl) {
        if(Trillium.moduleManager == null){
            renderStringAtPos(string, bl);
            return;
        }
        if (Trillium.moduleManager.getModuleByClass(PasswordHider.class).isEnabled()) {
            if(string.contains("/login") || string.contains("/register") && mc.currentScreen instanceof GuiChat) {
                StringBuilder final_string = new StringBuilder();
                for(char cha: string.replace("/login","").replace("/register","").toCharArray()){
                    final_string.append("*");
                }
                if (string.contains("/register")){
                    renderStringAtPos("/register " + final_string, bl);
                    return;
                } else if (string.contains("/login")){
                    renderStringAtPos("/login " + final_string, bl);
                    return;
                }
            }
        }
        renderStringAtPos(string, bl);
    }
}