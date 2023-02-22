package dev._3000IQPlay.trillium.util;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class RenderBuilder {
    private boolean depth;
    private boolean alpha;
    private boolean shade;
    private double length;
    private Color color;
    private boolean blend;
    private boolean texture;
    private boolean setup;
    private double height;
    private boolean cull;
    private Box box;
    private double width;
    private AxisAlignedBB axisAlignedBB;

    public RenderBuilder() {
        this.axisAlignedBB = new AxisAlignedBB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
        this.box = Box.FILL;
        this.color = Color.WHITE;
    }

    public RenderBuilder position(AxisAlignedBB axisAlignedBB) {
        this.axisAlignedBB = axisAlignedBB;
        return this;
    }

    public RenderBuilder depth(boolean bl) {
        if (bl) {
            GlStateManager.disableDepth();
            GlStateManager.depthMask(false);
        }
        this.depth = bl;
        return this;
    }

    public RenderBuilder cull(boolean bl) {
        if (this.cull) {
            GlStateManager.disableCull();
        }
        this.cull = bl;
        return this;
    }

    public RenderBuilder setup() {
        GlStateManager.pushMatrix();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        this.setup = true;
        return this;
    }

    public Color getColor() {
        return this.color;
    }

    public RenderBuilder box(Box box) {
        this.box = box;
        return this;
    }

    public RenderBuilder length(double d) {
        this.length = d;
        return this;
    }

    public RenderBuilder blend() {
        GlStateManager.enableBlend();
        this.blend = true;
        return this;
    }

    public RenderBuilder texture() {
        GlStateManager.disableTexture2D();
        this.texture = true;
        return this;
    }

    public double getHeight() {
        return this.height;
    }

    public RenderBuilder line(float f) {
        GlStateManager.glLineWidth(f);
        return this;
    }

    public double getWidth() {
        return this.width;
    }

    public RenderBuilder width(double d) {
        this.width = d;
        return this;
    }

    public RenderBuilder height(double d) {
        this.height = d;
        return this;
    }

    public Box getBox() {
        return this.box;
    }

    public double getLength() {
        return this.length;
    }

    public AxisAlignedBB getAxisAlignedBB() {
        return this.axisAlignedBB;
    }

    public RenderBuilder build() {
        if (this.depth) {
            GlStateManager.depthMask(true);
            GlStateManager.enableDepth();
        }
        if (this.texture) {
            GlStateManager.enableTexture2D();
        }
        if (this.blend) {
            GlStateManager.disableBlend();
        }
        if (this.cull) {
            GlStateManager.enableCull();
        }
        if (this.alpha) {
            GlStateManager.enableAlpha();
        }
        if (this.shade) {
            GlStateManager.shadeModel(7424);
        }
        if (this.setup) {
            GL11.glDisable(2848);
            GlStateManager.popMatrix();
        }
        return this;
    }

    public RenderBuilder position(BlockPos blockPos) {
        this.position(new AxisAlignedBB(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX() + 1, blockPos.getY() + 1, blockPos.getZ() + 1));
        return this;
    }

    public RenderBuilder shade(boolean bl) {
        if (bl) {
            GlStateManager.shadeModel(7425);
        }
        this.shade = bl;
        return this;
    }

    public RenderBuilder position(Vec3d vec3d) {
        this.position(new AxisAlignedBB(vec3d.x, vec3d.y, vec3d.z, vec3d.x + 1.0, vec3d.y + 1.0, vec3d.z + 1.0));
        return this;
    }

    public RenderBuilder alpha(boolean bl) {
        if (this.alpha) {
            GlStateManager.disableAlpha();
        }
        this.alpha = bl;
        return this;
    }

    public RenderBuilder color(Color color) {
        this.color = color;
        return this;
    }

    public enum Box {
        FILL,
        OUTLINE,
        BOTH,
        GLOW,
        REVERSE,
        CLAW,
        NONE

    }
}