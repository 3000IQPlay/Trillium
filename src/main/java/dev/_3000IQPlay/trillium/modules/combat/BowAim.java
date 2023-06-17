package dev._3000IQPlay.trillium.modules.combat;

import dev._3000IQPlay.trillium.Trillium;
import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.Setting;
import dev._3000IQPlay.trillium.util.EntityUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

public class BowAim extends Module {
    public EntityLivingBase target;
    public float rangeAimVelocity = 0.0f;
    private final Setting<Float> fov = this.register(new Setting<>("Fov", 60.0f, 0.0f, 180f));

    public BowAim() {
        super("BowAim", "AutoAims on Target when using a bow", Category.COMBAT, true, false, false);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (nullCheck()) {
            return;
        }
        if (target == null)
            return;
        if (Trillium.friendManager.isFriend(target.getName()))
            return;
        if (target.getHealth() <= 0)
            return;

        ItemStack itemStack = mc.player.getHeldItemMainhand();
        if (itemStack == null || !(itemStack.getItem() instanceof ItemBow)) {
            return;
        }
        if (!mc.gameSettings.keyBindUseItem.isKeyDown()) {
            return;
        }
        this.target = this.getClosestEntity();
        if (this.target == null) {
            return;
        }
        int rangeCharge = mc.player.getItemInUseCount();
        this.rangeAimVelocity = rangeCharge / 20.0f;
        this.rangeAimVelocity = (this.rangeAimVelocity * this.rangeAimVelocity + this.rangeAimVelocity * 2.0f) / 3.0f;
        this.rangeAimVelocity = 1.0f;
        if (this.rangeAimVelocity > 1.0f) {
            this.rangeAimVelocity = 1.0f;
        }
        double posX = this.target.posX - mc.player.posX;
        double posY = this.target.posY + (double) this.target.getEyeHeight() - 0.15 - mc.player.posY - (double) mc.player.getEyeHeight();
        double posZ = this.target.posZ - mc.player.posZ;
        double y2 = Math.sqrt(posX * posX + posZ * posZ);
        float g = 0.006f;
        float tmp = (float) ((double) (this.rangeAimVelocity * this.rangeAimVelocity * this.rangeAimVelocity * this.rangeAimVelocity) - (double) g * ((double) g * (y2 * y2) + 2.0 * posY * (double) (this.rangeAimVelocity * this.rangeAimVelocity)));
        float pitch = (float) (-Math.toDegrees(Math.atan(((double) (this.rangeAimVelocity * this.rangeAimVelocity) - Math.sqrt(tmp)) / ((double) g * y2))));
        mc.player.rotationPitch = pitch;
    }

    public boolean check(EntityLivingBase entity) {
        if (entity instanceof EntityArmorStand) {
            return false;
        }
        if (entity == mc.player) {
            return false;
        }
        if (entity.isDead) {
            return false;
        }
        if (!EntityUtil.canSeeEntityAtFov(entity, fov.getValue())) {
            return false;
        }
        return mc.player.canEntityBeSeen(entity);
    }

    EntityLivingBase getClosestEntity() {
        EntityLivingBase closestEntity = null;
        for (Entity entity : mc.world.loadedEntityList) {
            if (!(entity instanceof EntityLivingBase) || entity instanceof EntityArmorStand)
                continue;
            EntityLivingBase livingBase = (EntityLivingBase) entity;
            if (!this.check(livingBase) || (closestEntity != null && mc.player.getDistanceSq(livingBase) >= mc.player.getDistanceSq(closestEntity)))
                continue;
            closestEntity = livingBase;
        }
        return closestEntity;
    }

    @SubscribeEvent
    public void onFovModifier(EntityViewRenderEvent.FOVModifier event) {
        if (isEnabled() && target != null) {
            event.setFOV(fov.getValue());
        }
    }
}