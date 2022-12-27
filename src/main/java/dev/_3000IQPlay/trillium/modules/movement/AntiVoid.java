package dev._3000IQPlay.trillium.modules.movement;

import dev._3000IQPlay.trillium.Trillium;
import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.Setting;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class AntiVoid
        extends Module {
    Setting<Mode> mode = this.register(new Setting<Mode>("Mode", Mode.MotionStop));
    Setting<Integer> distance = this.register(new Setting<Integer>("Distance", 10, 1, 256));
    Setting<Integer> height = this.register(new Setting<Object>("Height", Integer.valueOf(4), Integer.valueOf(0), Integer.valueOf(10), v -> this.mode.getValue() == Mode.Packet));
    Setting<Float> speed = this.register(new Setting<Object>("Speed", Float.valueOf(5.0f), Float.valueOf(0.1f), Float.valueOf(10.0f), v -> this.mode.getValue() == Mode.Motion || this.mode.getValue() == Mode.Glide));

    public AntiVoid() {
        super("AntiVoid", "Prevents u from falling in the void", Module.Category.MOVEMENT, true, false, false);
    }

    @Override
    public void onToggle() {
        if (AntiVoid.fullNullCheck()) {
            return;
        }
    }

    @Override
    public void onUpdate() {
        if (AntiVoid.fullNullCheck()) {
            return;
        }
        if (AntiVoid.mc.player.noClip || AntiVoid.mc.player.posY > (double)this.distance.getValue().intValue() || AntiVoid.mc.player.isRiding()) {
            return;
        }
        RayTraceResult trace = AntiVoid.mc.world.rayTraceBlocks(AntiVoid.mc.player.getPositionVector(), new Vec3d(AntiVoid.mc.player.posX, 0.0, AntiVoid.mc.player.posZ), false, false, false);
        if (trace == null || trace.typeOfHit != RayTraceResult.Type.BLOCK) {
            switch (this.mode.getValue()) {
                case MotionStop: {
                    AntiVoid.mc.player.setVelocity(0.0, 0.0, 0.0);
                    AntiVoid.mc.player.motionY = 0.0;
                    break;
                }
                case Motion: {
                    AntiVoid.mc.player.motionY = this.speed.getValue().floatValue();
                    break;
                }
                case Glide: {
                    AntiVoid.mc.player.motionY *= (double)this.speed.getValue().floatValue();
                    break;
                }
                case Packet: {
                    mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(AntiVoid.mc.player.posX, (double)this.height.getValue().intValue(), AntiVoid.mc.player.posZ, AntiVoid.mc.player.onGround));
                }
            }
        }
    }

    static enum Mode {
        MotionStop,
        Motion,
        Glide,
        Packet;
    }
}