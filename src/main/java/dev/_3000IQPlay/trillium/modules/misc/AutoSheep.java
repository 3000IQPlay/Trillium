package dev._3000IQPlay.trillium.modules.misc;

import dev._3000IQPlay.trillium.event.events.EventPreMotion;
import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.Setting;
import dev._3000IQPlay.trillium.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.item.ItemShears;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Comparator;

public class AutoSheep extends Module {
    public AutoSheep() {
        super("AutoSheep", "Gives the sheep cool hair cut", Category.MISC, true, false, false);
    }

    public Setting<Integer> delay = this.register(new Setting<Integer>("Delay", 250, 0, 2000));
    public Setting<Boolean> rotate = this.register(new Setting<Boolean>("Rotate", true));
	private final Timer timer = new Timer();

    @SubscribeEvent
    public void onUpdateWalkingPlayerPre(EventPreMotion p_Event ) {
        if (!(mc.player.getHeldItemMainhand().getItem() instanceof ItemShears)) return;
		if (!timer.passed(this.delay.getValue())) return;
        EntitySheep l_Sheep = mc.world.loadedEntityList.stream()
                .filter(p_Entity -> IsValidSheep(p_Entity))
                .map(p_Entity -> (EntitySheep) p_Entity)
                .min(Comparator.comparing(p_Entity -> mc.player.getDistance(p_Entity)))
                .orElse(null);
        if (l_Sheep != null) {
            if (this.rotate.getValue()) {
                float[] angle = calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), l_Sheep.getPositionEyes(mc.getRenderPartialTicks()));
                mc.player.rotationYaw = (angle[0]);
                mc.player.rotationPitch = (angle[1]);
            }
            mc.player.connection.sendPacket(new CPacketUseEntity(l_Sheep, EnumHand.MAIN_HAND));
        }

    }

    private boolean IsValidSheep(Entity p_Entity)
    {
        if (!(p_Entity instanceof EntitySheep))
            return false;
        if (p_Entity.getDistance(mc.player) > 4)
            return false;
        EntitySheep l_Sheep = (EntitySheep)p_Entity;
        return l_Sheep.isShearable(mc.player.getHeldItemMainhand(), mc.world, p_Entity.getPosition());
    }

    public static float[] calcAngle(Vec3d from, Vec3d to) {
        double difX = to.x - from.x;
        double difY = (to.y - from.y) * -1.0;
        double difZ = to.z - from.z;
        double dist = MathHelper.sqrt(difX * difX + difZ * difZ);
        return new float[]{(float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0), (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difY, dist)))};
    }
}
