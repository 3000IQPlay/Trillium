package dev._3000IQPlay.trillium.mixin.mixins;

import dev._3000IQPlay.trillium.Trillium;
import dev._3000IQPlay.trillium.event.events.*;
import dev._3000IQPlay.trillium.modules.render.*;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.client.*;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.glu.Project;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import net.minecraft.client.entity.*;
import net.minecraftforge.common.*;
import net.minecraft.world.*;
import net.minecraft.entity.*;
import net.minecraft.block.state.*;
import net.minecraft.init.*;
import net.minecraft.client.renderer.*;
import net.minecraft.util.math.*;
import com.google.common.base.*;
import dev._3000IQPlay.trillium.modules.player.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.spongepowered.asm.mixin.injection.*;

import javax.annotation.Nullable;
import javax.vecmath.Vector3f;

@Mixin({ EntityRenderer.class })
public abstract class MixinEntityRenderer
{

    @Shadow
    private ItemStack itemActivationItem;
    @Shadow
    @Final
    private Minecraft mc;
    private boolean injection;
	@Shadow
    public Entity pointedEntity;

    public MixinEntityRenderer() {
        this.injection = true;
    }
	
	@Inject(method = "renderWorldPass", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;clear(I)V", ordinal = 1, shift = At.Shift.BEFORE))
    private void renderWorldPass(int pass, float partialTicks, long finishTimeNano, CallbackInfo ci) {
        if (Display.isActive() || Display.isVisible()) {
			PreRenderEvent render3dEventFirst = new PreRenderEvent(partialTicks);
            MinecraftForge.EVENT_BUS.post(render3dEventFirst);
            GlStateManager.disableTexture2D();
            GlStateManager.enableBlend();
            GlStateManager.disableAlpha();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.shadeModel(7425);
            GlStateManager.disableDepth();
            GlStateManager.glLineWidth(1.0F);
            PostRenderEvent render3dEvent = new PostRenderEvent(partialTicks);
            MinecraftForge.EVENT_BUS.post(render3dEvent);
            GlStateManager.glLineWidth(1.0F);
            GlStateManager.shadeModel(7424);
            GlStateManager.disableBlend();
            GlStateManager.enableAlpha();
            GlStateManager.enableTexture2D();
            GlStateManager.enableDepth();
            GlStateManager.enableCull();
            GlStateManager.enableCull();
            GlStateManager.depthMask(true);
            GlStateManager.enableTexture2D();
            GlStateManager.enableBlend();
            GlStateManager.enableDepth();
        }
    }

    @Inject(method = { "renderItemActivation" },  at = { @At("HEAD") },  cancellable = true)
    public void renderItemActivationHook(final CallbackInfo info) {
        if (this.itemActivationItem != null && NoRender.getInstance().isOn() && (boolean)NoRender.getInstance().totemPops.getValue() && this.itemActivationItem.getItem() == Items.TOTEM_OF_UNDYING) {
            info.cancel();
        }
    }

    @Shadow
    public int itemActivationTicks;

    @Shadow
    public float itemActivationOffX;

    @Shadow
    public float itemActivationOffY;

    @Overwrite
    public void renderItemActivation(int p_190563_1_, int p_190563_2_, float p_190563_3_) {
        if (this.itemActivationItem != null && NoRender.getInstance().isOn() && (boolean)NoRender.getInstance().totemPops.getValue() && this.itemActivationItem.getItem() == Items.TOTEM_OF_UNDYING) {
            return;
        }
        if (this.itemActivationItem != null && this.itemActivationTicks > 0) {
            int i = 40 - this.itemActivationTicks;
            float f = ((float)i + p_190563_3_) / 40.0F;
            float f1 = f * f;
            float f2 = f * f1;
            float f3 = 10.25F * f2 * f1 + -24.95F * f1 * f1 + 25.5F * f2 + -13.8F * f1 + 4.0F * f;
            float f4 = f3 * 3.1415927F;
            float f5 = this.itemActivationOffX * (float)(p_190563_1_ / 4);
            float f6 = this.itemActivationOffY * (float)(p_190563_2_ / 4);
            GlStateManager.enableAlpha();
            GlStateManager.pushMatrix();
            GlStateManager.pushAttrib();
            GlStateManager.enableDepth();
            GlStateManager.disableCull();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.translate((float)(p_190563_1_ / 2) + f5 * MathHelper.abs(MathHelper.sin(f4 * 2.0F)), (float)(p_190563_2_ / 2) + f6 * MathHelper.abs(MathHelper.sin(f4 * 2.0F)), -50.0F);
            float f7 = 50.0F + 175.0F * MathHelper.sin(f4);
            GlStateManager.scale(f7, -f7, f7);
            GlStateManager.rotate(900.0F * MathHelper.abs(MathHelper.sin(f4)), 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(6.0F * MathHelper.cos(f * 8.0F), 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(6.0F * MathHelper.cos(f * 8.0F), 0.0F, 0.0F, 1.0F);


            mc.getRenderItem().renderItem(this.itemActivationItem, ItemCameraTransforms.TransformType.FIXED);



            GlStateManager.popAttrib();
            GlStateManager.popMatrix();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.enableCull();
            GlStateManager.disableDepth();
        }
    }

    @Inject( method = "updateLightmap", at = @At( value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/DynamicTexture;updateDynamicTexture()V", shift = At.Shift.BEFORE ) )
    private void updateTextureHook(float partialTicks, CallbackInfo ci) {
        Ambience ambience = Trillium.moduleManager.getModuleByClass(Ambience.class);
        if (ambience.isEnabled()) {
            for (int i = 0; i < this.lightmapColors.length; ++i) {
                Color ambientColor = ambience.colorLight.getValue().getColorObject();
                int alpha = ambientColor.getAlpha();
                float modifier = ( float ) alpha / 255.0f;
                int color = this.lightmapColors[ i ];
                int[] bgr = toRGBAArray(color);
                Vector3f values = new Vector3f(( float ) bgr[ 2 ] / 255.0f, ( float ) bgr[ 1 ] / 255.0f, ( float ) bgr[ 0 ] / 255.0f);
                Vector3f newValues = new Vector3f(( float ) ambientColor.getRed() / 255.0f, ( float ) ambientColor.getGreen() / 255.0f, ( float ) ambientColor.getBlue() / 255.0f);
                Vector3f finalValues = mix(values, newValues, modifier);
                int red = ( int ) (finalValues.x * 255.0f);
                int green = ( int ) (finalValues.y * 255.0f);
                int blue = ( int ) (finalValues.z * 255.0f);
                this.lightmapColors[ i ] = 0xFF000000 | red << 16 | green << 8 | blue;
            }
        }
    }

    private int[] toRGBAArray(int colorBuffer) {
        return new int[] { colorBuffer >> 16 & 0xFF, colorBuffer >> 8 & 0xFF, colorBuffer & 0xFF };
    }

    private Vector3f mix(Vector3f first, Vector3f second, float factor) {
        return new Vector3f(first.x * (1.0f - factor) + second.x * factor, first.y * (1.0f - factor) + second.y * factor, first.z * (1.0f - factor) + first.z * factor);
    }

    @Redirect(method = { "setupCameraTransform" },  at = @At(value = "FIELD",  target = "Lnet/minecraft/client/entity/EntityPlayerSP;prevTimeInPortal:F"))
    public float prevTimeInPortalHook(final EntityPlayerSP entityPlayerSP) {
        if (NoRender.getInstance().isOn() && (NoRender.getInstance().nausea.getValue()) || NoRender.getInstance().portal.getValue()) {
            return -3.4028235E38f;
        }
        return entityPlayerSP.prevTimeInPortal;
    }
	
	@Redirect(method={"setupCameraTransform"}, at=@At(value="INVOKE", target="Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"))
    private void onSetupCameraTransform(float f, float f2, float f3, float f4) {
        PerspectiveEvent perspectiveEvent = new PerspectiveEvent((float)this.mc.displayWidth / (float)this.mc.displayHeight);
        MinecraftForge.EVENT_BUS.post((Event)perspectiveEvent);
        Project.gluPerspective((float)f, (float)perspectiveEvent.getAspect(), (float)f3, (float)f4);
    }

    @Redirect(method={"renderWorldPass"}, at=@At(value="INVOKE", target="Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"))
    private void onRenderWorldPass(float f, float f2, float f3, float f4) {
        PerspectiveEvent perspectiveEvent = new PerspectiveEvent((float)this.mc.displayWidth / (float)this.mc.displayHeight);
        MinecraftForge.EVENT_BUS.post((Event)perspectiveEvent);
        Project.gluPerspective((float)f, (float)perspectiveEvent.getAspect(), (float)f3, (float)f4);
    }

    @Redirect(method={"renderCloudsCheck"}, at=@At(value="INVOKE", target="Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"))
    private void onRenderCloudsCheck(float f, float f2, float f3, float f4) {
        PerspectiveEvent perspectiveEvent = new PerspectiveEvent((float)this.mc.displayWidth / (float)this.mc.displayHeight);
        MinecraftForge.EVENT_BUS.post((Event)perspectiveEvent);
        Project.gluPerspective((float)f, (float)perspectiveEvent.getAspect(), (float)f3, (float)f4);
    }

    @Inject(method = { "setupFog" },  at = { @At("HEAD") },  cancellable = true)
    public void setupFogHook(final int startCoords,  final float partialTicks,  final CallbackInfo info) {
        if (NoRender.getInstance().isOn() && NoRender.getInstance().fog.getValue() == NoRender.Fog.NOFOG) {
            info.cancel();
        }
    }

    @Redirect(method = { "setupFog" },  at = @At(value = "INVOKE",  target = "Lnet/minecraft/client/renderer/ActiveRenderInfo;getBlockStateAtEntityViewpoint(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;F)Lnet/minecraft/block/state/IBlockState;"))
    public IBlockState getBlockStateAtEntityViewpointHook(final World worldIn,  final Entity entityIn,  final float p_186703_2_) {
        if (NoRender.getInstance().isOn() && NoRender.getInstance().fog.getValue() == NoRender.Fog.AIR) {
            return Blocks.AIR.defaultBlockState;
        }
        return ActiveRenderInfo.getBlockStateAtEntityViewpoint(worldIn,  entityIn,  p_186703_2_);
    }

    @Inject(method = { "hurtCameraEffect" },  at = { @At("HEAD") },  cancellable = true)
    public void hurtCameraEffectHook(final float ticks,  final CallbackInfo info) {
        if (NoRender.getInstance().isOn() && (boolean)NoRender.getInstance().hurtcam.getValue()) {
            info.cancel();
        }
    }
	
    @Inject(method = "renderHand", at = @At("HEAD"), cancellable = true)
    public void renderHandMain(float partialTicks, int pass, CallbackInfo ci) {
        ItemShaders module = Trillium.moduleManager.getModuleByClass(ItemShaders.class);
        if (module.isEnabled()) {
            Minecraft mc = Minecraft.getMinecraft();
            if (!module.cancelItem.getValue()) {
                doRenderHand(partialTicks, pass, mc);
            }
            if (!(module.glowESP.getValue()== ItemShaders.glowESPmode.None) && !(module.fillShader.getValue() == ItemShaders.fillShadermode.None)) {
                GlStateManager.pushMatrix();
                RenderHand.PreBoth hand = new RenderHand.PreBoth(partialTicks);
                MinecraftForge.EVENT_BUS.post(hand);
                doRenderHand(partialTicks, pass, mc);
                RenderHand.PostBoth hand2 = new RenderHand.PostBoth(partialTicks);
                MinecraftForge.EVENT_BUS.post(hand2);
                GlStateManager.popMatrix();
            }
            if (!(module.glowESP.getValue() == ItemShaders.glowESPmode.None)) {
                GlStateManager.pushMatrix();
                RenderHand.PreOutline hand = new RenderHand.PreOutline(partialTicks);
                MinecraftForge.EVENT_BUS.post(hand);
                doRenderHand(partialTicks, pass, mc);
                RenderHand.PostOutline hand2 = new RenderHand.PostOutline(partialTicks);
                MinecraftForge.EVENT_BUS.post(hand2);
                GlStateManager.popMatrix();
            }
            if (!(module.fillShader.getValue() == ItemShaders.fillShadermode.None)) {
                GlStateManager.pushMatrix();
                RenderHand.PreFill hand = new RenderHand.PreFill(partialTicks);
                MinecraftForge.EVENT_BUS.post(hand);
                doRenderHand(partialTicks, pass, mc);
                RenderHand.PostFill hand2 = new RenderHand.PostFill(partialTicks);
                MinecraftForge.EVENT_BUS.post(hand2);
                GlStateManager.popMatrix();
            }
            ci.cancel();
        }
    }
	
	@Overwrite
    public void getMouseOver(float partialTicks) {
        BackTrack bt = Trillium.moduleManager.getModuleByClass(BackTrack.class);
        Entity entity = mc.getRenderViewEntity();
        if (entity != null && mc.world != null) {
            mc.profiler.startSection("pick");
            mc.pointedEntity = null;
            double d0 = (double) mc.playerController.getBlockReachDistance();
                mc.objectMouseOver = entity.rayTrace(d0, partialTicks);
                Vec3d vec3d = entity.getPositionEyes(partialTicks);
                boolean flag = false;
                double d1 = d0;
                if (mc.playerController.extendedReach()) {
                    d1 = 6.0;
                    d0 = d1;
                } else if (d0 > 3.0) {
                    flag = true;
                }
                if (mc.objectMouseOver != null) {
                    d1 = mc.objectMouseOver.hitVec.distanceTo(vec3d);
                }
                Vec3d vec3d1 = entity.getLook(1.0F);
                Vec3d vec3d2 = vec3d.add(vec3d1.x * d0, vec3d1.y * d0, vec3d1.z * d0);
                pointedEntity = null;
                Vec3d vec3d3 = null;
                float f = 1.0F;
                List<Entity> list = mc.world.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().expand(vec3d1.x * d0, vec3d1.y * d0, vec3d1.z * d0).grow(1.0, 1.0, 1.0), Predicates.and(EntitySelectors.NOT_SPECTATING, new Predicate<Entity>() {
                    public boolean apply(@Nullable Entity p_apply_1_) {
                        return p_apply_1_ != null && p_apply_1_.canBeCollidedWith();
                    }
                }));
                double d2 = d1;
                for (Entity value : list) {
                    AxisAlignedBB axisalignedbb = value.getEntityBoundingBox().grow((double) value.getCollisionBorderSize());
                    RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(vec3d, vec3d2);
                    if (axisalignedbb.contains(vec3d)) {
                        if (d2 >= 0.0) {
                            pointedEntity = value;
                            vec3d3 = raytraceresult == null ? vec3d : raytraceresult.hitVec;
                            d2 = 0.0;
                        }
                    } else if (raytraceresult != null) {
                        double d3 = vec3d.distanceTo(raytraceresult.hitVec);
                        if (d3 < d2 || d2 == 0.0) {
                            if (value.getLowestRidingEntity() == entity.getLowestRidingEntity() && !value.canRiderInteract()) {
                                if (d2 == 0.0) {
                                    pointedEntity = value;
                                    vec3d3 = raytraceresult.hitVec;
                                }
                            } else {
                                pointedEntity = value;
                                vec3d3 = raytraceresult.hitVec;
                                d2 = d3;
                            }
                        }
                    }
                }
                if (pointedEntity != null && flag && vec3d.distanceTo(vec3d3) > 3.0) {
                    pointedEntity = null;
                    mc.objectMouseOver = new RayTraceResult(RayTraceResult.Type.MISS, vec3d3, null, new BlockPos(vec3d3));
                }
                if (pointedEntity != null && (d2 < d1 || mc.objectMouseOver == null)) {
                    mc.objectMouseOver = new RayTraceResult(pointedEntity, vec3d3);
                    if (pointedEntity instanceof EntityLivingBase || pointedEntity instanceof EntityItemFrame) {
                        mc.pointedEntity = pointedEntity;
                    }
                }
                if (pointedEntity == null && bt.isOn()) {
                    for (EntityPlayer pl_box : mc.world.playerEntities) {
                        if (pl_box == mc.player) {
                            continue;
                        }
                        List<BackTrack.Box> trails22 = new ArrayList<>();
                        bt.entAndTrail.putIfAbsent(pl_box, trails22);
                        if (bt.entAndTrail.get(pl_box).size() > 0) {
                            for (int i = 0; i < bt.entAndTrail.get(pl_box).size(); i++) {
                                AxisAlignedBB axisalignedbb = new AxisAlignedBB(
                                        Trillium.moduleManager.getModuleByClass(BackTrack.class).entAndTrail.get(pl_box).get(i).getPosition().x - 0.3,
                                        Trillium.moduleManager.getModuleByClass(BackTrack.class).entAndTrail.get(pl_box).get(i).getPosition().y,
                                        Trillium.moduleManager.getModuleByClass(BackTrack.class).entAndTrail.get(pl_box).get(i).getPosition().z - 0.3,
                                        Trillium.moduleManager.getModuleByClass(BackTrack.class).entAndTrail.get(pl_box).get(i).getPosition().x + 0.3,
                                        Trillium.moduleManager.getModuleByClass(BackTrack.class).entAndTrail.get(pl_box).get(i).getPosition().y + 1.8,
                                        Trillium.moduleManager.getModuleByClass(BackTrack.class).entAndTrail.get(pl_box).get(i).getPosition().z + 0.3);

                                RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(vec3d, vec3d2);
                                if (axisalignedbb.contains(vec3d)) {
                                    if (d2 >= 0.0) {
                                        pointedEntity = pl_box;
                                        vec3d3 = raytraceresult == null ? vec3d : raytraceresult.hitVec;
                                        d2 = 0.0;
                                        if (raytraceresult != null) {
                                            mc.objectMouseOver = raytraceresult;
                                        }
                                    }
                                } else if (raytraceresult != null) {
                                    double d3 = vec3d.distanceTo(raytraceresult.hitVec);
                                    if (d3 < d2 || d2 == 0.0) {
                                        if (pl_box.getLowestRidingEntity() == entity.getLowestRidingEntity() && !pl_box.canRiderInteract()) {
                                            if (d2 == 0.0) {
                                                pointedEntity = pl_box;
                                            }
                                        } else {
                                            pointedEntity = pl_box;
                                            d2 = d3;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (pointedEntity != null) {
                        mc.objectMouseOver = new RayTraceResult(pointedEntity);
                    }
                }
            mc.profiler.endSection();
        }
    }

    @Shadow
    public
    boolean debugView;

    @Shadow
    public abstract float getFOVModifier(float partialTicks, boolean useFOVSetting);

    @Shadow
    public abstract void hurtCameraEffect(float partialTicks);

    @Shadow
    public abstract void applyBobbing(float partialTicks);

    @Shadow
    public abstract void enableLightmap();

    @Shadow
    public float farPlaneDistance;

    @Final
    @Shadow
    public ItemRenderer itemRenderer;

    @Shadow
    public abstract void disableLightmap();

    void doRenderHand(float partialTicks, int pass, Minecraft mc) {
        if (!this.debugView)
        {
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            float f = 0.07F;

            if (mc.gameSettings.anaglyph)
            {
                GlStateManager.translate((float)(-(pass * 2 - 1)) * 0.07F, 0.0F, 0.0F);
            }

            Project.gluPerspective(this.getFOVModifier(partialTicks, false), (float)mc.displayWidth / (float)mc.displayHeight, 0.05F, this.farPlaneDistance * 2.0F);
            GlStateManager.matrixMode(5888);
            GlStateManager.loadIdentity();

            if (mc.gameSettings.anaglyph)
            {
                GlStateManager.translate((float)(pass * 2 - 1) * 0.1F, 0.0F, 0.0F);
            }

            GlStateManager.pushMatrix();
            this.hurtCameraEffect(partialTicks);

            if (mc.gameSettings.viewBobbing)
            {
                this.applyBobbing(partialTicks);
            }

            boolean flag = mc.getRenderViewEntity() instanceof EntityLivingBase && ((EntityLivingBase)mc.getRenderViewEntity()).isPlayerSleeping();

            if (!net.minecraftforge.client.ForgeHooksClient.renderFirstPersonHand(mc.renderGlobal, partialTicks, pass))
                if (mc.gameSettings.thirdPersonView == 0 && !flag && !mc.gameSettings.hideGUI && !mc.playerController.isSpectator())
                {
                    this.enableLightmap();
                    this.itemRenderer.renderItemInFirstPerson(partialTicks);
                    this.disableLightmap();
                }

            GlStateManager.popMatrix();

            if (mc.gameSettings.thirdPersonView == 0 && !flag)
            {
                this.itemRenderer.renderOverlays(partialTicks);
                this.hurtCameraEffect(partialTicks);
            }

            if (mc.gameSettings.viewBobbing)
            {
                this.applyBobbing(partialTicks);
            }
        }
    }
    @Shadow
    @Final
    private int[] lightmapColors;

    @Shadow public abstract void renderHand(float partialTicks, int pass);

    @Shadow
    public float thirdPersonDistancePrev;

    @Shadow
    public boolean cloudFog;

    @Overwrite
    public void orientCamera(float partialTicks) {
        Entity entity = this.mc.getRenderViewEntity();
        float f = entity.getEyeHeight();
        double d0 = entity.prevPosX + (entity.posX - entity.prevPosX) * (double)partialTicks;
        double d1 = entity.prevPosY + (entity.posY - entity.prevPosY) * (double)partialTicks + (double)f;
        double d2 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double)partialTicks;
        float f1;
        if (entity instanceof EntityLivingBase && ((EntityLivingBase)entity).isPlayerSleeping()) {
            f = (float)((double)f + 1.0D);
            GlStateManager.translate(0.0F, 0.3F, 0.0F);
            if (!this.mc.gameSettings.debugCamEnable) {
                BlockPos blockpos = new BlockPos(entity);
                IBlockState iblockstate = this.mc.world.getBlockState(blockpos);
                ForgeHooksClient.orientBedCamera(this.mc.world, blockpos, iblockstate, entity);
                GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks + 180.0F, 0.0F, -1.0F, 0.0F);
                GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, -1.0F, 0.0F, 0.0F);
            }
        } else if (this.mc.gameSettings.thirdPersonView > 0) {
            double d3 = (double)(this.thirdPersonDistancePrev + (4.0F - this.thirdPersonDistancePrev) * partialTicks);
            if (this.mc.gameSettings.debugCamEnable) {
                GlStateManager.translate(0.0F, 0.0F, (float)(-d3));
            } else {
                float f2;
                f1 = entity.rotationYaw;
                f2 = entity.rotationPitch;
					
                if (this.mc.gameSettings.thirdPersonView == 2) {
                    f2 += 180.0F;
                }

                double d4 = (double)(-MathHelper.sin(f1 * 0.017453292F) * MathHelper.cos(f2 * 0.017453292F)) * d3;
                double d5 = (double)(MathHelper.cos(f1 * 0.017453292F) * MathHelper.cos(f2 * 0.017453292F)) * d3;
                double d6 = (double)(-MathHelper.sin(f2 * 0.017453292F)) * d3;

                for(int i = 0; i < 8; ++i) {
                    float f3 = (float)((i & 1) * 2 - 1);
                    float f4 = (float)((i >> 1 & 1) * 2 - 1);
                    float f5 = (float)((i >> 2 & 1) * 2 - 1);
                    f3 *= 0.1F;
                    f4 *= 0.1F;
                    f5 *= 0.1F;
                    RayTraceResult raytraceresult = this.mc.world.rayTraceBlocks(new Vec3d(d0 + (double)f3, d1 + (double)f4, d2 + (double)f5), new Vec3d(d0 - d4 + (double)f3 + (double)f5, d1 - d6 + (double)f4, d2 - d5 + (double)f5));
                    if (raytraceresult != null) {
                        double d7 = raytraceresult.hitVec.distanceTo(new Vec3d(d0, d1, d2));
                        if (d7 < d3) {
                            d3 = d7;
                        }
                    }
                }

                if (this.mc.gameSettings.thirdPersonView == 2) {
                    GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                }

                GlStateManager.rotate(entity.rotationPitch - f2, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(entity.rotationYaw - f1, 0.0F, 1.0F, 0.0F);
                GlStateManager.translate(0.0F, 0.0F, (float)(-d3));
                GlStateManager.rotate(f1 - entity.rotationYaw, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(f2 - entity.rotationPitch, 1.0F, 0.0F, 0.0F);
            }
        } else {
            GlStateManager.translate(0.0F, 0.0F, 0.05F);
        }

        if (!this.mc.gameSettings.debugCamEnable) {
            float yaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks + 180.0F;
            float pitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
            f1 = 0.0F;
            if (entity instanceof EntityAnimal) {
                EntityAnimal entityanimal = (EntityAnimal)entity;
                yaw = entityanimal.prevRotationYawHead + (entityanimal.rotationYawHead - entityanimal.prevRotationYawHead) * partialTicks + 180.0F;
            }

            IBlockState state = ActiveRenderInfo.getBlockStateAtEntityViewpoint(this.mc.world, entity, partialTicks);
            EntityViewRenderEvent.CameraSetup event = new EntityViewRenderEvent.CameraSetup(mc.entityRenderer, entity, state, (double)partialTicks, yaw, pitch, f1);
            MinecraftForge.EVENT_BUS.post(event);
            GlStateManager.rotate(event.getRoll(), 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(event.getPitch(), 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(event.getYaw(), 0.0F, 1.0F, 0.0F);
        }

        GlStateManager.translate(0.0F, -f, 0.0F);
        d0 = entity.prevPosX + (entity.posX - entity.prevPosX) * (double)partialTicks;
        d1 = entity.prevPosY + (entity.posY - entity.prevPosY) * (double)partialTicks + (double)f;
        d2 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double)partialTicks;
        cloudFog = this.mc.renderGlobal.hasCloudFog(d0, d1, d2, partialTicks);
    }
}