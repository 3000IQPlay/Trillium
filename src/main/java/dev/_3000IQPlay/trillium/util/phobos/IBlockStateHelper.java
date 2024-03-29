package dev._3000IQPlay.trillium.util.phobos;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public interface IBlockStateHelper extends IBlockAccess
{
    default void addAir(BlockPos pos)
    {
        this.addBlockState(pos, Blocks.AIR.getDefaultState());
    }

    void addBlockState(BlockPos pos, IBlockState state);

    void delete(BlockPos pos);

    void clearAllStates();

}