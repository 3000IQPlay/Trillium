package dev._3000IQPlay.trillium.modules.combat;

import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.Setting;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumHand;

public class AutoClicker extends Module {
	private Setting<ClickPage> clickPage = this.register(new Setting<>("ClickPage", ClickPage.Left));
	
	public Setting<Boolean> leftClick = this.register(new Setting<>("LeftClick", true, v -> this.clickPage.getValue() == ClickPage.Left));
	public Setting<LeftSwing> leftSwing = this.register(new Setting<>("LeftSwing", LeftSwing.Post, v -> this.clickPage.getValue() == ClickPage.Left));
	public Setting<Boolean> leftOffhand = this.register(new Setting<>("LeftOffhand", false, v -> this.clickPage.getValue() == ClickPage.Left));
	public Setting<Integer> lMaxCPS = this.register(new Setting<>("LeftMaxCPS", 8, 1, 25, v -> this.clickPage.getValue() == ClickPage.Left));
	public Setting<Integer> lMinCPS = this.register(new Setting<>("LeftMinCPS", 4, 1, 25, v -> this.clickPage.getValue() == ClickPage.Left));
	
	public Setting<Boolean> rightClick = this.register(new Setting<>("RightClick", false, v -> this.clickPage.getValue() == ClickPage.Right));
	public Setting<RightSwing> rightSwing = this.register(new Setting<>("RightSwing", RightSwing.Post, v -> this.clickPage.getValue() == ClickPage.Right));
	public Setting<Boolean> rightOffhand = this.register(new Setting<>("RightOffhand", false, v -> this.clickPage.getValue() == ClickPage.Right));
	public Setting<Integer> rMaxCPS = this.register(new Setting<>("RightMaxCPS", 8, 1, 25, v -> this.clickPage.getValue() == ClickPage.Right));
	public Setting<Integer> rMinCPS = this.register(new Setting<>("RightMinCPS", 4, 1, 25, v -> this.clickPage.getValue() == ClickPage.Right));
    long leftLastSwing;
    long leftDelay;
    long rightLastSwing;
    long rightDelay;

	public AutoClicker() {
        super("AutoClicker", "Automatically clicks for you", Module.Category.COMBAT, true, false, false);
    }

    @Override
    public void onUpdate() {
        int lMinCPS = (int) this.lMinCPS.getValue();
        int lMaxCPS = (int) this.lMaxCPS.getValue();
		int rMinCPS = (int) this.rMinCPS.getValue();
        int rMaxCPS = (int) this.rMaxCPS.getValue();
        boolean leftClick = this.leftClick.getValue();
        boolean rightClick = this.rightClick.getValue();

        if (System.currentTimeMillis() - this.leftLastSwing >= this.leftDelay) {
            if (mc.gameSettings.keyBindAttack.isKeyDown() && leftClick && System.currentTimeMillis() - this.leftLastSwing >= this.leftDelay) {
				if (this.leftSwing.getValue() == LeftSwing.Pre) {
				    mc.player.swingArm(leftOffhand.getValue() ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
				}
                KeyBinding.onTick(mc.gameSettings.keyBindAttack.keyCode);
				if (this.leftSwing.getValue() == LeftSwing.Post) {
				    mc.player.swingArm(leftOffhand.getValue() ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
				}
                this.leftLastSwing = System.currentTimeMillis();
                this.leftDelay = AutoClicker.randomDelay(lMinCPS, lMaxCPS);
            }
            if (mc.gameSettings.keyBindUseItem.isKeyDown() && !mc.player.isHandActive() && rightClick && System.currentTimeMillis() - this.rightLastSwing >= this.rightDelay) {
				if (this.rightSwing.getValue() == RightSwing.Pre) {
				    mc.player.swingArm(rightOffhand.getValue() ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
				}
                KeyBinding.onTick(mc.gameSettings.keyBindUseItem.keyCode);
				if (this.rightSwing.getValue() == RightSwing.Post) {
				    mc.player.swingArm(rightOffhand.getValue() ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
				}
                this.rightLastSwing = System.currentTimeMillis();
                this.rightDelay = AutoClicker.randomDelay(rMinCPS, rMaxCPS);
            }
        }
    }
	
	public static long randomDelay(final int minDelay, final int maxDelay) {
        return (int) Math.floor(Math.random() * (maxDelay - minDelay + 1) + minDelay);
    }
	
	public static enum ClickPage {
		Left,
		Right;
	}
	
	public static enum LeftSwing {
		Pre,
		Post;
	}
	
	public static enum RightSwing {
		Pre,
		Post;
	}
}