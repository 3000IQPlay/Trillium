package dev._3000IQPlay.trillium.modules.player;

import dev._3000IQPlay.trillium.event.events.PacketEvent;
import dev._3000IQPlay.trillium.event.events.PushEvent;
import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.Setting;
import dev._3000IQPlay.trillium.util.MathUtil;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.network.play.server.SPacketSetPassengers;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FreeCam extends Module {
    private static FreeCam INSTANCE;

    static {
        FreeCam.INSTANCE = new FreeCam();
    }

    public Setting<Float> speed = this.register(new Setting<>("Speed", 0.5f, 0.1f, 5.0f));
    public Setting<Boolean> view = this.register(new Setting<>("3D", false));
    public Setting<Boolean> packet = this.register(new Setting<>("Packet", true));
    public Setting<Boolean> disable = this.register(new Setting<>("Logout/Off", true));
    public Setting<Boolean> legit = this.register(new Setting<>("Legit", false));
    private AxisAlignedBB oldBoundingBox;
    private EntityOtherPlayerMP entity;
    private Vec3d position;
    private Entity riding;
    private float yaw;
    private float pitch;
	
	public boolean wasStrafeEnabled;

    public FreeCam() {
        super("FreeCam", "Look around freely.", Module.Category.PLAYER, true, false, false);
        this.setInstance();
    }

    public static FreeCam getInstance() {
        if (FreeCam.INSTANCE == null) {
            FreeCam.INSTANCE = new FreeCam();
        }
        return FreeCam.INSTANCE;
    }

    private void setInstance() {
        FreeCam.INSTANCE = this;
    }

    @Override
    public void onEnable() {
        if (mc.player != null || mc.world != null) {
            this.oldBoundingBox = FreeCam.mc.player.getEntityBoundingBox();
            FreeCam.mc.player.setEntityBoundingBox(new AxisAlignedBB(FreeCam.mc.player.posX, FreeCam.mc.player.posY, FreeCam.mc.player.posZ, FreeCam.mc.player.posX, FreeCam.mc.player.posY, FreeCam.mc.player.posZ));
            if (FreeCam.mc.player.getRidingEntity() != null) {
                this.riding = FreeCam.mc.player.getRidingEntity();
                FreeCam.mc.player.dismountRidingEntity();
            }
            (this.entity = new EntityOtherPlayerMP(FreeCam.mc.world, FreeCam.mc.session.getProfile())).copyLocationAndAnglesFrom(FreeCam.mc.player);
            this.entity.rotationYaw = FreeCam.mc.player.rotationYaw;
            this.entity.rotationYawHead = FreeCam.mc.player.rotationYawHead;
            this.entity.inventory.copyInventory(FreeCam.mc.player.inventory);
            FreeCam.mc.world.addEntityToWorld(69420, this.entity);
            this.position = FreeCam.mc.player.getPositionVector();
            this.yaw = FreeCam.mc.player.rotationYaw;
            this.pitch = FreeCam.mc.player.rotationPitch;
            FreeCam.mc.player.noClip = true;
        }
    }

    @Override
    public void onDisable() {
        if (mc.player != null || mc.world != null) {
            FreeCam.mc.player.setEntityBoundingBox(this.oldBoundingBox);
            if (this.riding != null) {
                FreeCam.mc.player.startRiding(this.riding, true);
            }
            if (this.entity != null) {
                FreeCam.mc.world.removeEntity(this.entity);
            }
            if (this.position != null) {
                FreeCam.mc.player.setPosition(this.position.x, this.position.y, this.position.z);
            }
            FreeCam.mc.player.rotationYaw = this.yaw;
            FreeCam.mc.player.rotationPitch = this.pitch;
            FreeCam.mc.player.noClip = false;
        }
    }

    @Override
    public void onUpdate() {
        FreeCam.mc.player.noClip = true;
        FreeCam.mc.player.setVelocity(0.0, 0.0, 0.0);
        FreeCam.mc.player.jumpMovementFactor = this.speed.getValue().floatValue();
        final double[] dir = MathUtil.directionSpeed(this.speed.getValue());
        if (FreeCam.mc.player.movementInput.moveStrafe != 0.0f || FreeCam.mc.player.movementInput.moveForward != 0.0f) {
            FreeCam.mc.player.motionX = dir[0];
            FreeCam.mc.player.motionZ = dir[1];
        } else {
            FreeCam.mc.player.motionX = 0.0;
            FreeCam.mc.player.motionZ = 0.0;
        }
        FreeCam.mc.player.setSprinting(false);
        if (this.view.getValue() && !FreeCam.mc.gameSettings.keyBindSneak.isKeyDown() && !FreeCam.mc.gameSettings.keyBindJump.isKeyDown()) {
            FreeCam.mc.player.motionY = this.speed.getValue() * -MathUtil.degToRad(FreeCam.mc.player.rotationPitch) * FreeCam.mc.player.movementInput.moveForward;
        }
        if (FreeCam.mc.gameSettings.keyBindJump.isKeyDown()) {
            final EntityPlayerSP player = FreeCam.mc.player;
            player.motionY += this.speed.getValue();
        }
        if (FreeCam.mc.gameSettings.keyBindSneak.isKeyDown()) {
            final EntityPlayerSP player2 = FreeCam.mc.player;
            player2.motionY -= this.speed.getValue();
        }
    }

    @Override
    public void onLogout() {
        if (this.disable.getValue()) {
            this.disable();
        }
    }

    @SubscribeEvent
    public void onPacketSend(final PacketEvent.Send event) {
        if (this.legit.getValue() && this.entity != null && event.getPacket() instanceof CPacketPlayer) {
            final CPacketPlayer packetPlayer = event.getPacket();
            packetPlayer.x = this.entity.posX;
            packetPlayer.y = this.entity.posY;
            packetPlayer.z = this.entity.posZ;
            return;
        }
        if (this.packet.getValue()) {
            if (event.getPacket() instanceof CPacketPlayer) {
                event.setCanceled(true);
            }
        } else if (!(event.getPacket() instanceof CPacketUseEntity) && !(event.getPacket() instanceof CPacketPlayerTryUseItem) && !(event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) && !(event.getPacket() instanceof CPacketPlayer) && !(event.getPacket() instanceof CPacketVehicleMove) && !(event.getPacket() instanceof CPacketChatMessage) && !(event.getPacket() instanceof CPacketKeepAlive)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onPacketReceive(final PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketSetPassengers) {
            final SPacketSetPassengers packet = event.getPacket();
            final Entity riding = FreeCam.mc.world.getEntityByID(packet.getEntityId());
            if (riding != null && riding == this.riding) {
                this.riding = null;
            }
        }
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            final SPacketPlayerPosLook packet2 = event.getPacket();
            if (this.packet.getValue()) {
                if (this.entity != null) {
                    this.entity.setPositionAndRotation(packet2.getX(), packet2.getY(), packet2.getZ(), packet2.getYaw(), packet2.getPitch());
                }
                this.position = new Vec3d(packet2.getX(), packet2.getY(), packet2.getZ());
                FreeCam.mc.player.connection.sendPacket(new CPacketConfirmTeleport(packet2.getTeleportId()));
                event.setCanceled(true);
            } else {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onPush(final PushEvent event) {
        if (event.getStage() == 1) {
            event.setCanceled(true);
        }
    }
}