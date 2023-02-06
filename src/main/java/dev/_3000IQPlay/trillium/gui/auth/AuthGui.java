package dev._3000IQPlay.trillium.gui.auth;

import dev._3000IQPlay.trillium.Trillium;
import dev._3000IQPlay.trillium.util.RenderUtil;
import dev._3000IQPlay.trillium.util.protect.keyauth.KeyAuthApp;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.*;

public class AuthGui extends GuiScreen {
    private GuiTextField keyField;
    private String key = "";
    private int statusTime;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.draw2DGradientRect(0, 0, width, height,
			new Color(0, 255, 255, 255).getRGB(),
	        new Color(135, 0, 255, 255).getRGB(),
			new Color(0, 255, 125, 255).getRGB(),
	        new Color(0, 135, 255, 255).getRGB()
		);
        drawCenteredStringWithShadow("Once you will enter the key and log in we will receive your IP, HWID and Last Login Date", width / 2, height / 4 + 6, new Color(255, 255, 255, 255).getRGB());

        keyField.drawTextBox();

        drawCenteredStringWithShadow("If you have access but haven't got a key, you can dm _3000IQPlay#8278 for help", width / 2, 10, new Color(255, 255, 255, 255).getRGB());
        drawCenteredStringWithShadow("(C) All rights reserved || TKDevs", width / 2, height - 14, new Color(255, 255, 255, 255).getRGB());

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawCenteredStringWithShadow(String text, double x, double y, int color) {
        fontRenderer.drawStringWithShadow(text,  (int) x - fontRenderer.getStringWidth(text) / 2, (int) y, color);
    }

    @Override
    public void updateScreen() {
        if (statusTime > 0) statusTime--;
        super.updateScreen();
    }

    @Override
    protected void keyTyped(char chr, int keyCode) {
        keyField.textboxKeyTyped(chr, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        try {super.mouseClicked(mouseX, mouseY, button);} catch (IOException e) {e.printStackTrace();}
        keyField.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void initGui() {
        super.initGui();
        KeyAuthApp.keyAuth.init();
        Keyboard.enableRepeatEvents(true);
        keyField = new GuiTextField(2, Minecraft.getMinecraft().fontRenderer, width / 2 - 70, height / 4 + 50, 140, 22);
		key = loadKey();
        if(key != null && !key.isEmpty()) keyField.setText(key);
        buttonList.add(new GuiButton(0, width - 25, 5, 20, 20, "X"));
        buttonList.add(new GuiButton(1, width / 2 - 50, height / 4 + 100, 100, 21, "Login"));

    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0:
                this.mc.shutdown();
                break;
            case 1:
                if(KeyAuthApp.keyAuth.license(keyField.getText())) {
                    Trillium.isOpenAuthGui = false;
                    mc.displayGuiScreen(new GuiMainMenu());
					saveKey(keyField.getText());
                }
                statusTime = 50;
                break;
        }
    }
	
	private void saveKey(String key) {
        try {
            FileWriter writer = new FileWriter("Trillium/key.txt");
            writer.write(key);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	private String loadKey() {
        try {
            FileReader reader = new FileReader("Trillium/key.txt");
            BufferedReader br = new BufferedReader(reader);
            key = br.readLine();
            br.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return key;
    }
}
