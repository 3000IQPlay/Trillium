package dev._3000IQPlay.trillium.command.commands;

import dev._3000IQPlay.trillium.command.Command;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;

public class TpCommand extends Command {

    public TpCommand() {
        super("tp", new String[]{"<int>", "<int>", "<int>"});
    }

    @Override
    public void execute(String[] commands) {
        if (commands.length == 1) {
            Command.sendMessage("Try .tp <X> <Y> <Z>");
            return;
        }
        if (commands.length > 2) {
            BlockPos pos = new BlockPos(Integer.valueOf(commands[0]),Integer.valueOf(commands[1]),Integer.valueOf(commands[2]));

            for (int i = 0; i < 10; ++i) {
                this.mc.player.connection.sendPacket(new CPacketPlayer.Position(pos.x, 1 + pos.y, pos.z, false));
            }
            mc.player.setPosition(pos.getX(), pos.getY() + 1, pos.getZ());

            Command.sendMessage("Teleporting to X: " + pos.x + " Y: " + pos.y + " Z: " + pos.z);
        }
    }
}
