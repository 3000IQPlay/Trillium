package dev._3000IQPlay.trillium.util;

import net.minecraft.item.ItemStack;

public class InvStack {

    public final int slot;
    public final ItemStack stack;

    public InvStack(int slot, ItemStack stack) {
        this.slot = slot;
        this.stack = stack;
    }

}