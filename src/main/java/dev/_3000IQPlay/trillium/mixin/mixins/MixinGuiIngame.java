package dev._3000IQPlay.trillium.mixin.mixins;

import dev._3000IQPlay.trillium.event.events.RenderAttackIndicatorEvent;
import dev._3000IQPlay.trillium.gui.hud.Potions;
import dev._3000IQPlay.trillium.modules.misc.AntiTittle;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import org.spongepowered.asm.mixin.injection.*;
import net.minecraft.client.gui.*;
import dev._3000IQPlay.trillium.*;

@Mixin({ GuiIngame.class })
public class MixinGuiIngame extends Gui
{

    @Inject(method = { "renderPotionEffects" },  at = { @At("HEAD") },  cancellable = true)
    protected void renderPotionEffectsHook(final ScaledResolution scaledRes,  final CallbackInfo info) {
        if (Trillium.moduleManager.getModuleByClass(Potions.class).isOn()) {
            info.cancel();
        }
    }

    @Inject(method = { "renderScoreboard" },  at = { @At("HEAD") },  cancellable = true)
    protected void renderScoreboardHook(ScoreObjective objective, ScaledResolution scaledRes, CallbackInfo ci) {
        if (Trillium.moduleManager.getModuleByClass(AntiTittle.class).scoreBoard.getValue()) {
            ci.cancel();
        }
    }


    @Inject(method = "renderAttackIndicator", at = @At("HEAD"), cancellable = true)
    public void onRenderAttackIndicator(float partialTicks, ScaledResolution p_184045_2_, CallbackInfo ci) {
        RenderAttackIndicatorEvent event = new RenderAttackIndicatorEvent();
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) ci.cancel();
    }
}