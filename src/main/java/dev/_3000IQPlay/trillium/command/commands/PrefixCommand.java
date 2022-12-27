package dev._3000IQPlay.trillium.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import dev._3000IQPlay.trillium.Trillium;
import dev._3000IQPlay.trillium.command.Command;

public class PrefixCommand
        extends Command {
    public PrefixCommand() {
        super("prefix", new String[]{"<char>"});
    }

    @Override
    public void execute(String[] commands) {
        if (commands.length == 1) {
            Command.sendMessage(ChatFormatting.GREEN + "Current prefix:" + Trillium.commandManager.getPrefix());
            return;
        }
        Trillium.commandManager.setPrefix(commands[0]);
        Command.sendMessage("Prefix changed to: " + ChatFormatting.GRAY + commands[0]);
    }
}

