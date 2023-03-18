package dev._3000IQPlay.trillium.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import dev._3000IQPlay.trillium.Trillium;
import dev._3000IQPlay.trillium.command.Command;

import java.awt.*;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ConfigCommand extends Command {
    public ConfigCommand() {
        super("config", new String[]{"<save/load/dir>"});
    }

    public void execute(String[] commands) {
        File dir = new File("Trillium/");
        if (commands.length == 1) {
            sendMessage("Configs are saved in Trillium/config");
            return;
        }
        if (commands.length == 2)
            if ("list".equals(commands[0])) {
                String configs = "Configs: ";
                File file = new File("Trillium/");
                List<File> directories = Arrays.stream(file.listFiles()).filter(File::isDirectory).filter(f -> !f.getName().equals("util")).collect(Collectors.toList());
                StringBuilder builder = new StringBuilder(configs);
                for (File file1 : directories)
                    builder.append(file1.getName() + ", ");
                configs = builder.toString();
                sendMessage(configs);
            } else if( "dir".equals(commands[0]) ){
                try {
                    Desktop.getDesktop().browse(dir.toURI());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                sendMessage("There is no such command... Maybe list?");
            }
        if (commands.length >= 3) {
            switch (commands[0]) {
                case "save":
                    Trillium.configManager.saveConfig(commands[1]);
                    sendMessage(ChatFormatting.GREEN + "Config '" + commands[1] + "' saved");
                    return;
                case "load":
                    if (Trillium.configManager.configExists(commands[1])) {
                        Trillium.configManager.loadConfig(commands[1],false);
                        sendMessage(ChatFormatting.GREEN + "Loaded config " + commands[1]);
                    } else {
                        sendMessage(ChatFormatting.RED + "Config " + commands[1] + " does not exist");
                    }
                    return;
            }
            sendMessage("There is no such command! Usage example: <save/load/dir>");
        }
    }
}
