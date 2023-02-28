package dev._3000IQPlay.trillium.command.commands;

import dev._3000IQPlay.trillium.command.Command;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;

public class HClipCommand extends Command {

        public HClipCommand() {
            super("hclip", new String[]{"<int>", "<name>"});
        }

        @Override
        public void execute(String[] commands) {
            if (commands.length == 1) {
                Command.sendMessage("Try .hclip <number>");
                return;
            }
            if (commands.length == 2) {
                try {
                    Command.sendMessage((Object)TextFormatting.GREEN + "Clipping on " + Double.valueOf(commands[0]) + " blocks");
                    float f = this.mc.player.rotationYaw * ((float)Math.PI / 180);
                    double speed = Double.valueOf(commands[0]);
                    double x = -((double) MathHelper.sin((float)f) * speed);
                    double z = (double)MathHelper.cos((float)f) * speed;
                    this.mc.player.setPosition(this.mc.player.posX + x, this.mc.player.posY, this.mc.player.posZ + z);
                }
                catch (Exception exception) {}

                return;
            }
        }
}
