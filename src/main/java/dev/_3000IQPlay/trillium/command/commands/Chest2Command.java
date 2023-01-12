package dev._3000IQPlay.trillium.command.commands;

import dev._3000IQPlay.trillium.command.Command;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.math.BlockPos;

public class Chest2Command
        extends Command {
    public Chest2Command() {
        super("chest2");
    }

	public static BlockPos POS;
	
    @Override
    public void execute(String[] strings) {
        if (mc.world == null && mc.player == null) return;
        if (mc.world.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock() == Blocks.CHEST) {
            POS = mc.objectMouseOver.getBlockPos();
            Command.sendMessage("Set " + mc.objectMouseOver.getBlockPos() + " as 2. chest");
        }
    }
}