package dev._3000IQPlay.trillium.util;

import dev._3000IQPlay.trillium.command.Command;

public class CleanerThread implements Runnable {

    public CleanerThread() {

    }

    @Override
    public void run() {
        Command.sendMessage("Memory cleaner thread started!");
        System.gc();
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException ignored) {
        }
        System.gc();
        Command.sendMessage("Memory cleaner thread finished!");
    }
}
