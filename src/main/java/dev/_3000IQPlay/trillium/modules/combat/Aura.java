package dev._3000IQPlay.trillium.modules.combat;

import dev._3000IQPlay.trillium.Trillium;
import dev._3000IQPlay.trillium.command.Command;
import dev._3000IQPlay.trillium.event.events.EventPostMotion;
import dev._3000IQPlay.trillium.event.events.EventPreMotion;
import dev._3000IQPlay.trillium.event.events.Render3DEvent;
import dev._3000IQPlay.trillium.mixin.mixins.IEntityRenderer;
import dev._3000IQPlay.trillium.mixin.mixins.IRenderManager;
import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.modules.render.BackTrack;
import dev._3000IQPlay.trillium.setting.ColorSetting;
import dev._3000IQPlay.trillium.setting.Setting;
import dev._3000IQPlay.trillium.util.*;
import dev._3000IQPlay.trillium.util.Timer;
import dev._3000IQPlay.trillium.util.phobos.IEntityLivingBase;
import dev._3000IQPlay.trillium.util.rotations.CastHelper;
import dev._3000IQPlay.trillium.util.rotations.RayTracingUtils;
import dev._3000IQPlay.trillium.util.rotations.ResolverUtil;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.*;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import javax.vecmath.Vector2f;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;

import static dev._3000IQPlay.trillium.util.MovementUtil.isMoving;
import static dev._3000IQPlay.trillium.util.MovementUtil.strafe;
import static net.minecraft.util.math.MathHelper.clamp;
import static net.minecraft.util.math.MathHelper.wrapDegrees;


public class Aura extends Module {

    public Aura() {
        super("KillAura", "Kills kids in your range", Module.Category.COMBAT, true, false, false);
    }
	
	public enum Page {
        AntiCheat, Exploits, Misc, Targets, Visuals;
    }

    public enum rotmod {
        None, NCP, AAC, Matrix, Matrix2, Matrix3;
    }
    public enum CritMode {
        WexSide, Simple;
    }
    public enum AutoSwitch {
        None, Default;
    }
    public enum PointsMode {
        Distance, Angle
    }
    public enum TimingMode {
        Default, Old
    }
    public enum RayTracingMode {
        NewJitter, New, Old, OldJitter
    }
	
	public final Setting<Page> page = this.register(new Setting<>("Page", Page.AntiCheat));
	
    /*-------------   AntiCheat  ----------*/
    public final Setting<Float> attackDistance = this.register(new Setting("AttackDistance", 3.4f, 0.0f, 7.0f, v -> this.page.getValue() == Page.AntiCheat));
	public final Setting<Float> rotateDistance = this.register(new Setting("RotateDistance", 0f, 0f, 5f, v -> this.rotation.getValue() != rotmod.None && this.page.getValue() == Page.AntiCheat));
    private final Setting<rotmod> rotation = this.register(new Setting("Rotation", rotmod.None, v -> this.page.getValue() == Page.AntiCheat));
    public final Setting<RayTracingMode> rayTracing = this.register(new Setting("RayTracing", RayTracingMode.NewJitter, v -> this.page.getValue() == Page.AntiCheat));
    public final Setting<PointsMode> pointsMode = this.register(new Setting("PointsSort", PointsMode.Distance, v -> this.page.getValue() == Page.AntiCheat));
    public final Setting<TimingMode> timingMode = this.register(new Setting("Timing", TimingMode.Default, v -> this.page.getValue() == Page.AntiCheat));
    public final Setting<Boolean> rtx = this.register(new Setting<>("RTX", true, v -> this.page.getValue() == Page.AntiCheat));
    public final Setting<Integer> minCPS = this.register(new Setting("MinCPS", 10, 1, 20, v -> timingMode.getValue() == TimingMode.Old && this.page.getValue() == Page.AntiCheat));
    public final Setting<Integer> maxCPS = this.register(new Setting("MaxCPS", 12, 1, 20, v -> timingMode.getValue() == TimingMode.Old && this.page.getValue() == Page.AntiCheat));
    public final Setting<Float> walldistance = this.register(new Setting("WallDistance", 3.6f, 0.0f, 7.0f, v -> this.page.getValue() == Page.AntiCheat));
    public final Setting<Integer> fov = this.register(new Setting("FOV", 180, 5, 180, v -> this.page.getValue() == Page.AntiCheat));
    public final Setting<Integer> yawStep = this.register(new Setting("YawStep", 80, 5, 180, v-> rotation.getValue() == rotmod.Matrix && this.page.getValue() == Page.AntiCheat));
    public final Setting<Float> hitboxScale = this.register(new Setting("HitBoxScale", 2.8f, 0.0f, 3.0f, v -> this.page.getValue() == Page.AntiCheat));
    /*-------------------------------------*/


    /*------------   Exploits  ------------*/
    public final Setting<Boolean> resolver = this.register(new Setting<>("Resolver", false, v -> this.page.getValue() == Page.Exploits));
    public final Setting<Boolean> shieldDesync = this.register(new Setting<>("Shield Desync", false, v -> this.page.getValue() == Page.Exploits));
    public final Setting<Boolean> backTrack = this.register(new Setting<>("RotateToBackTrack", true, v -> this.page.getValue() == Page.Exploits));
    public final Setting<Boolean> shiftTap = this.register(new Setting<>("ShiftTap", false, v -> this.page.getValue() == Page.Exploits));

    /*-------------------------------------*/




    /*-------------   Misc  ---------------*/
    public final Setting<Boolean> criticals = this.register(new Setting<>("OnlyCrits", true, v -> this.page.getValue() == Page.Misc));
    public final Setting<CritMode> critMode = this.register(new Setting("CritMode", CritMode.WexSide,v -> criticals.getValue() && this.page.getValue() == Page.Misc));
    public final Setting<Float> critdist = this.register(new Setting("FallDistance", 0.15f, 0.0f, 1.0f,v -> criticals.getValue() && critMode.getValue() == CritMode.Simple && this.page.getValue() == Page.Misc));
    public final Setting<Boolean> criticals_autojump = this.register(new Setting<>("AutoJump", false,v-> criticals.getValue() && this.page.getValue() == Page.Misc));
    public final Setting<Boolean> smartCrit = this.register(new Setting<>("SpaceOnly", true,v-> criticals.getValue() && this.page.getValue() == Page.Misc));
    public final Setting<Boolean> watercrits = this.register(new Setting<>("WaterCrits", false,v-> criticals.getValue() && this.page.getValue() == Page.Misc));
    public final Setting<Boolean> weaponOnly = this.register(new Setting<>("WeaponOnly", true, v -> this.page.getValue() == Page.Misc));
    public final Setting<AutoSwitch> autoswitch = this.register(new Setting("AutoSwitch", AutoSwitch.None, v -> this.page.getValue() == Page.Misc));
    public final Setting<Boolean> firstAxe = this.register(new Setting<>("FirstAxe", false,v -> autoswitch.getValue() != AutoSwitch.None && this.page.getValue() == Page.Misc));
    public final Setting<Boolean> shieldDesyncOnlyOnAura = this.register(new Setting<>("Wait Target", true, v->shieldDesync.getValue() && this.page.getValue() == Page.Misc));
    public final Setting<Boolean> clientLook = this.register(new Setting<>("ClientLook", false, v -> this.page.getValue() == Page.Misc));
    public final Setting<Boolean> snap = this.register(new Setting<>("Snap", false, v -> this.page.getValue() == Page.Misc));
    public final Setting<Boolean> shieldBreaker = this.register(new Setting<>("ShieldBreaker", true, v -> this.page.getValue() == Page.Misc));
    public final Setting<Boolean> offhand = this.register(new Setting<>("OffHandAttack", false, v -> this.page.getValue() == Page.Misc));
    public final Setting<Boolean> teleport = this.register(new Setting<>("TP", false, v -> this.page.getValue() == Page.Misc));
    public final Setting<Float> tpY = this.register(new Setting("TPY", 3f, -5.0f, 5.0f,v-> teleport.getValue() && this.page.getValue() == Page.Misc));
    public final Setting<Boolean> Debug = this.register(new Setting<>("HitsDebug", false, v -> this.page.getValue() == Page.Misc));
    /*-------------------------------------*/



    /*-------------   Targets  ------------*/
    public final Setting<Boolean> Playersss = this.register(new Setting<>("Players", true, v -> this.page.getValue() == Page.Targets));
    public final Setting<Boolean> Mobsss = this.register(new Setting<>("Mobs", true, v -> this.page.getValue() == Page.Targets));
    public final Setting<Boolean> Animalsss = this.register(new Setting<>("Animals", true, v -> this.page.getValue() == Page.Targets));
    public final Setting<Boolean> Villagersss = this.register(new Setting<>("Villagers", true, v -> this.page.getValue() == Page.Targets));
    public final Setting<Boolean> Slimesss = this.register(new Setting<>("Slimes", true, v -> this.page.getValue() == Page.Targets));
    public final Setting<Boolean> ignoreNaked = this.register(new Setting<>("IgnoreNaked", false, v -> this.page.getValue() == Page.Targets));
    public final Setting<Boolean> ignoreInvisible = this.register(new Setting<>("IgnoreInvis", false, v -> this.page.getValue() == Page.Targets));
    public final Setting<Boolean> ignoreCreativ = this.register(new Setting<>("IgnoreCreativ", true, v -> this.page.getValue() == Page.Targets));
    /*-------------------------------------*/


    /*-------------   Visual  -------------*/
    public final Setting<Boolean> RTXVisual = this.register(new Setting<>("RTXVisual", false, v -> this.page.getValue() == Page.Visuals));
    public final Setting<Boolean> targetesp = this.register(new Setting<>("TargetCircle", true, v -> this.page.getValue() == Page.Visuals));//(visual);
    /*-------------------------------------*/


    public static EntityLivingBase target;
	public static AstolfoAnimation astolfo = new AstolfoAnimation();
    private float prevCircleStep, circleStep, prevAdditionYaw;
    private final Timer oldTimer = new Timer();
    private final Timer hitttimer = new Timer();
    private boolean swappedToAxe, swapBack, rotatedBefore;
    public static BackTrack.Box bestBtBox;
    public static int CPSLimit;
    private Vec3d last_best_vec;
    private float rotation_smoother;


    @SubscribeEvent
    public void onRotate(EventPreMotion e){
        if(firstAxe.getValue() && hitttimer.passedMs(3000) && InventoryUtil.getBestAxe() != -1){
            if(autoswitch.getValue() == AutoSwitch.Default){
                mc.player.inventory.currentItem = InventoryUtil.getBestAxe();
                swappedToAxe = true;
            }
        } else {
            if(autoswitch.getValue() == AutoSwitch.Default){
                if(InventoryUtil.getBestSword() != -1){
                    mc.player.inventory.currentItem = InventoryUtil.getBestSword();
                } else if(InventoryUtil.getBestAxe() != -1){
                    mc.player.inventory.currentItem = InventoryUtil.getBestAxe();
                }
            }
        }


        if (CPSLimit > 0) CPSLimit--;

        boolean shieldDesyncActive = shieldDesync.getValue();
        if (shieldDesyncOnlyOnAura.getValue() && target == null) {
            shieldDesyncActive = false;
        }
        if (isActiveItemStackBlocking(mc.player, 4 + new Random().nextInt(4)) && shieldDesyncActive && mc.player.isHandActive()) {
            mc.playerController.onStoppedUsingItem(mc.player);
        }
        if (target != null) {
            if(target instanceof EntityOtherPlayerMP && resolver.getValue()){
                ResolverUtil.resolve((EntityOtherPlayerMP) target);
            }
            if (!isEntityValid(target, false)) {
                target = null;
                ResolverUtil.reset();
            }
        }

        if (target == null) {
            ResolverUtil.reset();
            target = findTarget();
        }

        if (target == null || mc.player.getDistanceSq(target) > attackDistance.getValue()) {
            BackTrack bt = Trillium.moduleManager.getModuleByClass(BackTrack.class);
            if(bt.isOn() && backTrack.getValue()){
                float best_distance = 100;
                for(EntityPlayer BTtarget : mc.world.playerEntities) {
                    if(mc.player.getDistanceSq(BTtarget) > 100) continue;
                    if(!isEntityValid(BTtarget, true)) continue;
                    if(bt.entAndTrail.get(BTtarget) == null) continue;
                    if(bt.entAndTrail.get(BTtarget).size() == 0) continue;
                    for (BackTrack.Box box : bt.entAndTrail.get(BTtarget)) {
                        if(getDistanceBT(box) < best_distance){
                            best_distance = getDistanceBT(box);
                            if(target != null && best_distance < mc.player.getDistanceSq(target)){
                                target = BTtarget;
                            } else if(target == null && best_distance < attackDistance.getValue()){
                                target = BTtarget;
                            }
                        }
                    }
                }
            }
        }

        if (target == null){
            return;
        }

        if (weaponOnly.getValue() && !(mc.player.getHeldItemMainhand().getItem() instanceof ItemSword || mc.player.getHeldItemMainhand().getItem() instanceof ItemAxe)) {
            return;
        }
        rotatedBefore = false;
        attack(target);
        if (!rotatedBefore) {
            rotate(target, false);
        }
        if(target != null && resolver.getValue()){
            if(target instanceof EntityOtherPlayerMP){
                ResolverUtil.releaseResolver((EntityOtherPlayerMP) target);
            }
        }
    }


    @Override
    public void onUpdate() {
		astolfo.update();
        if (mc.player.onGround && !isInLiquid() && !mc.player.isOnLadder() && !mc.player.isInWeb && !mc.player.isPotionActive(MobEffects.SLOWNESS) && target != null && criticals_autojump.getValue()) {
            mc.player.jump();
        }
        if (targetesp.getValue()) {
            prevCircleStep = circleStep;
            circleStep += 0.15;
        }
        if(target != null) {
            if(snap.getValue()){
                if(hitttimer.getPassedTimeMs() < 100){
                    mc.player.rotationPitch = Trillium.rotationManager.getServerPitch();
                    mc.player.rotationYaw = Trillium.rotationManager.getServerYaw();
                }
            }
        }
    }

    public static boolean isInLiquid() {
        return mc.player.isInWater() || mc.player.isInLava();
    }

    public static double absSinAnimation(double input) {
        return Math.abs(1 + Math.sin(input)) / 2;
    }


    @SubscribeEvent
    public void onRender3D(Render3DEvent e){
        if (targetesp.getValue()) {
            EntityLivingBase entity = Aura.target;
            if (entity != null) {
                double cs = prevCircleStep + (circleStep - prevCircleStep) * mc.getRenderPartialTicks();
                double prevSinAnim = absSinAnimation(cs - 0.15);
                double sinAnim = absSinAnimation(cs);
                double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.getRenderPartialTicks() - mc.getRenderManager().renderPosX;
                double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.getRenderPartialTicks() - mc.getRenderManager().renderPosY + prevSinAnim * 1.4f;
                double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.getRenderPartialTicks() - mc.getRenderManager().renderPosZ;
                double nextY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.getRenderPartialTicks() - mc.getRenderManager().renderPosY + sinAnim * 1.4f;

                GL11.glPushMatrix();

                boolean cullface = GL11.glIsEnabled(GL11.GL_CULL_FACE);
                boolean texture = GL11.glIsEnabled(GL11.GL_TEXTURE_2D);
                boolean blend = GL11.glIsEnabled(GL11.GL_BLEND);
                boolean depth = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
                boolean alpha = GL11.glIsEnabled(GL11.GL_ALPHA_TEST);


                GL11.glDisable(GL11.GL_CULL_FACE);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                GL11.glDisable(GL11.GL_ALPHA_TEST);

                GL11.glShadeModel(GL11.GL_SMOOTH);

                GL11.glBegin(GL11.GL_QUAD_STRIP);
                for (int i = 0; i <= 360; i++) {
				    double stage = (i + 90) / 360.;
                    int clr = astolfo.getColor(stage);
                    int red = ((clr >> 16) & 255);
                    int green = ((clr >> 8) & 255);
                    int blue = ((clr & 255));
                    GL11.glColor4f(red / 255f, green / 255f, blue / 255f, 0.8f);
                    GL11.glVertex3d(x + Math.cos(Math.toRadians(i)) * entity.width * 0.8, nextY, z + Math.sin(Math.toRadians(i)) * entity.width * 0.8);
                    GL11.glColor4f(red / 255f, green / 255f, blue / 255f, 0.21f);
                    GL11.glVertex3d(x + Math.cos(Math.toRadians(i)) * entity.width * 0.8, y, z + Math.sin(Math.toRadians(i)) * entity.width * 0.8);
                }

                GL11.glEnd();
                GL11.glEnable(GL11.GL_LINE_SMOOTH);
                GL11.glBegin(GL11.GL_LINE_LOOP);
                for (int i = 0; i <= 360; i++) {
			    	double stage = (i + 90) / 360.;
                    int clr = astolfo.getColor(stage);
                    int red = ((clr >> 16) & 255);
                    int green = ((clr >> 8) & 255);
                    int blue = ((clr & 255));
                    GL11.glColor4f(red / 255f, green / 255f, blue / 255f, 1.0f);
                    GL11.glVertex3d(x + Math.cos(Math.toRadians(i)) * entity.width * 0.8, nextY, z + Math.sin(Math.toRadians(i)) * entity.width * 0.8);
                }
                GL11.glEnd();

                if(!cullface)
                    GL11.glDisable(GL11.GL_LINE_SMOOTH);

                if(texture)
                    GL11.glEnable(GL11.GL_TEXTURE_2D);


                if(depth)
                    GL11.glEnable(GL11.GL_DEPTH_TEST);

                GL11.glShadeModel(GL11.GL_FLAT);

                if(!blend)
                    GL11.glDisable(GL11.GL_BLEND);
                if(cullface)
                    GL11.glEnable(GL11.GL_CULL_FACE);
                if(alpha)
                    GL11.glEnable(GL11.GL_ALPHA_TEST);
                GL11.glPopMatrix();
                GlStateManager.resetColor();


                if(RTXVisual.getValue()){
                    GlStateManager.pushMatrix();
                    Vec3d eyes = new Vec3d(0, 0, 1).rotatePitch(-(float) Math.toRadians(mc.player.rotationPitch)).rotateYaw(-(float) Math.toRadians(mc.player.rotationYaw));
                    for(Vec3d point : RayTracingUtils.getHitBoxPoints(target.getPositionVector(),hitboxScale.getValue()/10f)){
                        if(!isPointVisible(target, point, (attackDistance.getValue() + rotateDistance.getValue()))){
                            Aura.renderTracer(eyes.x, eyes.y + mc.player.getEyeHeight(), eyes.z, point.x - ((IRenderManager) mc.getRenderManager()).getRenderPosX(), point.y - ((IRenderManager) mc.getRenderManager()).getRenderPosY(), point.z - ((IRenderManager) mc.getRenderManager()).getRenderPosZ(), new Color(0xEA6E6E6E, true).getRGB());
                        } else {
                            Aura.renderTracer(eyes.x, eyes.y + mc.player.getEyeHeight(), eyes.z, point.x - ((IRenderManager) mc.getRenderManager()).getRenderPosX(), point.y - ((IRenderManager) mc.getRenderManager()).getRenderPosY(), point.z - ((IRenderManager) mc.getRenderManager()).getRenderPosZ(), new Color(0xC800802D, true).getRGB());
                        }
                    }
                    if (last_best_vec != null){
                        Aura.renderTracer(eyes.x, eyes.y + mc.player.getEyeHeight(), eyes.z, last_best_vec.x - ((IRenderManager) mc.getRenderManager()).getRenderPosX(), last_best_vec.y - ((IRenderManager) mc.getRenderManager()).getRenderPosY(), last_best_vec.z - ((IRenderManager) mc.getRenderManager()).getRenderPosZ(), new Color(0xEA00FF58, true).getRGB());
                    }
                    GlStateManager.popMatrix();
                    GlStateManager.resetColor();
                }
            }
        }
    }

    @Override
    public void onDisable(){
        target = null;
    }

    public boolean isPointVisible(Entity target, Vec3d vector, double dst) {
        return RayTracingUtils.getPointedEntity(getRotationForCoord(vector), dst, !ignoreWalls(target), target) == target;
    }


    public void attack(Entity base) {
        if (base instanceof EntityEnderCrystal || canAttack()) {
            if (getVector(base) != null) {
                rotate(base, true);
                if (
                        (RayTracingUtils.getMouseOver(base, Trillium.rotationManager.getServerYaw(), Trillium.rotationManager.getServerPitch(), attackDistance.getValue(), ignoreWalls(base)) == base)
                        || (base instanceof EntityEnderCrystal && mc.player.getDistanceSq(base) <= 20)
                        || (backTrack.getValue() && bestBtBox != null)
                        || !rtx.getValue()
                ) {
                    if(teleport.getValue()){
                        mc.player.setPosition(base.posX, base.posY + tpY.getValue(), base.posZ);
                    }
                    boolean blocking = mc.player.isHandActive() && mc.player.getActiveItemStack().getItem().getItemUseAction(mc.player.getActiveItemStack()) == EnumAction.BLOCK;
                    if (blocking) {
                        mc.playerController.onStoppedUsingItem(mc.player);
                    }
                    boolean needSwap = false;

                    if (mc.player.isSprinting()) {
                        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, Action.STOP_SPRINTING));
                        needSwap = true;
                    }
                    if (shiftTap.getValue()) {
                        mc.gameSettings.keyBindSneak.pressed = true;
                    }
                    mc.playerController.attackEntity(mc.player, base);
                    if(Debug.getValue()){
                        if(target != null && last_best_vec != null) {
                            Command.sendMessage("Attacked with delay: " + hitttimer.getPassedTimeMs() + " | Distance to target: " + mc.player.getDistance(target) + " | Distance to best point: " + mc.player.getDistance(last_best_vec.x, last_best_vec.y, last_best_vec.z));
                        }
                    }
                    hitttimer.reset();
                    mc.player.swingArm(offhand.getValue() ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);

                    if (InventoryUtil.getBestAxe() >= 0 && shieldBreaker.getValue() && base instanceof EntityPlayer && isActiveItemStackBlocking((EntityPlayer) base, 1)) {
                        mc.player.connection.sendPacket(new CPacketHeldItemChange(InventoryUtil.getBestAxe()));
                        mc.playerController.attackEntity(mc.player, (EntityPlayer) base);
                        mc.player.swingArm(EnumHand.MAIN_HAND);
                        mc.player.resetCooldown();
                        mc.player.connection.sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem));
                    }
                    if (blocking) {
                        mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(mc.player.getActiveHand()));
                    }
                    if (needSwap) {
                        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, Action.START_SPRINTING));
                    }
                    if (shiftTap.getValue()) {
                        mc.gameSettings.keyBindSneak.pressed = false;
                    }
                    if(swappedToAxe){
                        swapBack = true;
                        swappedToAxe = false;
                    }
                    CPSLimit = 10;
                }
            }
        }
    }

    @SubscribeEvent
    public void onPostAttack(EventPostMotion e){
        if(firstAxe.getValue() && InventoryUtil.getBestSword() != -1 && swapBack){
            if(autoswitch.getValue() == AutoSwitch.Default){
                mc.player.inventory.currentItem = InventoryUtil.getBestSword();
                swapBack = false;
            }
        }
        if (clientLook.getValue()) {
            mc.player.rotationYaw = Trillium.rotationManager.getServerYaw();
            mc.player.rotationPitch = Trillium.rotationManager.getServerPitch();
        }
    }

    public float getDistanceBT(BackTrack.Box box) {
        float f = (float)(mc.player.posX - box.getPosition().x);
        float f1 = (float)(mc.player.getPositionEyes(1).y - box.getPosition().y);
        float f2 = (float)(mc.player.posZ - box.getPosition().z);
        return (f * f + f1 * f1 + f2 * f2);
    }
	
    public float getDistanceBTPoint(Vec3d point) {
        float f = (float)(mc.player.posX - point.x);
        float f1 = (float)(mc.player.getPositionEyes(1).y - point.y);
        float f2 = (float)(mc.player.posZ - point.z);
        return (f * f + f1 * f1 + f2 * f2);
    }

    public boolean isNakedPlayer(EntityLivingBase base) {
        if (!(base instanceof EntityOtherPlayerMP)) {
            return false;
        }
        return base.getTotalArmorValue() == 0;
    }

    public boolean isInvisible(EntityLivingBase base) {
        if (!(base instanceof EntityOtherPlayerMP)) {
            return false;
        }
        return base.isInvisible();
    }

    public boolean needExplosion(Vec3d position) {
        ExplosionBuilder builder = new ExplosionBuilder(mc.world, null, position.x, position.y, position.z, 6);
        boolean needExplosion = false;
        for (Entry<EntityPlayer, Float> entry : builder.damageMap.entrySet()) {
            if (Trillium.friendManager.isFriend(entry.getKey().getName()) && entry.getValue() > entry.getKey().getHealth()) {
                return false;
            }
            if (entry.getKey() == mc.player && entry.getValue() > 25) {
                return false;
            }
            if (entry.getValue() > 35) {
                needExplosion = true;
            }
        }
        return needExplosion;
    }

    public boolean canAttack() {
        boolean reasonForCancelCritical = mc.player.isPotionActive(MobEffects.SLOWNESS) || mc.player.isOnLadder() || (isInLiquid()) || mc.player.isInWeb || (smartCrit.getValue() && (isCrystalNear() || (!criticals_autojump.getValue() && !mc.gameSettings.keyBindJump.isKeyDown())));

        if(timingMode.getValue() == TimingMode.Default) {
            if(CPSLimit > 0) return false;
            if(mc.player.getCooledAttackStrength(1.5f) <= 0.93) return false;
        } else {
            if (!oldTimer.passedMs((long) ((1000 + (MathUtil.random(1, 50) - MathUtil.random(1, 60) + MathUtil.random(1, 70))) / (int) MathUtil.random(minCPS.getValue(), maxCPS.getValue())))) {
                return false;
            }
        }

        if(last_best_vec != null){
            if(getDistanceFromHead( new Vec3d( last_best_vec.x,last_best_vec.y,last_best_vec.z)) > attackDistance.getValue()){
                return false;
            }
        }

        if (criticals.getValue() && watercrits.getValue() && (mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ)).getBlock() instanceof BlockLiquid && mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY + 1, mc.player.posZ)).getBlock() instanceof BlockAir)){
           return mc.player.fallDistance >= 0.08f;
        }

        if(criticals.getValue() && !reasonForCancelCritical) {
            if(critMode.getValue() == CritMode.WexSide) {
                if ((int) mc.player.posY != (int) Math.ceil(mc.player.posY) && mc.player.onGround && isBlockAboveHead()) {
                    return true;
                }
                return !mc.player.onGround && mc.player.fallDistance > 0.08;
            } else if(critMode.getValue() == CritMode.Simple) {
                return (isBlockAboveHead() ? mc.player.fallDistance > 0 : mc.player.fallDistance >= critdist.getValue()) && !mc.player.onGround;
            }
        }
        oldTimer.reset();
        return true;
    }


    private float getCooledAttackStrength() {
        return clamp(((float)  ((IEntityLivingBase) mc.player).getTicksSinceLastSwing()  + 1.5f ) / getCooldownPeriod(), 0.0F, 1.0F);
    }
	
    public float getCooldownPeriod() {
        return (float)(1.0 / mc.player.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED).getAttributeValue() * (20f * 1.0f));
    }

    private boolean isCrystalNear() {
        EntityEnderCrystal crystal = (EntityEnderCrystal) mc.world.loadedEntityList.stream().filter(e -> (e instanceof EntityEnderCrystal && mc.player.getDistance(e) <= 6)).min(Comparator.comparing(c -> mc.player.getDistance(c))).orElse(null);
        return crystal != null;
    }


    public static boolean isBlockAboveHead() {
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(mc.player.posX - 0.3, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ + 0.3, mc.player.posX + 0.3, mc.player.posY + (!mc.player.onGround ? 1.5 : 2.5), mc.player.posZ - 0.3);
        return !mc.world.getCollisionBoxes(mc.player, axisAlignedBB).isEmpty();
    }

    public EntityLivingBase findTarget() {
        List<EntityLivingBase> targets = new ArrayList<>();
        for (Entity entity : mc.world.loadedEntityList) {
            if (entity instanceof EntityLivingBase && isEntityValid((EntityLivingBase) entity, false)) {
                targets.add((EntityLivingBase) entity);
            }
        }
        targets.sort((e1, e2) -> {
            int dst1 = (int) (mc.player.getDistance(e1) * 1000);
            int dst2 = (int) (mc.player.getDistance(e2) * 1000);
            return dst1 - dst2;
        });
        return targets.isEmpty() ? null : targets.get(0);
    }


    public boolean isEntityValid(EntityLivingBase entity, boolean backtrack) {
        if (ignoreNaked.getValue()) {
            if (isNakedPlayer(entity))
                return false;
        }
        if (ignoreInvisible.getValue()) {
            if (isInvisible(entity))
                return false;
        }
        if(ignoreCreativ.getValue()) {
            if(entity instanceof EntityPlayer){
                if(((EntityPlayer) entity).isCreative()){
                    return false;
                }
            }
        }
        if (entity.getHealth() <= 0) {
            return false;
        }
        if (!targetsCheck(entity)) {
            return false;
        }
        if(backtrack){
            return true;
        }

        if (!ignoreWalls(entity))
            return getVector(entity) != null;
        else
            return mc.player.getDistanceSq(entity) <= Math.pow((attackDistance.getValue() + rotateDistance.getValue()),2) ;
    }
	
	public static void renderTracer(double x, double y, double z, double x2, double y2, double z2, int color){
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glLineWidth(1.5f);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);

        GL11.glColor4f(((color >> 16) & 0xFF) / 255F, ((color >> 8) & 0xFF) / 255F, ((color) & 0xFF) / 255F, ((color >> 24) & 0xFF) / 255F);
        GlStateManager.disableLighting();
        GL11.glLoadIdentity();

        ((IEntityRenderer) mc.entityRenderer).orientCam(mc.getRenderPartialTicks());

        GL11.glEnable(GL11.GL_LINE_SMOOTH);

        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(x, y, z);
        GL11.glVertex3d(x2, y2, z2);
        GL11.glVertex3d(x2, y2, z2);
        GL11.glEnd();

        GL11.glDisable(GL11.GL_LINE_SMOOTH);

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor3d(1d,1d,1d);
        GlStateManager.enableLighting();
    }

    public Vec3d getVector(Entity target) {
        BackTrack bt = Trillium.moduleManager.getModuleByClass(BackTrack.class);
        if(!backTrack.getValue()
                || (mc.player.getDistanceSq(target) <= attackDistance.getValue() )
                || bt.isOff()
                || !(target instanceof EntityPlayer)
                || (backTrack.getValue() && bt.entAndTrail.get(target) == null)
                || (backTrack.getValue() && bt.entAndTrail.get(target) != null  && bt.entAndTrail.get(target).size() == 0) ) {


            ArrayList<Vec3d> points = RayTracingUtils.getHitBoxPoints(target.getPositionVector(),hitboxScale.getValue()/10f);

            points.removeIf(point -> !isPointVisible(target, point, (attackDistance.getValue() + rotateDistance.getValue())));

            if (points.isEmpty()) {
                return null;
            }


            float best_distance = 100;
            Vec3d best_point = null;
            float best_angle = 180f;
            
            if(pointsMode.getValue() == PointsMode.Angle) {
                for (Vec3d point : points) {
                    Vector2f r = getDeltaForCoord(new Vector2f(Trillium.rotationManager.getServerYaw(), Trillium.rotationManager.getServerPitch()), point);
                    float y = Math.abs(r.y);
                    if(y < best_angle){
                        best_angle = y;
                        best_point = point;
                    }
                }
            } else {
                for (Vec3d point : points) {
                    if (getDistanceFromHead(point) < best_distance) {
                        best_point = point;
                        best_distance = getDistanceFromHead(point);
                    }
                }
            }
            last_best_vec = best_point;
            return best_point;
        } else {
            bestBtBox = null;
            float best_distance = 36;
            BackTrack.Box best_box = null;
            for(BackTrack.Box boxes : bt.entAndTrail.get(target)){
                if(getDistanceBT(boxes) < best_distance){
                    best_box = boxes;
                    best_distance = getDistanceBT(boxes);
                }
            }

            if(best_box != null){
                bestBtBox = best_box;
                ArrayList<Vec3d> points = RayTracingUtils.getHitBoxPoints(best_box.getPosition(),hitboxScale.getValue() / 10f);
                points.removeIf(point -> getDistanceBTPoint(point) > Math.pow((attackDistance.getValue() + rotateDistance.getValue()),2) );

                if (points.isEmpty()) {
                    return null;
                }


                float best_distance2 = 100;
                Vec3d best_point = null;
                float best_angle = 180f;

                if(pointsMode.getValue() == PointsMode.Angle) {
                    for (Vec3d point : points) {
                        Vector2f r = getDeltaForCoord(new Vector2f(Trillium.rotationManager.getServerYaw(), Trillium.rotationManager.getServerPitch()), point);
                        float y = Math.abs(r.y);
                        if(y < best_angle){
                            best_angle = y;
                            best_point = point;
                        }

                    }
                } else {
                    for (Vec3d point : points) {
                        if (getDistanceFromHead(point) < best_distance2) {
                            best_point = point;
                            best_distance2 = getDistanceFromHead(point);
                        }
                    }
                }

                last_best_vec = best_point;
                return best_point;
            }
        }
        return null;
    }

    public boolean targetsCheck(EntityLivingBase entity) {
        CastHelper castHelper = new CastHelper();
        if (Playersss.getValue()) {
            castHelper.apply(CastHelper.EntityType.PLAYERS);
        }
        if (Mobsss.getValue()) {
            castHelper.apply(CastHelper.EntityType.MOBS);
        }
        if (Animalsss.getValue()) {
            castHelper.apply(CastHelper.EntityType.ANIMALS);
        }
        if (Villagersss.getValue()) {
            castHelper.apply(CastHelper.EntityType.VILLAGERS);
        }
        if (entity instanceof EntitySlime) {
            return Slimesss.getValue();
        }
        return CastHelper.isInstanceof(entity, castHelper.build()) != null && !entity.isDead;
    }

    public boolean ignoreWalls(Entity input) {
        if (input instanceof EntityEnderCrystal) return true;
        if (mc.world.getBlockState(new BlockPos(Trillium.positionManager.getX(), Trillium.positionManager.getY(), Trillium.positionManager.getZ())).getMaterial() != Material.AIR) return true;
        return mc.player.getDistanceSq(input) <= walldistance.getValue();
    }

    public static Vector2f getDeltaForCoord(Vector2f rot, Vec3d point) {
        EntityPlayerSP client = Minecraft.getMinecraft().player;
        double x = point.x - client.posX;
        double y = point.y - client.getPositionEyes(1).y;
        double z = point.z - client.posZ;
        double dst = Math.sqrt(Math.pow(x, 2) + Math.pow(z, 2));
        float yawToTarget = (float) wrapDegrees(Math.toDegrees(Math.atan2(z, x)) - 90);
        float pitchToTarget = (float) (-Math.toDegrees(Math.atan2(y, dst)));
        float yawDelta = wrapDegrees(yawToTarget - rot.x);
        float pitchDelta = (pitchToTarget - rot.y);
        return new Vector2f(yawDelta, pitchDelta);
    }


    public static Vector2f getRotationForCoord(Vec3d point) {
        double x = point.x - mc.player.posX;
        double y = point.y - mc.player.getPositionEyes(1).y;
        double z = point.z - mc.player.posZ;
        double dst = Math.sqrt(Math.pow(x, 2) + Math.pow(z, 2));
        float yawToTarget = (float) wrapDegrees(Math.toDegrees(Math.atan2(z, x)) - 90);
        float pitchToTarget = (float) (-Math.toDegrees(Math.atan2(y, dst)));
        return new Vector2f(yawToTarget, pitchToTarget);
    }


    private float getDistanceFromHead(Vec3d d1) {
        double x = d1.x - mc.player.posX;
        double y = d1.y - mc.player.getPositionEyes(1).y;
        double z = d1.z - mc.player.posZ;
        return (float) (Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z,2));
    }


    public static boolean isActiveItemStackBlocking(EntityPlayer other, int time) {
        if (other.isHandActive() && !other.activeItemStack.isEmpty()) {
            Item item = other.activeItemStack.getItem();
            if (item.getItemUseAction(other.activeItemStack) != EnumAction.BLOCK) {
                return false;
            } else {
                return item.getMaxItemUseDuration(other.activeItemStack) - other.activeItemStackUseCount >= time;
            }
        } else {
            return false;
        }
    }

    public enum Hitbox {
        HEAD, CHEST, LEGS;
    }


    public void rotate(Entity base, boolean attackContext) {
        rotatedBefore = true;


        Vec3d bestVector = getVector(base);
        if (bestVector == null) {
            bestVector = base.getPositionEyes(1);
        }

        boolean inside_target = mc.player.boundingBox.intersects(target.boundingBox);


        if(rotation.getValue() == rotmod.Matrix3 && inside_target){
            bestVector = base.getPositionVector().add(new Vec3d(0,interpolateRandom(0.7f,0.9f),0));
        }


        double x =  (bestVector.x - mc.player.posX);
        double y =  bestVector.y - mc.player.getPositionEyes(1).y;
        double z =  bestVector.z - mc.player.posZ;
        double dst = Math.sqrt(Math.pow(x, 2) + Math.pow(z, 2));

        float yawToTarget = (float) wrapDegrees(Math.toDegrees(Math.atan2(z, x)) - 90);
        float pitchToTarget = (float) (-Math.toDegrees(Math.atan2(y, dst)));

        float sensitivity = 1.0001f;

        float yawDelta = wrapDegrees(yawToTarget - Trillium.rotationManager.getServerYaw()) / sensitivity;
        float pitchDelta = (pitchToTarget - Trillium.rotationManager.getServerPitch()) / sensitivity;


        if (yawDelta > 180) {
            yawDelta = yawDelta - 180;
        }

        int yawDeltaAbs = (int) Math.abs(yawDelta);


        if (yawDeltaAbs < fov.getValue()) {
            switch (rotation.getValue()) {
				case None: {
                    break;
                }
                case Matrix: {
                    float pitchDeltaAbs = Math.abs(pitchDelta);

                    float additionYaw = Math.min(Math.max(yawDeltaAbs, 1), yawStep.getValue());
                    float additionPitch = Math.max(attackContext ? pitchDeltaAbs : 1, 2);

                    if (Math.abs(additionYaw - prevAdditionYaw) <= 3.0f) {
                        additionYaw = prevAdditionYaw + 3.1f;
                    }

                    float newYaw = Trillium.rotationManager.getServerYaw() + (yawDelta > 0 ? additionYaw : -additionYaw) * sensitivity;
                    float newPitch = clamp(Trillium.rotationManager.getServerPitch() + (pitchDelta > 0 ? additionPitch : -additionPitch)  * sensitivity, -90, 90);

                    mc.player.rotationYaw = newYaw;
                    mc.player.rotationPitch = newPitch;
                    mc.player.rotationYawHead = newYaw;
                    mc.player.renderYawOffset = newYaw;
                    prevAdditionYaw = additionYaw;
                    break;
                }
                case Matrix2: {
                    float absoluteYaw = MathHelper.abs(yawDelta);

                    float randomize = interpolateRandom(-2.0F, 2.0F);
                    float randomizeClamp = interpolateRandom(-5.0F, 5.0F);

                    float deltaYaw = MathHelper.clamp(absoluteYaw + randomize, -60.0F + randomizeClamp, 60.0F + randomizeClamp);
                    float deltaPitch = MathHelper.clamp(pitchDelta + randomize, (-((false) ? 13 : 45)), (false) ? 13 : 45);

                    float newYaw = Trillium.rotationManager.getServerYaw() + (yawDelta > 0 ? deltaYaw : -deltaYaw);
                    float newPitch  = MathHelper.clamp(Trillium.rotationManager.getServerPitch() + deltaPitch / (false ? 4.0F : 2.0F), -90.0F, 90.0F);

                    float gcdFix1 = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
                    double gcdFix2 = Math.pow(gcdFix1, 3.0) * 8.0;
                    double gcdFix = gcdFix2 * 0.15000000596046448;

                    newYaw = (float) (newYaw - (newYaw - Trillium.rotationManager.getServerYaw()) % gcdFix);
                    newPitch = (float)(newPitch - (newPitch - Trillium.rotationManager.getServerPitch()) % gcdFix);


                    mc.player.rotationYaw = newYaw;
                    mc.player.rotationPitch = newPitch;
                    mc.player.rotationYawHead = newYaw;
                    mc.player.renderYawOffset = newYaw;
                    break;
                }
                case Matrix3: {
                    float absoluteYaw = MathHelper.abs(yawDelta);

                    float randomize = interpolateRandom(-2.0F, 2.0F);
                    float randomizeClamp = interpolateRandom(-5.0F, 5.0F);

                    boolean looking_at_box = RayTracingUtils.getMouseOver(base, Trillium.rotationManager.getServerYaw(), Trillium.rotationManager.getServerPitch(), attackDistance.getValue() + rotateDistance.getValue(), ignoreWalls(base)) == base;

                    if(looking_at_box){
                        rotation_smoother = 15f;
                    } else if(rotation_smoother < 60f){
                        rotation_smoother += 9f;
                    }

                    float yaw_speed = (inside_target && attackContext) ? 60f : rotation_smoother;
                    float pitch_speed = looking_at_box ? 0.5f : rotation_smoother / 2f;

                    float deltaYaw = MathHelper.clamp(absoluteYaw + randomize, -yaw_speed + randomizeClamp, yaw_speed + randomizeClamp);
                    float deltaPitch = MathHelper.clamp(pitchDelta, -pitch_speed, pitch_speed);

                    float newYaw = Trillium.rotationManager.getServerYaw() + (yawDelta > 0 ? deltaYaw : -deltaYaw);
                    float newPitch  = MathHelper.clamp(Trillium.rotationManager.getServerPitch() + deltaPitch, -90.0F, 90.0F);

                    float gcdFix1 = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
                    double gcdFix2 = Math.pow(gcdFix1, 3.0) * 8.0;
                    double gcdFix = gcdFix2 * 0.15000000596046448;

                    newYaw = (float) (newYaw - (newYaw - Trillium.rotationManager.getServerYaw()) % gcdFix);
                    newPitch = (float)(newPitch - (newPitch - Trillium.rotationManager.getServerPitch()) % gcdFix);


                    mc.player.rotationYaw = newYaw;
                    mc.player.rotationPitch = newPitch;
                    mc.player.rotationYawHead = newYaw;
                    mc.player.renderYawOffset = newYaw;
                    break;
                }
                case NCP: {
                    float[] ncp = SilentRotaionUtil.calcAngle(getVector(base));
                    if(ncp != null) {
                        mc.player.rotationYaw = ncp[0];
                        mc.player.rotationPitch = ncp[1];
                        mc.player.rotationYawHead = ncp[0];
                        mc.player.renderYawOffset = ncp[0];
                    }
                    break;
                }
                case AAC: {
                    if (attackContext) {
                        int pitchDeltaAbs = (int) Math.abs(pitchDelta);
                        float newYaw = Trillium.rotationManager.getServerYaw() + (yawDelta > 0 ? yawDeltaAbs : -yawDeltaAbs) * sensitivity;
                        float newPitch = clamp(Trillium.rotationManager.getServerPitch() + (pitchDelta > 0 ? pitchDeltaAbs : -pitchDeltaAbs) * sensitivity, -90, 90) ;
                        mc.player.rotationYaw = newYaw;
                        mc.player.rotationPitch = newPitch;
                        mc.player.rotationYawHead = newYaw;
                        mc.player.renderYawOffset = newYaw;
                    }
                    break;
                }
            }
        }
    }

    public static float interpolateRandom(float var0, float var1) {
        return (float) (var0 + (var1 - var0) * Math.random());
    }
}