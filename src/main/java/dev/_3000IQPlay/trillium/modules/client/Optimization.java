package dev._3000IQPlay.trillium.modules.client;

import dev._3000IQPlay.trillium.event.events.ConnectToServerEvent;
import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.Setting;
import dev._3000IQPlay.trillium.util.CleanerThread;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Optimization extends Module {
    public Optimization() {
        super("RamOptimization", "Optimizes your ram so it woudnt explode", Category.CLIENT, true, false, false);
    }

    public Setting<Boolean> cleanOnJoin = register(new Setting<>("CleanOnJoin", false));
    public Setting<Boolean> autoCleanup = register(new Setting<>("AutoCleanup", false));


    public Setting<Integer> minInterval = register(new Setting("MinInterval", 1800, 5, 10000));
    public Setting<Integer> maxInterval = register(new Setting("FOV", 1800, 5, 10000));
    public Setting<Integer> minIdleTime = register(new Setting("MinIdleTime", 1800, 5, 10000));
    public Setting<Integer> forceCleanPercentage = register(new Setting("ForceCleanPercentage", 80, 20, 100));


    public static long lastCleanTime = 0;
    public static int idleTime = 0;


    @Override
    public void onUpdate() {
        EntityPlayerSP player = mc.player;
        if (!mc.isGamePaused()  && player != null && player.world.isRemote) {
            boolean doClean = false;
            if ((System.currentTimeMillis() - lastCleanTime) > (long) minInterval.getValue() * 1000) {
                if ((double) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / Runtime.getRuntime().totalMemory() > (double) forceCleanPercentage.getValue() / 100.0) {
                    doClean = true;
                } else if (autoCleanup.getValue()) {
                    if (idleTime > minIdleTime.getValue() * 20) {
                        doClean = true;
                    }
                    if ((System.currentTimeMillis() - lastCleanTime) > (long) maxInterval.getValue() * 1000) {
                        doClean = true;
                    }
                }
                if (doClean) {
                    cleanMemory();
                    lastCleanTime = System.currentTimeMillis();
                    idleTime = 0;
                }
                if (autoCleanup.getValue()) {
                    if (player.motionX < 0.001 && player.motionY < 0.001 && player.motionZ < 0.001) {
                        idleTime++;
                    } else {
                        idleTime = 0;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLogin(ConnectToServerEvent event) {

        if (cleanOnJoin.getValue()) {
            cleanMemory();
        }
        lastCleanTime = System.currentTimeMillis();
        idleTime = 0;
    }



    public static void cleanMemory() {
        Runnable runnable = new CleanerThread();
        Thread gcThread = new Thread(runnable, "MemoryCleaner GC Thread");
        gcThread.setDaemon(true);
        gcThread.start();
    }


}
