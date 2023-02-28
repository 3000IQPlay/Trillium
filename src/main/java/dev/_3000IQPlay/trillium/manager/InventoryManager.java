package dev._3000IQPlay.trillium.manager;

import dev._3000IQPlay.trillium.event.events.PacketEvent;
import dev._3000IQPlay.trillium.mixin.mixins.IPlayerControllerMP;
import dev._3000IQPlay.trillium.util.Util;
import net.minecraft.block.Block;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class InventoryManager
        implements Util {
    private int serverSlot;

    public InventoryManager() {
        this.serverSlot = -1;
    }

    public void switchToBlock(Block block, Switch switch_) {
        this.switchToItem(Item.getItemFromBlock(block), switch_);
    }

    public void syncSlot() {
        if (this.serverSlot != InventoryManager.mc.player.inventory.currentItem) {
            InventoryManager.mc.player.inventory.currentItem = this.serverSlot;
        }
    }

    public int searchSlot(Class<? extends Item> class_, InventoryRegion inventoryRegion) {
        int n = -1;
        for (int i = inventoryRegion.getStart(); i <= inventoryRegion.getBound(); ++i) {
            if (!class_.isInstance(InventoryManager.mc.player.inventory.getStackInSlot(i).getItem()))
                continue;
            n = i;
            break;
        }
        return n;
    }

    public void switchToSlot(int n, Switch switch_) {
        if (InventoryPlayer.isHotbar(n) && InventoryManager.mc.player.inventory.currentItem != n) {
            switch (switch_) {
                case NORMAL: {
                    InventoryManager.mc.player.inventory.currentItem = n;
                    InventoryManager.mc.player.connection.sendPacket(new CPacketHeldItemChange(n));
                    break;
                }
                case PACKET: {
                    InventoryManager.mc.player.connection.sendPacket(new CPacketHeldItemChange(n));
                    ((IPlayerControllerMP) InventoryManager.mc.playerController).setCurrentPlayerItem(n);
                }
            }
        }
    }

    public void switchToItem(Item[] arritem, Switch switch_) {
        int n = -1;
        for (int i = InventoryRegion.HOTBAR.getStart(); i < InventoryRegion.HOTBAR.getBound(); ++i) {
            for (Item item : arritem) {
                if (!InventoryManager.mc.player.inventory.getStackInSlot(i).getItem().equals(item)) continue;
                n = i;
                break;
            }
            if (n != -1) break;
        }
        this.switchToSlot(n, switch_);
    }

    public void switchToBlock(Block[] arrblock, Switch switch_) {
        Item[] arritem = new Item[arrblock.length];
        for (int i = 0; i < arrblock.length; ++i) {
            arritem[i] = Item.getItemFromBlock(arrblock[i]);
        }
        this.switchToItem(arritem, switch_);
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send send) {
        if (send.getPacket() instanceof CPacketHeldItemChange) {
            if (!InventoryPlayer.isHotbar(((CPacketHeldItemChange) send.getPacket()).getSlotId())) {
                send.setCanceled(true);
                return;
            }
            this.serverSlot = ((CPacketHeldItemChange) send.getPacket()).getSlotId();
        }
    }

    public int searchSlot(Item item, InventoryRegion inventoryRegion) {
        int n = -1;
        for (int i = inventoryRegion.getStart(); i < inventoryRegion.getBound(); ++i) {
            if (!InventoryManager.mc.player.inventory.getStackInSlot(i).getItem().equals(item)) continue;
            n = i;
            break;
        }
        return n;
    }

    public void switchToItem(Item item, Switch switch_) {
        int n = this.searchSlot(item, InventoryRegion.HOTBAR);
        this.switchToSlot(n, switch_);
    }

    public void switchToItem(Class<? extends Item> class_, Switch switch_) {
        int n = this.searchSlot(class_, InventoryRegion.HOTBAR);
        this.switchToSlot(n, switch_);
    }

    public int searchSlot(Block[] arrblock, InventoryRegion inventoryRegion) {
        int n = -1;
        block0:
        for (int i = inventoryRegion.getStart(); i < inventoryRegion.getBound(); ++i) {
            for (Block block : arrblock) {
                if (n != -1 || !InventoryManager.mc.player.inventory.getStackInSlot(i).getItem().equals(Item.getItemFromBlock(block)))
                    continue;
                n = i;
                continue block0;
            }
        }
        return n;
    }

    public int getServerSlot() {
        return this.serverSlot;
    }

    public enum InventoryRegion {
        INVENTORY(0, 45),
        HOTBAR(0, 8),
        CRAFTING(80, 83),
        ARMOR(100, 103);

        private final int start;
        private final int bound;

        InventoryRegion(int n2, int n3) {
            this.start = n2;
            this.bound = n3;
        }

        public int getBound() {
            return this.bound;
        }

        public int getStart() {
            return this.start;
        }
    }

    public enum Switch {
        NORMAL,
        PACKET,
        NONE

    }
}