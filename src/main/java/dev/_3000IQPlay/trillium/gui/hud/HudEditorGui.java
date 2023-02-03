package dev._3000IQPlay.trillium.gui.hud;

import com.google.common.collect.Lists;
import dev._3000IQPlay.trillium.Trillium;
import dev._3000IQPlay.trillium.gui.clickui.ClickUI;
import dev._3000IQPlay.trillium.gui.clickui.EaseBackIn;
import dev._3000IQPlay.trillium.gui.clickui.base.AbstractWindow;
import dev._3000IQPlay.trillium.gui.clickui.window.ModuleWindow;
import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.modules.client.ClickGui;
import dev._3000IQPlay.trillium.notification.Animation;
import dev._3000IQPlay.trillium.notification.DecelerateAnimation;
import dev._3000IQPlay.trillium.notification.Direction;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.List;

public class HudEditorGui extends GuiScreen {

    private Animation openAnimation, bgAnimation, rAnimation;
    private final List<AbstractWindow> windows;

    private double scrollSpeed;
    private boolean firstOpen;
    private double dWheel;
    private double mamer;

    public HudEditorGui() {
        windows = Lists.newArrayList();
        firstOpen = true;
        this.setInstance();
    }

    private static HudEditorGui INSTANCE = new HudEditorGui();

    public static HudEditorGui getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HudEditorGui();
        }
        return INSTANCE;
    }

    public static HudEditorGui getHudGui() {
        return HudEditorGui.getInstance();
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
                if(!category.getName().contains("HUD")) continue;
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

        dWheel = Mouse.getDWheel();


        if (dWheel > 0)
            scrollSpeed += 14;
        else if (dWheel < 0)
            scrollSpeed -= 14;

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