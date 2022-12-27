package dev._3000IQPlay.trillium.mixin.mixins;

import dev._3000IQPlay.trillium.mixin.ducks.ISPacketSpawnObject;
import net.minecraft.network.play.server.SPacketSpawnObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(SPacketSpawnObject.class)
public abstract class MixinSPacketSpawnObject implements ISPacketSpawnObject {
    @Unique
    private boolean attacked;

    @Override
    public void setAttacked(boolean attacked) {
        this.attacked = attacked;
    }

    @Override
    public boolean isAttacked() {
        return attacked;
    }

}