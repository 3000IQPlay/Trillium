package dev._3000IQPlay.trillium.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import dev._3000IQPlay.trillium.command.Command;
import dev._3000IQPlay.trillium.Trillium;

public class HelpCommand
        extends Command {
    public HelpCommand() {
        super("help");
    }

    @Override
    public void execute(String[] commands) {
        HelpCommand.sendMessage("Commands: ");
        for (Command command : Trillium.commandManager.getCommands()) {
            HelpCommand.sendMessage(ChatFormatting.GRAY + Trillium.commandManager.getPrefix() + command.getName());
        }
    }
}

