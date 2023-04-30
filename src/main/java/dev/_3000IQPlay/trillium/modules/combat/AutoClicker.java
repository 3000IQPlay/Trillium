package dev._3000IQPlay.trillium.modules.combat;

import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.Setting;

public class AutoClicker extends Module {
	private Setting<ClickMode> clickType = this.register(new Setting<>("ClickType", ClickMode.Left));
	public Setting<Integer> maxCPS = this.register(new Setting<>("MaxCPS", 8, 1, 25));
	public Setting<Integer> minCPS = this.register(new Setting<>("MinCPS", 4, 1, 25));

    long leftLastSwing;
    long leftDelay;
    long rightLastSwing;
    long rightDelay;

	public AutoClicker() {
        super("AutoClicker", "Automatically clicks for you", Module.Category.COMBAT, true, false, false);
    }

    @Override
    public void onUpdate() {
        int minCPS = (int) this.minCPS.getValue();
        int maxCPS = (int) this.maxCPS.getValue();
        boolean leftClick = this.clickType.getValue() == ClickMode.Left;
        boolean rightClick = this.clickType.getValue() == ClickMode.Right;

        if (System.currentTimeMillis() - this.leftLastSwing >= this.leftDelay) {
            if (mc.gameSettings.keyBindAttack.isKeyDown() && leftClick && System.currentTimeMillis() - this.leftLastSwing >= this.leftDelay) {
                mc.clickMouse();
                this.leftLastSwing = System.currentTimeMillis();
                this.leftDelay = AutoClicker.randomDelay(minCPS, maxCPS);
            }
            if (mc.gameSettings.keyBindUseItem.isKeyDown() && !mc.player.isHandActive() && rightClick && System.currentTimeMillis() - this.rightLastSwing >= this.rightDelay) {
                mc.rightClickMouse();
                this.rightLastSwing = System.currentTimeMillis();
                this.rightDelay = AutoClicker.randomDelay(minCPS, maxCPS);
            }
        }
    }
	
	public static long randomDelay(final int minDelay, final int maxDelay) {
        return (int) Math.floor(Math.random() * (maxDelay - minDelay + 1) + minDelay);
    }
	
	public static enum ClickMode {
		Left,
		Right;
	}
}