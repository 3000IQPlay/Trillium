package dev._3000IQPlay.trillium.modules.misc;

import com.mojang.realmsclient.gui.ChatFormatting;
import dev._3000IQPlay.trillium.command.Command;
import dev._3000IQPlay.trillium.mixin.mixins.IGuiMerchant;
import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.Setting;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketConfirmTransaction;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;

/**
 * @author gerald0mc
 * @since 7/8/22
 */
public class AutoTrader
        extends Module {
	public Setting<Page> page = this.register(new Setting<Page>("Page", Page.Settings));
    public Setting<Boolean> potato  = this.register(new Setting<Boolean>("Potato", false, v -> this.page.getValue() == Page.Items));
	public Setting<Boolean> carrot  = this.register(new Setting<Boolean>("Carrot", false, v -> this.page.getValue() == Page.Items));
	public Setting<Boolean> wheat  = this.register(new Setting<Boolean>("Wheat", false, v -> this.page.getValue() == Page.Items));
	public Setting<Boolean> pumpkin  = this.register(new Setting<Boolean>("Pumpkin", false, v -> this.page.getValue() == Page.Items));
	public Setting<Boolean> melon  = this.register(new Setting<Boolean>("Melon", false, v -> this.page.getValue() == Page.Items));
	public Setting<Boolean> string  = this.register(new Setting<Boolean>("String", false, v -> this.page.getValue() == Page.Items));
	public Setting<Boolean> whiteWool  = this.register(new Setting<Boolean>("White Wool", false, v -> this.page.getValue() == Page.Items));
	public Setting<Boolean> paper  = this.register(new Setting<Boolean>("Paper", false, v -> this.page.getValue() == Page.Items));
	public Setting<Boolean> coal  = this.register(new Setting<Boolean>("Coal", false, v -> this.page.getValue() == Page.Items));
	public Setting<Boolean> iron  = this.register(new Setting<Boolean>("Iron", false, v -> this.page.getValue() == Page.Items));
	public Setting<Boolean> diamond  = this.register(new Setting<Boolean>("Diamond", false, v -> this.page.getValue() == Page.Items));
	public Setting<Boolean> leather  = this.register(new Setting<Boolean>("Leather", false, v -> this.page.getValue() == Page.Items));
	public Setting<Boolean> rawPorkChop  = this.register(new Setting<Boolean>("Raw PorkChop", false, v -> this.page.getValue() == Page.Items));
	public Setting<Boolean> rawChicken  = this.register(new Setting<Boolean>("Raw Chicken", false, v -> this.page.getValue() == Page.Items));
	public Setting<Boolean> rottenFlesh  = this.register(new Setting<Boolean>("Rotten Flesh", false, v -> this.page.getValue() == Page.Items));
	public Setting<Boolean> goldIngot  = this.register(new Setting<Boolean>("Gold Ingot", false, v -> this.page.getValue() == Page.Items));
	
    public Setting<Integer> delay = this.register(new Setting<Integer>("Delay", 250, 0, 1000, v -> this.page.getValue() == Page.Settings));
    public Setting<Boolean> tradeMessage = this.register(new Setting<Boolean>("TradeMessage", true, v -> this.page.getValue() == Page.Settings));
    public Setting<Boolean> messages = this.register(new Setting<Boolean>("DebugMessages", false, v -> this.page.getValue() == Page.Settings));
	
	public AutoTrader() {
        super("AutoTrader", "Makes insane deals with Villagers", Module.Category.MISC, true, false, false);
    }

    //List of trades you can add with the Trade command
    public List<Trade> customTrades = new LinkedList<>();
    //The MerchantRecipeList that is set when you open a villagers GUI
    private MerchantRecipeList recipeList = null;
    private final Timer timer = new Timer();

    @Override
    public void onUpdate() {
        if (nullCheck()) return;
        //Check if you are in the Trade GUI and set the recipeList to null if so
        if (!(mc.currentScreen instanceof GuiMerchant) && recipeList != null)
            recipeList = null;
        //Check if you are in the Trade GUI and set the recipeList to the villagers RecipeList
        if (mc.currentScreen instanceof GuiMerchant && recipeList == null)
            recipeList = ((GuiMerchant) mc.currentScreen).getMerchant().getRecipes(mc.player);
        //Check if the recipeList is set to null so you are not trying to trade at random
        if (recipeList == null)
            return;
        GuiMerchant gui = (GuiMerchant) mc.currentScreen;
        //Loop through all of the trades in the RecipeList
        for (int i = 0; i < recipeList.size(); i++) {
            MerchantRecipe recipe = recipeList.get(i);
            //Check if the recipe allows you to trade or not and continue if so
            if (recipe.isRecipeDisabled())
                continue;
            //Check if the current page you are on is the current recipe
            if (i != ((IGuiMerchant) gui).getSelectedMerchantRecipe())
                continue;
            //Check if the correct amount of time has passed
            if (!timer.passed((long) this.delay.getValue().intValue()))
                return;
            //Get all of the trades from the customTrades list
            List<Trade> tradingItems = new LinkedList<>(customTrades);
			
            if (this.potato.getValue().booleanValue()) {
                tradingItems.add(new Trade(getTradeItem("Potato"), Items.EMERALD));
            }
			if (this.carrot.getValue().booleanValue()) {
                tradingItems.add(new Trade(getTradeItem("Carrot"), Items.EMERALD));
            }
			if (this.wheat.getValue().booleanValue()) {
                tradingItems.add(new Trade(getTradeItem("Wheat"), Items.EMERALD));
            }
			if (this.pumpkin.getValue().booleanValue()) {
                tradingItems.add(new Trade(getTradeItem("Pumpkin"), Items.EMERALD));
            }
			if (this.melon.getValue().booleanValue()) {
                tradingItems.add(new Trade(getTradeItem("Melon"), Items.EMERALD));
            }
			if (this.string.getValue().booleanValue()) {
                tradingItems.add(new Trade(getTradeItem("String"), Items.EMERALD));
            }
			if (this.whiteWool.getValue().booleanValue()) {
                tradingItems.add(new Trade(getTradeItem("White Wool"), Items.EMERALD));
            }
			if (this.paper.getValue().booleanValue()) {
                tradingItems.add(new Trade(getTradeItem("Paper"), Items.EMERALD));
            }
			if (this.coal.getValue().booleanValue()) {
                tradingItems.add(new Trade(getTradeItem("Coal"), Items.EMERALD));
            }
			if (this.diamond.getValue().booleanValue()) {
                tradingItems.add(new Trade(getTradeItem("Diamond"), Items.EMERALD));
            }
			if (this.leather.getValue().booleanValue()) {
                tradingItems.add(new Trade(getTradeItem("Leather"), Items.EMERALD));
            }
			if (this.rawPorkChop.getValue().booleanValue()) {
                tradingItems.add(new Trade(getTradeItem("Raw PorkChop"), Items.EMERALD));
            }
			if (this.rawChicken.getValue().booleanValue()) {
                tradingItems.add(new Trade(getTradeItem("Raw Chicken"), Items.EMERALD));
            }
			if (this.rottenFlesh.getValue().booleanValue()) {
                tradingItems.add(new Trade(getTradeItem("Rotten Flesh"), Items.EMERALD));
            }
			
            //Checks if you have no trades either enabled or in your customTrades list
            if (tradingItems.isEmpty()) {
                Command.sendMessage("Please enable some trades in the AutoTrader settings.");
                toggle();
                return;
            }
            boolean debugMessages = messages.getValue();
            //Loop through the tradingItems
            for (Trade item : tradingItems) {
                //Make sure the current looped trade is the correct one for you
                if (item.getSellingItemOne().equals(recipe.getItemToBuy().getItem()) && item.getSellingItemTwo().equals(recipe.getSecondItemToBuy().getItem())) {
                    //Checks if your current count is below the required count and will return if so
                    if (recipe.getItemToBuy().getCount() > item.getMaxOne() || recipe.getSecondItemToBuy().getCount() > item.getMaxTwo())
                        return;
                    //Checks if the item in the bought slot is equal to the item you are buying
                    if (mc.player.openContainer.getSlot(2).getStack().getItem().equals(item.getBuyingItem())) {
                        //Moves the item to your inventory
                        mc.playerController.windowClick(gui.inventorySlots.windowId, 2, 0, ClickType.QUICK_MOVE, mc.player);
                        if (debugMessages)
                            Command.sendMessage("Took emeralds out.");
                    }
                    //Checks if the first slot you are buying has a count less then what you need to buy it
                    if (mc.player.openContainer.getSlot(0).getStack().getCount() < recipe.getItemToBuy().getCount()) {
                        int slot = -1;
                        for (int i1 = 3; i1 < 38; i1++) {
                            ItemStack stack = mc.player.openContainer.getSlot(i1).getStack();
                            if (stack.getItem().equals(item.getSellingItemOne()))
                                slot = i1;
                        }
                        if (slot != -1 && !recipe.isRecipeDisabled()) {
                            mc.playerController.windowClick(gui.inventorySlots.windowId, slot, 0, ClickType.PICKUP, mc.player);
                            mc.playerController.windowClick(gui.inventorySlots.windowId, 0, 0, ClickType.PICKUP, mc.player);
                            mc.playerController.windowClick(gui.inventorySlots.windowId, slot, 0, ClickType.PICKUP, mc.player);
                            if (debugMessages)
                                Command.sendMessage("Added item to merchant menu.");
                        }
                    //Checks if the second slot you are buying has a count less then what you need to buy it, and that the item is not equal to null that you are buying    
                    } else if (mc.player.openContainer.getSlot(1).getStack().getCount() < recipe.getSecondItemToBuy().getCount() && !item.getSellingItemTwo().equals(Items.AIR)) {
                        int slot = -1;
                        for (int i1 = 3; i1 < 38; i1++) {
                            ItemStack stack = mc.player.openContainer.getSlot(i1).getStack();
                            if (stack.getItem().equals(item.getSellingItemTwo()))
                                slot = i1;
                        }
                        if (slot != -1 && !recipe.isRecipeDisabled()) {
                            mc.playerController.windowClick(gui.inventorySlots.windowId, slot, 0, ClickType.PICKUP, mc.player);
                            mc.playerController.windowClick(gui.inventorySlots.windowId, 1, 0, ClickType.PICKUP, mc.player);
                            if (debugMessages)
                                Command.sendMessage("Added item to merchant menu.");
                        }
                    //Checks if the item in the first is ready to be bought    
                    } else if (mc.player.openContainer.getSlot(0).getStack().getCount() >= recipe.getItemToBuy().getCount() && !recipe.isRecipeDisabled()) {
                        //Sends the packet needed to process a trade
                        mc.player.connection.sendPacket(new CPacketConfirmTransaction(gui.inventorySlots.windowId, gui.inventorySlots.getNextTransactionID(mc.player.inventory), true));
                        if (debugMessages)
                            Command.sendMessage("Sent transaction packet.");
                        if (tradeMessage.getValue())
                            Command.sendMessage(ChatFormatting.GREEN + "Performed trade.");
                    }
                    //Reset the delay timer
                    timer.reset();
                }
            }
        }
    }
    
    public Item getTradeItem(String setting) {
        for (Item item : Item.REGISTRY) {
            if (item.getItemStackDisplayName(new ItemStack(item)).equals(setting)) {
                return item;
            }
        }
        return Items.AIR;
    }

    public static class Trade {
        private final Item sellingItemOne;
        private int maxOne = 64;
        private Item sellingItemTwo = Items.AIR;
        private int maxTwo = 64;
        private final Item buyingItem;

        public Trade(Item buyingItem, Item sellingItem) {
            this.sellingItemOne = buyingItem;
            this.buyingItem = sellingItem;
        }

        public Trade(Item buyingItem, Item secondBuyingItem, Item sellingItem) {
            this.sellingItemOne = buyingItem;
            this.sellingItemTwo = secondBuyingItem;
            this.buyingItem = sellingItem;
        }

        public Trade(Item buyingItem, int max, Item secondBuyingItem, Item sellingItem) {
            this.sellingItemOne = buyingItem;
            this.maxOne = max;
            this.sellingItemTwo = secondBuyingItem;
            this.maxTwo = 64;
            this.buyingItem = sellingItem;
        }

        public Trade(Item buyingItem, int max, Item secondBuyingItem, int maxTwo, Item sellingItem) {
            this.sellingItemOne = buyingItem;
            this.maxOne = max;
            this.sellingItemTwo = secondBuyingItem;
            this.maxTwo = maxTwo;
            this.buyingItem = sellingItem;
        }

        public Item getSellingItemOne() {
            return sellingItemOne;
        }

        public int getMaxOne() {
            return maxOne;
        }

        public Item getSellingItemTwo() {
            return sellingItemTwo;
        }

        public int getMaxTwo() {
            return maxTwo;
        }

        public Item getBuyingItem() {
            return buyingItem;
        }
    }
	
	public enum Page {
		Settings,
		Items
    }
}