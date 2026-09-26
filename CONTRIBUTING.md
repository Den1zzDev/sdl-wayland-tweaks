# Contributing to SDL Wayland Tweaks

Anyone is free and encouraged to contribute. Whether you want to fix a bug, adapt a new Wayland protocol extension, clean up an SDL3 hint, or rewrite a mixin, your PR is welcome.

## Code origin and AI disclosure

This project was built heavily with AI assistance. I believe in using modern tools to solve Linux desktop friction quickly.

Because of that, you do not need to hide your tools here. You can write your PR entirely by hand, use an LLM to scaffold it, or have an agent do the heavy lifting. All of those are accepted.

The only requirement is to disclose it:
- If you used an AI assistant or LLM, say so in your PR description. Mention which model or tool you used if possible.
- If you wrote the code by hand without AI generation, say that instead.

This disclosure lets reviewers focus their review. Generated code usually needs closer inspection around null checks, concurrency, and undocumented API changes, while handwritten code usually needs different checks.

## Development setup

### Requirements

- JDK 25 (Temurin, OpenJDK, or your distro package)
- A Linux desktop running a Wayland compositor (COSMIC, GNOME, KDE, Sway, Hyprland, etc.)

### Build commands

Build the mod jar:
```bash
./gradlew assemble
```

Run a test client:
```bash
./gradlew runClient
```

The built jar lands in `build/libs/sdl-wayland-tweaks-26.3-1.0.0.jar`.

## Submission guidelines

1. Test your change on a real Wayland session before submitting.
2. Keep changes focused on Wayland, SDL3, Linux input, or windowing behavior.
3. Keep commit messages plain and descriptive.
