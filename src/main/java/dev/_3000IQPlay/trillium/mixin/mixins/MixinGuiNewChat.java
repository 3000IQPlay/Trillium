package dev._3000IQPlay.trillium.mixin.mixins;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiNewChat;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = {GuiNewChat.class})
public class MixinGuiNewChat
        extends Gui {
}