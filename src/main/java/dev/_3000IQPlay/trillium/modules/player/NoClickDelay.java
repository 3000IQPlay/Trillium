package dev._3000IQPlay.trillium.modules.player;

import dev._3000IQPlay.trillium.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Field;

public class NoClickDelay extends Module {
    private final Field leftClickCounterField;

    public NoClickDelay() {
        super("NoClickDelay", "Remove your click delay", Category.PLAYER, true, false, false);
        this.leftClickCounterField = ReflectionHelper.findField(Minecraft.class, "field_71429_W", "leftClickCounter");
    }

    @SubscribeEvent
    public void playerTickEvent(TickEvent.PlayerTickEvent event) {
        if (!isPlayerInGame() || leftClickCounterField == null) {
            return;
        }

        try {
            leftClickCounterField.setInt(Minecraft.getMinecraft(), 0);
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
            this.disable();
        }
    }

    public static boolean isPlayerInGame() {
        Minecraft mc = Minecraft.getMinecraft();
        return mc.player != null && mc.world != null;
    }
}