package dev._3000IQPlay.trillium.command.commands;

import dev._3000IQPlay.trillium.Trillium;
import dev._3000IQPlay.trillium.command.Command;

public class ReloadCommand
        extends Command {
    public ReloadCommand() {
        super("reload", new String[0]);
    }

    @Override
    public void execute(String[] commands) {
        Trillium.reload();
    }
}

