package dev._3000IQPlay.trillium.modules.misc;

import dev._3000IQPlay.trillium.event.events.PacketEvent;
import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.Setting;
import dev._3000IQPlay.trillium.util.DynamicAnimation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Timer extends Module {
    public Timer() {
        super("Timer", "Timer", Category.MOVEMENT, true, false, false);
    }

    private DynamicAnimation violation = new DynamicAnimation();
    public static long lastUpdateTime;
    public static double value;

    public Setting<Float> speed = register(new Setting("TimerSpeed", 2.0f, 0.1f, 10.0f));
    public Setting<Boolean> smart = register(new Setting<>("Smart", true));
    public Setting<Integer> maxTicks = register(new Setting("MaxTicks", 0, 0, 15));




    public int getMin() {
        return - (15 - maxTicks.getValue());
    }

    @Override
    public void onUpdate(){
        update();
        violation.update();
    }


    @Override
    public void onDisable() {
        Timer.mc.timer.tickLength = 50.0f / 1.0f;
    }

    public void update() {
        if (!smart.getValue() || canEnableTimer(this.speed.getValue() + 0.2f)) {
            Timer.mc.timer.tickLength = 50.0f / Math.max(this.speed.getValue() + (mc.player.ticksExisted % 2 == 0 ? -0.2f : 0.2f), 0.1f);
        } else {
            Timer.mc.timer.tickLength = 50.0f / 1.0f;
        }
    }

    public boolean canEnableTimer(float speed) {
        double predictVl = (50.0 - (double) 50 / speed) / 50.0;
        return predictVl + value < 10 - this.speed.getValue();
    }

    public boolean canEnableTimerIgnoreSettings(float speed) {
        double predictVl = (50.0 - (double) 50 / speed) / 50.0;
        return predictVl + value < 10;
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send e){
        if(fullNullCheck()){
            return;
        }
        m();
    }

    public void m() {
        long now = System.currentTimeMillis();
        long timeElapsed = now - lastUpdateTime;
        lastUpdateTime = now;
        value += (50.0 - (double) timeElapsed) / 50.0;
        value -= 0.001;
        value = MathHelper.clamp((double) value, (double) getMin(), (double) 25.0);
    }
}
