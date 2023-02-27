package dev._3000IQPlay.trillium.manager;

import dev._3000IQPlay.trillium.Trillium;
import dev._3000IQPlay.trillium.modules.Feature;
import dev._3000IQPlay.trillium.modules.Module;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileManager
        extends Feature {
    private final Path base = this.getMkDirectory(this.getRoot(), "Trillium");
    private final Path config = this.getMkDirectory(this.base, "config");
    private final Path avatars = this.getMkDirectory(this.base, "FriendsAvatars");
    private final Path skins = this.getMkDirectory(this.base, "skins");
    private final Path niggs = this.getMkDirectory(this.base, "customimg");


    public FileManager() {
        this.getMkDirectory(this.base, "pvp");
        for (Module.Category category : Trillium.moduleManager.getCategories()) {
            this.getMkDirectory(this.config, category.getName());
        }
    }

    private Path lookupPath(Path root, String... paths) {
        return Paths.get(root.toString(), paths);
    }
    private Path getRoot() {
        return Paths.get("");
    }

    private void createDirectory(Path dir) {
        try {
            if (!Files.isDirectory(dir)) {
                if (Files.exists(dir)) {
                    Files.delete(dir);
                }
                Files.createDirectories(dir);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Path getMkDirectory(Path parent, String... paths) {
        if (paths.length < 1) {
            return parent;
        }
        Path dir = this.lookupPath(parent, paths);
        this.createDirectory(dir);
        return dir;
    }

    public Path getBasePath() {
        return this.base;
    }

    public Path getConfig() {
        return this.getBasePath().resolve("config");
    }


}

