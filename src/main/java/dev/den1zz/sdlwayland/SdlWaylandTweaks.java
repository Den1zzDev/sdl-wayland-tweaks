package dev.den1zz.sdlwayland;

import dev.den1zz.sdlwayland.config.SdlWaylandConfig;
import dev.den1zz.sdlwayland.sdl.Sdl3WindowManager;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SdlWaylandTweaks implements ClientModInitializer {
    public static final String MOD_ID = "sdl-wayland-tweaks";
    public static final Logger LOGGER = LoggerFactory.getLogger("SDL-Wayland-Tweaks");

    @Override
    public void onInitializeClient() {
        SdlWaylandConfig config = SdlWaylandConfig.get();
        Sdl3WindowManager.preInitHints();
        LOGGER.info("SDL Wayland Tweaks initialized (Wayland display: {})", System.getenv("WAYLAND_DISPLAY"));
    }
}
