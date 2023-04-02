package dev._3000IQPlay.trillium.gui.mainmenu;

public enum GLSLShaderList {
    BlueAurora("/assets/minecraft/mainmenu/blueaurora.fsh"),
	CoolBlob("/assets/minecraft/mainmenu/coolblob.fsh"),
    CoolTerrain("/assets/minecraft/mainmenu/coolterrain.fsh"),
	CrazyColorMix("/assets/minecraft/mainmenu/crazycolormix.fsh"),
    PurpleGradient("/assets/minecraft/mainmenu/purplegradient.fsh");

    private static final GLSLShaderList[] shaderList = values();
    private String shader;

    GLSLShaderList(String s) {
        this.shader = s;
    }

    public static GLSLShaderList[] cloneList() {
        return shaderList.clone();
    }

    public static GLSLShaderList getShader(String name) {
        return valueOf(name);
    }

    public String getShader() {
        return shader;
    }
}