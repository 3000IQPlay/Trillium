package dev._3000IQPlay.trillium.manager;

import dev._3000IQPlay.trillium.modules.Feature;
import net.minecraft.network.play.client.CPacketPlayer;

public class MovementManager
        extends Feature {
	private double x;
    private double y;
    private double z;
    private boolean onground;
	
    public void setMotion(double d, double d2, double d3) {
        if (MovementManager.mc.player != null) {
            if (MovementManager.mc.player.isRiding()) {
                MovementManager.mc.player.ridingEntity.motionX = d;
                MovementManager.mc.player.ridingEntity.motionY = d2;
                MovementManager.mc.player.ridingEntity.motionZ = d;
            } else {
                MovementManager.mc.player.motionX = d;
                MovementManager.mc.player.motionY = d2;
                MovementManager.mc.player.motionZ = d3;
            }
        }
    }

    public void updatePosition() {
        this.x = MovementManager.mc.player.posX;
        this.y = MovementManager.mc.player.posY;
        this.z = MovementManager.mc.player.posZ;
        this.onground = MovementManager.mc.player.onGround;
    }

    public void restorePosition() {
        MovementManager.mc.player.posX = this.x;
        MovementManager.mc.player.posY = this.y;
        MovementManager.mc.player.posZ = this.z;
        MovementManager.mc.player.onGround = this.onground;
    }

    public void setPlayerPosition(double x, double y, double z) {
        MovementManager.mc.player.posX = x;
        MovementManager.mc.player.posY = y;
        MovementManager.mc.player.posZ = z;
    }

    public void setPlayerPosition(double x, double y, double z, boolean onground) {
        MovementManager.mc.player.posX = x;
        MovementManager.mc.player.posY = y;
        MovementManager.mc.player.posZ = z;
        MovementManager.mc.player.onGround = onground;
    }

    public void setPositionPacket(double x, double y, double z, boolean onGround, boolean setPos, boolean noLagBack) {
        MovementManager.mc.player.connection.sendPacket(new CPacketPlayer.Position(x, y, z, onGround));
        if (setPos) {
            MovementManager.mc.player.setPosition(x, y, z);
            if (noLagBack) {
                this.updatePosition();
            }
        }
    }

    public double getX() {
        return this.x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return this.y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return this.z;
    }

    public void setZ(double z) {
        this.z = z;
    }
}