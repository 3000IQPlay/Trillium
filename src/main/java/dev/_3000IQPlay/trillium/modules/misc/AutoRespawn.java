package dev._3000IQPlay.trillium.modules.misc;

import com.mojang.realmsclient.gui.ChatFormatting;
import dev._3000IQPlay.trillium.command.Command;
import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.Setting;
import net.minecraft.client.gui.GuiGameOver;
import dev._3000IQPlay.trillium.util.Timer;

public class AutoRespawn extends Module {
    public AutoRespawn() {
        super("AutoRespawn", "Automatically respawns when you die", Category.PLAYER, true, false, false);
        this.timer = new Timer();
    }
    public Setting<Boolean> deathcoords = this.register ( new Setting <> ( "Death Coords", true ) );
    public Setting<Boolean> autokit = this.register ( new Setting <> ( "Auto Kit", false ) );
    public Setting<String> kit = this.register(new Setting<String>("kit name", "kitname", v -> autokit.getValue()));
    public Setting<Boolean> autohome = this.register ( new Setting <> ( "Auto Home", false ) );


    private final Timer timer;


    @Override public void onTick() {
        if (nullCheck()) return;

        if(timer.passedMs( 2100)) {
            timer.reset();
        }
        if (mc.currentScreen instanceof GuiGameOver ) {
            mc.player.respawnPlayer();
            mc.displayGuiScreen(null);
        }
        if (mc.currentScreen instanceof GuiGameOver && this.timer.getPassedTimeMs() > 200) {
            if(autokit.getValue()) {
                mc.player.sendChatMessage("/kit "+ kit.getValue());
            }
            if(deathcoords.getValue()){
                Command.sendMessage(ChatFormatting.GOLD + "[PlayerDeath] " + ChatFormatting.YELLOW + (int) mc.player.posX + " " + (int) mc.player.posY + " " + (int) mc.player.posZ);
            }
            timer.reset();

        }
        if (mc.currentScreen instanceof GuiGameOver && this.timer.getPassedTimeMs() > 1000) {
            if(autohome.getValue()) {
                mc.player.sendChatMessage("/home");
            }
            if(deathcoords.getValue()){
                Command.sendMessage(ChatFormatting.GOLD + "[PlayerDeath] " + ChatFormatting.YELLOW + (int) mc.player.posX + " " + (int) mc.player.posY + " " + (int) mc.player.posZ);
            }
            timer.reset();
        }
    }

}