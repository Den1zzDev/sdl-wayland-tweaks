# SDL Wayland Tweaks

A lightweight Fabric mod that adds native Wayland adjustments and SDL3 windowing tweaks for Minecraft on Linux.

## What it does

- Sets Wayland display scaling (`SDL_HINT_VIDEO_WAYLAND_SCALE_TO_DISPLAY = "1"`), double-buffering, and server-side decoration hints before window initialization.
- Sets the Wayland `app_id` to `minecraft` and injects desktop metadata so compositors (such as GNOME, KDE, and COSMIC) group the window on the dock and taskbar properly.
- Drops the 1-pixel borderless window padding that Minecraft leaves on screen under Wayland.
- Re-locks cursor and keyboard grabs when Minecraft regains focus after an Alt+Tab or workspace switch.
- Syncs text with the Linux primary selection buffer using SDL3, letting you paste highlighted text.
- Optional middle-click paste directly into text fields from the Linux primary selection buffer (off by default).
- Filters out non-printable ASCII control characters below 32 that certain Wayland IME inputs send into chat boxes.
- Sets the Wayland tearing protocol hint (`SDL_VIDEO_WAYLAND_ALLOW_TEARING`) and swap interval so compositors supporting `wp_tearing_control_v1` can tear without tearing being blocked.
- Preserves fractional mouse motion deltas across frames without truncation loss and wraps yaw angles to prevent float precision jitter.
- Includes an in-game ModMenu settings screen saved to `config/sdl-wayland-tweaks.json`.

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
