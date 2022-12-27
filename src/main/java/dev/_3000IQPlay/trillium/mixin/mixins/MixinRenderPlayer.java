package dev._3000IQPlay.trillium.mixin.mixins;

import dev._3000IQPlay.trillium.Trillium;
import dev._3000IQPlay.trillium.event.events.FreecamEvent;
import dev._3000IQPlay.trillium.command.commands.ChangeSkinCommand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.entity.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import org.spongepowered.asm.mixin.injection.*;
import net.minecraft.client.*;
import dev._3000IQPlay.trillium.modules.render.*;
import org.lwjgl.opengl.*;
import net.minecraft.client.renderer.*;
import dev._3000IQPlay.trillium.util.*;

import static dev._3000IQPlay.trillium.util.Util.mc;

@Mixin({ RenderPlayer.class })
public class MixinRenderPlayer
{
    @Inject(method = { "renderEntityName" },  at = { @At("HEAD") },  cancellable = true)
    public void renderEntityNameHook(final AbstractClientPlayer entityIn,  final double x,  final double y,  final double z,  final String name,  final double distanceSq,  final CallbackInfo info) {
        if (NameTags.getInstance().isOn()) {
            info.cancel();
        }
    }
    @Redirect(method = "doRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/AbstractClientPlayer;isUser()Z"))
    private boolean isUserRedirect(AbstractClientPlayer abstractClientPlayer) {
        Minecraft mc = Minecraft.getMinecraft();
        FreecamEvent event = new FreecamEvent();
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            return abstractClientPlayer.isUser() && abstractClientPlayer == mc.getRenderViewEntity();
        }
        return abstractClientPlayer.isUser();
    }

/*
    @Redirect(method = "renderRightArm", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;color(FFF)V"))
    public void renderRightArmHook(float colorRed, float colorGreen, float colorBlue) {
        if (HandChams.getInstance().isEnabled() && HandChams.getInstance().Mode.getValue() == HandChams.ChamsMode.Gradient) {
            GlStateManager.color(1.0f, 1.0f, 1.0f, HandChams.getInstance().coloralpha.getValue() / 255.0f);
        }
    }

/*
    @Redirect(method = "renderLeftArm", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;color(FFF)V"))
    public void renderLeftArmHook(float colorRed, float colorGreen, float colorBlue) {
        if (HandChams.getInstance().isEnabled() && HandChams.getInstance().Mode.getValue() == HandChams.ChamsMode.Gradient) {
            GlStateManager.color(1.0f, 1.0f, 1.0f, HandChams.getInstance().coloralpha.getValue() / 255.0f);
        }
    }

 */

    private final ResourceLocation amogus = new ResourceLocation("textures/amogus.png");
    private final ResourceLocation demon = new ResourceLocation("textures/demon.png");
    private final ResourceLocation rabbit = new ResourceLocation("textures/rabbit.png");
    private final ResourceLocation fred = new ResourceLocation("textures/freddy.png");

    @Overwrite
    public ResourceLocation getEntityTexture(AbstractClientPlayer entity){

        if(Trillium.moduleManager.getModuleByClass(Models.class).isEnabled() && (!Trillium.moduleManager.getModuleByClass(Models.class).onlySelf.getValue() || entity == Minecraft.getMinecraft().player || Trillium.friendManager.isFriend(entity.getName()) && Trillium.moduleManager.getModuleByClass(Models.class).friends.getValue())){
            if (Trillium.moduleManager.getModuleByClass(Models.class).Mode.getValue() == Models.mode.Amogus) {
                return amogus;

                //return new ResourceLocation("assets/minecraft/textures/amogus.png");
            }

            if (Trillium.moduleManager.getModuleByClass(Models.class).Mode.getValue() == Models.mode.Rabbit) {
                return rabbit;
            }
            if (Trillium.moduleManager.getModuleByClass(Models.class).Mode.getValue() == Models.mode.Freddy) {
                return fred;
            }
        } else {
            if (ChangeSkinCommand.getInstance().changedplayers.contains(entity.getName())) {
                GL11.glColor4f(1f, 1f, 1f, 1f);
                return PNGtoResourceLocation.getTexture3(entity.getName(), "png");
            } else {
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                return entity.getLocationSkin();
            }
        }
        return entity.getLocationSkin();
    }

}