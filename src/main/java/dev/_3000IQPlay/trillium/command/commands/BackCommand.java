package dev._3000IQPlay.trillium.command.commands;

import dev._3000IQPlay.trillium.command.Command;
import dev._3000IQPlay.trillium.manager.EventManager;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;

public class BackCommand extends Command {
    public BackCommand() {
        super("back");
    }


    @Override
    public void execute(String[] var1) {
        if(EventManager.backX == 0 && EventManager.backY == 0 && EventManager.backZ == 0){
            return;
        }
        BlockPos pos = new BlockPos(EventManager.backX,EventManager.backY,EventManager.backZ);

        for (int i = 0; i < 10; ++i) {
            this.mc.player.connection.sendPacket(new CPacketPlayer.Position(pos.x, 1 + pos.y, pos.z, false));
        }
        mc.player.setPosition(pos.getX(), pos.getY() + 1, pos.getZ());

        Command.sendMessage("Teleporting to X: " + EventManager.backX + " Y: " + EventManager.backY + " Z: " + EventManager.backZ);
    }
}
