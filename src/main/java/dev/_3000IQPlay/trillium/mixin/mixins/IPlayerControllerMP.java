package dev._3000IQPlay.trillium.mixin.mixins;

import net.minecraft.client.multiplayer.PlayerControllerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = {PlayerControllerMP.class})
public interface IPlayerControllerMP {
    @Accessor(value = "curBlockDamageMP")
    float getCurrentBlockDamage();

    @Accessor(value = "curBlockDamageMP")
    void setCurrentBlockDamage(float var1);

    @Accessor(value = "blockHitDelay")
    void setBlockHitDelay(int var1);

    @Accessor(value = "currentPlayerItem")
    int getCurrentPlayerItem();

    @Accessor(value = "currentPlayerItem")
    void setCurrentPlayerItem(int var1);

    @Invoker(value = "syncCurrentPlayItem")
    void hookSyncCurrentPlayItem();
}