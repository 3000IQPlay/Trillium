package dev._3000IQPlay.trillium.command.commands;

import dev._3000IQPlay.trillium.command.Command;

import dev._3000IQPlay.trillium.util.TrilliumUtils;

import java.util.ArrayList;

public class ChangeSkinCommand extends Command {
    public ChangeSkinCommand() {
        super("skinset", new String[]{"<name>", "<skinname>"});
        this.setInstance();
    }
    private void setInstance() {
        INSTANCE = this;
    }

    public ArrayList<String> changedplayers = new ArrayList<String>();


    private static ChangeSkinCommand INSTANCE = new ChangeSkinCommand();
    public static ChangeSkinCommand getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ChangeSkinCommand();
        }
        return INSTANCE;
    }

    @Override
    public void execute(String[] commands) {
        if (commands.length == 1) {
            Command.sendMessage("skinset <playername> <skinname>");
            return;
        }
        if (commands.length == 2) {
            Command.sendMessage("skinset <playername> <skinname>");
            return;
        }
        if (commands.length == 3) {
            TrilliumUtils.savePlayerSkin("https://minotar.net/skin/" + commands[1],commands[0]);
            changedplayers.add(commands[0]);
            Command.sendMessage("Player Skin " + commands[0] + " amended to " + commands[1]);
        }
    }

}
