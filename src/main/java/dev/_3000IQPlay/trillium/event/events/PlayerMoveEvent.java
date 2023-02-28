package dev._3000IQPlay.trillium.event.events;

import dev._3000IQPlay.trillium.event.EventStage;
import net.minecraft.entity.MoverType;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class PlayerMoveEvent extends EventStage {
    private static final PlayerMoveEvent INSTANCE = new PlayerMoveEvent();

    private MoverType type;
	private float yaw;
    private float pitch;
    private double x;
    private double y;
    private double z;

    public static PlayerMoveEvent get(MoverType type, float yaw, float pitch, double x, double y, double z) {
        INSTANCE.type = type;
		INSTANCE.yaw = yaw;
		INSTANCE.pitch = pitch;
        INSTANCE.x = x;
        INSTANCE.y = y;
        INSTANCE.z = z;
        return INSTANCE;
    }

    public MoverType getType() {
        return type;
    }
	
	public float getYaw() {
        return this.yaw;
    }

    public void setYaw(float f) {
        this.yaw = f;
    }

    public float getPitch() {
        return this.pitch;
    }

    public void setPitch(float f) {
        this.pitch = f;
    }

    public void setYaw(double d) {
        this.yaw = (float)d;
    }

    public void setPitch(double d) {
        this.pitch = (float)d;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public void setType(MoverType type) {
        this.type = type;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }
}