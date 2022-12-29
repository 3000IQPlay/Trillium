package dev._3000IQPlay.trillium.command.commands;

import dev._3000IQPlay.trillium.command.Command;
import dev._3000IQPlay.trillium.modules.player.FakePlayer;

public class FakePlayerCommand extends Command {

    public FakePlayerCommand() { super("fakeplayer"); }

    @Override
    public void execute(String[] commands) {
        FakePlayer.getInstance().enable();
    }
}