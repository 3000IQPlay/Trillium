package dev._3000IQPlay.trillium.modules.movement;

import dev._3000IQPlay.trillium.Trillium;
import dev._3000IQPlay.trillium.event.events.EventPreMotion;
import dev._3000IQPlay.trillium.event.events.StepEvent;
import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.modules.player.FreeCam;
import dev._3000IQPlay.trillium.setting.Setting;
import dev._3000IQPlay.trillium.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityLlama;
import net.minecraft.entity.passive.EntityMule;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
* @author Doogie13, linustouchtips, aesthetical
* @since 12/27/2021
* Advenced by _3000IQPlay#8278
*/

public class Step
        extends Module {
	private static Step instance;
    private Setting<Mode> mode = this.register (new Setting<>("Mode", Mode.NCP));
    public Setting<Float> height = register(new Setting("Height", 2.0F, 1F, 2.5F, v -> this.mode.getValue() == Mode.Vanilla || this.mode.getValue() == Mode.NCP));
    public Setting<Boolean> entityStep = this.register(new Setting<>("EntityStep", false, v -> this.mode.getValue() == Mode.Vanilla || this.mode.getValue() == Mode.NCP));
	public Setting<Float> timerr = register(new Setting("Timer", 1.0F, 0.1F, 2.5F, v -> this.mode.getValue() == Mode.NCP));
    public Setting<Boolean> strict = this.register(new Setting<>("Strict", false, v -> this.mode.getValue() == Mode.NCP));
    public Setting<Integer> stepDelay = register(new Setting("StepDelay", 200, 0, 1000, v -> this.mode.getValue() == Mode.NCP));

    private boolean timer;
    private Entity entityRiding;
    private final Timer stepTimer = new Timer();
	
	public Step() {
        super("Step", "Lets you step up blocks", Module.Category.MOVEMENT, true, false, false);
		instance = this;
    }
	
	public static Step getInstance() {
        if (instance == null) {
            instance = new Step();
        }
        return instance;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        mc.player.stepHeight = 0.6F;
        if (entityRiding != null) {
            if (entityRiding instanceof EntityHorse || entityRiding instanceof EntityLlama || entityRiding instanceof EntityMule || entityRiding instanceof EntityPig && entityRiding.isBeingRidden() && ((EntityPig) entityRiding).canBeSteered()) {
                entityRiding.stepHeight = 1;
            } else {
                entityRiding.stepHeight = 0.5F;
            }
        }
    }

    @Override
    public void onUpdate() {
		if (this.mode.getValue() == Mode.Jump) {
			if (mc.player.onGround && mc.player.collidedHorizontally) {
                mc.player.jump();
			}
		}
        if (mc.player.capabilities.isFlying || Trillium.moduleManager.getModuleByClass(FreeCam.class).isOn()) {
            mc.player.stepHeight = 0.6F;
            return;
        }
        if (Jesus.isInLiquid()) {
            mc.player.stepHeight = 0.6F;
            return;
        }
        if (timer && mc.player.onGround) {
            Step.mc.timer.tickLength = 50.0f;
            timer = false;
        }
		if (this.mode.getValue() == Mode.NCP || this.mode.getValue() == Mode.Vanilla) {
			if (mc.player.onGround && stepTimer.passedMs(stepDelay.getValue())) {
				if (mc.player.isRiding() && mc.player.getRidingEntity() != null) {
					entityRiding = mc.player.getRidingEntity();
					if (entityStep.getValue()) {
						mc.player.getRidingEntity().stepHeight = height.getValue().floatValue();
					}
				} else {
					mc.player.stepHeight = height.getValue().floatValue();
				}
			} else {
				if (mc.player.isRiding() && mc.player.getRidingEntity() != null) {
					entityRiding = mc.player.getRidingEntity();
					if (entityRiding != null) {
						if (entityRiding instanceof EntityHorse || entityRiding instanceof EntityLlama || entityRiding instanceof EntityMule || entityRiding instanceof EntityPig && entityRiding.isBeingRidden() && ((EntityPig) entityRiding).canBeSteered()) {
							entityRiding.stepHeight = 1;
						} else {
							entityRiding.stepHeight = 0.5F;
						}
					}
				} else {
					mc.player.stepHeight = 0.6F;
				}
			}
		}
    }

    @SubscribeEvent
    public void onStep(StepEvent event) {
        if (this.mode.getValue() == Mode.NCP) {
            double stepHeight = event.getAxisAlignedBB().minY - mc.player.posY;
            if (stepHeight <= 0 || stepHeight > height.getValue()) {
                return;
            }
            double[] offsets = getOffset(stepHeight);
            if (offsets != null && offsets.length > 1) {
			    Step.mc.timer.tickLength = 50.0f / this.timerr.getValue();
                timer = true;
                for (double offset : offsets) {
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + offset, mc.player.posZ, false));
                }
            }
            stepTimer.reset();
        }
    }

    public double[] getOffset(double height) {
        if (height == 0.75) {
            if (strict.getValue()) {
                return new double[] {0.42, 0.753, 0.75};}
            else {return new double[] {0.42, 0.753};}
        }

        else if (height == 0.8125) {
            if (strict.getValue()) {
                return new double[] {0.39, 0.7, 0.8125};}
            else {return new double[] {0.39, 0.7};}
        }
        else if (height == 0.875) {
            if (strict.getValue()) {
                return new double[] {0.39, 0.7, 0.875};
            }

            else {return new double[] {0.39, 0.7};}
        }
        else if (height == 1) {
            if (strict.getValue()) {return new double[] {0.42, 0.753, 1};}
            else {return new double[] {0.42, 0.753};}
        }
        else if (height == 1.5) {
            return new double[] {0.42, 0.75, 1.0, 1.16, 1.23, 1.2};
        }
        else if (height == 2) {
            return new double[] {0.42, 0.78, 0.63, 0.51, 0.9, 1.21, 1.45, 1.43};
        }
        else if (height == 2.5) {
            return new double[] {0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869, 2.019, 1.907};
        }

        return null;
    }
	
	public static enum Mode {
		Vanilla,
		Jump,
        NCP;
    }
}