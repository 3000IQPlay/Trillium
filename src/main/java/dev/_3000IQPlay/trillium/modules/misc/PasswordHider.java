package dev._3000IQPlay.trillium.modules.misc;

import dev._3000IQPlay.trillium.modules.Module;

public class PasswordHider extends Module {
    public PasswordHider() {
        super("PasswordHider", "Hids your server login password (Useful when you are streaming)", Module.Category.MISC, true, false, false);
    }
}
