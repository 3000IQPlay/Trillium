package dev._3000IQPlay.trillium.gui.widgets;

import dev._3000IQPlay.trillium.gui.fonttwo.fontstuff.FontRender;
import dev._3000IQPlay.trillium.gui.clickui.ColorUtil;
import dev._3000IQPlay.trillium.modules.client.ClickGui;
import dev._3000IQPlay.trillium.util.Drawable;
import dev._3000IQPlay.trillium.notification.Animation;
import dev._3000IQPlay.trillium.notification.DecelerateAnimation;
import dev._3000IQPlay.trillium.notification.Direction;
import dev._3000IQPlay.trillium.util.Drawable;
import dev._3000IQPlay.trillium.util.RoundedShader;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;

import java.awt.Color;

public class TGuiButton extends GuiButton {
	protected static final ResourceLocation BUTTON_TEXTURES = new ResourceLocation("textures/gui/widgets.png");

	private final ResourceLocation image;

	/**
	 * Button width in pixels
	 */
	protected int width;

	/**
	 * Button height in pixels
	 */
	protected int height;

	/**
	 * The x position of this control.
	 */
	public int xPosition;

	/**
	 * The y position of this control.
	 */
	public int yPosition;

	/**
	 * The string displayed on this control.
	 */
	public String displayString;
	public int id;

	/**
	 * True if this control is enabled, false to disable.
	 */
	public boolean enabled;

	/**
	 * Hides the button completely if false.
	 */
	public boolean visible;
	protected boolean hovered;
	private boolean rect = true;

	private Color rectColor = new Color(34, 34, 34);

	private boolean waveMode = false;

	private final Animation animation = new DecelerateAnimation(240, 1f);

	private boolean background;

	public TGuiButton(int buttonId, int x, int y, String buttonText) {
		this(buttonId, x, y, 200, 20, null, buttonText);
	}

	public TGuiButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
		this(buttonId, x, y, widthIn, heightIn, null, buttonText);
	}

	public TGuiButton(int buttonId, int x, int y, ResourceLocation image) {
		this(buttonId, x, y, 200, 20, image, "");
	}
	
	public TGuiButton(int buttonId, int x, int y, int widthIn, int heightIn, ResourceLocation image) {
		this(buttonId, x, y, widthIn, heightIn, image, "");
	}

	public TGuiButton(int buttonId, int x, int y, int widthIn, int heightIn, ResourceLocation image, String buttonText) {
		super(buttonId, x, y, widthIn, heightIn, buttonText);
		this.width = 200;
		this.height = 20;
		this.enabled = true;
		this.visible = true;
		this.id = buttonId;
		this.xPosition = x;
		this.yPosition = y;
		this.width = widthIn;
		this.height = heightIn;
		this.image = image;
		this.displayString = buttonText;
		background = true;
	}

	/**
	 * Returns 0 if the button is disabled, 1 if the mouse is NOT hovering over this
	 * button and 2 if it IS hovering over this button.
	 */
	@Override
	protected int getHoverState(boolean mouseOver) {
		int i = 1;

		if (!this.enabled) {
			i = 0;
		} else if (mouseOver) {
			i = 2;
		}

		return i;
	}

	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		if (this.visible) {
			this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition
					&& mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;

			GlStateManager.pushMatrix();

			double centerX = xPosition + width / 2;
			double centerY = yPosition + height / 2;
			int j = 14737632;

			if (!this.enabled) {
				j = 10526880;
			} else if (this.hovered) {
				j = 16777120;
			}

			animation.setDirection(hovered ? Direction.FORWARDS : Direction.BACKWARDS);
			int plusAlpha = (int) (100f * animation.getOutput());
			
			GlStateManager.translate(centerX, centerY, 0);
			GlStateManager.scale(1f + (0.03 * animation.getOutput()), 1f + (0.03 * animation.getOutput()), 1);
			GlStateManager.translate(-centerX, -centerY, 0);

			Color color = new Color(rectColor.getRed(), rectColor.getGreen(), rectColor.getBlue(), 255);

			if (background) {
				if (waveMode)
					Drawable.horizontalGradient((double)xPosition, (double)yPosition, (double)width, (double)height,
							ColorUtil.applyOpacity(ClickGui.getInstance().getColor(200), 0.9f).getRGB(),
							ColorUtil.applyOpacity(ClickGui.getInstance().getColor(0), 0.9f).getRGB());
				else
					RoundedShader.drawRound(xPosition, yPosition, width, height, 1, color);
			}

			this.mouseDragged(mc, mouseX, mouseY);

			float size = 0.98f;

			if (image != null)
				Drawable.drawTexture(image, xPosition + 6, yPosition + 6, width - 12, height - 12, new Color(j));
			else
				FontRender.drawCentString6(this.displayString, this.xPosition + this.width / 2,
						this.yPosition + this.height / 2 - (FontRender.getFontHeight6() / 2), -1);

			GlStateManager.popMatrix();
		}
	}

	/**
	 * Fired when the mouse button is dragged. Equivalent of
	 * MouseListener.mouseDragged(MouseEvent e).
	 */
	@Override
	protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
	}

	/**
	 * Fired when the mouse button is released. Equivalent of
	 * MouseListener.mouseReleased(MouseEvent e).
	 */
	@Override
	public void mouseReleased(int mouseX, int mouseY) {
	}

	public void setRectColor(Color color) {
		rectColor = color;
	}

	/**
	 * Returns true if the mouse has been pressed on this control. Equivalent of
	 * MouseListener.mousePressed(MouseEvent e).
	 */
	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		return this.enabled && this.visible && mouseX >= this.xPosition && mouseY >= this.yPosition
				&& mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
	}

	public TGuiButton setBackground(boolean state) {
		this.background = state;
		return this;
	}

	public TGuiButton disableRect() {
		this.rect = false;
		return this;
	}

	public TGuiButton coolWave() {
		this.waveMode = true;
		return this;
	}

	/**
	 * Whether the mouse cursor is currently over the button.
	 */
	@Override
	public boolean isMouseOver() {
		return this.hovered;
	}

    @Override
	public void drawButtonForegroundLayer(int mouseX, int mouseY) {
	}

    @Override
	public void playPressSound(SoundHandler soundHandlerIn) {
		soundHandlerIn.playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
	}

    @Override
	public int getButtonWidth() {
		return this.width;
	}

    @Override
	public void setWidth(int width) {
		this.width = width;
	}

	public void setText(String text) {
		this.displayString = text;
	}
}
