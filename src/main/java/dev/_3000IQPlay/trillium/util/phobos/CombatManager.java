package dev._3000IQPlay.trillium.util.phobos;

import dev._3000IQPlay.trillium.event.events.DeathEvent;
import dev._3000IQPlay.trillium.event.events.PacketEvent;
import dev._3000IQPlay.trillium.modules.Feature;
import dev._3000IQPlay.trillium.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CombatManager extends Feature
{
    private final Map<EntityPlayer, PopCounter> pops =
            new ConcurrentHashMap<>();


    public void init() {
        MinecraftForge.EVENT_BUS.register(this);
    }


    public CombatManager() {
        /*
        this.listeners.add(
                new EventListener<DeathEvent>(DeathEvent.class, Integer.MIN_VALUE) {
                    @Override
                    public void invoke(DeathEvent event) {
                        onDeath(event.getEntity());
                    }
                });

         */

    }
    /*
    @SubscribeEvent
    public void onDeath(DeathEvent e){
        if e.ge
    }

     */

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive e){
        if(fullNullCheck()) return;
        if(e.getPacket() instanceof SPacketEntityStatus) {
            switch (((SPacketEntityStatus) e.getPacket()).getOpCode()) {
                case 3:
                    mc.addScheduledTask(() ->
                            onDeath(mc.world == null
                                    ? null
                                    : ((SPacketEntityStatus) e.getPacket()).getEntity(mc.world)));
                    break;
                case 35:
                    mc.addScheduledTask(() -> onTotemPop(e.getPacket()));
                default:
            }

        }
    }

    public void reset() {
        pops.clear();
    }

    public int getPops(Entity player) {
        if (player instanceof EntityPlayer) {
            PopCounter popCounter = pops.get(player);
            if (popCounter != null) {
                return popCounter.getPops();
            }
        }

        return 0;
    }

    public long lastPop(Entity player) {
        if (player instanceof EntityPlayer) {
            PopCounter popCounter = pops.get(player);
            if (popCounter != null) {
                return popCounter.lastPop();
            }
        }

        return Integer.MAX_VALUE;
    }

    private void onTotemPop(SPacketEntityStatus packet) {
        Entity player = packet.getEntity(mc.world);
        if (player instanceof EntityPlayer) {
            pops.computeIfAbsent((EntityPlayer) player, v -> new PopCounter())
                    .pop();
        }
    }

    private void onDeath(Entity entity) {
        if (entity instanceof EntityPlayer) {
            pops.remove(entity);
        }
    }

    private static class PopCounter {
        private final Timer timer = new Timer();
        private int pops;

        public int getPops() {
            return pops;
        }

        public void pop() {
            timer.reset();
            pops++;
        }

        public void reset() {
            pops = 0;
        }

        public long lastPop() {
            return timer.getTime();
        }
    }

}