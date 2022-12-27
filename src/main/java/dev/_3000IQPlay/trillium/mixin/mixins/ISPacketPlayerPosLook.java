package dev._3000IQPlay.trillium.mixin.mixins;

import net.minecraft.network.play.server.SPacketPlayerPosLook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SPacketPlayerPosLook.class)
public interface ISPacketPlayerPosLook {
    @Accessor(value = "yaw")
    void setYaw(float yaw);

    @Accessor(value = "pitch")
    void setPitch(float pitch);
	
	@Accessor(value="x")
    public void setX(double x);

    @Accessor(value="y")
    public void setY(double y);

    @Accessor(value="z")
    public void setZ(double z);
}