package dev._3000IQPlay.trillium.command.commands;

import dev._3000IQPlay.trillium.command.Command;
import dev._3000IQPlay.trillium.modules.client.Optimization;

public class RamClearCommand extends Command {
    public RamClearCommand() {
        super("clearram");
    }

    @Override
    public void execute(String[] var1) {
        Optimization.cleanMemory();
    }
}
