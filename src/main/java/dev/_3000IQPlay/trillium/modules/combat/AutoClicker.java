package dev._3000IQPlay.trillium.modules.combat;

import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;

import java.util.Random;

public class AutoClicker extends Module {
	private Setting<ClickPage> clickPage = this.register(new Setting<>("ClickPage", ClickPage.Left));
	
	public Setting<Boolean> leftClick = this.register(new Setting<>("LeftClick", true, v -> this.clickPage.getValue() == ClickPage.Left));
	public Setting<LeftSwing> leftSwing = this.register(new Setting<>("LeftSwing", LeftSwing.Post, v -> this.clickPage.getValue() == ClickPage.Left));
	public Setting<Boolean> leftOffhand = this.register(new Setting<>("LeftOffhand", false, v -> this.clickPage.getValue() == ClickPage.Left));
	public Setting<Float> leftJitterAmount = this.register(new Setting<>("LeftJitterAmount", 0.0f, 0.0f, 5.0f, v -> this.clickPage.getValue() == ClickPage.Left));
	/*public Setting<Float> leftJitterMinSpeed = this.register(new Setting<>("LeftJitterMinSpeed", 0.3f, 0.1f, 2.0f, v -> this.clickPage.getValue() == ClickPage.Left));
	public Setting<Float> leftJitterMaxSpeed = this.register(new Setting<>("LeftJitterMaxSpeed", 0.5f, 0.1f, 2.0f, v -> this.clickPage.getValue() == ClickPage.Left));*/
	public Setting<Integer> lMaxCPS = this.register(new Setting<>("LeftMaxCPS", 8, 1, 25, v -> this.clickPage.getValue() == ClickPage.Left));
	public Setting<Integer> lMinCPS = this.register(new Setting<>("LeftMinCPS", 4, 1, 25, v -> this.clickPage.getValue() == ClickPage.Left));
	
	public Setting<Boolean> rightClick = this.register(new Setting<>("RightClick", false, v -> this.clickPage.getValue() == ClickPage.Right));
	public Setting<RightSwing> rightSwing = this.register(new Setting<>("RightSwing", RightSwing.Post, v -> this.clickPage.getValue() == ClickPage.Right));
	public Setting<Boolean> rightOffhand = this.register(new Setting<>("RightOffhand", false, v -> this.clickPage.getValue() == ClickPage.Right));
	public Setting<Float> rightJitterAmount = this.register(new Setting<>("RightJitterAmount", 0.0f, 0.0f, 5.0f, v -> this.clickPage.getValue() == ClickPage.Right));
	/*public Setting<Float> rightJitterMinSpeed = this.register(new Setting<>("RightJitterMinSpeed", 0.3f, 0.1f, 2.0f, v -> this.clickPage.getValue() == ClickPage.Right));
	public Setting<Float> rightJitterMaxSpeed = this.register(new Setting<>("RightJitterMaxSpeed", 0.5f, 0.1f, 2.0f, v -> this.clickPage.getValue() == ClickPage.Right));*/
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
		float pitch = mc.player.rotationPitch;
        float yaw = mc.player.rotationYaw;
        if (System.currentTimeMillis() - this.leftLastSwing >= this.leftDelay) {
            if (mc.gameSettings.keyBindAttack.isKeyDown() && this.leftClick.getValue() && System.currentTimeMillis() - this.leftLastSwing >= this.leftDelay) {
				/*float lJitterYaw = (float) (Math.random() * this.leftJitterAmount.getValue() * 2 - this.leftJitterAmount.getValue());
                float lJitterPitch = (float) (Math.random() * this.leftJitterAmount.getValue() * 2 - this.leftJitterAmount.getValue());
				float leftJitterSpeed = (float) getRandomSpeedBetween(this.leftJitterMinSpeed.getValue(), this.leftJitterMaxSpeed.getValue());*/
				
				AutoClicker.performJitter(this.leftJitterAmount.getValue());
				
				if (this.leftSwing.getValue() == LeftSwing.Pre) {
				    mc.player.swingArm(leftOffhand.getValue() ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
				}
				
                KeyBinding.onTick(mc.gameSettings.keyBindAttack.keyCode);
				
				if (this.leftSwing.getValue() == LeftSwing.Post) {
				    mc.player.swingArm(leftOffhand.getValue() ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
				}
				
                this.leftLastSwing = System.currentTimeMillis();
                this.leftDelay = AutoClicker.randomDelay(this.lMinCPS.getValue(), this.lMaxCPS.getValue());
            }
            if (mc.gameSettings.keyBindUseItem.isKeyDown() && !mc.player.isHandActive() && this.rightClick.getValue() && System.currentTimeMillis() - this.rightLastSwing >= this.rightDelay) {
				/*float rJitterYaw = (float) (Math.random() * this.rightJitterAmount.getValue() * 2 - this.rightJitterAmount.getValue());
                float rJitterPitch = (float) (Math.random() * this.rightJitterAmount.getValue() * 2 - this.rightJitterAmount.getValue());
				float rightJitterSpeed = (float) getRandomSpeedBetween(this.rightJitterMinSpeed.getValue(), this.rightJitterMaxSpeed.getValue());*/
				
				AutoClicker.performJitter(this.rightJitterAmount.getValue());
				
				if (this.rightSwing.getValue() == RightSwing.Pre) {
				    mc.player.swingArm(rightOffhand.getValue() ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
				}
				
                KeyBinding.onTick(mc.gameSettings.keyBindUseItem.keyCode);
				
				if (this.rightSwing.getValue() == RightSwing.Post) {
				    mc.player.swingArm(rightOffhand.getValue() ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
				}
				
                this.rightLastSwing = System.currentTimeMillis();
                this.rightDelay = AutoClicker.randomDelay(this.rMinCPS.getValue(), this.rMaxCPS.getValue());
            }
        }
    }
	
	static long lastTime = System.currentTimeMillis();
	
	public static void performJitter(float intensity) {
		Minecraft minecraft = Minecraft.getMinecraft();
		EntityPlayerSP player = minecraft.player;

		// Get the current pitch and yaw of the player
		float pitch = player.rotationPitch;
		float yaw = player.rotationYaw;

		// Calculate the new pitch and yaw values by adding the jitter effect
		float newPitch = pitch + (minecraft.world.rand.nextFloat() - 0.5f) * intensity;
		float newYaw = yaw + (minecraft.world.rand.nextFloat() - 0.5f) * intensity;

		// Limit the new pitch value to prevent looking too far up or down
		newPitch = MathHelper.clamp(newPitch, -90f, 90f);

		// Set the new pitch and yaw values for the player's camera
		CameraPlayer camera = minecraft.gameSettings.thirdPersonView == 0 ? minecraft.player.getRidingEntity() instanceof EntityLivingBase ? new CameraPlayer(minecraft, minecraft.player.getRidingEntity()) : new CameraPlayer(minecraft) : new CameraPlayer(minecraft);
		camera.update(newPitch, newYaw);
	}

	private static class CameraPlayer {
		private Minecraft minecraft;
		private Entity entity;

		public CameraPlayer(Minecraft minecraft) {
			this.minecraft = minecraft;
			this.entity = minecraft.player;
		}

		public CameraPlayer(Minecraft minecraft, Entity entity) {
			this.minecraft = minecraft;
			this.entity = entity;
		}

		public void update(float pitch, float yaw) {
			EntityPlayerSP player = mc.player;
			if (player == null) {
				return;
			}
			player.prevRotationPitch = player.rotationPitch;
			player.prevRotationYaw = player.rotationYaw;
			player.rotationPitch = pitch;
			player.rotationYaw = yaw;
			player.rotationYawHead = yaw;
			player.cameraYaw = yaw;
		}
	}
	
	public static float lerp(float t, float startValue, float endValue) {
		return startValue + (endValue - startValue) * t;
	}
	
	private double getRandomSpeedBetween(double min, double max) {
        Random r = new Random();
        double speed = min + (max - min) * r.nextDouble();
        return speed;
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