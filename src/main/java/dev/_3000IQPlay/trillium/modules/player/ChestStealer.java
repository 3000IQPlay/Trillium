package dev._3000IQPlay.trillium.modules.player;

import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.Setting;
import dev._3000IQPlay.trillium.util.Timer;
import dev._3000IQPlay.trillium.util.Util;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.Item;

public class ChestStealer extends Module {
    public ChestStealer() {
        super("ChestStealer", "Steals loot from chests", Module.Category.PLAYER, true, false, false);
    }


    Timer timer = new Timer();
    public Setting<Integer> delayed = this.register ( new Setting <> ( "Delay", 100, 0, 1000 ) );


    @Override
    public void onUpdate() {
        if (Util.mc.player.openContainer != null) {
            if (Util.mc.player.openContainer instanceof ContainerChest) {
                ContainerChest container = (ContainerChest)Util.mc.player.openContainer;
                for (int i = 0; i < container.inventorySlots.size(); ++i) {
                    if (container.getLowerChestInventory().getStackInSlot(i).getItem() != Item.getItemById(0) && timer.passedMs(delayed.getValue())) {
                        mc.playerController.windowClick(container.windowId, i, 0, ClickType.QUICK_MOVE, Util.mc.player);
                        this.timer.reset();
                        continue;
                    }
                    if (!this.empty(container)) continue;
                    Util.mc.player.closeScreen();
                }
            }
        }
    }

    public boolean empty(Container container) {
        boolean voll = true;
        int slotAmount = container.inventorySlots.size() == 90 ? 54 : 27;
        for (int i = 0; i < slotAmount; ++i) {
            if (!container.getSlot(i).getHasStack()) continue;
            voll = false;
        }
        return voll;
    }
}
