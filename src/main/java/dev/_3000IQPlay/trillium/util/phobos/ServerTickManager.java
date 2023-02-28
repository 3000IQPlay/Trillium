package dev._3000IQPlay.trillium.util.phobos;

import dev._3000IQPlay.trillium.Trillium;
import dev._3000IQPlay.trillium.event.events.ConnectToServerEvent;
import dev._3000IQPlay.trillium.event.events.PacketEvent;
import dev._3000IQPlay.trillium.modules.Feature;
import dev._3000IQPlay.trillium.util.Timer;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;


/**
 * @author megyn
 * fixed bugs with old version, now accurate within ~5 ms if start of tick is counted as when the time update packet is sent
 * TODO: use average time between packets being sent to more accurately approximate TPS, this will increase accuracy
 */
public class ServerTickManager extends Feature
{

    public void init() {
        MinecraftForge.EVENT_BUS.register(this);
    }
    private int serverTicks;
    private Map<BlockPos, Long> timeMap = new HashMap<>();
    private final Timer serverTickTimer = new Timer();
    private boolean flag = true;
    private boolean initialized = false; // will be used for checks in the future

    private final ArrayDeque<Integer> spawnObjectTimes = new ArrayDeque<>();
    private int averageSpawnObjectTime; // around 8-9 in vanilla

    public ServerTickManager()
    {



/*
        this.listeners.add(new EventListener<DisconnectEvent>(DisconnectEvent.class) {
            @Override
            public void invoke(DisconnectEvent event)
            {
                initialized = false;
            }
        });

 */
    }


    @SubscribeEvent
    public void onConnect(ConnectToServerEvent e){
        initialized = false;
        reset();
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive e){
        if(fullNullCheck()) return;
        if(e.getPacket() instanceof SPacketTimeUpdate){
            if (mc.world != null
                    && mc.world.isRemote)
            {
                reset();
            }
        }
        if(e.getPacket() instanceof SPacketSpawnObject) {
            if (mc.world != null
                    && mc.world.isRemote) {
                onSpawnObject();
            }
        }
    }

    /**
     * Retrieves the time into the current server tick
     * @return time into the current server tick
     */
    public int getTickTime() {
        if (serverTickTimer.getTime() < 50) return (int) serverTickTimer.getTime();
        return (int) (serverTickTimer.getTime() % getServerTickLengthMS());
    }

    /**
     * Retrieves the time into a tick that the server will receive a sent packet (experimental)
     * @return time that sent packets will be received by the client
     */
    public int getTickTimeAdjusted() {
        int time = getTickTime() + (Trillium.serverManager.getPing() / 2);
        if (time < getServerTickLengthMS()) return time; // redundant? idrk how modulus works in java
        return time % getServerTickLengthMS();
    }

    /**
     * Get the time into a tick that a packet was sent by the server
     * @return tick time adjusted for server packets
     */
    public int getTickTimeAdjustedForServerPackets() {
        int time = getTickTime() - (Trillium.serverManager.getPing() / 2);
        if (time < getServerTickLengthMS() && time > 0) return time; // redundant? idrk how modulus works in java
        if (time < 0) return time + getServerTickLengthMS();
        return time % getServerTickLengthMS();
    }

    public void reset() {
        serverTickTimer.reset();
        serverTickTimer.adjust(Trillium.serverManager.getPing() / 2);
        // flag = true;
        initialized = true;
    }

    public int getServerTickLengthMS() {
        if (Trillium.serverManager.getTPS() == 0) return 50;
        return (int) (50 * (20.0f / Trillium.serverManager.getTPS()));
    }

    public void onSpawnObject() {
        int time = getTickTimeAdjustedForServerPackets();
        if (spawnObjectTimes.size() > 10) spawnObjectTimes.poll();
        spawnObjectTimes.add(time);
        int totalTime = 0;
        for (int spawnTime : spawnObjectTimes) {
            totalTime += spawnTime;
        }
        averageSpawnObjectTime = totalTime / spawnObjectTimes.size();
    }

    public int normalize(int toNormalize) {
        while (toNormalize < 0) {
            toNormalize += getServerTickLengthMS();
        }
        while (toNormalize > getServerTickLengthMS()) {
            toNormalize -= getServerTickLengthMS();
        }
        return toNormalize;
    }

    public boolean valid(int currentTime, int minTime, int maxTime) {
        if (minTime > maxTime) {
            return currentTime >= minTime || currentTime <= maxTime;
        } else {
            return currentTime >= minTime && currentTime <= maxTime;
        }
    }

    public int getSpawnTime() {
        return averageSpawnObjectTime;
    }

}