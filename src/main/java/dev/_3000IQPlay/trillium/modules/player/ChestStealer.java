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
	public Setting<Integer> minDelay = this.register(new Setting<>("Min-Delay", 100, 0, 1000));
	public Setting<Integer> mixDelay = this.register(new Setting<>("Max-Delay", 100, 0, 1000));
	public Setting<Boolean> autoClose = this.register(new Setting<>("AutoClose", true));
	public Setting<CT> closeType = this.register(new Setting<>("CloseType", CT.CloseScreen, v -> this.autoClose.getValue()));
	Timer timer = new Timer();
	
	public ChestStealer() {
        super("ChestStealer", "Steals loot from chests", Module.Category.PLAYER, true, false, false);
    }
	
    @Override
    public void onUpdate() {
        if (Util.mc.player.openContainer != null) {
            if (Util.mc.player.openContainer instanceof ContainerChest) {
				int minD = (int) this.minDelay.getValue();
                int maxD = (int) this.mixDelay.getValue();
                ContainerChest container = (ContainerChest)Util.mc.player.openContainer;
                for (int i = 0; i < container.inventorySlots.size(); ++i) {
                    if (container.getLowerChestInventory().getStackInSlot(i).getItem() != Item.getItemById(0) && timer.passedMs(ChestStealer.randomDelay(minD, maxD))) {
                        mc.playerController.windowClick(container.windowId, i, 0, ClickType.QUICK_MOVE, Util.mc.player);
                        this.timer.reset();
                        continue;
                    }
                    if (!this.empty(container)) continue;
					if (this.autoClose.getValue()) {
					    if (this.closeType.getValue() == CT.CloseScreen) {
							Util.mc.player.closeScreen();
						} else {
							// This could possibly allow you to dupe on some servers
							Util.mc.displayGuiScreen(null);
						}	
					} else {
						return;
					}
                }
            }
        }
    }
	
	public static long randomDelay(final int minDelay, final int maxDelay) {
        return (int) Math.floor(Math.random() * (maxDelay - minDelay + 1) + minDelay);
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
	
	public static enum CT {
		CloseScreen,
		NullScreen;
	}
}
