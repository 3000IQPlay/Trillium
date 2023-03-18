package dev._3000IQPlay.trillium.gui.auth;

import dev._3000IQPlay.trillium.Trillium;
import dev._3000IQPlay.trillium.TrilliumSpy;
import dev._3000IQPlay.trillium.gui.font.CustomFont;
import dev._3000IQPlay.trillium.util.Drawable;
import dev._3000IQPlay.trillium.util.RenderUtil;
import dev._3000IQPlay.trillium.util.RoundedShader;
import dev._3000IQPlay.trillium.protect.keyauth.KeyAuthApp;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.*;

public class AuthGui extends GuiScreen {
	private static final CustomFont cFont = new CustomFont(new Font("Calibri", 0, 25), true, false);
    private GuiTextField keyField;
    private String key = "";
    private int statusTime;
	
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		Drawable.horizontalGradient(0, 0, width, height, new Color(33, 0, 127, 255).getRGB(), new Color(212, 0, 255, 255).getRGB());
		int containerWidth = 200;
		int containerHeight = 125;

		RoundedShader.drawRound((width - containerWidth) / 2, height / 3.5f - 6.0f, containerWidth, containerHeight, 2, false, new Color(24, 24, 24, 255));
        cFont.drawCenteredStringWithShadow("Trillium Auth", width / 2, height / 3.5f + 6, new Color(255, 255, 255, 255).getRGB());

        keyField.drawTextBox();

        cFont.drawCenteredStringWithShadow("DM _3000IQPlay#8278 for any type of Help", width / 2, 10, new Color(255, 255, 255, 255).getRGB());

        super.drawScreen(mouseX, mouseY, partialTicks);
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
		keyField.setMaxStringLength(31);
        buttonList.add(new GuiButton(0, width / 2 - 50, height / 4 + 100, 100, 21, "Login"));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0:
                if (KeyAuthApp.keyAuth.license(keyField.getText())) {
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