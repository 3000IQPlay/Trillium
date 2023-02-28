package dev._3000IQPlay.trillium.manager;

import dev._3000IQPlay.trillium.macro.Macro;

import java.io.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class MacroManager  {

    private static CopyOnWriteArrayList<Macro> macros = new CopyOnWriteArrayList<>();

    public static void addMacro(Macro macro) {
        if (!macros.contains(macro)) {
            macros.add(macro);
        }
    }

    public static void removeMacro(Macro macro) {
        macros.remove(macro);
    }

    public static CopyOnWriteArrayList<Macro> getMacros() {
        return macros;
    }

    public static Macro getMacroByName(String name) {
        for (Macro macro : getMacros()) {
            if (macro.getName().equalsIgnoreCase(name)) {
                return macro;
            }
        }
        return null;
    }

    public static void onLoad(){
        macros = new CopyOnWriteArrayList<>();
        try {
            File file = new File("Trillium/macro.txt");

            if (file.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    while (reader.ready()) {
                        String[] nameandkey = reader.readLine().split(":");
                        String name = nameandkey[0];
                        String key = nameandkey[1];
                        String command = nameandkey[2];
                        MacroManager.addMacro(new Macro(name, command,Integer.parseInt(key)));
                    }

                }
            }
        } catch (Exception ignored) {}
    }

    public static void saveMacro() {
        File file = new File("Trillium/macro.txt");
            try {
                new File("Trillium").mkdirs();
                file.createNewFile();
            } catch (Exception e){

            }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Macro macro : macros) {
                writer.write(macro.getName() + ":" + macro.getBind() + ":" + macro.getText() + "\n");
            }
        } catch (Exception ignored){}
    }
}