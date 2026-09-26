package dev.den1zz.sdlwayland.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class SdlWaylandConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger("SDL-Wayland-Tweaks");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("sdl-wayland-tweaks.json");
    private static SdlWaylandConfig INSTANCE;

    // Wayland core & SDL3 hints
    public boolean waylandHints = true;
    public boolean appMetadata = true;
    public boolean fixBorderlessPadding = true;
    public boolean focusGrabRecovery = true;
    public boolean imeControlCharFiltering = true;
    public boolean primarySelectionSync = true;
    public boolean asyncPageFlip = false;

    // Relative mouse & desktop tuning
    public boolean relativeMouseOptimizations = true;
    public boolean bypassOsScale = true;
    public boolean bypassCompositor = true;
    public boolean preventMinimizeOnFocusLoss = true;
    public boolean focusClickthrough = true;
    public boolean motionDeltaAccumulation = true;

    public static SdlWaylandConfig get() {
        if (INSTANCE == null) {
            INSTANCE = load();
        }
        return INSTANCE;
    }

    public static SdlWaylandConfig load() {
        if (Files.exists(CONFIG_PATH)) {
            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                SdlWaylandConfig config = GSON.fromJson(reader, SdlWaylandConfig.class);
                if (config != null) {
                    return config;
                }
            } catch (Throwable t) {
                LOGGER.warn("Failed to read configuration file, using defaults: {}", t.getMessage());
            }
        }
        SdlWaylandConfig config = new SdlWaylandConfig();
        config.save();
        return config;
    }

    public void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(this, writer);
            }
        } catch (Throwable t) {
            LOGGER.warn("Failed to write configuration file: {}", t.getMessage());
        }
    }
}
