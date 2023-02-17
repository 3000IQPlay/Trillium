package dev._3000IQPlay.trillium.gui.clickui;

import dev._3000IQPlay.trillium.Trillium;
import dev._3000IQPlay.trillium.gui.clickui.base.AbstractWindow;
import dev._3000IQPlay.trillium.gui.clickui.window.ModuleWindow;
import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.modules.client.ClickGui;
import dev._3000IQPlay.trillium.notification.Animation;
import dev._3000IQPlay.trillium.notification.DecelerateAnimation;
import dev._3000IQPlay.trillium.notification.Direction;
import dev._3000IQPlay.trillium.util.RenderUtil;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

public class ClickUI extends GuiScreen {

	private Animation openAnimation, bgAnimation, rAnimation;
	private final List<AbstractWindow> windows;

	private double scrollSpeed;
	private boolean firstOpen;
	private double dWheel;
	private double mamer;

	public ClickUI() {
		windows = Lists.newArrayList();
		firstOpen = true;
		this.setInstance();
	}

	private static ClickUI INSTANCE = new ClickUI();

	public static ClickUI getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ClickUI();
		}
		return INSTANCE;
	}

	public static ClickUI getClickGui() {
		return ClickUI.getInstance();
	}

	private void setInstance() {
		INSTANCE = this;
	}


	@Override
	public void initGui() {
		openAnimation = new EaseBackIn(270, .4f, 1.13f);
		rAnimation = new DecelerateAnimation(300, 1f);
		bgAnimation = new DecelerateAnimation(300, 1f);
		if (firstOpen) {
			double x = 20, y = 20;
			double offset = 0;
			int windowHeight = 18;
			ScaledResolution sr = new ScaledResolution(mc);
			int i = 0;
			for (final Module.Category category : Trillium.moduleManager.getCategories()) {
				if(category.getName().contains("HUD")) continue;
				ModuleWindow window = new ModuleWindow(category.getName(), Trillium.moduleManager.getModulesByCategory(category), i, x + offset, y, 108, windowHeight);
				window.setOpen(true);
				windows.add(window);
				offset += 110;

				if (offset > sr.getScaledWidth()) {
					offset = 0;
				}
				i++;
			}
			firstOpen = false;
		}

		windows.forEach(AbstractWindow::init);

		super.initGui();
	}


	@Override
	public void drawScreen(int mouseX, int mouseY, float delta) {
		if (openAnimation.isDone() && openAnimation.getDirection().equals(Direction.BACKWARDS)) {
			windows.forEach(AbstractWindow::onClose);
			mc.currentScreen = null;
			mc.displayGuiScreen(null);
		}
		
		ScaledResolution sr = new ScaledResolution(mc);
		
		if (ClickGui.getInstance().gradientBG.getValue().booleanValue()) {
            RenderUtil.draw2DGradientRect(0, 0, sr.getScaledWidth(), sr.getScaledHeight(),
			    new Color(ClickGui.getInstance().gradientLB.getValue().getRed(), ClickGui.getInstance().gradientLB.getValue().getGreen(), ClickGui.getInstance().gradientLB.getValue().getBlue(), ClickGui.getInstance().gradientLB.getValue().getAlpha() / 2).getRGB(),
			    new Color(ClickGui.getInstance().gradientLT.getValue().getRed(), ClickGui.getInstance().gradientLT.getValue().getGreen(), ClickGui.getInstance().gradientLT.getValue().getBlue(), ClickGui.getInstance().gradientLT.getValue().getAlpha() / 2).getRGB(),
			    new Color(ClickGui.getInstance().gradientRB.getValue().getRed(), ClickGui.getInstance().gradientRB.getValue().getGreen(), ClickGui.getInstance().gradientRB.getValue().getBlue(), ClickGui.getInstance().gradientRB.getValue().getAlpha() / 2).getRGB(),
			    new Color(ClickGui.getInstance().gradientRT.getValue().getRed(), ClickGui.getInstance().gradientRT.getValue().getGreen(), ClickGui.getInstance().gradientRT.getValue().getBlue(), ClickGui.getInstance().gradientRT.getValue().getAlpha() / 2).getRGB()
		    );
        }
		
		int dWheel = Mouse.getDWheel();
		
        if (dWheel > 0) {
            if (ClickGui.getInstance().scroll.getValue().booleanValue()) {
                scrollSpeed += ClickGui.getInstance().scrollval.getValue();
            }
        } else if (dWheel < 0 && ClickGui.getInstance().scroll.getValue().booleanValue()) {
            scrollSpeed -= ClickGui.getInstance().scrollval.getValue();
        }

		double anim = (openAnimation.getOutput() + .6f);


		GlStateManager.pushMatrix();

		double centerX = width >> 1;
		double centerY = height >> 1;

		GlStateManager.translate(centerX, centerY, 0);
		GlStateManager.scale(anim, anim, 1);
		GlStateManager.translate(-centerX, -centerY, 0);

		for (AbstractWindow window : windows) {
			if (Keyboard.isKeyDown(Keyboard.KEY_DOWN))
				window.setY(window.getY() + 2);
			else if (Keyboard.isKeyDown(Keyboard.KEY_UP))
				window.setY(window.getY() - 2);
			else if (Keyboard.isKeyDown(Keyboard.KEY_LEFT))
				window.setX(window.getX() - 2);
			else if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
				window.setX(window.getX() + 2);
			if (dWheel != 0)
				window.setY(window.getY() + scrollSpeed);
			else
				scrollSpeed = 0;

			window.render(mouseX, mouseY, delta, ClickGui.getInstance().hcolor1.getValue().getColorObject(), openAnimation.isDone() && openAnimation.getDirection() == Direction.FORWARDS);
		}
		GlStateManager.popMatrix();

		super.drawScreen(mouseX, mouseY, delta);
	}

	@Override
	public void onGuiClosed() {
	}

	@Override
	public void updateScreen() {
		windows.forEach(AbstractWindow::tick);
		super.updateScreen();
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
		windows.forEach(w -> {
			w.mouseClicked(mouseX, mouseY, button);

			windows.forEach(w1 -> {
				if (w.dragging && w != w1)
					w1.dragging = false;
			});
		});
		super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY, int button) {
		windows.forEach(w -> w.mouseReleased(mouseX, mouseY, button));
		super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public void handleMouseInput() throws IOException {
		windows.forEach(w -> {
			try {
				w.handleMouseInput();
			} catch (IOException ignored) {

			}
		});
		super.handleMouseInput();
	}

	@Override
	public void keyTyped(char chr, int keyCode) throws IOException {
		windows.forEach(w -> {
			w.keyTyped(chr, keyCode);
		});

		if (keyCode == 1 || keyCode == Trillium.moduleManager.getModuleByClass(ClickGui.class).getBind().getKey()) {
			bgAnimation.setDirection(Direction.BACKWARDS);
			rAnimation.setDirection(Direction.BACKWARDS);
			openAnimation.setDirection(Direction.BACKWARDS);
		}
	}

}
