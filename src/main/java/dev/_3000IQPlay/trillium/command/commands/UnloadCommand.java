package dev._3000IQPlay.trillium.command.commands;

import dev._3000IQPlay.trillium.Trillium;
import dev._3000IQPlay.trillium.command.Command;

public class UnloadCommand
        extends Command {
    public UnloadCommand() {
        super("unload", new String[0]);
    }

    @Override
    public void execute(String[] commands) {
        Trillium.unload(true);
    }
}

