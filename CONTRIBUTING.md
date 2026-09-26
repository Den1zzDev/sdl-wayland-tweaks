# Contributing to SDL Wayland Tweaks

Thank you for your interest in improving SDL Wayland Tweaks! This project aims to bring native Wayland ergonomics and low-latency SDL3 graphics/input tuning to modern Minecraft on Linux desktops.

## Getting Started

### Prerequisites

- **Java Development Kit (JDK) 25** (e.g. Eclipse Temurin or OpenJDK 25)
- **Git**
- A Linux environment with a Wayland compositor (COSMIC, GNOME Shell, Sway, Hyprland, KDE Plasma 6, etc.) to test runtime behavior.

### Setting Up Local Environment

1. Fork and clone the repository:
   ```bash
   git clone https://github.com/Den1zzDev/sdl-wayland-tweaks.git
   cd sdl-wayland-tweaks
   ```

2. Compile and package the mod:
   ```bash
   ./gradlew assemble
   ```

   The compiled jar will be located in `build/libs/sdl-wayland-tweaks-<minecraft-version>-<mod-version>.jar`.

3. Launch Minecraft test client in development:
   ```bash
   ./gradlew runClient
   ```

## Development Guidelines

1. **Keep It Pure & Focused**:
   - Only implement features and patches related to SDL3, Wayland protocols (`wl_surface`, `xdg_toplevel`, `wp_tearing_control_v1`, etc.), Linux input, or desktop clipboard/windowing ergonomics.
   - Avoid game-modifying gameplay mechanics or unrelated client features.
2. **Follow Loom / Fabric Standards**:
   - Mixins should target specific methods cleanly with minimal invasiveness.
   - Prioritize `@Inject` and cancel or mutate return values safely.
   - Support ModMenu integration where applicable.
3. **Commit Messages**:
   - Use conventional commit style:
     - `feat: ...` for new features
     - `fix: ...` for bug fixes
     - `docs: ...` for documentation
     - `refactor: ...` for code refactoring
     - `ci: ...` for GitHub Actions changes

## Reporting Issues

If you encounter bugs, screen tearing issues, improper scaling, or input bugs:
- Check existing issues to see if it has already been reported.
- Open an issue describing your:
  - Linux distribution and desktop compositor (e.g., AerynOS with COSMIC `cosmic-comp`, Arch with Sway, Fedora with GNOME).
  - GPU backend and graphics driver (Mesa RADV, NVIDIA proprietary, etc.).
  - Steps to reproduce and any relevant log entries.
