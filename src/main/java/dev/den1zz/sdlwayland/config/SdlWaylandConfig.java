package dev.den1zz.sdlwayland.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SdlWaylandConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger("SDL-Wayland-Tweaks");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "sdl-wayland-tweaks.json");

    private static SdlWaylandConfig INSTANCE;

    public boolean waylandHints = true;
    public boolean appMetadata = true;
    public boolean fixBorderlessPadding = true;
    public boolean focusGrabRecovery = true;
    public boolean imeControlCharFiltering = true;
    public boolean primarySelectionSync = true;
    public boolean middleClickPaste = false;
    public boolean asyncPageFlip = false;

    // Relative mouse & desktop tuning
    public boolean bypassOsScale = false;
    public boolean bypassCompositor = false;
    public boolean preventMinimizeOnFocusLoss = true;
    public boolean focusClickthrough = true;
    public boolean relativeMouseOptimizations = true;
    public boolean motionDeltaAccumulation = true;

    public static SdlWaylandConfig get() {
        if (INSTANCE == null) {
            INSTANCE = load();
        }
        return INSTANCE;
    }

    public static SdlWaylandConfig load() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                SdlWaylandConfig config = GSON.fromJson(reader, SdlWaylandConfig.class);
                if (config != null) {
                    return config;
                }
            } catch (IOException e) {
                LOGGER.warn("Failed to load config file: {}", e.getMessage());
            }
        }
        SdlWaylandConfig config = new SdlWaylandConfig();
        config.save();
        return config;
    }

    public void save() {
        try {
            File parent = CONFIG_FILE.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
                GSON.toJson(this, writer);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to save config file: {}", e.getMessage());
        }
    }
}
