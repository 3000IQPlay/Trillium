package dev._3000IQPlay.trillium.mixin.mixins;

import dev._3000IQPlay.trillium.Trillium;
import dev._3000IQPlay.trillium.modules.player.AntiPlantStomp;
import net.minecraft.block.BlockFarmland;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockFarmland.class)
public class MixinBlockFarmland {
    @Inject(method = "onFallenUpon", at = @At("HEAD"), cancellable = true)
    public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance, CallbackInfo ci) {
        if (Trillium.moduleManager.getModuleByClass(AntiPlantStomp.class).isEnabled() && entityIn.equals(Minecraft.getMinecraft().player)) {
            ci.cancel();
        }
    }
}
