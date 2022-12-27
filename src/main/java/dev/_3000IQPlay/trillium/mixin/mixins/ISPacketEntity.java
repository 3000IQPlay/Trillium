package dev._3000IQPlay.trillium.mixin.mixins;

import net.minecraft.network.play.server.SPacketEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SPacketEntity.class)
public interface ISPacketEntity
{
    @Accessor("entityId")
    int getEntityId();

}