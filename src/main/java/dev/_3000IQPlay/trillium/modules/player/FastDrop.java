package dev._3000IQPlay.trillium.modules.player;

import dev._3000IQPlay.trillium.modules.Module;

public class FastDrop
        extends Module {
    public FastDrop() {
        super("FastDrop", "Make you drop your items fast af", Module.Category.PLAYER, true, false, false);
    }
	
	@Override
	public void onUpdate() {
        if (mc.player == null || mc.world == null || mc.player.isInWater() || mc.player.isInLava() || mc.player.isOnLadder()) {
            return;
        }
        if (mc.player.onGround) {
            mc.player.motionY -= (double)((float)20 / 10.0f);
        }
    }
}