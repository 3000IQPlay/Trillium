package dev._3000IQPlay.trillium.mixin.mixins;

import dev._3000IQPlay.trillium.Trillium;
import dev._3000IQPlay.trillium.modules.misc.SolidBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockVine;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import javax.annotation.Nullable;

@Mixin(value={BlockVine.class})
public class MixinBlockVine
extends Block {
    protected MixinBlockVine() {
        super(Material.VINE);
    }

    @Nullable
    @Overwrite
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        if (Trillium.moduleManager.getModuleByClass(SolidBlock.class).isEnabled()) {
			if (SolidBlock.getInstance().vine.getValue().booleanValue()) {
                return FULL_BLOCK_AABB;
		    }
        }
        return NULL_AABB;
    }
}