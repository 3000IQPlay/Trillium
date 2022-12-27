package dev._3000IQPlay.trillium.util.phobos;

import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.util.EnumHand;

import static dev._3000IQPlay.trillium.util.Util.mc;

public enum Swing
{
    None
            {
                @Override
                public void swing(EnumHand hand)
                {
                    /* Nothing */
                }
            },
    Packet
            {
                @Override
                public void swing(EnumHand hand)
                {
                    mc.player.connection.sendPacket(new CPacketAnimation(hand));
                }
            },
    Full
            {
                @Override
                public void swing(EnumHand hand)
                {
                    mc.player.swingArm(hand);
                }
            },
    Client
            {
                @Override
                public void swing(EnumHand hand)
                {
                    mc.player.swingArm(hand);
                }
            };

    public abstract void swing(EnumHand hand);

}