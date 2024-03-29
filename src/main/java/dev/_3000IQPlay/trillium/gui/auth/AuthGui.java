package dev._3000IQPlay.trillium.gui.auth;

import dev._3000IQPlay.trillium.Trillium;
import dev._3000IQPlay.trillium.TrilliumSpy;
import dev._3000IQPlay.trillium.gui.font.CustomFont;
import dev._3000IQPlay.trillium.gui.mainmenu.*;
import dev._3000IQPlay.trillium.gui.widgets.*;
import dev._3000IQPlay.trillium.util.Drawable;
import dev._3000IQPlay.trillium.util.RenderUtil;
import dev._3000IQPlay.trillium.util.RoundedShader;
import dev._3000IQPlay.trillium.protect.keyauth.KeyAuthApp;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.awt.*;
import java.io.*;
import java.net.*;

public class AuthGui extends GuiScreen {
	private static final CustomFont cFont = new CustomFont(new Font("Calibri", 0, 25), true, false);
	private GuiTextField keyField;
    private String key = "";
    private int statusTime;
	public GLSLShader shader;
    public long initTime;
	
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		GlStateManager.disableCull();
        this.shader.useShader(this.width * 2, this.height * 2, mouseX * 2, mouseY * 2, (float)(System.currentTimeMillis() - this.initTime) / 1000.0f);
        GL11.glBegin((int)7);
        GL11.glVertex2f((float)-1.0f, (float)-1.0f);
        GL11.glVertex2f((float)-1.0f, (float)1.0f);
        GL11.glVertex2f((float)1.0f, (float)1.0f);
        GL11.glVertex2f((float)1.0f, (float)-1.0f);
        GL11.glEnd();
        GL20.glUseProgram((int)0);
		
		int containerWidth = 200;
		int containerHeight = 125;

		RoundedShader.drawRound((width - containerWidth) / 2, height / 3.5f - 6.0f, containerWidth, containerHeight, 5, false, new Color(24, 24, 24, 255));
		cFont.drawCenteredStringWithShadow("Trillium Auth", width / 2, height / 3.5f + 2, new Color(255, 255, 255, 255).getRGB());

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
		GLSLShaderList[] shaders = GLSLShaderList.cloneList();
		this.initTime = System.currentTimeMillis();
		try {
            this.shader = new GLSLShader(GLSLShaderList.PurpleGradient.getShader());
        }
        catch (IOException e) {
            throw new IllegalStateException("Failed to load background shader", e);
        }
        keyField = new TGuiTextField(2, Minecraft.getMinecraft().fontRenderer, width / 2 - 70, height / 4 + 45, 140, 22, true, true, 5.0f);
		key = loadKey();
        if(key != null && !key.isEmpty()) keyField.setText(key);
		keyField.setMaxStringLength(31);
        buttonList.add(new TGuiButton(0, width / 2 - 54, height / 4 + 85, 107, 27, true, true, 7.0f, false, "Login"));
		//buttonList.add(new TGuiButton(1, 10, 10, 90, 20, true, true, 7.0f, true, "Discord"));
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
			/*case 1:
                String url = "https://discord.gg/A9XhxPDzex";
                try {
                    Desktop.getDesktop().browse(new URI(url));
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                break;*/
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