package dev._3000IQPlay.trillium.modules.movement;

import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.Setting;

public class Sprint extends Module {

    public Sprint() {
        super("Sprint", "Auto sprints for you", Category.MOVEMENT, true, false, false);
    }

    public enum mode {
        legit, Rage;
    }

    private Setting<mode> Mode = register(new Setting("Mode", mode.legit));



    @Override
    public void onUpdate() {
        if(nullCheck())return;
        if (Mode.getValue() == mode.legit) {
            if (mc.gameSettings.keyBindForward.isKeyDown()) {
                mc.player.setSprinting(true);
            }
        } else {
            mc.player.setSprinting(true);
        }
    }

    @Override
    public String getDisplayInfo() {
        return  Mode.getValue().toString();
    }
}
