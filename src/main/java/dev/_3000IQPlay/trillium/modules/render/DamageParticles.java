package dev._3000IQPlay.trillium.modules.render;

import dev._3000IQPlay.trillium.event.events.Render3DEvent;
import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.ColorSetting;
import dev._3000IQPlay.trillium.setting.Setting;
import dev._3000IQPlay.trillium.util.MathUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

public class DamageParticles extends Module {
    ArrayList<Particle> particles = new ArrayList<>();
    public Setting<ColorSetting> particleColor = this.register(new Setting<ColorSetting>("ParticleColor", new ColorSetting(0x8800FF00)));
    public Setting<Boolean> selfp = this.register(new Setting<Boolean>("Self", false));
    public Setting<Integer> dissapearTime = this.register(new Setting<Integer>("Time", 1500, 1, 10000));
	public Setting<Integer> particleSpeed = this.register(new Setting<Integer>("Speed", 20, 1, 1000));

    public DamageParticles() {
        super("DamageParticles", "Spawns particles when someone takes damage", Module.Category.RENDER, true, false, false);
    }

    @Override
    public void onUpdate() {
        if (mc.world != null && mc.player != null) {
            for (EntityPlayer player : mc.world.playerEntities) {
                if (!this.selfp.getValue() && player == mc.player) {
                    continue;
                }
                if (player.hurtTime > 0) {
                    particles.add(new Particle(player.posX + MathUtil.random(-0.05f, 0.05f), MathUtil.random((float) (player.posY + player.height), (float) player.posY), player.posZ + MathUtil.random(-0.05f, 0.05f)));
                    particles.add(new Particle(player.posX, MathUtil.random((float) (player.posY + player.height), (float) (player.posY + 0.1f)), player.posZ));
                    particles.add(new Particle(player.posX, MathUtil.random((float) (player.posY + player.height), (float) (player.posY + 0.1f)), player.posZ));
                }
                for (int i = 0; i < particles.size(); i++) {
                    if (System.currentTimeMillis() - particles.get(i).getTime() >= this.dissapearTime.getValue()) {
                        particles.remove(i);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onRender3D(Render3DEvent event) {
        if (mc.player != null && mc.world != null) {
            for (Particle particle : particles) {
                particle.render(new Color(this.particleColor.getValue().getRed(), this.particleColor.getValue().getGreen(), this.particleColor.getValue().getBlue(), Math.round(particle.alpha)).getRGB());
            }
        }
    }

    public class Particle {
        public int alpha = 180;
        double x;
        double y;
        double z;
        double motionX;
        double motionY;
        double motionZ;
        long time;

        public Particle(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
            motionX = MathUtil.random(-(float) particleSpeed.getValue() / 1000.0f, (float) particleSpeed.getValue() / 1000.0f);
            motionY = MathUtil.random(-(float) particleSpeed.getValue() / 1000.0f, (float) particleSpeed.getValue() / 1000.0f);
            motionZ = MathUtil.random(-(float) particleSpeed.getValue() / 1000.0f, (float) particleSpeed.getValue() / 1000.0f);
            time = System.currentTimeMillis();
        }


        public long getTime() {
            return time;
        }

        public void update() {
            double yEx = 0.0;
            double sp = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ) * 1.0;
            this.x += this.motionX;
            this.y += this.motionY;
            if (this.posBlock(this.x, this.y, this.z)) {
                this.motionY = -this.motionY / 1.1;
            } else if (this.posBlock(this.x, this.y, this.z) || this.posBlock(this.x, this.y - yEx, this.z) || this.posBlock(this.x, this.y + yEx, this.z) || this.posBlock(this.x - sp, this.y, this.z - sp) || this.posBlock(this.x + sp, this.y, this.z + sp) || this.posBlock(this.x + sp, this.y, this.z - sp) || this.posBlock(this.x - sp, this.y, this.z + sp) || this.posBlock(this.x + sp, this.y, this.z) || this.posBlock(this.x - sp, this.y, this.z) || this.posBlock(this.x, this.y, this.z + sp) || this.posBlock(this.x, this.y, this.z - sp) || this.posBlock(this.x - sp, this.y - yEx, this.z - sp) || this.posBlock(this.x + sp, this.y - yEx, this.z + sp) || this.posBlock(this.x + sp, this.y - yEx, this.z - sp) || this.posBlock(this.x - sp, this.y - yEx, this.z + sp) || this.posBlock(this.x + sp, this.y - yEx, this.z) || this.posBlock(this.x - sp, this.y - yEx, this.z) || this.posBlock(this.x, this.y - yEx, this.z + sp) || this.posBlock(this.x, this.y - yEx, this.z - sp) || this.posBlock(this.x - sp, this.y + yEx, this.z - sp) || this.posBlock(this.x + sp, this.y + yEx, this.z + sp) || this.posBlock(this.x + sp, this.y + yEx, this.z - sp) || this.posBlock(this.x - sp, this.y + yEx, this.z + sp) || this.posBlock(this.x + sp, this.y + yEx, this.z) || this.posBlock(this.x - sp, this.y + yEx, this.z) || this.posBlock(this.x, this.y + yEx, this.z + sp) || this.posBlock(this.x, this.y + yEx, this.z - sp)) {
                this.motionX = -this.motionX + this.motionZ;
                this.motionZ = -this.motionZ + this.motionX;
            }
            this.z += this.motionZ;
            this.motionX /= 1.005;
            this.motionZ /= 1.005;
            this.motionY /= 1.005;
        }

        public void render(int color) {
            update();
            alpha -= 0.1;
            float scale = 0.07f;
            GlStateManager.disableDepth();
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            try {
                final double posX = x - (mc.getRenderManager()).renderPosX;
                final double posY = y - (mc.getRenderManager()).renderPosY;
                final double posZ = z - (mc.getRenderManager()).renderPosZ;
                final double distanceFromPlayer = mc.player.getDistance(x, y - 1, z);
                int quality = (int) (distanceFromPlayer * 4 + 10);
				
                if (quality > 350) quality = 350;
				
                GL11.glPushMatrix();
                GL11.glTranslated(posX, posY, posZ);
                GL11.glScalef(-scale, -scale, -scale);
                GL11.glRotated(-(mc.getRenderManager()).playerViewY, 0.0D, 1.0D, 0.0D);
                GL11.glRotated((mc.getRenderManager()).playerViewX, 1.0D, 0.0D, 0.0D);

                final Color c = new Color(color);

                DamageParticles.drawFilledCircleNoGL(0, 0, 0.7, c.hashCode(), quality);

                if (distanceFromPlayer < 4) DamageParticles.drawFilledCircleNoGL(0, 0, 1.4, new Color(c.getRed(), c.getGreen(), c.getBlue(), 50).hashCode(), quality);

                if (distanceFromPlayer < 20) DamageParticles.drawFilledCircleNoGL(0, 0, 2.3, new Color(c.getRed(), c.getGreen(), c.getBlue(), 30).hashCode(), quality);

                GL11.glScalef(0.8f, 0.8f, 0.8f);
                GL11.glPopMatrix();
            } catch (final ConcurrentModificationException ignored) {
				// ignore
            }

            GL11.glDisable(GL11.GL_LINE_SMOOTH);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_BLEND);
            GlStateManager.enableDepth();
            GL11.glColor3d(255, 255, 255);
        }

        private boolean posBlock(double x, double y, double z) {
            return (mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.AIR &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.WATER &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.LAVA &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.BED &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.CAKE &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.TALLGRASS &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.GRASS &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.FLOWER_POT &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.RED_FLOWER &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.YELLOW_FLOWER &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.SAPLING &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.VINE &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.ACACIA_FENCE &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.ACACIA_FENCE_GATE &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.BIRCH_FENCE &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.BIRCH_FENCE_GATE &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.DARK_OAK_FENCE &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.DARK_OAK_FENCE_GATE &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.JUNGLE_FENCE &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.JUNGLE_FENCE_GATE &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.NETHER_BRICK_FENCE &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.OAK_FENCE &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.OAK_FENCE_GATE &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.SPRUCE_FENCE &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.SPRUCE_FENCE_GATE &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.ENCHANTING_TABLE &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.END_PORTAL_FRAME &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.DOUBLE_PLANT &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.STANDING_SIGN &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.WALL_SIGN &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.SKULL &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.DAYLIGHT_DETECTOR &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.DAYLIGHT_DETECTOR_INVERTED &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.STONE_SLAB &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.WOODEN_SLAB &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.CARPET &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.DEADBUSH &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.VINE &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.REDSTONE_WIRE &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.REEDS &&
                    mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.SNOW_LAYER);
        }
    }
	
	public static void drawFilledCircleNoGL(final int x, final int y, final double r, final int c, final int quality) {
        final float f = ((c >> 24) & 0xff) / 255F;
        final float f1 = ((c >> 16) & 0xff) / 255F;
        final float f2 = ((c >> 8) & 0xff) / 255F;
        final float f3 = (c & 0xff) / 255F;

        GL11.glColor4f(f1, f2, f3, f);
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);

        for (int i = 0; i <= 360 / quality; i++) {
            final double x2 = Math.sin(((i * quality * Math.PI) / 180)) * r;
            final double y2 = Math.cos(((i * quality * Math.PI) / 180)) * r;
            GL11.glVertex2d(x + x2, y + y2);
        }

        GL11.glEnd();
    }
}