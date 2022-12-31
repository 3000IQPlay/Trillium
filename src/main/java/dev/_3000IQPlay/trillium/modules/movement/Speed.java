package dev._3000IQPlay.trillium.modules.movement;

import dev._3000IQPlay.trillium.Trillium;
import dev._3000IQPlay.trillium.event.events.PacketEvent;
import dev._3000IQPlay.trillium.event.events.UpdateWalkingPlayerEvent;
import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.modules.movement.Strafe;
import dev._3000IQPlay.trillium.setting.Setting;
import dev._3000IQPlay.trillium.util.EntityUtil;
import dev._3000IQPlay.trillium.util.MovementUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Objects;

public class Speed
        extends Module {
	private static Speed instance;
    private final Setting<SpeedNewModes> mode = this.register(new Setting<SpeedNewModes>("Mode", SpeedNewModes.Custom));
	private final Setting<Float> yPortAirSpeed = this.register(new Setting<Float>("YPortAirSpeed", 0.35f, 0.2f, 5.0f, t -> this.mode.getValue().equals((Object)SpeedNewModes.YPort)));
	private final Setting<Float> yPortGroundSpeed = this.register(new Setting<Float>("YPortGroundSpeed", 0.35f, 0.2f, 5.0f, t -> this.mode.getValue().equals((Object)SpeedNewModes.YPort)));
	private final Setting<Float> yPortJumpMotionY = this.register(new Setting<Float>("YPortJumpMotionY", 0.42f, 0.0f, 4.0f, t -> this.mode.getValue().equals((Object)SpeedNewModes.YPort)));
	private final Setting<Float> yPortFallSpeed = this.register(new Setting<Float>("FallSpeed", 1.0f, 0.0f, 4.0f, t -> this.mode.getValue().equals((Object)SpeedNewModes.YPort)));
	private final Setting<Boolean> yPortTimerSpeed = this.register(new Setting<Boolean>("Timer", false, t -> this.mode.getValue().equals((Object)SpeedNewModes.YPort)));
	private final Setting<Float> yPortTimerSpeedVal = this.register(new Setting<Float>("TimerSpeed", 1.8f, 0.1f, 5.0f, t -> this.yPortTimerSpeed.getValue() && this.mode.getValue().equals((Object)SpeedNewModes.YPort)));
	
	private final Setting<Float> cpvpccSpeed = this.register(new Setting<Float>("Speed", 0.435f, 0.2f, 5.0f, t -> this.mode.getValue().equals((Object)SpeedNewModes.NCP)));
	private final Setting<Float> ccTimer = this.register(new Setting<Float>("Timer", 1.0f, 0.2f, 5.0f, t -> this.mode.getValue().equals((Object)SpeedNewModes.NCP)));
	
	private final Setting<UpSpeed> usMode = this.register(new Setting<UpSpeed>("UpSpeedType", UpSpeed.Custom, t -> this.mode.getValue().equals((Object)SpeedNewModes.Custom)));
    private final Setting<Float> upAirSpeed = this.register(new Setting<Float>("UpAirSpeed", 0.272f, 0.2f, 5.0f, t -> this.mode.getValue().equals((Object)SpeedNewModes.Custom) && this.usMode.getValue() == UpSpeed.Custom));
	private final Setting<DownSpeed> dsMode = this.register(new Setting<DownSpeed>("DownSpeedType", DownSpeed.Custom, t -> this.mode.getValue().equals((Object)SpeedNewModes.Custom)));
	private final Setting<Float> downAirSpeed = this.register(new Setting<Float>("DownAirSpeed", 0.272f, 0.2f, 5.0f, t -> this.mode.getValue().equals((Object)SpeedNewModes.Custom) && this.dsMode.getValue() == DownSpeed.Custom));
	private final Setting<Float> onGroundSpeed = this.register(new Setting<Float>("GroundSpeed", 0.272f, 0.2f, 5.0f, t -> this.mode.getValue().equals((Object)SpeedNewModes.Custom)));
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
	public boolean wasStrafeEnabled;
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
                        EntityUtil.moveEntityStrafe(this.onGroundSpeed.getValue().floatValue(), (Entity)Speed.mc.player);
						Speed.mc.timer.tickLength = 50.0f / this.groundTimer.getValue().floatValue();
                        if (this.autoJump.getValue().booleanValue()) {
                            Speed.mc.player.motionY = this.jumpMotionY.getValue().floatValue();
					    	break;
						}
                    }
					if (Speed.mc.player.motionY > 0) {
						Speed.mc.timer.tickLength = 50.0f / this.upTimerValue.getValue();
						if (this.usMode.getValue() == UpSpeed.Custom) {
						    EntityUtil.moveEntityStrafe(this.upAirSpeed.getValue().floatValue(), (Entity)Speed.mc.player);
                            break;
						} else {
							EntityUtil.moveEntityStrafe(Math.sqrt(Speed.mc.player.motionX * Speed.mc.player.motionX + Speed.mc.player.motionZ * Speed.mc.player.motionZ), (Entity)Speed.mc.player);
						    break;
						}
	                } else {
						if (this.downMode.getValue() == DownMode.Timer) {
		                    Speed.mc.timer.tickLength = 50.0f / this.downTimerValue.getValue().floatValue();
					    } else if (this.downMode.getValue() == DownMode.Motion) {
							Speed.mc.player.motionY =- this.downMotionValue.getValue().floatValue();
					    }
						if (this.dsMode.getValue() == DownSpeed.Custom) {
						    EntityUtil.moveEntityStrafe(this.downAirSpeed.getValue().floatValue(), (Entity)Speed.mc.player);
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
			case YPort: {
                if (!MovementUtil.isMoving((EntityLivingBase)Speed.mc.player) || Speed.mc.player.collidedHorizontally) {
                    return;
                }
				if (this.yPortTimerSpeed.getValue().booleanValue()) {
					Speed.mc.timer.tickLength = 50.0f / this.yPortTimerSpeedVal.getValue().floatValue();
				} else {
					Speed.mc.timer.tickLength = 50.0f / 1.0f;
				}
				if (Speed.mc.player.onGround) {
                    Speed.mc.player.motionY = this.yPortJumpMotionY.getValue().floatValue();
                    EntityUtil.moveEntityStrafe(this.yPortGroundSpeed.getValue().floatValue(), (Entity)Speed.mc.player);
                } else {
                    Speed.mc.player.motionY =- this.yPortFallSpeed.getValue().floatValue();
					EntityUtil.moveEntityStrafe(this.yPortAirSpeed.getValue().floatValue(), (Entity)Speed.mc.player);
                }
            }
			case NCP: {
			    if (MovementUtil.isMoving((EntityLivingBase)Speed.mc.player)) {
                    if (Speed.mc.player.onGround) {
                        Speed.mc.player.jump();
					    Speed.mc.timer.tickLength = 50.0f / this.ccTimer.getValue().floatValue();
						EntityUtil.moveEntityStrafe(this.cpvpccSpeed.getValue().floatValue(), (Entity)Speed.mc.player);
						break;
                    } else {
						Speed.mc.timer.tickLength = 50.0f / 1.0f;
					}
                    EntityUtil.moveEntityStrafe(Math.sqrt(Speed.mc.player.motionX * Speed.mc.player.motionX + Speed.mc.player.motionZ * Speed.mc.player.motionZ), (Entity)Speed.mc.player);
					break;
                } else {
                    Speed.mc.player.motionX = 0.0;
                    Speed.mc.player.motionZ = 0.0;
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
                        EntityUtil.moveEntityStrafe(this.onGroundSpeed.getValue().floatValue(), (Entity)Speed.mc.player);
						Speed.mc.timer.tickLength = 50.0f / this.groundTimer.getValue().floatValue();
                        if (this.autoJump.getValue().booleanValue()) {
                            Speed.mc.player.motionY = this.jumpMotionY.getValue().floatValue();
					    	break;
						}
                    }
					if (Speed.mc.player.motionY > 0) {
						Speed.mc.timer.tickLength = 50.0f / this.upTimerValue.getValue();
						if (this.usMode.getValue() == UpSpeed.Custom) {
						    EntityUtil.moveEntityStrafe(this.upAirSpeed.getValue().floatValue(), (Entity)Speed.mc.player);
                            break;
						} else {
							EntityUtil.moveEntityStrafe(Math.sqrt(Speed.mc.player.motionX * Speed.mc.player.motionX + Speed.mc.player.motionZ * Speed.mc.player.motionZ), (Entity)Speed.mc.player);
						    break;
						}
	                } else {
						if (this.downMode.getValue() == DownMode.Timer) {
		                    Speed.mc.timer.tickLength = 50.0f / this.downTimerValue.getValue().floatValue();
					    } else if (this.downMode.getValue() == DownMode.Motion) {
							Speed.mc.player.motionY =- this.downMotionValue.getValue().floatValue();
					    }
						if (this.dsMode.getValue() == DownSpeed.Custom) {
						    EntityUtil.moveEntityStrafe(this.downAirSpeed.getValue().floatValue(), (Entity)Speed.mc.player);
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
			case YPort: {
                if (!MovementUtil.isMoving((EntityLivingBase)Speed.mc.player) || Speed.mc.player.collidedHorizontally) {
                    return;
                }
				if (this.yPortTimerSpeed.getValue().booleanValue()) {
					Speed.mc.timer.tickLength = 50.0f / this.yPortTimerSpeedVal.getValue().floatValue();
				} else {
					Speed.mc.timer.tickLength = 50.0f / 1.0f;
				}
				if (Speed.mc.player.onGround) {
                    Speed.mc.player.motionY = this.yPortJumpMotionY.getValue().floatValue();
                    EntityUtil.moveEntityStrafe(this.yPortGroundSpeed.getValue().floatValue(), (Entity)Speed.mc.player);
                } else {
                    Speed.mc.player.motionY =- this.yPortFallSpeed.getValue().floatValue();
					EntityUtil.moveEntityStrafe(this.yPortAirSpeed.getValue().floatValue(), (Entity)Speed.mc.player);
                }
            }
			case NCP: {
			    if (MovementUtil.isMoving((EntityLivingBase)Speed.mc.player)) {
                    if (Speed.mc.player.onGround) {
                        Speed.mc.player.jump();
					    Speed.mc.timer.tickLength = 50.0f / this.ccTimer.getValue().floatValue();
						EntityUtil.moveEntityStrafe(this.cpvpccSpeed.getValue().floatValue(), (Entity)Speed.mc.player);
						break;
                    } else {
						Speed.mc.timer.tickLength = 50.0f / 1.0f;
					}
                    EntityUtil.moveEntityStrafe(Math.sqrt(Speed.mc.player.motionX * Speed.mc.player.motionX + Speed.mc.player.motionZ * Speed.mc.player.motionZ), (Entity)Speed.mc.player);
					break;
                } else {
                    Speed.mc.player.motionX = 0.0;
                    Speed.mc.player.motionZ = 0.0;
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
		if (Trillium.moduleManager.getModuleByClass(Strafe.class).isEnabled()) {
			Strafe.getInstance().disable();
			this.wasStrafeEnabled = true;
		} else {
			this.wasStrafeEnabled = false;
		}
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
        Speed.mc.timer.tickLength = 50.0f / 1.0f;
		if (this.wasStrafeEnabled == true) { // return shit somehow doesnt work so im making this chinese check
			Strafe.getInstance().enable();
			this.wasStrafeEnabled = false;
		} else {
			this.wasStrafeEnabled = false;
		}
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
        Custom,
		NCP,
		YPort;
    }
}