package dev._3000IQPlay.trillium.modules.movement;

import dev._3000IQPlay.trillium.command.Command;
import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.Setting;
import dev._3000IQPlay.trillium.event.events.PacketEvent;
import dev._3000IQPlay.trillium.event.events.EventPlayerTravel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraft.network.play.server.SPacketDisconnect;
import net.minecraft.network.play.server.SPacketEntity;
import net.minecraft.network.play.server.SPacketEntityAttach;
import net.minecraft.network.play.server.SPacketMoveVehicle;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import io.netty.util.internal.ConcurrentSet;
import java.util.Iterator;

public class BoatFly
        extends Module {
    private Setting<Mode> modos = this.register(new Setting<Mode>("Mode", Mode.Packet));
    private Setting<Float> speed = this.register(new Setting<Float>("Speed", Float.valueOf(2.0f), Float.valueOf(0.0f), Float.valueOf(45.0f)));
    private Setting<Float> ySpeed = this.register(new Setting<Float>("YSpeed", Float.valueOf(1.0f), Float.valueOf(0.0f), Float.valueOf(10.0f)));
    private Setting<Float> gSpeed = this.register(new Setting<Float>("GlideSpeed", Float.valueOf(1.0f), Float.valueOf(0.0f), Float.valueOf(10.0f)));
    private Setting<Float> timer = this.register(new Setting<Float>("Timer", Float.valueOf(1.0f), Float.valueOf(0.0f), Float.valueOf(5.0f)));
    private Setting<Float> height = this.register(new Setting<Float>("Height", Float.valueOf(127.0f), Float.valueOf(0.0f), Float.valueOf(256.0f)));
    private Setting<Float> offset = this.register(new Setting<Float>("Offset", Float.valueOf(0.1f), Float.valueOf(0.0f), Float.valueOf(10.0f)));
    private Setting<Integer> eTicks = this.register(new Setting<Integer>("EnableTicks", 10, 1, 100));
    private Setting<Integer> wTicks = this.register(new Setting<Integer>("WaitTicks", 10, 1, 100));
    public Setting<Boolean> strict = this.register(new Setting<Boolean>("Strict", false));
    public Setting<Boolean> limit = this.register(new Setting<Boolean>("Limit", true));
    public Setting<Boolean> phase = this.register(new Setting<Boolean>("Phase", true));
    public Setting<Boolean> gravity = this.register(new Setting<Boolean>("Gravity", true));
    public Setting<Boolean> onGroundPackets = this.register(new Setting<Boolean>("OnGroundPacket", false));
    public Setting<Boolean> spoofPackets = this.register(new Setting<Boolean>("SpoofPackets", false));
    public Setting<Boolean> cancelRotations = this.register(new Setting<Boolean>("CancelRotations", true));
    public Setting<Boolean> cancel = this.register(new Setting<Boolean>("Cancel", true));
    public Setting<Boolean> remount = this.register(new Setting<Boolean>("Remount", true));
    public Setting<Boolean> stop = this.register(new Setting<Boolean>("Stop", false));
    public Setting<Boolean> yLimit = this.register(new Setting<Boolean>("yLimit", false));
    public Setting<Boolean> debug = this.register(new Setting<Boolean>("Debug", false));
    public Setting<Boolean> autoMount = this.register(new Setting<Boolean>("AutoMount", true));
    public Setting<Boolean> stopUnloaded = this.register(new Setting<Boolean>("StopUnloaded", true));
    private final ConcurrentSet Field23 = new ConcurrentSet();
    private int Field24 = 0;
    private int Field25 = 0;
    private boolean Field26 = false;
    private boolean Field27 = false;
    private boolean Field28 = false;

    public BoatFly() {
        super("BoatFly", "Truns your boat into a Dangerous Dragon", Module.Category.MOVEMENT, true, false, false);
    }

    @Override
    public void onEnable() {
        if (BoatFly.mc.player == null || BoatFly.mc.player.world == null) {
            this.disable();
            return;
        }
        if (this.autoMount.getValue().booleanValue()) {
            this.getIntoBoat();
        }
    }

    @Override
    public void onUpdate() {
        BoatFly.mc.timer.tickLength = 50.0f / 1.0f;
        this.Field23.clear();
        this.Field26 = false;
        if (BoatFly.mc.player == null) {
            return;
        }
        if (this.phase.getValue().booleanValue() && this.modos.getValue() == Mode.Motion) {
            if (BoatFly.mc.player.getRidingEntity() != null) {
                BoatFly.mc.player.getRidingEntity().noClip = false;
            }
            BoatFly.mc.player.noClip = false;
        }
        if (BoatFly.mc.player.getRidingEntity() != null) {
            BoatFly.mc.player.getRidingEntity().setNoGravity(false);
        }
        BoatFly.mc.player.setNoGravity(false);
    }

    private float Method4() {
        this.Field28 = !this.Field28;
        return this.Field28 ? this.offset.getValue().floatValue() : -this.offset.getValue().floatValue();
    }

    private void Method5(CPacketVehicleMove cPacketVehicleMove) {
        this.Field23.add((Object)cPacketVehicleMove);
        BoatFly.mc.player.connection.sendPacket((Packet)cPacketVehicleMove);
    }

    private void Method6(Entity entity) {
        double d = entity.posY;
        BlockPos blockPos = new BlockPos(entity.posX, (double)((int)entity.posY), entity.posZ);
        for (int i = 0; i < 255; ++i) {
            if (!BoatFly.mc.world.getBlockState(blockPos).getMaterial().isReplaceable() || BoatFly.mc.world.getBlockState(blockPos).getBlock() == Blocks.WATER) {
                entity.posY = blockPos.getY() + 1;
                if (this.debug.getValue().booleanValue()) {
                    Command.sendMessage("GroundY" + entity.posY);
                }
                this.Method5(new CPacketVehicleMove(entity));
                entity.posY = d;
                break;
            }
            blockPos = blockPos.add(0, -1, 0);
        }
    }

    private void getIntoBoat() {
        Iterator iterator = BoatFly.mc.world.loadedEntityList.iterator();
        while (iterator.hasNext()) {
            Entity entity = (Entity)iterator.next();
            if (!(entity instanceof EntityBoat) || !(BoatFly.mc.player.getDistance(entity) < 5.0f)) continue;
            BoatFly.mc.player.connection.sendPacket((Packet)new CPacketUseEntity(entity, EnumHand.MAIN_HAND));
            break;
        }
    }

    public static double[] Method8(double d) {
        float f = BoatFly.mc.player.movementInput.moveForward;
        float f2 = BoatFly.mc.player.movementInput.moveStrafe;
        float f3 = BoatFly.mc.player.prevRotationYaw + (BoatFly.mc.player.rotationYaw - BoatFly.mc.player.prevRotationYaw) * BoatFly.mc.getRenderPartialTicks();
        if (f != 0.0f) {
            if (f2 > 0.0f) {
                f3 += (float)(f > 0.0f ? -45 : 45);
            } else if (f2 < 0.0f) {
                f3 += (float)(f > 0.0f ? 45 : -45);
            }
            f2 = 0.0f;
            if (f > 0.0f) {
                f = 1.0f;
            } else if (f < 0.0f) {
                f = -1.0f;
            }
        }
        double d2 = Math.sin(Math.toRadians(f3 + 90.0f));
        double d3 = Math.cos(Math.toRadians(f3 + 90.0f));
        double d4 = (double)f * d * d3 + (double)f2 * d * d2;
        double d5 = (double)f * d * d2 - (double)f2 * d * d3;
        return new double[]{d4, d5};
    }

    @SubscribeEvent
    public void Method9(EventPlayerTravel eventPlayerTravel) {
        if (BoatFly.fullNullCheck()) {
            return;
        }
        if (BoatFly.mc.player.getRidingEntity() == null) {
            if (this.autoMount.getValue().booleanValue()) {
                this.getIntoBoat();
            }
            return;
        }
        if (this.phase.getValue().booleanValue() && this.modos.getValue() == Mode.Motion) {
            BoatFly.mc.player.getRidingEntity().noClip = true;
            BoatFly.mc.player.getRidingEntity().setNoGravity(true);
            BoatFly.mc.player.noClip = true;
        }
        if (!this.Field27) {
            BoatFly.mc.player.getRidingEntity().setNoGravity(this.gravity.getValue() == false);
            BoatFly.mc.player.setNoGravity(this.gravity.getValue() == false);
        }
        if (this.stop.getValue().booleanValue()) {
            if (this.Field24 > this.eTicks.getValue() && !this.Field26) {
                this.Field24 = 0;
                this.Field26 = true;
                this.Field25 = this.wTicks.getValue();
            }
            if (this.Field25 > 0 && this.Field26) {
                --this.Field25;
                return;
            }
            if (this.Field25 <= 0) {
                this.Field26 = false;
            }
        }
        Entity entity = BoatFly.mc.player.getRidingEntity();
        if (this.debug.getValue().booleanValue()) {
            Command.sendMessage("Y" + entity.posY);
            Command.sendMessage("Fall" + entity.fallDistance);
        }
        if ((!BoatFly.mc.world.isChunkGeneratedAt(entity.getPosition().getX() >> 4, entity.getPosition().getZ() >> 4) || entity.getPosition().getY() < 0) && this.stopUnloaded.getValue().booleanValue()) {
            if (this.debug.getValue().booleanValue()) {
                Command.sendMessage("Detected unloaded chunk!");
            }
            this.Field27 = true;
            return;
        }
        if (this.timer.getValue().floatValue() != 1.0f) {
            BoatFly.mc.timer.tickLength = 50.0f / this.timer.getValue().floatValue();
        }
        entity.rotationYaw = BoatFly.mc.player.rotationYaw;
        double[] dArray = BoatFly.Method8(this.speed.getValue().floatValue());
        double d = entity.posX + dArray[0];
        double d2 = entity.posZ + dArray[1];
        double d3 = entity.posY;
        if ((!BoatFly.mc.world.isChunkGeneratedAt((int)d >> 4, (int)d2 >> 4) || entity.getPosition().getY() < 0) && this.stopUnloaded.getValue().booleanValue()) {
            if (this.debug.getValue().booleanValue()) {
                Command.sendMessage("Detected unloaded chunk!");
            }
            this.Field27 = true;
            return;
        }
        this.Field27 = false;
        entity.motionY = -(this.gSpeed.getValue().floatValue() / 100.0f);
        if (this.modos.getValue() == Mode.Motion) {
            entity.motionX = dArray[0];
            entity.motionZ = dArray[1];
        }
        if (BoatFly.mc.player.movementInput.jump) {
            if (!this.yLimit.getValue().booleanValue() || entity.posY <= (double)this.height.getValue().floatValue()) {
                if (this.modos.getValue() == Mode.Motion) {
                    entity.motionY += (double)this.ySpeed.getValue().floatValue();
                } else {
                    d3 += (double)this.ySpeed.getValue().floatValue();
                }
            }
        } else if (BoatFly.mc.player.movementInput.sneak) {
            if (this.modos.getValue() == Mode.Motion) {
                entity.motionY += (double)(-this.ySpeed.getValue().floatValue());
            } else {
                d3 += (double)(-this.ySpeed.getValue().floatValue());
            }
        }
        if (BoatFly.mc.player.movementInput.moveStrafe == 0.0f && BoatFly.mc.player.movementInput.moveForward == 0.0f) {
            entity.motionX = 0.0;
            entity.motionZ = 0.0;
        }
        if (this.onGroundPackets.getValue().booleanValue()) {
            this.Method6(entity);
        }
        if (this.modos.getValue() != Mode.Motion) {
            entity.setPosition(d, d3, d2);
        }
        if (this.modos.getValue() == Mode.Packet) {
            this.Method5(new CPacketVehicleMove(entity));
        }
        if (this.strict.getValue().booleanValue()) {
            BoatFly.mc.player.connection.sendPacket((Packet)new CPacketClickWindow(0, 0, 0, ClickType.CLONE, ItemStack.EMPTY, (short) 0));
        }
        if (this.spoofPackets.getValue().booleanValue()) {
            Vec3d vec3d = entity.getPositionVector().add(0.0, (double)this.Method4(), 0.0);
            EntityBoat entityBoat = new EntityBoat((World)BoatFly.mc.world, vec3d.x, vec3d.y, vec3d.z);
            entityBoat.rotationYaw = entity.rotationYaw;
            entityBoat.rotationPitch = entity.rotationPitch;
            this.Method5(new CPacketVehicleMove((Entity)entityBoat));
        }
        if (this.remount.getValue().booleanValue()) {
            BoatFly.mc.player.connection.sendPacket((Packet)new CPacketUseEntity(entity, EnumHand.MAIN_HAND));
        }
        eventPlayerTravel.setCanceled(true);
        ++this.Field24;
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send eventNetworkPrePacketEvent) {
        if (BoatFly.fullNullCheck()) {
            return;
        }
        if (eventNetworkPrePacketEvent.getPacket() instanceof SPacketDisconnect) {
            this.disable();
        }
        if (!BoatFly.mc.player.isRiding() || this.Field27 || this.Field26) {
            return;
        }
        if (eventNetworkPrePacketEvent.getPacket() instanceof SPacketMoveVehicle && BoatFly.mc.player.isRiding() && this.cancel.getValue().booleanValue()) {
            eventNetworkPrePacketEvent.setCanceled(true);
        }
        if (eventNetworkPrePacketEvent.getPacket() instanceof SPacketPlayerPosLook && BoatFly.mc.player.isRiding() && this.cancel.getValue().booleanValue()) {
            eventNetworkPrePacketEvent.setCanceled(true);
        }
        if (eventNetworkPrePacketEvent.getPacket() instanceof SPacketEntity && this.cancel.getValue().booleanValue()) {
            eventNetworkPrePacketEvent.setCanceled(true);
        }
        if (eventNetworkPrePacketEvent.getPacket() instanceof SPacketEntityAttach && this.cancel.getValue().booleanValue()) {
            eventNetworkPrePacketEvent.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive eventNetworkPostPacketEvent) {
        if (BoatFly.fullNullCheck()) {
            return;
        }
        if ((eventNetworkPostPacketEvent.getPacket() instanceof CPacketPlayer.Rotation && this.cancelRotations.getValue().booleanValue() || eventNetworkPostPacketEvent.getPacket() instanceof CPacketInput) && BoatFly.mc.player.isRiding()) {
            eventNetworkPostPacketEvent.setCanceled(true);
        }
        if (this.Field27 && eventNetworkPostPacketEvent.getPacket() instanceof CPacketVehicleMove) {
            eventNetworkPostPacketEvent.setCanceled(true);
        }
        if (!BoatFly.mc.player.isRiding() || this.Field27 || this.Field26) {
            return;
        }
        Entity entity = BoatFly.mc.player.getRidingEntity();
        if ((!BoatFly.mc.world.isChunkGeneratedAt(entity.getPosition().getX() >> 4, entity.getPosition().getZ() >> 4) || entity.getPosition().getY() < 0) && this.stopUnloaded.getValue().booleanValue()) {
            return;
        }
        if (eventNetworkPostPacketEvent.getPacket() instanceof CPacketVehicleMove && this.limit.getValue().booleanValue() && this.modos.getValue() == Mode.Packet) {
            CPacketVehicleMove cPacketVehicleMove = (CPacketVehicleMove)eventNetworkPostPacketEvent.getPacket();
            if (this.Field23.contains((Object)cPacketVehicleMove)) {
                this.Field23.remove((Object)cPacketVehicleMove);
            } else {
                eventNetworkPostPacketEvent.setCanceled(true);
            }
        }
    }

    public static enum  Mode {
        Packet,
		Motion;
    }
}
