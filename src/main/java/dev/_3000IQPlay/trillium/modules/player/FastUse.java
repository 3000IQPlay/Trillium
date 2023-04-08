package dev._3000IQPlay.trillium.modules.player;

import dev._3000IQPlay.trillium.mixin.mixins.IMinecraft;
import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.Setting;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

public class FastUse extends Module {
    private final Setting<Integer> delay = this.register(new Setting<>("Delay", 1, 0, 4));
    public Setting<Boolean> blocks = this.register(new Setting<>("Blocks", false));
    public Setting<Boolean> crystals = this.register(new Setting<>("Crystals", false));
    public Setting<Boolean> xp = this.register(new Setting<>("XP", false));
    public Setting<Boolean> all = this.register(new Setting<>("All", true));
	
	public FastUse() {
        super("FastUse", "Fast RClick", Module.Category.PLAYER, true, false, false);
    }

    @Override
    public void onUpdate() {
        if(check(mc.player.getHeldItemMainhand().getItem())){
            if (((IMinecraft)mc).getRightClickDelayTimer() > delay.getValue())
                ((IMinecraft)mc).setRightClickDelayTimer(delay.getValue());
        }
    }

    public boolean check(Item item){
        return (item instanceof ItemBlock && blocks.getValue())
                || (item == Items.END_CRYSTAL && crystals.getValue())
                || (item == Items.EXPERIENCE_BOTTLE && xp.getValue())
                || (all.getValue());
    }
}