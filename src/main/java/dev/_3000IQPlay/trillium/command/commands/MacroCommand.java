package dev._3000IQPlay.trillium.command.commands;

import dev._3000IQPlay.trillium.command.Command;
import dev._3000IQPlay.trillium.macro.Macro;
import dev._3000IQPlay.trillium.manager.MacroManager;
import org.lwjgl.input.Keyboard;

import java.util.Arrays;

public class MacroCommand extends Command {


    public MacroCommand() {
        super("macro", new String[]{"<add/remove/list>", "<name>"});
    }

    @Override
    public void execute(String[] args) {
            if(args[0] == null){
                Command.sendMessage(usage());
            }
            if (args[0].equals("list")) {
                sendMessage("Macros:");
                sendMessage(" ");
                MacroManager.getMacros().forEach(macro -> sendMessage(macro.getName() + (macro.getBind() != Keyboard.KEY_NONE ? " [" + Keyboard.getKeyName(macro.getBind()) + "]" : "") + " {" + macro.getText() + "}"));
            }
            if (args[0].equals("remove")) {
                if (MacroManager.getMacroByName(args[1]) != null) {
                    Macro macro = MacroManager.getMacroByName(args[1]);
                    MacroManager.removeMacro(macro);
                    sendMessage("Removed macro " + macro.getName());
                } else {
                    sendMessage("There is no macro named " + args[1]);
                }
            }
        if(args.length >= 4) {
            if (args[0].equals("add")) {
                String name = args[1];
                String bind = args[2].toUpperCase();
                String text = String.join(" ", Arrays.copyOfRange(args, 3, args.length - 1));
                if(Keyboard.getKeyIndex(bind) == Keyboard.KEY_NONE) {
                    sendMessage("Wrong bind!");
                    return;
                }
                Macro macro = new Macro(name, text, Keyboard.getKeyIndex(bind));
                MacroManager.addMacro(macro);
                sendMessage("Added macro " + name + " on key " + Keyboard.getKeyName(macro.getBind()));
            }else {
                sendMessage(usage());
            }
        }
    }


    String usage(){
        return "macro add/remove/list (macro add name key text), (macro remove name)";
    }
}
