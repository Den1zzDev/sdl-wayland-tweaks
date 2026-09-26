# SDL Wayland Tweaks

A lightweight, standalone Minecraft Fabric mod for Linux desktops that brings first-class native Wayland support and low-latency SDL3 optimizations.

## Features

- **Early SDL3 Wayland Hint Bootstrap**: Enforces native Wayland display scaling (`SDL_HINT_VIDEO_WAYLAND_SCALE_TO_DISPLAY = "1"`), double-buffering, and server-side decoration preference before window initialization.
- **Application Metadata & Grouping**: Registers Wayland `app_id` as `minecraft` with proper desktop entry metadata, ensuring seamless taskbar grouping and window identification on COSMIC, GNOME, and KDE.
- **Borderless Fullscreen 1px Slit Gap Fix**: Eliminates the legacy Windows-specific 1-pixel frame padding on Wayland compositors.
- **Focus Grab & Confinement Recovery**: Automatically re-asserts pointer confinement and keyboard grabs upon window focus regain.
- **Primary Selection Clipboard Synchronization**: Seamlessly synchronizes text between Minecraft and Linux primary selection buffers (`SDLClipboard.SDL_SetPrimarySelectionText` / `SDL_GetPrimarySelectionText`).
- **Wayland IME Input Sanitization**: Prevents stray raw control codes (`< 32`) from corrupting chat and text inputs.
- **Async Page Flip / Tearing Protocol**: Optional toggle for `wp_tearing_control_v1` async page flips via `SDL_GL_SetSwapInterval(0)` on supported Wayland compositors.
- **Smooth Relative Mouse Motion**: Optimizes relative cursor mode without synthetic centering warps or OS acceleration lag.
- **In-Game ModMenu Config Screen**: Configure toggles dynamically with instant application and persistent JSON storage.

## License

MIT © Den1zz
