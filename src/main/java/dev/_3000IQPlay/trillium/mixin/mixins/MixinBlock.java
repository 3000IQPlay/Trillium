package dev._3000IQPlay.trillium.mixin.mixins;

import net.minecraft.block.state.*;
import net.minecraft.world.*;
import org.spongepowered.asm.mixin.*;
import net.minecraft.util.math.*;
import net.minecraft.block.*;

@Mixin({ Block.class })
public abstract class MixinBlock {
    @Shadow
    @Deprecated
    public abstract float getBlockHardness(final IBlockState p0,  final World p1,  final BlockPos p2);
}