package dev.den1zz.sdlwayland.sdl;

import dev.den1zz.sdlwayland.config.SdlWaylandConfig;
import net.minecraft.client.Minecraft;
import org.lwjgl.sdl.SDLClipboard;
import org.lwjgl.sdl.SDLHints;
import org.lwjgl.sdl.SDLInit;
import org.lwjgl.sdl.SDLMouse;
import org.lwjgl.sdl.SDLVideo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Sdl3WindowManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("SDL-Wayland-Tweaks");

    private static volatile long windowHandle = 0;
    private static volatile boolean waylandDetected = false;
    private static volatile Boolean lastAppliedTearing = null;

    public static void preInitHints() {
        SdlWaylandConfig cfg = SdlWaylandConfig.get();
        try {
            if (cfg.appMetadata) {
                SDLHints.SDL_SetHintWithPriority(SDLHints.SDL_HINT_APP_ID, "minecraft", SDLHints.SDL_HINT_OVERRIDE);
                try {
                    SDLInit.SDL_SetAppMetadata("Minecraft", "26.3", "minecraft");
                    SDLInit.SDL_SetAppMetadataProperty(SDLInit.SDL_PROP_APP_METADATA_IDENTIFIER_STRING, "minecraft");
                    SDLInit.SDL_SetAppMetadataProperty(SDLInit.SDL_PROP_APP_METADATA_NAME_STRING, "Minecraft 26.3");
                    SDLInit.SDL_SetAppMetadataProperty(SDLInit.SDL_PROP_APP_METADATA_TYPE_STRING, "game");
                } catch (Throwable ignored) {
                }
            }

            String waylandDisplay = System.getenv("WAYLAND_DISPLAY");
            String xdgSessionType = System.getenv("XDG_SESSION_TYPE");
            boolean isWaylandEnv = (waylandDisplay != null && !waylandDisplay.isEmpty())
                    || "wayland".equalsIgnoreCase(xdgSessionType);

            if (isWaylandEnv) {
                SDLHints.SDL_SetHintWithPriority(SDLHints.SDL_HINT_VIDEO_DRIVER, "wayland,x11", SDLHints.SDL_HINT_NORMAL);
            }

            if (cfg.waylandHints) {
                SDLHints.SDL_SetHintWithPriority(SDLHints.SDL_HINT_VIDEO_WAYLAND_SCALE_TO_DISPLAY, "1", SDLHints.SDL_HINT_OVERRIDE);
                SDLHints.SDL_SetHintWithPriority(SDLHints.SDL_HINT_VIDEO_WAYLAND_MODE_EMULATION, "0", SDLHints.SDL_HINT_OVERRIDE);
                SDLHints.SDL_SetHintWithPriority(SDLHints.SDL_HINT_VIDEO_WAYLAND_MODE_SCALING, "0", SDLHints.SDL_HINT_OVERRIDE);
                SDLHints.SDL_SetHintWithPriority(SDLHints.SDL_HINT_VIDEO_WAYLAND_PREFER_LIBDECOR, "0", SDLHints.SDL_HINT_OVERRIDE);
                SDLHints.SDL_SetHintWithPriority(SDLHints.SDL_HINT_VIDEO_WAYLAND_ALLOW_LIBDECOR, "1", SDLHints.SDL_HINT_OVERRIDE);
                SDLHints.SDL_SetHintWithPriority(SDLHints.SDL_HINT_VIDEO_DOUBLE_BUFFER, "1", SDLHints.SDL_HINT_OVERRIDE);
            }

            if (cfg.relativeMouseOptimizations) {
                SDLHints.SDL_SetHintWithPriority(SDLHints.SDL_HINT_MOUSE_EMULATE_WARP_WITH_RELATIVE, "1", SDLHints.SDL_HINT_OVERRIDE);
                SDLHints.SDL_SetHintWithPriority(SDLHints.SDL_HINT_MOUSE_RELATIVE_MODE_CENTER, "1", SDLHints.SDL_HINT_OVERRIDE);
                SDLHints.SDL_SetHintWithPriority(SDLHints.SDL_HINT_MOUSE_DPI_SCALE_CURSORS, "1", SDLHints.SDL_HINT_OVERRIDE);
                SDLHints.SDL_SetHintWithPriority(SDLHints.SDL_HINT_ALLOW_ALT_TAB_WHILE_GRABBED, "1", SDLHints.SDL_HINT_OVERRIDE);
                SDLHints.SDL_SetHintWithPriority(SDLHints.SDL_HINT_MOUSE_AUTO_CAPTURE, "0", SDLHints.SDL_HINT_OVERRIDE);
            }

            if (cfg.bypassOsScale) {
                SDLHints.SDL_SetHintWithPriority(SDLHints.SDL_HINT_MOUSE_RELATIVE_SYSTEM_SCALE, "0", SDLHints.SDL_HINT_OVERRIDE);
            }

            if (cfg.focusClickthrough) {
                SDLHints.SDL_SetHintWithPriority(SDLHints.SDL_HINT_MOUSE_FOCUS_CLICKTHROUGH, "1", SDLHints.SDL_HINT_OVERRIDE);
            }

            if (cfg.preventMinimizeOnFocusLoss) {
                SDLHints.SDL_SetHintWithPriority(SDLHints.SDL_HINT_VIDEO_MINIMIZE_ON_FOCUS_LOSS, "0", SDLHints.SDL_HINT_OVERRIDE);
            }

            if (cfg.bypassCompositor) {
                SDLHints.SDL_SetHintWithPriority(SDLHints.SDL_HINT_VIDEO_X11_NET_WM_BYPASS_COMPOSITOR, "1", SDLHints.SDL_HINT_OVERRIDE);
            }
        } catch (Throwable t) {
            LOGGER.warn("Failed to set early SDL3 Wayland hints: {}", t.getMessage());
        }
    }

    public static void updateRuntimeHints() {
        SdlWaylandConfig cfg = SdlWaylandConfig.get();
        try {
            SDLHints.SDL_SetHintWithPriority(SDLHints.SDL_HINT_MOUSE_RELATIVE_SYSTEM_SCALE,
                cfg.bypassOsScale ? "0" : "1", SDLHints.SDL_HINT_OVERRIDE);

            SDLHints.SDL_SetHintWithPriority(SDLHints.SDL_HINT_MOUSE_FOCUS_CLICKTHROUGH,
                cfg.focusClickthrough ? "1" : "0", SDLHints.SDL_HINT_OVERRIDE);

            SDLHints.SDL_SetHintWithPriority(SDLHints.SDL_HINT_VIDEO_MINIMIZE_ON_FOCUS_LOSS,
                cfg.preventMinimizeOnFocusLoss ? "0" : "1", SDLHints.SDL_HINT_OVERRIDE);

            SDLHints.SDL_SetHintWithPriority(SDLHints.SDL_HINT_VIDEO_X11_NET_WM_BYPASS_COMPOSITOR,
                cfg.bypassCompositor ? "1" : "0", SDLHints.SDL_HINT_OVERRIDE);

            if (cfg.relativeMouseOptimizations) {
                SDLHints.SDL_SetHintWithPriority(SDLHints.SDL_HINT_MOUSE_EMULATE_WARP_WITH_RELATIVE, "1", SDLHints.SDL_HINT_OVERRIDE);
                SDLHints.SDL_SetHintWithPriority(SDLHints.SDL_HINT_MOUSE_RELATIVE_MODE_CENTER, "1", SDLHints.SDL_HINT_OVERRIDE);
                SDLHints.SDL_SetHintWithPriority(SDLHints.SDL_HINT_MOUSE_DPI_SCALE_CURSORS, "1", SDLHints.SDL_HINT_OVERRIDE);
                SDLHints.SDL_SetHintWithPriority(SDLHints.SDL_HINT_ALLOW_ALT_TAB_WHILE_GRABBED, "1", SDLHints.SDL_HINT_OVERRIDE);
                SDLHints.SDL_SetHintWithPriority(SDLHints.SDL_HINT_MOUSE_AUTO_CAPTURE, "0", SDLHints.SDL_HINT_OVERRIDE);
            }

            applySwapInterval(cfg.asyncPageFlip);
            LOGGER.info("[SDL3] Applied dynamic runtime hints");
        } catch (Throwable t) {
            LOGGER.warn("Failed to apply dynamic runtime hints: {}", t.getMessage());
        }
    }

    public static void onWindowCreated(long handle) {
        windowHandle = handle;
        if (handle == 0) return;

        try {
            String driver = SDLVideo.SDL_GetCurrentVideoDriver();
            waylandDetected = "wayland".equalsIgnoreCase(driver);

            if (waylandDetected) {
                SDLVideo.SDL_SyncWindow(handle);
                applySwapInterval(SdlWaylandConfig.get().asyncPageFlip);
                LOGGER.info("[Wayland] Attached native window (handle=0x{}, driver={})",
                    Long.toHexString(handle), driver);
            } else {
                LOGGER.info("[SDL3] Video driver active: {}", driver);
            }
        } catch (Throwable t) {
            LOGGER.warn("Failed to inspect SDL3 window properties: {}", t.getMessage());
        }
    }

    public static boolean isWaylandDriver() {
        if (waylandDetected) return true;
        try {
            String driver = SDLVideo.SDL_GetCurrentVideoDriver();
            waylandDetected = "wayland".equalsIgnoreCase(driver);
            return waylandDetected;
        } catch (Throwable t) {
            return false;
        }
    }

    public static void applySwapInterval(boolean tearing) {
        if (lastAppliedTearing != null && lastAppliedTearing == tearing) {
            return;
        }
        lastAppliedTearing = tearing;

        try {
            Minecraft mc = Minecraft.getInstance();
            if (mc != null) {
                mc.invalidateSurfaceConfiguration();
                LOGGER.info("[SDL3] Requested surface present mode update (async tearing={})", tearing);
            }
        } catch (Throwable t) {
            LOGGER.warn("Failed to update surface configuration: {}", t.getMessage());
        }
    }

    public static void reassertGrabs() {
        Minecraft mc = Minecraft.getInstance();
        if (mc != null && mc.getWindow() != null) {
            long handle = mc.getWindow().handle();
            if (handle != 0 && mc.mouseHandler != null && mc.mouseHandler.isMouseGrabbed()) {
                try {
                    SDLVideo.SDL_SetWindowMouseGrab(handle, true);
                    SDLVideo.SDL_SetWindowKeyboardGrab(handle, true);
                    SDLMouse.SDL_SetWindowRelativeMouseMode(handle, true);
                } catch (Throwable ignored) {
                }
            }
        }
    }

    public static void setPrimarySelectionText(String text) {
        if (text == null) return;
        try {
            SDLClipboard.SDL_SetPrimarySelectionText(text);
        } catch (Throwable ignored) {
        }
    }

    public static String getPrimarySelectionText() {
        try {
            if (SDLClipboard.SDL_HasPrimarySelectionText()) {
                String text = SDLClipboard.SDL_GetPrimarySelectionText();
                return text != null ? text : "";
            }
        } catch (Throwable ignored) {
        }
        return "";
    }
}
