package dev._3000IQPlay.trillium.modules;

import com.mojang.realmsclient.gui.ChatFormatting;
import dev._3000IQPlay.trillium.Trillium;
import dev._3000IQPlay.trillium.event.events.ClientEvent;
import dev._3000IQPlay.trillium.event.events.Render2DEvent;
import dev._3000IQPlay.trillium.event.events.Render3DEvent;
import dev._3000IQPlay.trillium.command.Command;
import dev._3000IQPlay.trillium.modules.client.ClickGui;
import dev._3000IQPlay.trillium.modules.client.MainSettings;
import dev._3000IQPlay.trillium.setting.Bind;
import dev._3000IQPlay.trillium.setting.Setting;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import dev._3000IQPlay.trillium.notification.NotificationManager;
import dev._3000IQPlay.trillium.notification.NotificationType;

import java.util.Objects;

import static dev._3000IQPlay.trillium.util.PlayerUtils.getPlayerPos;

public class Module
        extends Feature {
    private final String description;
    private final Category category;
    public Setting<Boolean> enabled = this.register(new Setting<Boolean>("Enabled", false));
    public Setting<String> displayName;
    public boolean alwaysListening;
    public boolean hidden;
    public boolean settingopened;
    public float offset;
    public boolean sliding;
    public Setting<Bind> bind = this.register(new Setting<Bind>("Keybind",  new Bind(-1)));
    public Setting<Boolean> drawn = this.register(new Setting<Boolean>("Drawn", true));


    public Module(String name, String description, Category category, boolean EventUsage, boolean hidden, boolean AlwaysOn) {
        super(name);
        this.displayName = this.register(new Setting<String>("DisplayName", name));
        this.description = description;
        this.category = category;
        this.hidden = hidden;
        this.alwaysListening = AlwaysOn;
    }

    public boolean isSetting(){
        return this.settingopened;
    }

    public void setSetting(Boolean a){
        this.settingopened = a;
    }


    public void render(int mouseX, int mouseY, float partialTicks) {

    }

    public boolean isSliding() {
        return this.sliding;
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    public void onToggle() {
    }

    public void onLoad() {
    }


    public void onTick() {
    }

    public void onLogin() {
    }

    public void onLogout() {
    }

    public void onUpdate() {
    }

    public void onRender2D(Render2DEvent event) {
    }

    public void onRender3D(Render3DEvent event) {
    }

    public void onUnload() {
    }

    public String getDisplayInfo() {
        return null;
    }

    public boolean isOn() {
        return this.enabled.getValue();
    }

    public boolean isOff() {
        return this.enabled.getValue() == false;
    }

    public void setEnabled(boolean enabled) {
        if (enabled) {
            this.enable();
        } else {
            this.disable();
        }
    }

    public void enable() {
        this.enabled.setValue(Boolean.TRUE);
        this.onToggle();
        this.onEnable();

        if ((Objects.equals(this.getDisplayName(), "ClickGUI"))){
            mc.world.playSound(getPlayerPos(), SoundEvents.BLOCK_ENDERCHEST_OPEN, SoundCategory.AMBIENT, 150.0f, 1.5F, true);
        } else {
            mc.world.playSound(getPlayerPos(), SoundEvents.BLOCK_NOTE_XYLOPHONE, SoundCategory.AMBIENT, 150.0f, 2.0F, true);
        }


        if (((!Objects.equals(this.getDisplayName(), "ACRender")) && (!Objects.equals(this.getDisplayName(), "ClickGui")))) {
            NotificationManager.publicity(this.getDisplayName(), "was enabled!", 2, NotificationType.INFO);
        }
        if (Trillium.moduleManager.getModuleByClass(MainSettings.class).notifyToggles.getValue()) {
            TextComponentString text = new TextComponentString(Trillium.commandManager.getClientMessage() + " " + ChatFormatting.GREEN + this.getDisplayName() + " toggled on.");
            Module.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(text, 1);
        }
        if (this.isOn() && !this.alwaysListening) {
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    public void disable() {
        if (!this.alwaysListening) {
            MinecraftForge.EVENT_BUS.unregister(this);
        }
        if (mc.player == null){
            return;
        }
        if ((Objects.equals(this.getDisplayName(), "ClickGUI"))){
            mc.world.playSound(getPlayerPos(), SoundEvents.BLOCK_ENDERCHEST_CLOSE, SoundCategory.AMBIENT, 150.0f, 1.0F, true);
        } else {
            mc.world.playSound(getPlayerPos(), SoundEvents.BLOCK_NOTE_XYLOPHONE, SoundCategory.AMBIENT, 150.0f, 1.0F, true);

        }

        this.enabled.setValue(false);
        if ((!Objects.equals(this.getDisplayName(), "ClickGui"))) {
            NotificationManager.publicity(this.getDisplayName(), "was disabled!", 2, NotificationType.INFO);
        }
        if (Trillium.moduleManager.getModuleByClass(MainSettings.class).notifyToggles.getValue()) {
            TextComponentString text = new TextComponentString(Trillium.commandManager.getClientMessage() + " " + ChatFormatting.RED + this.getDisplayName() + " toggled off.");
            if(text != null) {
                Module.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(text, 1);
            }
        }
        this.onToggle();
        this.onDisable();
    }

    public void toggle() {
        ClientEvent event = new ClientEvent(!this.isEnabled() ? 1 : 0, this);
        MinecraftForge.EVENT_BUS.post(event);
        if (!event.isCanceled()) {
            this.setEnabled(!this.isEnabled());
        }
    }

    public boolean isValidBind(String s){
        return s.length() < 2;
    }

    public String getDisplayName() {
        return this.displayName.getValue();
    }

    public void setDisplayName(String name) {
        Module module = Trillium.moduleManager.getModuleByDisplayName(name);
        Module originalModule = Trillium.moduleManager.getModuleByName(name);
        if (module == null && originalModule == null) {
            Command.sendMessage(this.getDisplayName() + ", name: " + this.getName() + ", has been renamed to: " + name);
            this.displayName.setValue(name);
            return;
        }
        Command.sendMessage(ChatFormatting.RED + "A module of this name already exists.");
    }

    public String getDescription() {
        return this.description;
    }

    public boolean isDrawn() {
        return this.drawn.getValue();
    }

    public void setDrawn(boolean drawn) {
        this.drawn.setValue(drawn);
    }


    public Category getCategory() {
        return this.category;
    }

    public String getInfo() {
        return null;
    }

    public Bind getBind() {
        return this.bind.getValue();
    }

    public void setBind(int key) {
        this.bind.setValue(new Bind(key));
    }

    public boolean listening() {
        return  this.isOn() || this.alwaysListening;
    }

    public String getFullArrayString() {
        return this.getDisplayName() + ChatFormatting.BLUE + (this.getDisplayInfo() != null ? " [" + ChatFormatting.AQUA + this.getDisplayInfo() + ChatFormatting.BLUE + "]" : "");
    }



    public enum Category {
        COMBAT("Combat"),
        EXPLOIT("Exploit"),
        MOVEMENT("Movement"),
        RENDER("Render"),
        PLAYER("Player"),
        MISC("Misc"),
        CLIENT("Client"),
        HUD("HUD");

        private final String name;

        Category(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }
}

