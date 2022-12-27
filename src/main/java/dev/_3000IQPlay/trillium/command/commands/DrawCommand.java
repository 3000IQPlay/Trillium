package dev._3000IQPlay.trillium.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import dev._3000IQPlay.trillium.Trillium;
import dev._3000IQPlay.trillium.command.Command;
import dev._3000IQPlay.trillium.modules.Module;

public class DrawCommand extends Command {
    public DrawCommand() {
        super("draw", new String[]{"<module>"});
    }

    @Override
    public void execute(String[] commands) {
        if (commands.length == 1) {
            Command.sendMessage("Write the name of the module");
            return;
        }
        String moduleName = commands[0];
        Module module = Trillium.moduleManager.getModuleByName(moduleName);
        if (module == null) {
            Command.sendMessage("Unknown module'" + module + "'!");
            return;
        }

        module.setDrawn(!module.isDrawn());
        BindCommand.sendMessage("Module " + ChatFormatting.GREEN + module.getName() + ChatFormatting.WHITE + " now " + (module.isDrawn() ? "visible in ArrayList" : "not visible in ArrayList"));
    }
}
