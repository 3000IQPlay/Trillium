package dev._3000IQPlay.trillium.protect;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class RickTwoll { // $5000 Trillium Protector
    public static void openRickTwoll() {
        String videoUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ";
        try {
            URI uri = new URI(videoUrl);
            Desktop desktop = Desktop.getDesktop();
            desktop.browse(uri);
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }
}