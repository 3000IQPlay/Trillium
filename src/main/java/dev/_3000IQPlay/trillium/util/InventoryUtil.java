package dev._3000IQPlay.trillium.util;

import dev._3000IQPlay.trillium.Trillium;
import dev._3000IQPlay.trillium.command.Command;
import dev._3000IQPlay.trillium.mixin.ducks.IPlayerControllerMP;
import net.minecraft.block.Block;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.*;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumHand;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;

import static dev._3000IQPlay.trillium.util.phobos.HelperRotation.acquire;

public class InventoryUtil
        implements Util {
	public static int getBestSword() {
        int b = -1;
        float f = 1.0F;
        for (int b1 = 0; b1 < 9; b1++) {
            ItemStack itemStack =  Util.mc.player.inventory.getStackInSlot(b1);
            if (itemStack != null && itemStack.getItem() instanceof ItemSword) {
                ItemSword itemSword = (ItemSword)itemStack.getItem();
                float f1 = itemSword.getMaxDamage();
                f1 += EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(20), itemStack);
                if (f1 > f) {
                    f = f1;
                    b = b1;
                }
            }
        }
        return b;
    }
	
    public static int getBestAxe() {
        int b = -1;
        float f = 1.0F;
        for (int b1 = 0; b1 < 9; b1++) {
            ItemStack itemStack =  Util.mc.player.inventory.getStackInSlot(b1);
            if (itemStack != null && itemStack.getItem() instanceof ItemAxe) {
                ItemAxe axe = (ItemAxe)itemStack.getItem();
                float f1 = axe.getMaxDamage();
                f1 += EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(20), itemStack);
                if (f1 > f) {
                    f = f1;
                    b = b1;
                }
            }
        }
        return b;
    }
	
    public static int getItemHotbar(Item input) {
        for (int i = 0; i < 9; ++i) {
            Item item = InventoryUtil.mc.player.inventory.getStackInSlot(i).getItem();
            if (Item.getIdFromItem(item) != Item.getIdFromItem(input)) continue;
            return i;
        }
        return -1;
    }
	
	public static int findStackInventory(Item input) {
        return InventoryUtil.findStackInventory(input, false);
    }

    public static int findStackInventory(Item input, boolean withHotbar) {
        int i;
        i = withHotbar ? 0 : 9;
        while (i < 36) {
            Item item = InventoryUtil.mc.player.inventory.getStackInSlot(i).getItem();
            if (Item.getIdFromItem(input) == Item.getIdFromItem(item)) {
                return i + (i < 9 ? 36 : 0);
            }
            ++i;
        }
        return -1;
    }

    public static int hotbarToInventory(int slot)
    {
        if (slot == -2)
        {
            return 45;
        }

        if (slot > -1 && slot < 9)
        {
            return 36 + slot;
        }

        return slot;
    }
    public static void bypassSwitch(int slot)
    {
        if (slot >= 0)
        {
            mc.playerController.pickItem(slot);
        }
    }

    public static void switchTo(int slot)
    {
        if (mc.player.inventory.currentItem != slot && slot > -1 && slot < 9)
        {
            mc.player.inventory.currentItem = slot;
            syncItem();
        }
    }

    public static void switchToBypass(int slot)
    {
        acquire(() ->
       {
            if (mc.player.inventory.currentItem != slot
                    && slot > -1 && slot < 9)
            {
                int lastSlot = mc.player.inventory.currentItem;
                int targetSlot = hotbarToInventory(slot);
                int currentSlot = hotbarToInventory(lastSlot);
                mc.playerController.windowClick(0, targetSlot, 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(0, currentSlot, 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(0, targetSlot, 0, ClickType.PICKUP, mc.player);
            }
        });
    }
	
	public static boolean isBlock(Item item, Class clazz) {
        if (item instanceof ItemBlock) {
            Block block = ((ItemBlock) item).getBlock();
            return clazz.isInstance(block);
        }
        return false;
    }

    /**
     * Bypasses NCP item switch cooldown
     * @param slot INVENTORY SLOT (NOT HOTBAR) to switch to
     */
    public static void switchToBypassAlt(int slot)
    {
        acquire(() ->
        {
            if (mc.player.inventory.currentItem != slot
                    && slot > -1 && slot < 9)
            {
                acquire(() ->
                mc.playerController.windowClick(0, slot, mc.player.inventory.currentItem, ClickType.SWAP, mc.player))
            ;
            }
        });
    }
	
	public static boolean areStacksCompatible(ItemStack stack1, ItemStack stack2) {
        if (!stack1.getItem().equals(stack2.getItem())) {
            return false;
        }
        if (stack1.getItem() instanceof ItemBlock && stack2.getItem() instanceof ItemBlock) {
            Block block1 = ((ItemBlock) stack1.getItem()).getBlock();
            Block block2 = ((ItemBlock) stack2.getItem()).getBlock();
            if (!block1.material.equals(block2.material)) {
                return false;
            }
        }
        if (!stack1.getDisplayName().equals(stack2.getDisplayName())) {
            return false;
        }
        return stack1.getItemDamage() == stack2.getItemDamage();
    }
	
	public static int findAnyBlock() {
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = InventoryUtil.mc.player.inventory.getStackInSlot(i);
            if (stack == ItemStack.EMPTY || !(stack.getItem() instanceof ItemBlock)) continue;
            return i;
        }
        return -1;
    }

    public static EnumHand getHand(int slot)
    {
        return slot == -2 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
    }
    public static void syncItem()
    {
        ((IPlayerControllerMP) mc.playerController).syncItem();
    }
    public static EnumHand getHand(Item item)
    {
        return mc.player.getHeldItemMainhand().getItem() == item
                ? EnumHand.MAIN_HAND
                : mc.player.getHeldItemOffhand().getItem() == item
                ? EnumHand.OFF_HAND
                : null;
    }

    public static void switchToHotbarSlot(int slot, boolean silent) {
        if (mc.player.inventory.currentItem == slot || slot < 0) {
            return;
        }
        if (silent) {
            mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
            mc.playerController.updateController();
        } else {
            mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
            mc.player.inventory.currentItem = slot;
            mc.playerController.updateController();
        }
    }

    public static int containerToSlots(int containerSlot) {
        if (containerSlot < 5 || containerSlot > 45) { // crafting slots
            return -1;
        }

        if (containerSlot <= 9) {
            return 44 - containerSlot;
        }

        if (containerSlot < 36) {
            return containerSlot;
        }

        if (containerSlot < 45) {
            return containerSlot - 36;
        }

        return 40; // offhand is 40 here
    }
    public static void put(int slot, ItemStack stack)
    {
        if (slot == -2)
        {
            mc.player.inventory.setItemStack(stack);
        }

        mc.player.inventoryContainer.putStackInSlot(slot, stack);

        int invSlot = containerToSlots(slot);
        if (invSlot != -1) {
            mc.player.inventory.setInventorySlotContents(invSlot, stack);
        }
    }
    public static void click(int slot)
    {
        mc.playerController
                .windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
    }

    public static void clickLocked(int slot, int to, Item inSlot, Item inTo)
    {

            if ((slot == -1 || get(slot).getItem() == inSlot)
                    && get(to).getItem() == inTo)
            {
                boolean multi = slot >= 0;
                if (multi)
                {
                    click(slot);
                }

                click(to);

                if (multi)
                {
                }
            }

    }

    public static int findEmptyHotbarSlot()
    {
        int result = -1;
        for (int i = 8; i > -1; i--)
        {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.isEmpty() || stack.getItem() == Items.AIR)
            {
                result = i;
            }
        }

        return result;
    }


    public static int findItem(Item item, boolean xCarry)
    {
        return findItem(item, xCarry, Collections.emptySet());
    }


    public static int findItem(Item item, boolean xCarry, Set<Integer> ignore)
    {
        if (mc.player.inventory.getItemStack().getItem() == item
                && !ignore.contains(-2))
        {
            return -2;
        }

        for (int i = 9; i < 45; i++)
        {
            if (ignore.contains(i))
            {
                continue;
            }

            if (get(i).getItem() == item)
            {
                return i;
            }
        }

        if (xCarry)
        {
            for (int i = 1; i < 5; i++)
            {
                if (ignore.contains(i))
                {
                    continue;
                }

                if (get(i).getItem() == item)
                {
                    return i;
                }
            }
        }

        return -1;
    }
    public static int getCount(Item item)
    {
        int result = 0;
        for (int i = 0; i < 46; i++)
        {
            ItemStack stack = mc.player
                    .inventoryContainer
                    .getInventory()
                    .get(i);

            if (stack.getItem() == item)
            {
                result += stack.getCount();
            }
        }

        if (mc.player.inventory.getItemStack().getItem() == item)
        {
            result += mc.player.inventory.getItemStack().getCount();
        }

        return result;
    }
    public static ItemStack get(int slot)
    {
        if (slot == -2)
        {
            return mc.player.inventory.getItemStack();
        }

        return mc.player.inventoryContainer.getInventory().get(slot);
    }

    public static int findSoupAtHotbar() {
        int b = -1;
        for (int a = 0; a < 9; ++a) {
            if (InventoryUtil.mc.player.inventory.getStackInSlot(a).getItem() != Items.MUSHROOM_STEW) continue;
            b = a;
        }
        return b;
    }


    public static int findFirstBlockSlot(Class<? extends Block> blockToFind, int lower, int upper) {
        int slot = -1;
        List<ItemStack> mainInventory = mc.player.inventory.mainInventory;

        for (int i = lower; i <= upper; i++) {
            ItemStack stack = mainInventory.get(i);

            if (stack == ItemStack.EMPTY || !(stack.getItem() instanceof ItemBlock)) {
                continue;
            }

            if (blockToFind.isInstance(((ItemBlock) stack.getItem()).getBlock())) {
                slot = i;
                break;
            }
        }
        return slot;
    }

    public static int findFirstItemSlot(Class<? extends Item> itemToFind, int lower, int upper) {
        int slot = -1;
        List<ItemStack> mainInventory = mc.player.inventory.mainInventory;

        for (int i = lower; i <= upper; i++) {
            ItemStack stack = mainInventory.get(i);

            if (stack == ItemStack.EMPTY || !(itemToFind.isInstance(stack.getItem()))) {
                continue;
            }

            if (itemToFind.isInstance(stack.getItem())) {
                slot = i;
                break;
            }
        }
        return slot;
    } //ШИИИИИШ

    public static void switchToHotbarSlot(Class clazz, boolean silent) {
        int slot = InventoryUtil.findHotbarBlock(clazz);
        if (slot > -1) {
            InventoryUtil.switchToHotbarSlot(slot, silent);
        }
    }
	
	public static Map<Integer, ItemStack> getInventoryAndHotbarSlots() {
        if (InventoryUtil.mc.currentScreen instanceof GuiCrafting) {
            return InventoryUtil.fuckYou3arthqu4kev2(10, 45);
        }
        return InventoryUtil.getInventorySlots(9, 44);
    }
	
	private static Map<Integer, ItemStack> fuckYou3arthqu4kev2(int currentI, int last) {
        HashMap<Integer, ItemStack> fullInventorySlots = new HashMap<>();
        for (int current = currentI; current <= last; ++current) {
            fullInventorySlots.put(current, InventoryUtil.mc.player.openContainer.getInventory().get(current));
        }
        return fullInventorySlots;
    }

    public static int getAxeAtHotbar() {
        for (int i = 0; i < 9; ++i) {
            ItemStack itemStack = Util.mc.player.inventory.getStackInSlot(i);
            if (!(itemStack.getItem() instanceof ItemAxe)) continue;
            return i;
        }
        return -1;
    }

    public static int getCrysathotbar() {
        for (int i = 0; i < 9; ++i) {
            ItemStack itemStack = Util.mc.player.inventory.getStackInSlot(i);
            if (!(itemStack.getItem() instanceof ItemEndCrystal)) continue;
            return i;
        }
        return -1;
    }
    public static int getPicatHotbar() {
        for (int i = 0; i < 9; ++i) {
            ItemStack itemStack = Util.mc.player.inventory.getStackInSlot(i);
            if (!(itemStack.getItem() instanceof ItemPickaxe)) continue;
            return i;
        }
        return -1;
    }


    public static int getPowderAtHotbar() {
        for (int i = 0; i < 9; ++i) {
            ItemStack itemStack = Util.mc.player.inventory.getStackInSlot(i);
            if (!(itemStack.getItem().getItemStackDisplayName(itemStack).equals("Порох"))) continue;
            return i;
        }
        return 1;
    }


    public static int findHotbarBlock(Class clazz) {
        for (int i = 0; i < 9; ++i) {
            Block block;
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack == ItemStack.EMPTY) continue;
            if (clazz.isInstance(stack.getItem())) {
                return i;
            }
            if (!(stack.getItem() instanceof ItemBlock) || !clazz.isInstance(block = ((ItemBlock) stack.getItem()).getBlock()))
                continue;
            return i;
        }
        return -1;
    }


    public static int findItemAtHotbar(Item stacks) {
        for (int i = 0; i < 9; ++i) {
            Item stack = mc.player.inventory.getStackInSlot(i).getItem();
            if (stack == Items.AIR) continue;
            if (stack == stacks) {
                return i;
            }
        }
        return -1;
    }

    public static int findHotbarBlock(Block blockIn) {
        for (int i = 0; i < 9; ++i) {
            Block block;
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack == ItemStack.EMPTY || !(stack.getItem() instanceof ItemBlock) || (block = ((ItemBlock) stack.getItem()).getBlock()) != blockIn)
                continue;
            return i;
        }
        return -1;
    }

    public static int getCappuchinoAtHotbar() {
        for (int i = 0; i < 9; ++i) {
            ItemStack itemStack = Util.mc.player.inventory.getStackInSlot(i);
            if (!(itemStack.getItem() == Items.POTIONITEM)) continue;
            Item maybepot = itemStack.getItem();
            if (!(itemStack.getItem().getItemStackDisplayName(itemStack).equals("Несоздаваемое зелье"))) continue;
            return i;
        }
        return -1;
    }

    private static Map<Integer, ItemStack> getInventorySlots(int currentI, int last) {
        HashMap<Integer, ItemStack> fullInventorySlots = new HashMap<Integer, ItemStack>();
        for (int current = currentI; current <= last; ++current) {
            fullInventorySlots.put(current, mc.player.inventoryContainer.getInventory().get(current));
        }
        return fullInventorySlots;
    }


    public static boolean holdingItem(Class clazz) {
        boolean result = false;
        ItemStack stack = mc.player.getHeldItemMainhand();
        result = InventoryUtil.isInstanceOf(stack, clazz);
        if (!result) {
            ItemStack offhand = mc.player.getHeldItemOffhand();
            result = InventoryUtil.isInstanceOf(stack, clazz);
        }
        return result;
    }

    public static boolean isInstanceOf(ItemStack stack, Class clazz) {
        if (stack == null) {
            return false;
        }
        Item item = stack.getItem();
        if (clazz.isInstance(item)) {
            return true;
        }
        if (item instanceof ItemBlock) {
            Block block = Block.getBlockFromItem(item);
            return clazz.isInstance(block);
        }
        return false;
    }


    public static boolean isHolding(EntityPlayer player, Item experienceBottle) {
        return player.getHeldItemMainhand().getItem() == experienceBottle || player.getHeldItemOffhand().getItem() == experienceBottle;
    }
	
    public static boolean isHolding(Item experienceBottle) {
        return mc.player.getHeldItemMainhand().getItem() == experienceBottle || mc.player.getHeldItemOffhand().getItem() == experienceBottle;
    }
	
	public static int convertHotbarToInv(int input) {
        return 36 + input;
    }

    public static boolean isHolding(Block block)
    {
        ItemStack mainHand = mc.player.getHeldItemMainhand();
        ItemStack offHand  = mc.player.getHeldItemOffhand();

        if(!(mainHand.getItem() instanceof ItemBlock)  || !(offHand.getItem() instanceof ItemBlock) )return false;
        return ((ItemBlock) mainHand.getItem()).getBlock() == block || ((ItemBlock) offHand.getItem()).getBlock() == block;
    }

    public static class Task {
        private final int slot;
        private final boolean update;
        private final boolean quickClick;

        public Task() {
            this.update = true;
            this.slot = -1;
            this.quickClick = false;
        }

        public Task(int slot) {
            this.slot = slot;
            this.quickClick = false;
            this.update = false;
        }

        public Task(int slot, boolean quickClick) {
            this.slot = slot;
            this.quickClick = quickClick;
            this.update = false;
        }

        public void run() {
            if (this.update) {
                Util.mc.playerController.updateController();
            }
            if (this.slot != -1) {
                Util.mc.playerController.windowClick(Util.mc.player.inventoryContainer.windowId, this.slot, 0, this.quickClick ? ClickType.QUICK_MOVE : ClickType.PICKUP, Util.mc.player);
            }
        }

        public boolean isSwitching() {
            return !this.update;
        }
    }
}

