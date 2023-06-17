package dev._3000IQPlay.trillium.modules.misc;

import dev._3000IQPlay.trillium.command.Command;
import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.Setting;

public class AntiNullPointer
        extends Module {
    public final Setting<Boolean> debug = this.register(new Setting<Boolean>("Debug", true));

    public AntiNullPointer() {
        super("AntiNull", "Anti null pointer kick", Module.Category.MISC, true, false, false);
    }

    public void sendWarning(Throwable throwable) {
        if (this.debug.getValue().booleanValue()) {
            Command.sendMessage("Patched null point kick!");
        }
        throwable.printStackTrace();
    }
}