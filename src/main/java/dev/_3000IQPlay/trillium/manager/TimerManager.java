package dev._3000IQPlay.trillium.manager; // THIS SHIT DOESNT WOOOOORK

import dev._3000IQPlay.trillium.modules.Feature;

public class TimerManager
        extends Feature {
    private float timer = 1.0f;

    public void init() {
		this.timer = 1.0f;
	}

    public void unload() {
        this.timer = 1.0f;
        TimerManager.mc.timer.tickLength = 50.0f;
    }

    public void update() {
        TimerManager.mc.timer.tickLength = 50.0f / (this.timer <= 0.0f ? 0.1f : this.timer);
    }

    public float getTimer() {
        return this.timer;
    }

    public void setTimer(float timer) {
        if (timer > 0.0f) {
            this.timer = timer;
        }
    }

    @Override
    public void reset() {
        this.timer = 1.0f;
    }
}