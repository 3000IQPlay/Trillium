package dev._3000IQPlay.trillium.gui.hud;

import dev._3000IQPlay.trillium.gui.clickui.ColorUtil;
import dev._3000IQPlay.trillium.gui.fonttwo.fontstuff.FontRender;
import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.Setting;
import dev._3000IQPlay.trillium.setting.ColorSetting;
import dev._3000IQPlay.trillium.util.MathUtil;
import dev._3000IQPlay.trillium.util.RenderUtil;
import dev._3000IQPlay.trillium.util.Drawable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.util.Objects;

public class WaterMark
        extends Module {
	private static WaterMark INSTANCE = new WaterMark();
	public Setting<Double> barPosY = this.register(new Setting<Double>("BarPosY", 0.0, -2.0, 15.0));
	public Setting<Double> textPosY = this.register(new Setting<Double>("TextPosY", 0.0, -6.0, 3.0));
	public Setting<Integer> colorSpeed = this.register(new Setting<Integer>("ColorSpeed", 18, 2, 25));
	public Setting<ColorSetting> bgC = register(new Setting<ColorSetting>("BackgroundColor", new ColorSetting(-15461356)));
	public Setting<ColorSetting> bC = register(new Setting<ColorSetting>("BorderColor", new ColorSetting(-519435766)));
	public Setting<ColorSetting> color1 = this.register(new Setting<ColorSetting>("Color1", new ColorSetting(-16711681)));
    public Setting<ColorSetting> color2 = this.register(new Setting<ColorSetting>("Color2", new ColorSetting(-65536)));
	
    public WaterMark() {
        super("CSGOWaterMark", "Hot csgo watermark", Module.Category.HUD, true, false, false);
		this.setInstance();
    }
	
	public static WaterMark getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new WaterMark();
        }
        return INSTANCE;
    }
	
	private void setInstance() {
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (WaterMark.nullCheck()) {
            return;
        }
        if (event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) {
			String ping = this.getPing((EntityPlayer)WaterMark.mc.player) + "ms";
            String fpsText = Minecraft.debugFPS + "fps ";
			String name = mc.player.getDisplayNameString();
			String server = Minecraft.getMinecraft().isSingleplayer() ? "singleplayer".toLowerCase() : WaterMark.mc.getCurrentServerData().serverIP.toLowerCase();
			String Trillium = "Trillium b1.5.9";
			String text = Trillium + " | " + server + " | " + ping + " | " + fpsText;
            float width = Minecraft.getMinecraft().fontRenderer.getStringWidth(text) + 6;
            int height = 20;
            int posX = 2;
            int posY = 2;
			double barPosY = this.barPosY.getValue();
			double textPosY = this.textPosY.getValue();
            RenderUtil.drawRectangleCorrectly(posX - 4, posY - 4, (int)(width + 10.0f), height + 6, ColorUtil.toRGBA(this.bC.getValue().getRed(), this.bC.getValue().getGreen(), this.bC.getValue().getBlue(), this.bC.getValue().getAlpha()));
            RenderUtil.drawRectangleCorrectly(posX - 4, posY - 4, (int)(width + 11.0f), height + 7, ColorUtil.toRGBA(this.bC.getValue().getRed(), this.bC.getValue().getGreen(), this.bC.getValue().getBlue(), this.bC.getValue().getAlpha()));
            WaterMark.drawRect(posX, posY, (float)posX + width + 2.0f, posY + height, new Color(this.bgC.getValue().getRed(), this.bgC.getValue().getGreen(), this.bgC.getValue().getBlue(), this.bgC.getValue().getAlpha()).getRGB());
            WaterMark.drawRect((double)posX + 2.5, (double)posY + 2.5, (double)((float)posX + width) - 0.5, (double)posY + 4.5, new Color(this.bgC.getValue().getRed(), this.bgC.getValue().getGreen(), this.bgC.getValue().getBlue(), this.bgC.getValue().getAlpha()).getRGB());
            Drawable.horizontalGradient(4.0, (posY + barPosY) + 3, width, (posY + barPosY) + 4, ColorUtil.applyOpacity(WaterMark.getInstance().getColor(200), 0.7f).getRGB(), ColorUtil.applyOpacity(WaterMark.getInstance().getColor(0), 0.7f).getRGB());
			Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(text, (float)(4 + posX), (float)(8 + (posY + textPosY)), -1);
        }
    }
	
	public Color getColor(int count) {
        int index = (int) (count);
        int val = 1;
        Color analogous = ColorUtil.getAnalogousColor(this.color2.getValue().getColorObject())[val];
        return ColorUtil.interpolateColorsBackAndForth((int)30 - this.colorSpeed.getValue(), index, this.color1.getValue().getColorObject(), analogous, true);
    }

    public static void drawRect(double left, double top, double right, double bottom, int color) {
        if (left < right) {
            double i = left;
            left = right;
            right = i;
        }
        if (top < bottom) {
            double j = top;
            top = bottom;
            bottom = j;
        }
        float f3 = (float)(color >> 24 & 0xFF) / 255.0f;
        float f = (float)(color >> 16 & 0xFF) / 255.0f;
        float f1 = (float)(color >> 8 & 0xFF) / 255.0f;
        float f2 = (float)(color & 0xFF) / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)1, (int)0);
        GlStateManager.color((float)f, (float)f1, (float)f2, (float)f3);
        bufferBuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferBuilder.pos(left, bottom, 0.0).endVertex();
        bufferBuilder.pos(right, bottom, 0.0).endVertex();
        bufferBuilder.pos(right, top, 0.0).endVertex();
        bufferBuilder.pos(left, top, 0.0).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
	
    private int getPing(EntityPlayer player) {
        int ping = 0;
        try {
            ping = (int)MathUtil.clamp((float)Objects.requireNonNull(mc.getConnection()).getPlayerInfo(player.getUniqueID()).getResponseTime(), 1.0f, 300.0f);
        }
        catch (NullPointerException nullPointerException) {
            // empty catch block
        }
        return ping;
    }
}