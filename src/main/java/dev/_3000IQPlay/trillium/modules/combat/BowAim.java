package dev._3000IQPlay.trillium.modules.combat;




import dev._3000IQPlay.trillium.Trillium;
import dev._3000IQPlay.trillium.event.events.EventPostMotion;
import dev._3000IQPlay.trillium.event.events.EventPreMotion;
import dev._3000IQPlay.trillium.event.events.Render3DEvent;
import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.Setting;
import dev._3000IQPlay.trillium.util.EntityUtil;
import dev._3000IQPlay.trillium.util.PaletteHelper;
import dev._3000IQPlay.trillium.util.RenderHelper;
import dev._3000IQPlay.trillium.util.RotationUtil;
import dev._3000IQPlay.trillium.util.phobos.RayTraceUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBow;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;


public class BowAim extends Module {


    private Setting<Boolean> ignoreWalls = register(new Setting<>("IgnoreWalls", false));
    private Setting<Boolean> noVertical = register(new Setting<>("NoVertical", false));

    private  final Setting<Float> range = this.register( new Setting<>("Range", 60.0f, 0.0f, 200f));
    private  final Setting<Float> fov = this.register( new Setting<>("fov", 60.0f, 0.0f, 180f));

    public BowAim() {
        super("AimBot", "AimBot",Category.COMBAT, true, false, false);
    }


    Entity target;
    private double sideMultiplier;
    private double upMultiplier;
    private Vec3d predict;

    @SubscribeEvent
    public void onMotionUpdate(EventPostMotion event) {
            if (mc.player.getHeldItemMainhand().getItem() instanceof ItemBow && mc.player.isHandActive()
                    && mc.player.getItemInUseMaxCount() > 0) {
                target = findTarget();

                if (target == null)
                    return;

                double xPos = target.posX;
                double yPos = target.posY;
                double zPos = target.posZ;
                sideMultiplier = mc.player.getDistance(target) / ((mc.player.getDistance(target) / 2f)) * 5f;
                upMultiplier = (mc.player.getDistance(target) / 320) * 1.1;
               // predict = new Vec3d((xPos - 0.5) + (xPos - target.lastTickPosX) * sideMultiplier, yPos + upMultiplier, (zPos - 0.5) + (zPos - target.lastTickPosZ) * sideMultiplier);

                predict = new Vec3d(xPos , yPos + upMultiplier, zPos);


                float[] rotation = lookAtPredict(predict);

                mc.player.rotationYaw = rotation[0];
                if(noVertical.getValue())
                    mc.player.rotationPitch = rotation[1];
                target = null;
            }

    }

    private float[] lookAtPredict(Vec3d vec) {
        double diffX = vec.x + 0.5 - mc.player.posX;
        double diffY = vec.y + 0.5 - (mc.player.posY + mc.player.getEyeHeight());
        double diffZ = vec.z + 0.5 - mc.player.posZ;
        double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) -(Math.atan2(diffY, dist) * 180.0D / Math.PI);
        return new float[] {mc.player.rotationYaw + MathHelper.wrapDegrees(yaw - mc.player.rotationYaw), mc.player.rotationPitch + MathHelper.wrapDegrees(pitch - mc.player.rotationPitch) };
    }

    public EntityPlayer findTarget() {
        EntityPlayer target = null;
        double distance = range.getValue() * range.getValue();
        for (EntityPlayer entity : mc.world.playerEntities) {
            if (entity == mc.player) {
                continue;
            }
            if (Trillium.friendManager.isFriend(entity)) {
                continue;
            }
            if(EntityUtil.canEntityBeSeen(entity) && !ignoreWalls.getValue()){
                continue;
            }

            if(!EntityUtil.canSeeEntityAtFov(entity,fov.getValue())){
                continue;
            }
            if (mc.player.getDistanceSq(entity) <= distance) {
                target = entity;
                distance = mc.player.getDistanceSq(entity);
            }
        }
        return target;
    }



    @SubscribeEvent
    public void onRender3D( Render3DEvent event) {

        if (BowAim.mc.player.getHeldItemMainhand().getItem() instanceof ItemBow && target != null) {
            RenderHelper.drawEntityBox((Entity)target, new Color(PaletteHelper.astolfo(false, 12).getRGB()), false, 255.0f);
        }
    }
}
