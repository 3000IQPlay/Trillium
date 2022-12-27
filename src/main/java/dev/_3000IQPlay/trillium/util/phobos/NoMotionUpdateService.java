package dev._3000IQPlay.trillium.util.phobos;

import dev._3000IQPlay.trillium.event.events.EventPreMotion;
import dev._3000IQPlay.trillium.event.events.NoMotionUpdateEvent;
import dev._3000IQPlay.trillium.event.events.PacketEvent;
import dev._3000IQPlay.trillium.modules.Feature;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static dev._3000IQPlay.trillium.util.Util.mc;

/**
 * Posts a {@link NoMotionUpdateEvent} Event,
 * if onUpdateWalkingPlayer is called,
 * but no CPacketPlayer is sent.
 */
public class NoMotionUpdateService extends Feature
{
    private boolean awaiting;

    public NoMotionUpdateService()
    {

    }

    public void init() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event){
        if(event.getPacket() instanceof  CPacketPlayer.PositionRotation){
            setAwaiting(false);
        }
        if(event.getPacket() instanceof  CPacketPlayer.Position){
            setAwaiting(false);
        }
        if(event.getPacket() instanceof  CPacketPlayer.Rotation){
            setAwaiting(false);
        }
        if(event.getPacket() instanceof  CPacketPlayer){
            setAwaiting(false);
        }
    }

    @SubscribeEvent
    public void onMotion(EventPreMotion e){
        if (e.isCanceled())
        {
            return;
        }

        if (e.getStage() == 0)
        {
            setAwaiting(true);
        }
        else
        {
            if (isAwaiting())
            {
                NoMotionUpdateEvent noMotionUpdate =
                        new NoMotionUpdateEvent();
                MinecraftForge.EVENT_BUS.post(noMotionUpdate);
            }

            setAwaiting(false);
        }
    }

    public void setAwaiting(boolean awaiting)
    {
        this.awaiting = awaiting;
    }

    public boolean isAwaiting()
    {
        return awaiting;
    }

}