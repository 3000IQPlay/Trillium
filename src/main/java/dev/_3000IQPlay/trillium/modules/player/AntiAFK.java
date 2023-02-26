package dev._3000IQPlay.trillium.modules.player;

import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.Setting;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import java.util.Random;
import java.util.UUID;

public class AntiAFK
        extends Module {
    private final Random random = new Random();
    private final Setting<Boolean> swing = this.register(new Setting<Boolean>("Swing", true));
    private final Setting<Boolean> turn = this.register(new Setting<Boolean>("Turn", true));
    private final Setting<Boolean> jump = this.register(new Setting<Boolean>("Jump", true));
    private final Setting<Boolean> sneak = this.register(new Setting<Boolean>("Sneak", false));
	private final Setting<Boolean> itemSwap = this.register(new Setting<Boolean>("ItemSwap", false));
    private final Setting<Boolean> interactBlock = this.register(new Setting<Boolean>("InteractBlock", false));
	private final Setting<Boolean> hitBlock = this.register(new Setting<Boolean>("HitBlock", true));
    private final Setting<Boolean> chat = this.register(new Setting<Boolean>("ChatMsgs", false));

    public AntiAFK() {
        super("AntiAFK", "Attempts to stop the server from kicking u when ur afk.", Module.Category.PLAYER, true, false, false);
    }

    @Override
    public void onUpdate() {
        BlockPos blockPos;
        if (AntiAFK.mc.player.ticksExisted % 45 == 0 && this.swing.getValue().booleanValue()) {
            AntiAFK.mc.player.swingArm(EnumHand.MAIN_HAND);
        }
        if (AntiAFK.mc.player.ticksExisted % 20 == 0 && this.turn.getValue().booleanValue()) {
            AntiAFK.mc.player.rotationYaw = this.random.nextInt(360) - 180;
        }
        if (AntiAFK.mc.player.ticksExisted % 60 == 0 && this.jump.getValue().booleanValue() && AntiAFK.mc.player.onGround) {
            AntiAFK.mc.player.jump();
        }
        if (AntiAFK.mc.player.ticksExisted % 50 == 0 && this.sneak.getValue().booleanValue() && !AntiAFK.mc.player.isSneaking()) {
            AntiAFK.mc.player.movementInput.sneak = true;
        }
        if ((double)AntiAFK.mc.player.ticksExisted % 52.5 == 0.0 && this.sneak.getValue().booleanValue() && AntiAFK.mc.player.isSneaking()) {
            AntiAFK.mc.player.movementInput.sneak = false;
        }
        if (AntiAFK.mc.player.ticksExisted % 30 == 0 && this.interactBlock.getValue().booleanValue() && !AntiAFK.mc.world.isAirBlock(blockPos = AntiAFK.mc.objectMouseOver.getBlockPos())) {
            AntiAFK.mc.playerController.clickBlock(blockPos, AntiAFK.mc.objectMouseOver.sideHit);
        }
        if (AntiAFK.mc.player.ticksExisted % 200 == 0 && this.chat.getValue().booleanValue() && !AntiAFK.mc.player.isDead) {
            AntiAFK.mc.player.sendChatMessage("Im currently afk " + this.random.nextInt());
        }
        if (AntiAFK.mc.player.ticksExisted % 70 == 0 && this.itemSwap.getValue().booleanValue() && !AntiAFK.mc.player.isDead) {
            AntiAFK.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.SWAP_HELD_ITEMS, AntiAFK.mc.player.getPosition(), EnumFacing.DOWN));
        }
        if (AntiAFK.mc.player.ticksExisted % 50 == 0 && this.hitBlock.getValue().booleanValue()) {
            AntiAFK.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, AntiAFK.mc.player.getPosition(), EnumFacing.DOWN));
        }
    }
}
