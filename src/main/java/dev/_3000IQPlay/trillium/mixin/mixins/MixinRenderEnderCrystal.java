package dev._3000IQPlay.trillium.mixin.mixins;

import dev._3000IQPlay.trillium.Trillium;
import dev._3000IQPlay.trillium.modules.render.CrystalChams;
import dev._3000IQPlay.trillium.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderEnderCrystal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(value = {RenderEnderCrystal.class})
public abstract class MixinRenderEnderCrystal {
    private static final ResourceLocation RES_ITEM_GLINT;
    @Final
    @Shadow
    private static ResourceLocation ENDER_CRYSTAL_TEXTURES;

    static {
        RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    }

    @Shadow
    public ModelBase modelEnderCrystal;
    @Shadow
    public ModelBase modelEnderCrystalNoBase;

    @Shadow
    public abstract void doRender(EntityEnderCrystal var1, double var2, double var4, double var6, float var8, float var9);

    @Redirect(method = {"doRender(Lnet/minecraft/entity/item/EntityEnderCrystal;DDDFF)V"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelBase;render(Lnet/minecraft/entity/Entity;FFFFFF)V"))
    private void render1(ModelBase var1, Entity var2, float var3, float var4, float var5, float var6, float var7, float var8) {
        if (!Trillium.moduleManager.getModuleByClass(CrystalChams.class).isEnabled()) {
            var1.render(var2, var3, var4, var5, var6, var7, var8);
        }
    }

    @Redirect(method = {"doRender(Lnet/minecraft/entity/item/EntityEnderCrystal;DDDFF)V"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelBase;render(Lnet/minecraft/entity/Entity;FFFFFF)V", ordinal = 1))
    private void render2(ModelBase var1, Entity var2, float var3, float var4, float var5, float var6, float var7, float var8) {
        if (!Trillium.moduleManager.getModuleByClass(CrystalChams.class).isEnabled()) {
            var1.render(var2, var3, var4, var5, var6, var7, var8);
        }
    }

    @Inject(method = {"doRender(Lnet/minecraft/entity/item/EntityEnderCrystal;DDDFF)V"}, at = {@At(value = "RETURN")}, cancellable = true)
    public void IdoRender(EntityEnderCrystal var1, double var2, double var4, double var6, float var8, float var9, CallbackInfo var10) {
        Minecraft mc = Minecraft.getMinecraft();
        mc.gameSettings.fancyGraphics = false;
        if (Trillium.moduleManager.getModuleByClass(CrystalChams.class).isEnabled()) {
            Color outlineColor;
            GL11.glPushMatrix();
            float var14 = (float) var1.innerRotation + var9;
            GlStateManager.translate((double) var2, (double) var4, (double) var6);
            GlStateManager.scale((float) CrystalChams.INSTANCE.size.getValue().floatValue(), (float) CrystalChams.INSTANCE.size.getValue().floatValue(), (float) CrystalChams.INSTANCE.size.getValue().floatValue());
            Minecraft.getMinecraft().getRenderManager().renderEngine.bindTexture(ENDER_CRYSTAL_TEXTURES);
            float var15 = MathHelper.sin((float) (var14 * 0.2f)) / 2.0f + 0.5f;
            var15 += var15 * var15;
            float spinSpeed = CrystalChams.INSTANCE.crystalSpeed.getValue().floatValue();
            float bounceSpeed = CrystalChams.INSTANCE.crystalBounce.getValue().floatValue();
            if (CrystalChams.INSTANCE.texture.getValue().booleanValue()) {
                if (var1.shouldShowBottom()) {
                    this.modelEnderCrystal.render((Entity) var1, 0.0f, var14 * spinSpeed, var15 * bounceSpeed, 0.0f, 0.0f, 0.0625f);
                } else {
                    this.modelEnderCrystalNoBase.render((Entity) var1, 0.0f, var14 * spinSpeed, var15 * bounceSpeed, 0.0f, 0.0f, 0.0625f);
                }
            }
            GL11.glPushAttrib(1048575);
            if (CrystalChams.INSTANCE.mode.getValue().equals((Object) CrystalChams.modes.WIREFRAME)) {
                GL11.glPolygonMode(1032, 6913);
            }
            if (CrystalChams.INSTANCE.blendModes.getValue().equals((Object) CrystalChams.BlendModes.Default)) {
				GL11.glBlendFunc(770, 771);
            }
			if (CrystalChams.INSTANCE.blendModes.getValue().equals((Object) CrystalChams.BlendModes.Brighter)) {
                GL11.glBlendFunc(770, 32772);
            }
            GL11.glDisable(3008);
            GL11.glDisable(3553);
            GL11.glDisable(2896);
            GL11.glEnable(3042);
            GL11.glLineWidth(1.5f);
            GL11.glEnable(2960);
            GL11.glDisable(2929);
            GL11.glDepthMask(false);
            GL11.glEnable(10754);
            Color visibleColor = new Color(CrystalChams.INSTANCE.colorC.getValue().getRed(), CrystalChams.INSTANCE.colorC.getValue().getGreen(), CrystalChams.INSTANCE.colorC.getValue().getBlue());
            Color hiddenColor = new Color(CrystalChams.INSTANCE.hiddenC.getValue().getRed(), CrystalChams.INSTANCE.hiddenC.getValue().getGreen(), CrystalChams.INSTANCE.hiddenC.getValue().getBlue());
            Color color = outlineColor = new Color(CrystalChams.INSTANCE.outlineC.getValue().getRed(), CrystalChams.INSTANCE.outlineC.getValue().getGreen(), CrystalChams.INSTANCE.outlineC.getValue().getBlue());
            if (CrystalChams.INSTANCE.hiddenSync.getValue().booleanValue()) {
                GL11.glColor4f((float) visibleColor.getRed() / 255.0f, (float) visibleColor.getGreen() / 255.0f, (float) visibleColor.getBlue() / 255.0f, (float) CrystalChams.INSTANCE.colorC.getValue().getAlpha() / 255.0f);
            } else {
                GL11.glColor4f((float) hiddenColor.getRed() / 255.0f, (float) hiddenColor.getGreen() / 255.0f, (float) hiddenColor.getBlue() / 255.0f, (float) CrystalChams.INSTANCE.hiddenC.getValue().getAlpha() / 255.0f);
            }
            if (var1.shouldShowBottom()) {
                this.modelEnderCrystal.render((Entity) var1, 0.0f, var14 * spinSpeed, var15 * bounceSpeed, 0.0f, 0.0f, 0.0625f);
            } else {
                this.modelEnderCrystalNoBase.render((Entity) var1, 0.0f, var14 * spinSpeed, var15 * bounceSpeed, 0.0f, 0.0f, 0.0625f);
            }
            GL11.glEnable(2929);
            GL11.glDepthMask(true);
            GL11.glColor4f((float) visibleColor.getRed() / 255.0f, (float) visibleColor.getGreen() / 255.0f, (float) visibleColor.getBlue() / 255.0f, (float) CrystalChams.INSTANCE.colorC.getValue().getAlpha() / 255.0f);
            if (var1.shouldShowBottom()) {
                this.modelEnderCrystal.render((Entity) var1, 0.0f, var14 * spinSpeed, var15 * bounceSpeed, 0.0f, 0.0f, 0.0625f);
            } else {
                this.modelEnderCrystalNoBase.render((Entity) var1, 0.0f, var14 * spinSpeed, var15 * bounceSpeed, 0.0f, 0.0f, 0.0625f);
            }
            if (CrystalChams.INSTANCE.enchanted.getValue().booleanValue()) {
                mc.getTextureManager().bindTexture(RES_ITEM_GLINT);
                GL11.glTexCoord3d(1.0, 1.0, 1.0);
                GL11.glEnable(3553);
                GL11.glBlendFunc(768, 771);
                GL11.glColor4f((float) CrystalChams.INSTANCE.glintC.getValue().getRed() / 255.0f, (float) CrystalChams.INSTANCE.glintC.getValue().getGreen() / 255.0f, (float) CrystalChams.INSTANCE.glintC.getValue().getBlue() / 255.0f, (float) CrystalChams.INSTANCE.glintC.getValue().getAlpha() / 255.0f);
                if (var1.shouldShowBottom()) {
                    this.modelEnderCrystal.render((Entity) var1, 0.0f, var14 * spinSpeed, var15 * bounceSpeed, 0.0f, 0.0f, 0.0625f);
                } else {
                    this.modelEnderCrystalNoBase.render((Entity) var1, 0.0f, var14 * spinSpeed, var15 * bounceSpeed, 0.0f, 0.0f, 0.0625f);
                }
                if (CrystalChams.INSTANCE.blendModes.getValue().equals((Object) CrystalChams.BlendModes.Default)) {
				    GL11.glBlendFunc(768, 771);
                }
			    if (CrystalChams.INSTANCE.blendModes.getValue().equals((Object) CrystalChams.BlendModes.Brighter)) {
                    GL11.glBlendFunc(770, 32772);
			    } else {
                    GL11.glBlendFunc(770, 771);
                }
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            }
            GL11.glEnable(3042);
            GL11.glEnable(2896);
            GL11.glEnable(3553);
            GL11.glEnable(3008);
            GL11.glPopAttrib();
            if (CrystalChams.INSTANCE.outline.getValue().booleanValue()) {
                if (CrystalChams.INSTANCE.outlineMode.getValue().equals((Object) CrystalChams.outlineModes.WIRE)) {
                    GL11.glPushAttrib(1048575);
                    GL11.glPolygonMode(1032, 6913);
                    GL11.glDisable(3008);
                    GL11.glDisable(3553);
                    GL11.glDisable(2896);
                    GL11.glEnable(3042);
                    GL11.glBlendFunc(770, 771);
                    GL11.glLineWidth((float) CrystalChams.INSTANCE.lineWidth.getValue().floatValue());
                    GL11.glEnable(2960);
                    GL11.glDisable(2929);
                    GL11.glDepthMask(false);
                    GL11.glEnable(10754);
                    GL11.glColor4f((float) outlineColor.getRed() / 255.0f, (float) outlineColor.getGreen() / 255.0f, (float) outlineColor.getBlue() / 255.0f, (float) CrystalChams.INSTANCE.outlineC.getValue().getAlpha() / 255.0f);
                    if (var1.shouldShowBottom()) {
                        this.modelEnderCrystal.render((Entity) var1, 0.0f, var14 * spinSpeed, var15 * bounceSpeed, 0.0f, 0.0f, 0.0625f);
                    } else {
                        this.modelEnderCrystalNoBase.render((Entity) var1, 0.0f, var14 * spinSpeed, var15 * bounceSpeed, 0.0f, 0.0f, 0.0625f);
                    }
                    GL11.glEnable(2929);
                    GL11.glDepthMask(true);
                    if (var1.shouldShowBottom()) {
                        this.modelEnderCrystal.render((Entity) var1, 0.0f, var14 * spinSpeed, var15 * bounceSpeed, 0.0f, 0.0f, 0.0625f);
                    } else {
                        this.modelEnderCrystalNoBase.render((Entity) var1, 0.0f, var14 * spinSpeed, var15 * bounceSpeed, 0.0f, 0.0f, 0.0625f);
                    }
                    GL11.glEnable(3042);
                    GL11.glEnable(2896);
                    GL11.glEnable(3553);
                    GL11.glEnable(3008);
                    GL11.glPopAttrib();
                } else {
                    RenderUtil.setColor(new Color(outlineColor.getRed(), outlineColor.getGreen(), outlineColor.getBlue()));
                    RenderUtil.renderOne(CrystalChams.INSTANCE.lineWidth.getValue().floatValue());
                    if (var1.shouldShowBottom()) {
                        this.modelEnderCrystal.render((Entity) var1, 0.0f, var14 * spinSpeed, var15 * bounceSpeed, 0.0f, 0.0f, 0.0625f);
                    } else {
                        this.modelEnderCrystalNoBase.render((Entity) var1, 0.0f, var14 * spinSpeed, var15 * bounceSpeed, 0.0f, 0.0f, 0.0625f);
                    }
                    RenderUtil.renderTwo();
                    if (var1.shouldShowBottom()) {
                        this.modelEnderCrystal.render((Entity) var1, 0.0f, var14 * spinSpeed, var15 * bounceSpeed, 0.0f, 0.0f, 0.0625f);
                    } else {
                        this.modelEnderCrystalNoBase.render((Entity) var1, 0.0f, var14 * spinSpeed, var15 * bounceSpeed, 0.0f, 0.0f, 0.0625f);
                    }
                    RenderUtil.renderThree();
                    RenderUtil.renderFour(outlineColor);
                    RenderUtil.setColor(new Color(outlineColor.getRed(), outlineColor.getGreen(), outlineColor.getBlue()));
                    if (var1.shouldShowBottom()) {
                        this.modelEnderCrystal.render((Entity) var1, 0.0f, var14 * spinSpeed, var15 * bounceSpeed, 0.0f, 0.0f, 0.0625f);
                    } else {
                        this.modelEnderCrystalNoBase.render((Entity) var1, 0.0f, var14 * spinSpeed, var15 * bounceSpeed, 0.0f, 0.0f, 0.0625f);
                    }
                    RenderUtil.renderFive();
                    RenderUtil.setColor(Color.WHITE);
                }
            }
            GL11.glPopMatrix();
        }
    }
}