package dev._3000IQPlay.trillium.modules.movement;

import dev._3000IQPlay.trillium.event.events.EventMove;
import dev._3000IQPlay.trillium.event.events.PacketEvent;
import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.Setting;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static dev._3000IQPlay.trillium.modules.movement.Jesus.isInLiquid;
import static dev._3000IQPlay.trillium.util.PyroSpeed.isMovingClient;

public class LiquidBoost extends Module {
    public LiquidBoost() {
        super("LiquidBoost", "LiquidBoost", Category.MOVEMENT, true, false, false);
    }


    Setting<Float> wFactor = register(new Setting("WFactor", 64.0f, 0.1f, 80.0f));
    Setting<Float> lFactor = register(new Setting("LFactor", 105.0f, 0.1f, 250.0f));
    Setting<Float> lVFactor = register(new Setting("LVertical", 1.0f, 0.1f, 20.0f));
    boolean flag;


    @Override
    public void onToggle() {
        flag = false;
    }

    @SubscribeEvent
    public void onMove(EventMove e) {
        if (!isMovingClient()) {
            return;
        }

        if (isInLiquid() && shouldBoost()) {
            mc.getConnection().sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SPRINTING));
            mc.player.setSprinting(true);
            if (!mc.gameSettings.keyBindSneak.isKeyDown() && mc.gameSettings.keyBindJump.isKeyDown()) {
                e.setY(0.11);
                flag = false;
            } else if (mc.gameSettings.keyBindSneak.isKeyDown() && !mc.gameSettings.keyBindJump.isKeyDown()) {
                mc.player.setSneaking(false);
                flag = false;
            } else {
                e.setY(0);
                flag = !flag;
            }
            e.setX(e.get_x() * (mc.player.isInLava() ? lFactor.getValue() : wFactor.getValue()));
            e.setY(lVFactor.getValue() <= 1.0f ? 0 : e.get_y() * lVFactor.getValue());
            e.setZ(e.get_z() * (mc.player.isInLava() ? lFactor.getValue() : wFactor.getValue()));
        }
        e.setCanceled(true);
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send e) {
        if (isDisabled() || fullNullCheck() || !isInLiquid()) {
            return;
        }

        if (e.getPacket() instanceof CPacketPlayer.Position || e.getPacket() instanceof CPacketPlayer.PositionRotation) {
            if (flag) {
                ((CPacketPlayer) e.getPacket()).y -= 0.005;
            }
        }
    }

    @SubscribeEvent
    public void onInputUpdate(InputUpdateEvent e) {
        if (fullNullCheck() || isDisabled()) {
            return;
        }
        if (mc.gameSettings.keyBindSneak.isKeyDown() && isInLiquid() && isMovingClient() && shouldBoost()) {
            e.getMovementInput().moveStrafe *= 5.0f;
            e.getMovementInput().moveForward *= 5.0f;
        }
    }



    public boolean shouldBoost() {
        return !mc.player.onGround;
    }
}
