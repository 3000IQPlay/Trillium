package dev._3000IQPlay.trillium.modules.player;

import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.Setting;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemPickaxe;

public class NoEntityTrace extends Module
{
    private static NoEntityTrace INSTANCE;
    public Setting<Boolean> pick;
    public Setting<Boolean> gap;
    public Setting<Boolean> obby;
    public boolean noTrace;

    public NoEntityTrace() {
        super("NoEntityTrace",  "Makes you mine thru entities",  Module.Category.PLAYER,  false,  false,  false);
        this.pick = (Setting<Boolean>)this.register(new Setting("Pick", true));
        this.gap = (Setting<Boolean>)this.register(new Setting("Gap", false));
        this.obby = (Setting<Boolean>)this.register(new Setting("Obby", false));
        this.setInstance();
    }

    public static NoEntityTrace getINSTANCE() {
        if (NoEntityTrace.INSTANCE == null) {
            NoEntityTrace.INSTANCE = new NoEntityTrace();
        }
        return NoEntityTrace.INSTANCE;
    }

    private void setInstance() {
        NoEntityTrace.INSTANCE = this;
    }

    public void onUpdate() {
        final Item item = NoEntityTrace.mc.player.getHeldItemMainhand().getItem();
        if (item instanceof ItemPickaxe && this.pick.getValue()) {
            this.noTrace = true;
            return;
        }
        if (item == Items.GOLDEN_APPLE && this.gap.getValue()) {
            this.noTrace = true;
            return;
        }
        if (item instanceof ItemBlock) {
            this.noTrace = (((ItemBlock)item).getBlock() == Blocks.OBSIDIAN && this.obby.getValue());
            return;
        }
        this.noTrace = false;
    }

    static {
        NoEntityTrace.INSTANCE = new NoEntityTrace();
    }
}