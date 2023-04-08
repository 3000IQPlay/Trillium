package dev._3000IQPlay.trillium.modules.misc;

import dev._3000IQPlay.trillium.Trillium;
import dev._3000IQPlay.trillium.command.Command;
import dev._3000IQPlay.trillium.event.events.ClickMiddleEvent;
import dev._3000IQPlay.trillium.event.events.EventPostMotion;
import dev._3000IQPlay.trillium.event.events.EventPreMotion;
import dev._3000IQPlay.trillium.mixin.mixins.IMinecraft;
import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.Setting;
import dev._3000IQPlay.trillium.util.InventoryUtil;
import dev._3000IQPlay.trillium.util.Timer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemFood;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

public class MiddleClick extends Module {
    public Setting<Boolean> fm = register(new Setting<>("FriendMessage", true));
    public Setting<Boolean> friend = register(new Setting<>("Friend", true));
    public Setting<Boolean> rocket = register(new Setting<>("Rocket", false));
    public Setting<Boolean> xp = register(new Setting<>("XP", false));
    public Setting<Boolean> feetExp = register(new Setting<>("FeetXP", false,v->xp.getValue()));
    public Setting<Boolean> silent = register(new Setting<>("SilentXP", true,v->xp.getValue()));
    public Setting<Boolean> whileEating = register(new Setting<>("WhileEating", true));
    public Setting<Boolean> pickBlock = register(new Setting<>("CancelMC", true));
    public Timer timr = new Timer();
    private int lastSlot = -1;

    public MiddleClick() {
        super("MiddleClick", "MiddleClick == RClick", Module.Category.MISC, true, false, false);
    }

    @SubscribeEvent
    public void onPreMotion(EventPreMotion event) {
        if (fullNullCheck()) return;

        if (xp.getValue() && feetExp.getValue() && Mouse.isButtonDown(2)) {
            mc.player.rotationPitch = 90f;
        }

        if (friend.getValue() && mc.objectMouseOver != null && mc.objectMouseOver.entityHit != null && Mouse.isButtonDown(2)) {
            Entity entity = mc.objectMouseOver.entityHit;
            if (entity instanceof EntityPlayer && timr.passedMs(2500)) {
                if (Trillium.friendManager.isFriend(entity.getName())) {
                    Trillium.friendManager.removeFriend(entity.getName());
                    Command.sendMessage("Removed §b" + entity.getName() + "§r from friend list!");
                } else {
                    Trillium.friendManager.addFriend(entity.getName());
                    if (fm.getValue()) {
                        mc.player.sendChatMessage("/w " + entity.getName() + " I have friended you on Trillium");
                    }
                    Command.sendMessage("Added §b" + entity.getName() + "§r into friend list!");
                }
                timr.reset();
                return;
            }
        }

        if (rocket.getValue() && findRocketSlot() != -1 && timr.passedMs(500) && Mouse.isButtonDown(2)) {
            int rocketSlot = findRocketSlot();
            int originalSlot = mc.player.inventory.currentItem;

            if (rocketSlot != -1) {
                mc.player.inventory.currentItem = rocketSlot;
                mc.player.connection.sendPacket(new CPacketHeldItemChange(rocketSlot));

                mc.playerController.processRightClick(mc.player, mc.world, EnumHand.MAIN_HAND);

                mc.player.inventory.currentItem = originalSlot;
                mc.player.connection.sendPacket(new CPacketHeldItemChange(originalSlot));
                timr.reset();
                return;
            }
        }
    }

    @SubscribeEvent
    public void onPostMotion(EventPostMotion event) {
        if (xp.getValue()) {
            if (Mouse.isButtonDown(2) && (whileEating.getValue() || !(mc.player.getActiveItemStack().getItem() instanceof ItemFood))) {
                int slot = InventoryUtil.findItemAtHotbar(Items.EXPERIENCE_BOTTLE);
                if (slot != -1) {
                    int lastSlot = mc.player.inventory.currentItem;
                    InventoryUtil.switchTo(slot);
                    mc.playerController.processRightClick(mc.player, mc.world, InventoryUtil.getHand(slot));
                    if (silent.getValue()) {
                        InventoryUtil.switchTo(lastSlot);
                    }
                } else if (lastSlot != -1) {
                    InventoryUtil.switchTo(lastSlot);
                    lastSlot = -1;
                }
            } else if (lastSlot != -1) {
                InventoryUtil.switchTo(lastSlot);
                lastSlot = -1;
            }
        }
    }

    private int findRocketSlot() {
        int rocketSlot = -1;
        if (mc.player.getHeldItemMainhand().getItem() == Items.FIREWORKS) {
            rocketSlot = mc.player.inventory.currentItem;
        }
		
        if (rocketSlot == -1) {
            for (int l = 0; l < 9; ++l) {
                if (mc.player.inventory.getStackInSlot(l).getItem() == Items.FIREWORKS) {
                    rocketSlot = l;
                    break;
                }
            }
        }
		
        return rocketSlot;
    }

    @SubscribeEvent
    public void onMiddleClick(ClickMiddleEvent event) {
        if (!xp.getValue()) return;
        if (pickBlock.getValue()) {
            int slot = InventoryUtil.findItemAtHotbar(Items.EXPERIENCE_BOTTLE);
            if (slot != -1 && slot != -2 && slot != mc.player.inventory.currentItem) {
                event.setCanceled(true);
            }
        }
    }
}