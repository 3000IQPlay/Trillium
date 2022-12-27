package dev._3000IQPlay.trillium.command.commands;

import dev._3000IQPlay.trillium.command.Command;
import dev._3000IQPlay.trillium.Trillium;
import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.Setting;

public class RenameCommand extends Command {

    public RenameCommand() {
        super("rename", new String[]{"<module>",  "<name>"});
    }

    @Override
    public void execute(String[] commands) {
        Setting setting;
        if (commands.length == 1) {
            ModuleCommand.sendMessage("Moron");
            return;
        }
        Module module = Trillium.moduleManager.getModuleByDisplayName(commands[0]);
        if (module == null) {
            ModuleCommand.sendMessage("There is no such module idiot..");
        }
        if (commands.length == 2) {

            ModuleCommand.sendMessage("Module " + module.getDisplayName() + " renamed to " + commands[1]);

            module.setDisplayName(commands[1]);
        }


    }

}
