package dev._3000IQPlay.trillium.util.phobos;

import dev._3000IQPlay.trillium.Trillium;
import dev._3000IQPlay.trillium.event.events.PacketEvent;
import dev._3000IQPlay.trillium.mixin.mixins.ICPacketPlayer;
import dev._3000IQPlay.trillium.modules.combat.AutoCrystal;
import dev._3000IQPlay.trillium.modules.render.Rotation;
import dev._3000IQPlay.trillium.setting.Setting;
import dev._3000IQPlay.trillium.util.Timer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static dev._3000IQPlay.trillium.modules.combat.Burrow.rotation;
import static dev._3000IQPlay.trillium.util.ItemUtil.mc;

public class RotationCanceller
{


    private final Timer timer = new Timer();
    private final Setting<Integer> maxCancel;
    private final AutoCrystal module;

    private volatile CPacketPlayer last;

    public RotationCanceller(AutoCrystal module, Setting<Integer> maxCancel)
    {
        this.module = module;
        this.maxCancel = maxCancel;
    }

    /**
     * Sends the last cancelled packet if
     * the timer passed the MaxCancel time.
     */
    public void onGameLoop()
    {
        if (last != null && timer.passedMs(maxCancel.getValue()))
        {
            sendLast();
        }
    }

    public synchronized void onPacketNigger(PacketEvent.Send event)
    {
        if(event.getPacket() instanceof CPacketPlayer) {
            if (event.isCanceled()) {
                return;
            }

            reset(); // Send last Packet if it hasn't been yet
            if (Trillium.rotationManager.isBlocking()) {
                return;
            }

            event.setCanceled(true);
            last = event.getPacket();
            timer.reset();
        }
    }


    /**
     * Sets the Rotations of the last Packet and sends it,
     * if it has been cancelled.
     *
     * @param function the RotationFunction setting the packet.
     * @return <tt>true</tt> if Rotations have been set.
     */
    public synchronized boolean setRotations(RotationFunction function)
    {
        if (last == null)
        {
            return false;
        }

        double x = last.getX(Trillium.positionManager.getX());
        double y = last.getX(Trillium.positionManager.getY());
        double z = last.getX(Trillium.positionManager.getZ());
        float yaw   = Trillium.rotationManager.getServerYaw();
        float pitch = Trillium.rotationManager.getServerPitch();
        boolean onGround = last.isOnGround();

        ICPacketPlayer accessor = (ICPacketPlayer) last;
        float[] r = function.apply(x, y, z, yaw, pitch);
        if (r[0] - yaw == 0.0 || r[1] - pitch == 0.0)
        {
            if (!accessor.isRotating()
                    && !accessor.isMoving()
                    && onGround == Trillium.positionManager.isOnGround())
            {
                last = null;
                return true;
            }

            sendLast();
            return true;
        }

        if (accessor.isRotating())
        {
            accessor.setYaw(r[0]);
            accessor.setPitch(r[1]);
            sendLast();
        }
        else if (accessor.isMoving())
        {
            last = positionRotation(x, y, z, r[0], r[1], onGround);
            sendLast();
        }
        else
        {
            last = rotation(r[0], r[1], onGround);
            sendLast();
        }

        return true;
    }

    public static CPacketPlayer positionRotation(double x,
                                                 double y,
                                                 double z,
                                                 float yaw,
                                                 float pitch,
                                                 boolean onGround)
    {
        return new CPacketPlayer.PositionRotation(x, y, z, yaw, pitch, onGround);
    }

    /**
     * Sends the last Packet if it has been cancelled.
     */
    public void reset()
    {
        if (last != null && mc.player != null)
        {
            sendLast();
        }
    }

    /**
     * Drops the current packet. It won't be send.
     */
    public synchronized void drop()
    {
        last = null;
    }

    private synchronized void sendLast()
    {
        CPacketPlayer packet = last;
        if (packet != null && mc.player != null)
        {
            mc.player.connection.sendPacket(packet);
            module.runPost();
        }

        last = null;
    }


    public void onPacketNigger9(CPacketPlayer.Rotation rotation) {
            reset(); // Send last Packet if it hasn't been yet
            if (Trillium.rotationManager.isBlocking()) {
                return;
            }


            last = rotation;
            timer.reset();

    }
}