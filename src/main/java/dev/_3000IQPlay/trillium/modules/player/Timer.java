package dev._3000IQPlay.trillium.modules.player;

import dev._3000IQPlay.trillium.Trillium;
import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.Setting;

public class Timer
        extends Module {
	private static Timer instance;
    public Setting<Float> timerSpeed = this.register(new Setting<Float>("TimerSpeed", Float.valueOf(4.0f), Float.valueOf(0.1f), Float.valueOf(50.0f)));
    public Setting<Boolean> Switch = this.register(new Setting<Boolean>("Switch", false));
    public Setting<Integer> activeTicks = this.register(new Setting<Integer>("Active", 5, 1, 20, v -> this.Switch.getValue()));
    public Setting<Integer> inactiveTicks = this.register(new Setting<Integer>("Inactive", 5, 1, 20, v -> this.Switch.getValue()));
    public Setting<Float> inactiveSpeed = this.register(new Setting<Float>("InactiveSpeed", Float.valueOf(2.0f), Float.valueOf(0.1f), Float.valueOf(50.0f), v -> this.Switch.getValue()));
    private int counter = 0;

    public Timer() {
        super("Timer", "Changes game tick length", Module.Category.PLAYER, true, false, false);
		instance = this;
    }
	
	public static Timer getInstance() {
        if (instance == null) {
            instance = new Timer();
        }
        return instance;
    }

    @Override
    public void onEnable() {
        this.counter = 0;
    }
	
	@Override
    public void onTick() {
		if (Timer.mc.world == null || Timer.mc.player == null) {
            return;
        }
        float speed = this.timerSpeed.getValue().floatValue();
        if (this.Switch.getValue().booleanValue()) {
            if (this.counter > this.activeTicks.getValue() + this.inactiveTicks.getValue()) {
                this.counter = 0;
            }
            if (this.counter > this.activeTicks.getValue()) {
                speed = this.inactiveSpeed.getValue().floatValue();
            }
        }
        Trillium.TIMER = speed;
        ++this.counter;
    }

    @Override
    public void onDisable() {
        Trillium.TIMER = 1.0f;
    }
}
