package dev._3000IQPlay.trillium.modules.movement;

import dev._3000IQPlay.trillium.Trillium;
import dev._3000IQPlay.trillium.event.events.EventMove;
import dev._3000IQPlay.trillium.event.events.EventPreMotion;
import dev._3000IQPlay.trillium.event.events.PacketEvent;
import dev._3000IQPlay.trillium.event.events.UpdateWalkingPlayerEvent;
import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.Setting;
import dev._3000IQPlay.trillium.util.EntityUtil;
import dev._3000IQPlay.trillium.util.MovementUtil;
import dev._3000IQPlay.trillium.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static dev._3000IQPlay.trillium.util.PyroSpeed.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

public class Speed
        extends Module {
	private static Speed instance;
    private final Setting<SpeedNewModes> mode = this.register(new Setting<SpeedNewModes>("Mode", SpeedNewModes.Custom));
	
	
	public Setting<Integer> bticks  = this.register(new Setting<>("BoostTicks", 10, 1, 40, v -> this.mode.getValue() == SpeedNewModes.NCP));
    public Setting<Boolean> strafeBoost = this.register(new Setting<>("StrafeBoost", false, v -> this.mode.getValue() == SpeedNewModes.NCP));
    public Setting<Float> reduction  = this.register(new Setting<>("Reduction ", 2f, 1f, 10f, v -> this.mode.getValue() == SpeedNewModes.NCP));
    public Setting<Boolean> jumpBoost = this.register (new Setting<>("JumpBoostApplifier", false, v -> this.mode.getValue() == SpeedNewModes.NCP));
	public Setting<Boolean> uav = this.register( new Setting<>("UseAllVelocity", false, v -> this.mode.getValue() == SpeedNewModes.NCP));
	
	private final Setting<Boolean> accelerate = this.register(new Setting<Boolean>("Accelerate", false, t -> this.mode.getValue().equals((Object)SpeedNewModes.Custom) && this.usMode.getValue() == UpSpeed.Custom || this.dsMode.getValue() == DownSpeed.Custom));
	private final Setting<Float> acceleration = this.register(new Setting<Float>("Acceleration", 0.0f, 0.0f, 10.0f, t -> this.mode.getValue().equals((Object)SpeedNewModes.Custom) && this.accelerate.getValue() && this.usMode.getValue() == UpSpeed.Custom || this.dsMode.getValue() == DownSpeed.Custom));
	private final Setting<Float> deceleration = this.register(new Setting<Float>("Deceleration", 0.0f, 0.0f, 10.0f, t -> this.mode.getValue().equals((Object)SpeedNewModes.Custom) && this.accelerate.getValue() && this.usMode.getValue() == UpSpeed.Custom || this.dsMode.getValue() == DownSpeed.Custom));
	private final Setting<UpSpeed> usMode = this.register(new Setting<UpSpeed>("UpSpeedType", UpSpeed.Custom, t -> this.mode.getValue().equals((Object)SpeedNewModes.Custom)));
    private final Setting<Float> upAirSpeed = this.register(new Setting<Float>("UpAirSpeed", 0.272f, 0.0f, 5.0f, t -> this.mode.getValue().equals((Object)SpeedNewModes.Custom) && this.usMode.getValue() == UpSpeed.Custom));
	private final Setting<DownSpeed> dsMode = this.register(new Setting<DownSpeed>("DownSpeedType", DownSpeed.Custom, t -> this.mode.getValue().equals((Object)SpeedNewModes.Custom)));
	private final Setting<Float> downAirSpeed = this.register(new Setting<Float>("DownAirSpeed", 0.272f, 0.0f, 5.0f, t -> this.mode.getValue().equals((Object)SpeedNewModes.Custom) && this.dsMode.getValue() == DownSpeed.Custom));
	private final Setting<Float> onGroundSpeed = this.register(new Setting<Float>("GroundSpeed", 0.272f, 0.0f, 5.0f, t -> this.mode.getValue().equals((Object)SpeedNewModes.Custom)));
	private final Setting<Boolean> autoJump = this.register(new Setting<Boolean>("AutoJump", true, t -> this.mode.getValue().equals((Object)SpeedNewModes.Custom)));
    private final Setting<Float> jumpMotionY = this.register(new Setting<Float>("JumpMotionY", 0.42f, 0.0f, 4.0f, t -> this.mode.getValue().equals((Object)SpeedNewModes.Custom)));
	private final Setting<Float> groundTimer = this.register(new Setting<Float>("GroundTimer", 1.0f, 0.1f, 3.0f, t -> this.mode.getValue().equals((Object)SpeedNewModes.Custom)));
	private final Setting<DownMode> downMode = this.register(new Setting<DownMode>("DownType", DownMode.Timer, t -> this.mode.getValue().equals((Object)SpeedNewModes.Custom)));
	private final Setting<Float> downTimerValue = this.register(new Setting<Float>("CustomDownTimer", 1.0f, 0.1f, 3.0f, t -> this.downMode.getValue() == DownMode.Timer && this.mode.getValue().equals((Object)SpeedNewModes.Custom)));
	private final Setting<Float> downMotionValue = this.register(new Setting<Float>("CustomDownMotion", 0.2f, 0.0f, 3.0f, t -> this.downMode.getValue() == DownMode.Motion && this.mode.getValue().equals((Object)SpeedNewModes.Custom)));
	private final Setting<Float> upTimerValue = this.register(new Setting<Float>("CustomUpTimer", 1.0f, 0.1f, 3.0f, t -> this.mode.getValue().equals((Object)SpeedNewModes.Custom)));
	private final Setting<Boolean> sprintPacket = this.register(new Setting<Boolean>("SprintPacket", true));
    private final Setting<Boolean> resetXZ = this.register(new Setting<Boolean>("ResetXZ", false, t -> this.mode.getValue().equals((Object)SpeedNewModes.Custom)));
    private final Setting<Boolean> resetY = this.register(new Setting<Boolean>("ResetY", false, t -> this.mode.getValue().equals((Object)SpeedNewModes.Custom)));
	public double NCPBaseSpeed = getBaseMoveSpeed();
    public double distance;
    public int Field2015 = 4;
    public int speedStage;
    public boolean flip;
    int velocity = 0;
    int boostticks = 0;
    boolean isBoosting = false;
    private double maxVelocity = 0;
    private Timer velocityTimer = new Timer();
    int stage;

    public Speed() {
        super("Speed", "placeholder", Module.Category.MOVEMENT, false, false, false);
		instance = this;
    }
	
	public static Speed getInstance() {
        if (instance == null) {
            instance = new Speed();
        }
        return instance;
    }
	
	@SubscribeEvent( priority = EventPriority.HIGHEST)
    public void onPacketReceive(PacketEvent.Receive event) {
		if (this.mode.getValue() == SpeedNewModes.NCP) {
        if (event.getPacket() instanceof SPacketEntityVelocity) {
            if(((SPacketEntityVelocity) event.getPacket()).getEntityID() == mc.player.getEntityId()) {
                SPacketEntityVelocity pack = event.getPacket();
                int vX = pack.getMotionX();
                int vZ = pack.getMotionZ();
                if (vX < 0) vX *= -1;
                if (vZ < 0) vZ *= -1;

                if((vX + vZ) < 3000 && !this.uav.getValue()) return;
                velocity = vX + vZ;

                boostticks = this.bticks.getValue();
            }
        }
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            maxVelocity = 0;
            toggle();
        } else if (event.getPacket() instanceof SPacketExplosion) {
            SPacketExplosion velocity = event.getPacket();
            maxVelocity = Math.sqrt(velocity.getMotionX() * velocity.getMotionX() + velocity.getMotionZ() * velocity.getMotionZ());
            velocityTimer.reset();
        }
		}
    }
	
	public double getBaseMoveSpeed() {
        if (Speed.fullNullCheck()) {
            return 0.2873;
        }

        int n;
        double d = 0.2873;
        if (mc.player.isPotionActive(MobEffects.SPEED)) {
            n = Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.SPEED)).getAmplifier();
            d *= 1.0 + 0.2 * (double)(n + 1);
        }
        if (mc.player.isPotionActive(MobEffects.JUMP_BOOST) && this.jumpBoost.getValue()) {
            n = Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST)).getAmplifier();
            d /= 1.0 + 0.2 * (double)(n + 1);
        }
        if (this.strafeBoost.getValue() && velocity > 0 && boostticks > 0){
            d += (velocity / 8000f) / this.reduction.getValue();
            boostticks--;
        }
        if(boostticks == 1){
            velocity = 0;
        }
        return d;
    }
	
	public double getBaseMotionSpeed() {
        double baseSpeed =  0.2873D;
        if (mc.player.isPotionActive(MobEffects.SPEED)) {
            int amplifier = Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.SPEED)).getAmplifier();
            baseSpeed *= 1.0D + 0.2D * ((double) amplifier + 1);
        }
        return baseSpeed;
    }
	
	public double isJumpBoost(){
        if (mc.player.isPotionActive(MobEffects.JUMP_BOOST)) {
            return 0.2;
        } else {
            return 0;
        }
    }
	
	private double round(double value) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(3, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
	
	@SubscribeEvent
	public void onMoveEvent(EventMove event) {
	    if (event.getStage() == 1) return;
        if (Speed.fullNullCheck()) return;
	    switch (this.mode.getValue()) {
	        case NCP: {
                double d;
                if (event.getStage() != 0) return;
                if (event.isCanceled()) {
                    return;
                }
                if (!isMovingClient() || mc.player.fallDistance > 5.0f) {
                    return;
                }
                if (mc.player.collidedHorizontally) {
                    if (mc.player.onGround && (d = Method5402(1.0)) == 1.0) {
                        speedStage++;
                    }
                    if (speedStage > 0) {
                        switch (speedStage) {
                            case 1: {
                                event.setCanceled(true);

                                event.set_y(0.41999998688698);
                                int n2 = speedStage;
                                speedStage = n2 + 1;
                                return;
                            }
                            case 2: {
                                event.setCanceled(true);

                                event.set_y(0.33319999363422);
                                int n3 = speedStage;
                                speedStage = n3 + 1;
                                return;
                            }
                            case 3: {
                                float f = (float)Method718();
                                event.set_y(0.24813599859094704);
                                event.set_x((double) (-MathHelper.sin(f)) * 0.2);
                                event.set_z((double) MathHelper.cos(f) * 0.2);
                                speedStage = 0;
                                mc.player.motionY = 0.0;
                                event.setCanceled(true);
                                return;
                            }
                        }
                        return;
                    }
                }
                speedStage = 0;
                if (this.Field2015 == 1 && (mc.player.moveForward != 0.0f || mc.player.moveStrafing != 0.0f)) {
                    NCPBaseSpeed = 1.35 * getBaseMoveSpeed() - 0.01;
                } else if (this.Field2015 == 2 && mc.player.collidedVertically) {
                    d = 0.4;
                    double d2 = d;
                    mc.player.motionY = d2 + isJumpBoost();
                    double d3 = d;
                    event.set_y(d3 + isJumpBoost());
                    flip = !flip;
                    NCPBaseSpeed *= flip ? 1.6835 : 1.395;
                } else if (this.Field2015 == 3) {
                    d = 0.66 * (distance - getBaseMoveSpeed());
                    NCPBaseSpeed = distance - d;
                } else {
                    List<AxisAlignedBB> list = mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, mc.player.motionY, 0.0));
                    if ((list.size() > 0 || mc.player.collidedVertically) && this.Field2015 > 0) {
                        this.Field2015 = 1;
                    }
                    NCPBaseSpeed = distance - distance / 159.0;
                }
                event.setCanceled(true);
                NCPBaseSpeed = Math.max(NCPBaseSpeed, getBaseMoveSpeed());
                Method744(event, NCPBaseSpeed);
                ++this.Field2015;
                break;
            }
		}
	}
	
	@SubscribeEvent
    public void onUpdateWalkingPlayerPre(EventPreMotion event) {
		if (this.mode.getValue() == SpeedNewModes.NCP) {
		    if (this.strafeBoost.getValue() && isBoosting){
                return;
            }
		}
		if (this.mode.getValue() == SpeedNewModes.NCP || this.mode.getValue() == SpeedNewModes.Custom) {
			double d2 = mc.player.posX - mc.player.prevPosX;
            double d3 = mc.player.posZ - mc.player.prevPosZ;
            double d4 = d2 * d2 + d3 * d3;
			distance = Math.sqrt(d4);
		}
    }

    @SubscribeEvent
    public void onMotion(UpdateWalkingPlayerEvent event) {
        if (event.getStage() == 1) {
            return;
        }
		if (this.sprintPacket.getValue().booleanValue() && !Speed.mc.player.isSprinting()) {
            if (Speed.mc.getConnection() != null) {
                Speed.mc.getConnection().getNetworkManager().sendPacket(new CPacketEntityAction(Speed.mc.player, CPacketEntityAction.Action.START_SPRINTING));
            }
        }
		switch (this.mode.getValue()) {
            case Custom: {
                if (MovementUtil.isMoving((EntityLivingBase)Speed.mc.player)) {
                    if (Speed.mc.player.onGround) {
						if (this.accelerate.getValue()) {
                            EntityUtil.moveEntityStrafe(this.onGroundSpeed.getValue().floatValue() + (getBaseMoveSpeed() * this.acceleration.getValue().floatValue()), (Entity)Speed.mc.player);
						} else {
							EntityUtil.moveEntityStrafe(this.onGroundSpeed.getValue().floatValue(), (Entity)Speed.mc.player);
						}
						Trillium.TIMER = this.groundTimer.getValue().floatValue();
                        if (this.autoJump.getValue().booleanValue()) {
                            Speed.mc.player.motionY = this.jumpMotionY.getValue().floatValue();
					    	break;
						}
                    }
					if (Speed.mc.player.motionY > 0) {
						Trillium.TIMER = this.upTimerValue.getValue();
						if (this.usMode.getValue() == UpSpeed.Custom) {
							if (this.accelerate.getValue()) {
								EntityUtil.moveEntityStrafe(this.upAirSpeed.getValue().floatValue() + (distance - 0.66f * (distance - getBaseMoveSpeed())), (Entity)Speed.mc.player);
							} else {
						        EntityUtil.moveEntityStrafe(this.upAirSpeed.getValue().floatValue(), (Entity)Speed.mc.player);
							}
                            break;
						} else {
							EntityUtil.moveEntityStrafe(Math.sqrt(Speed.mc.player.motionX * Speed.mc.player.motionX + Speed.mc.player.motionZ * Speed.mc.player.motionZ), (Entity)Speed.mc.player);
						    break;
						}
	                } else {
						if (this.downMode.getValue() == DownMode.Timer) {
		                    Trillium.TIMER = this.downTimerValue.getValue().floatValue();
					    } else if (this.downMode.getValue() == DownMode.Motion) {
							Speed.mc.player.motionY =- this.downMotionValue.getValue().floatValue();
					    }
						if (this.dsMode.getValue() == DownSpeed.Custom) {
							if (this.accelerate.getValue()) {
						        EntityUtil.moveEntityStrafe(this.downAirSpeed.getValue().floatValue() + (distance - distance / (this.deceleration.getValue().floatValue() * 100)), (Entity)Speed.mc.player);
							} else {
								EntityUtil.moveEntityStrafe(this.downAirSpeed.getValue().floatValue(), (Entity)Speed.mc.player);
							}
                            break;
						} else {
							EntityUtil.moveEntityStrafe(Math.sqrt(Speed.mc.player.motionX * Speed.mc.player.motionX + Speed.mc.player.motionZ * Speed.mc.player.motionZ), (Entity)Speed.mc.player);
						    break;
						}
			        }
                }
				if (this.resetXZ.getValue().booleanValue()) {
                    Speed.mc.player.motionX = Speed.mc.player.motionZ = 0.0;
                    break;
				}
            }
        }
    }

    @Override
    public void onTick() {
		if (this.sprintPacket.getValue().booleanValue() && !Speed.mc.player.isSprinting()) {
            if (Speed.mc.getConnection() != null) {
                Speed.mc.getConnection().getNetworkManager().sendPacket(new CPacketEntityAction(Speed.mc.player, CPacketEntityAction.Action.START_SPRINTING));
            }
        }
		switch (this.mode.getValue()) {
            case Custom: {
                if (MovementUtil.isMoving((EntityLivingBase)Speed.mc.player)) {
                    if (Speed.mc.player.onGround) {
						if (this.accelerate.getValue()) {
                            EntityUtil.moveEntityStrafe(this.onGroundSpeed.getValue().floatValue() + (getBaseMoveSpeed() * this.acceleration.getValue().floatValue()), (Entity)Speed.mc.player);
						} else {
							EntityUtil.moveEntityStrafe(this.onGroundSpeed.getValue().floatValue(), (Entity)Speed.mc.player);
						}
						Trillium.TIMER = this.groundTimer.getValue().floatValue();
                        if (this.autoJump.getValue().booleanValue()) {
                            Speed.mc.player.motionY = this.jumpMotionY.getValue().floatValue();
					    	break;
						}
                    }
					if (Speed.mc.player.motionY > 0) {
						Trillium.TIMER = this.upTimerValue.getValue();
						if (this.usMode.getValue() == UpSpeed.Custom) {
							if (this.accelerate.getValue()) {
								EntityUtil.moveEntityStrafe(this.upAirSpeed.getValue().floatValue() + (distance - 0.66f * (distance - getBaseMoveSpeed())), (Entity)Speed.mc.player);
							} else {
						        EntityUtil.moveEntityStrafe(this.upAirSpeed.getValue().floatValue(), (Entity)Speed.mc.player);
							}
                            break;
						} else {
							EntityUtil.moveEntityStrafe(Math.sqrt(Speed.mc.player.motionX * Speed.mc.player.motionX + Speed.mc.player.motionZ * Speed.mc.player.motionZ), (Entity)Speed.mc.player);
						    break;
						}
	                } else {
						if (this.downMode.getValue() == DownMode.Timer) {
		                    Trillium.TIMER = this.downTimerValue.getValue().floatValue();
					    } else if (this.downMode.getValue() == DownMode.Motion) {
							Speed.mc.player.motionY =- this.downMotionValue.getValue().floatValue();
					    }
						if (this.dsMode.getValue() == DownSpeed.Custom) {
							if (this.accelerate.getValue()) {
						        EntityUtil.moveEntityStrafe(this.downAirSpeed.getValue().floatValue() + (distance - distance / (this.deceleration.getValue().floatValue() * 100)), (Entity)Speed.mc.player);
							} else {
								EntityUtil.moveEntityStrafe(this.downAirSpeed.getValue().floatValue(), (Entity)Speed.mc.player);
							}
                            break;
						} else {
							EntityUtil.moveEntityStrafe(Math.sqrt(Speed.mc.player.motionX * Speed.mc.player.motionX + Speed.mc.player.motionZ * Speed.mc.player.motionZ), (Entity)Speed.mc.player);
						    break;
						}
			        }
                }
				if (this.resetXZ.getValue().booleanValue()) {
                    Speed.mc.player.motionX = Speed.mc.player.motionZ = 0.0;
                    break;
				}
            }
        }
    }
	
	@SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketEntityAction) {
            if (((CPacketEntityAction) event.getPacket()).getAction().equals(CPacketEntityAction.Action.STOP_SPRINTING) || ((CPacketEntityAction) event.getPacket()).getAction().equals(CPacketEntityAction.Action.START_SNEAKING)) {
                if (this.sprintPacket.getValue().booleanValue()) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @Override
    public void onEnable() {
		this.maxVelocity = 0;
		if (this.mode.getValue() == SpeedNewModes.Custom) {
            if (this.resetXZ.getValue().booleanValue()) {
                Speed.mc.player.motionX = Speed.mc.player.motionZ = 0.0;
            }
            if (this.resetY.getValue().booleanValue()) {
                Speed.mc.player.motionX = 0.0;
            }
		}
        super.onEnable();
    }
	
	@Override
    public void onDisable() {
        Trillium.TIMER = 1.0f;
		this.NCPBaseSpeed = getBaseMoveSpeed();
        this.Field2015 = 4;
        this.distance = 0.0;
        this.speedStage = 0;
        this.velocity = 0;
		super.onDisable();
    }
	
	public static enum DownMode {
		Timer,
		Motion;
	}
	
	public static enum DownSpeed {
        Custom,
		SlowDown;
    }
	
	public static enum UpSpeed {
        Custom,
		SlowDown;
    }
	
	public static enum SpeedNewModes {
		NCP,
        Custom;
    }
}