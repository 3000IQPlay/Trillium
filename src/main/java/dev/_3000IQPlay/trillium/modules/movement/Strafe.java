package dev._3000IQPlay.trillium.modules.movement;

import dev._3000IQPlay.trillium.Trillium;
import dev._3000IQPlay.trillium.event.events.EventMove;
import dev._3000IQPlay.trillium.event.events.UpdateWalkingPlayerEvent;
import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.modules.exploit.PacketFly;
import dev._3000IQPlay.trillium.modules.movement.ElytraFlight;
import dev._3000IQPlay.trillium.modules.movement.ElytraFly2b2tNew;
import dev._3000IQPlay.trillium.modules.movement.HoleSnap;
import dev._3000IQPlay.trillium.modules.movement.Speed;
import dev._3000IQPlay.trillium.modules.player.FreeCam;
import dev._3000IQPlay.trillium.setting.Setting;
import net.minecraft.entity.Entity;
import net.minecraft.init.MobEffects;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Objects;

public class Strafe
        extends Module {
	public Setting<Mode> mode = this.register(new Setting<Mode>("Strafe", Mode.Normal));
    private static Strafe INSTANCE = new Strafe();
    private double lastDist;
    private double moveSpeed;
    int stage;

    public Strafe() {
		super("Strafe", "Makes you go from side to side", Category.MOVEMENT, true, false, false);
        this.setInstance();
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public static Strafe getInstance() {
        if (INSTANCE != null) {
            return INSTANCE;
        }
        INSTANCE = new Strafe();
        return INSTANCE;
    }
	
	@Override
    public void onUpdate() {
        if (Strafe.fullNullCheck() || Trillium.moduleManager.getModuleByClass(HoleSnap.class).isEnabled() || Trillium.moduleManager.getModuleByClass(FreeCam.class).isEnabled() || Trillium.moduleManager.getModuleByClass(PacketFly.class).isEnabled() || Trillium.moduleManager.getModuleByClass(ElytraFlight.class).isEnabled() || Trillium.moduleManager.getModuleByClass(ElytraFly2b2tNew.class).isEnabled() || Trillium.moduleManager.getModuleByClass(Speed.class).isEnabled()) {
            return;
        }
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayerEvent(UpdateWalkingPlayerEvent event) {
        if (event.getStage() == 1 && Strafe.fullNullCheck()) {
            return;
        }
        this.lastDist = Math.sqrt((mc.player.posX - mc.player.prevPosX) * (mc.player.posX - mc.player.prevPosX) + (mc.player.posZ - mc.player.prevPosZ) * (mc.player.posZ - mc.player.prevPosZ));
    }

    @SubscribeEvent
    public void onStrafe(EventMove event) {
        if (Strafe.fullNullCheck()) {
            return;
        }
        if (mc.player.isInWater()) {
            return;
        }
        if (mc.player.isInLava()) {
            return;
        }
        if (mc.player.onGround) {
            this.stage = 2;
        }
        switch (this.stage) {
            case 0: {
                ++this.stage;
                this.lastDist = 0.0;
                break;
            }
            case 2: {
                double motionY = 0.40123128;
                if (!mc.player.onGround || !mc.gameSettings.keyBindJump.isKeyDown()) {
                    break;
                }
                if (mc.player.isPotionActive(MobEffects.JUMP_BOOST)) {
                    motionY += (float)(mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1f;
                }
                mc.player.motionY = motionY;
                event.setY(mc.player.motionY);
                this.moveSpeed *= this.mode.getValue() == Mode.Normal ? 1.67 : 2.149;
                break;
            }
            case 3: {
                this.moveSpeed = this.lastDist - (this.mode.getValue() == Mode.Normal ? 0.6896 : 0.795) * (this.lastDist - this.getBaseMoveSpeed());
                break;
            }
            default: {
                if ((mc.world.getCollisionBoxes((Entity)mc.player, mc.player.getEntityBoundingBox().offset(0.0, mc.player.motionY, 0.0)).size() > 0 || mc.player.collidedVertically) && this.stage > 0) {
                    this.stage = mc.player.moveForward != 0.0f || mc.player.moveStrafing != 0.0f ? 1 : 0;
                }
                this.moveSpeed = this.lastDist - this.lastDist / (this.mode.getValue() == Mode.Normal ? 730.0 : 159.0);
            }
        }
        this.moveSpeed = !mc.gameSettings.keyBindJump.isKeyDown() && mc.player.onGround ? this.getBaseMoveSpeed() : Math.max(this.moveSpeed, this.getBaseMoveSpeed());
        double n = mc.player.movementInput.moveForward;
        double n2 = mc.player.movementInput.moveStrafe;
        double n3 = mc.player.rotationYaw;
        if (n == 0.0 && n2 == 0.0) {
            event.setX(0.0);
            event.setZ(0.0);
        } else if (n != 0.0 && n2 != 0.0) {
            n *= Math.sin(0.7853981633974483);
            n2 *= Math.cos(0.7853981633974483);
        }
        double n4 = this.mode.getValue() == Mode.Normal ? 0.993 : 0.99;
        event.setX((n * this.moveSpeed * -Math.sin(Math.toRadians(n3)) + n2 * this.moveSpeed * Math.cos(Math.toRadians(n3))) * n4);
        event.setZ((n * this.moveSpeed * Math.cos(Math.toRadians(n3)) - n2 * this.moveSpeed * -Math.sin(Math.toRadians(n3))) * n4);
        ++this.stage;
        event.setCanceled(true);
    }

    public double getBaseMoveSpeed() {
        double n = 0.2873;
        if (!mc.player.isPotionActive(MobEffects.SPEED)) {
            return n;
        }
        n *= 1.0 + 0.2 * (double)(Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.SPEED)).getAmplifier() + 1);
        return n;
    }
	
	public static enum Mode {
		Normal,
		Strict;
	}
}