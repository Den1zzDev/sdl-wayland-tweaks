package dev.den1zz.sdlwayland.sdl;

import dev.den1zz.sdlwayland.config.SdlWaylandConfig;
import net.minecraft.client.Minecraft;
import org.lwjgl.sdl.SDLClipboard;
import org.lwjgl.sdl.SDLHints;
import org.lwjgl.sdl.SDLInit;
import org.lwjgl.sdl.SDLProperties;
import org.lwjgl.sdl.SDLVideo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Sdl3WindowManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("SDL-Wayland-Tweaks");

    private static volatile long windowHandle = 0;
    private static volatile boolean waylandDetected = false;
    private static volatile long wlDisplay = 0;
    private static volatile long wlSurface = 0;
    private static volatile long xdgToplevel = 0;
    private static volatile float displayScale = 1.0f;
    private static volatile float pixelDensity = 1.0f;

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
            if (waylandDisplay != null && !waylandDisplay.isEmpty()) {
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

            if (cfg.asyncPageFlip) {
                SDLHints.SDL_SetHintWithPriority("SDL_VIDEO_WAYLAND_ALLOW_TEARING", "1", SDLHints.SDL_HINT_OVERRIDE);
            }

            if (cfg.bypassCompositor) {
                SDLHints.SDL_SetHintWithPriority(SDLHints.SDL_HINT_VIDEO_X11_NET_WM_BYPASS_COMPOSITOR, "1", SDLHints.SDL_HINT_OVERRIDE);
            }
        } catch (Throwable t) {
            LOGGER.warn("Failed to set early SDL3 Wayland hints: {}", t.getMessage());
        }
    }

    public static void onWindowCreated(long handle) {
        windowHandle = handle;
        if (handle == 0) return;

        try {
            String driver = SDLVideo.SDL_GetCurrentVideoDriver();
            waylandDetected = "wayland".equalsIgnoreCase(driver);

            if (waylandDetected) {
                int props = SDLVideo.SDL_GetWindowProperties(handle);
                wlDisplay = SDLProperties.SDL_GetPointerProperty(props, SDLVideo.SDL_PROP_WINDOW_WAYLAND_DISPLAY_POINTER, 0L);
                wlSurface = SDLProperties.SDL_GetPointerProperty(props, SDLVideo.SDL_PROP_WINDOW_WAYLAND_SURFACE_POINTER, 0L);
                xdgToplevel = SDLProperties.SDL_GetPointerProperty(props, SDLVideo.SDL_PROP_WINDOW_WAYLAND_XDG_TOPLEVEL_POINTER, 0L);

                displayScale = SDLVideo.SDL_GetWindowDisplayScale(handle);
                pixelDensity = SDLVideo.SDL_GetWindowPixelDensity(handle);

                SDLVideo.SDL_SyncWindow(handle);
                applySwapInterval(SdlWaylandConfig.get().asyncPageFlip);

                LOGGER.info("[Wayland] Attached native window (handle=0x{}, wl_surface=0x{}, xdg_toplevel=0x{}, scale={}, density={})",
                    Long.toHexString(handle), Long.toHexString(wlSurface), Long.toHexString(xdgToplevel), displayScale, pixelDensity);
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
        try {
            SDLHints.SDL_SetHintWithPriority(
                "SDL_VIDEO_WAYLAND_ALLOW_TEARING",
                tearing ? "1" : "0",
                SDLHints.SDL_HINT_OVERRIDE
            );
            int interval = tearing ? 0 : 1;
            SDLVideo.SDL_GL_SetSwapInterval(interval);
            SDLHints.SDL_SetHintWithPriority(
                SDLHints.SDL_HINT_RENDER_VSYNC,
                tearing ? "0" : "1",
                SDLHints.SDL_HINT_OVERRIDE
            );
            LOGGER.info("[SDL3] Applied swap interval: {} (async tearing={})", interval, tearing);
        } catch (Throwable t) {
            LOGGER.warn("Failed to set SDL3 swap interval: {}", t.getMessage());
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
                } catch (Throwable ignored) {
                }
            }
        }
    }

    public static void setPrimarySelectionText(String text) {
        if (text == null || !isWaylandDriver()) return;
        try {
            SDLClipboard.SDL_SetPrimarySelectionText(text);
        } catch (Throwable ignored) {
        }
    }

    public static String getPrimarySelectionText() {
        if (!isWaylandDriver()) return "";
        try {
            if (SDLClipboard.SDL_HasPrimarySelectionText()) {
                String text = SDLClipboard.SDL_GetPrimarySelectionText();
                return text != null ? text : "";
            }
        } catch (Throwable ignored) {
        }
        return "";
    }

    public static long getWlSurface() {
        return wlSurface;
    }

    public static float getDisplayScale() {
        return displayScale;
    }

    public static float getPixelDensity() {
        return pixelDensity;
    }
}
