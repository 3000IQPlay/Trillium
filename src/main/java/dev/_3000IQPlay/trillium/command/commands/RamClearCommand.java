package dev._3000IQPlay.trillium.command.commands;

import dev._3000IQPlay.trillium.command.Command;
import dev._3000IQPlay.trillium.util.CleanerThread;

public class RamClearCommand extends Command {
    public RamClearCommand() {
        super("clearram");
    }

    @Override
    public void execute(String[] var1) {
        Runnable runnable = new CleanerThread();
        Thread gcThread = new Thread(runnable, "MemoryCleaner GC Thread");
        gcThread.setDaemon(true);
        gcThread.start();
    }
}