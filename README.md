# SDL Wayland Tweaks

A lightweight Fabric mod that adds native Wayland adjustments and SDL3 windowing tweaks for Minecraft on Linux.

## What it does

- Sets Wayland display scaling (`SDL_HINT_VIDEO_WAYLAND_SCALE_TO_DISPLAY = "1"`), double-buffering, and server-side decoration hints before window initialization.
- Sets the Wayland `app_id` to `minecraft` and injects desktop metadata so compositors (such as GNOME, KDE, and COSMIC) group the window on the dock and taskbar properly.
- Strips the 1-pixel borderless window width offset and framebuffer padding that Minecraft adds on Wayland, allowing clean direct scanout.
- Re-locks cursor, relative pointer mode (`SDL_SetWindowRelativeMouseMode`), and keyboard grabs when Minecraft regains focus after an Alt+Tab or workspace switch.
- Syncs text with the Linux primary selection buffer using SDL3, letting you paste highlighted text.
- Middle-click paste directly into editable text fields (`EditBox` and `MultiLineEditBox`) from the Linux primary selection buffer.
- Strips unhandled ASCII control characters below 32 emitted by Wayland IME engines globally while allowing Backspace, Tab, and Enter through to chat and inputs.
- Routes async page flip directly into RenderPearl to request `PresentMode.IMMEDIATE` and disable swap interval, allowing compositors that support tearing to reduce input lag when V-Sync is disabled.
- Accumulates fractional sub-pixel mouse motion deltas across frames without snapping camera interpolation.
- Keeps fullscreen Minecraft visible on focus loss and allows focus click-through by default.
- Applies configuration toggles dynamically at runtime without requiring a game restart.
- Includes an in-game ModMenu settings screen with detailed tooltips and responsive layout, saved to `config/sdl-wayland-tweaks.json`.

## Transparency about AI

This mod was built almost entirely with LLMs and coding agents. I directed the architecture, reviewed the patches, debugged runtime quirks, and tested it on AerynOS with COSMIC. The machine generated the bulk of the boilerplate, SDL3 bindings, and mixins.

I have no problem with AI code. If you use models to write patches, write them yourself by hand, or do a mix of both, your contributions are welcome.

## Contributing

Anyone is welcome to contribute code, report issues, or suggest improvements. There are no restrictions on how you write your code.

The only rule is honesty: in your pull request description, state whether you used AI to help write the code, or if you wrote it yourself. That helps reviewers know what kind of edge cases or hallucinations to look for during review.

See [CONTRIBUTING.md](CONTRIBUTING.md) for build instructions and local testing.

## Building from source

Requires JDK 25:

```bash
git clone https://github.com/Den1zzDev/sdl-wayland-tweaks.git
cd sdl-wayland-tweaks
./gradlew assemble
```

The output jar is in `build/libs/`.

## License

MIT (c) 2026 Den1zz. See [LICENSE](LICENSE).
