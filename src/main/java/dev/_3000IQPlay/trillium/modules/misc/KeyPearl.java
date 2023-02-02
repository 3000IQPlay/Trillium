package dev._3000IQPlay.trillium.modules.misc;

import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.Setting;
import dev._3000IQPlay.trillium.util.InventoryUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import org.lwjgl.input.Mouse;

public class KeyPearl
        extends Module {
    private final Setting<Mode> mode = this.register(new Setting<Mode>("Mode", Mode.Middleclick));
    private final Setting<Boolean> antiFriend = this.register(new Setting<Boolean>("NoPlayerTrace", true));
    private boolean clicked;

    public KeyPearl() {
        super("KeyPearl", "Throws a pearl.", Module.Category.MISC, true, false, false);
    }

    @Override
    public void onEnable() {
        if (!fullNullCheck() && mode.getValue() == Mode.Key) {
            throwPearl();
            disable();
        }
    }

    @Override
    public void onTick() {
        if (mode.getValue() == Mode.Middleclick) {
            if (Mouse.isButtonDown(2)) {
                if (!clicked) {
                    throwPearl();
                }
                clicked = true;
            } else {
                clicked = false;
            }
        }
    }

    private void throwPearl() {
        boolean offhand;
        Entity entity;
        RayTraceResult result;
        if (antiFriend.getValue() && (result = KeyPearl.mc.objectMouseOver) != null && result.typeOfHit == RayTraceResult.Type.ENTITY && (entity = result.entityHit) instanceof EntityPlayer) {
            return;
        }
        int pearlSlot = InventoryUtil.findHotbarBlock(ItemEnderPearl.class);
        boolean bl = offhand = KeyPearl.mc.player.getHeldItemOffhand().getItem() == Items.ENDER_PEARL;
        if (pearlSlot != -1 || offhand) {
            int oldslot = KeyPearl.mc.player.inventory.currentItem;
            if (!offhand) {
                InventoryUtil.switchToHotbarSlot(pearlSlot, false);
            }
            KeyPearl.mc.playerController.processRightClick(KeyPearl.mc.player, KeyPearl.mc.world, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
            if (!offhand) {
                InventoryUtil.switchToHotbarSlot(oldslot, false);
            }
        }
    }
	
	public static enum Mode {
        Key,
        Middleclick
    }
}