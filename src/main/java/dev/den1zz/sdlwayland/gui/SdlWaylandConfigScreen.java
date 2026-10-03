package dev.den1zz.sdlwayland.gui;

import dev.den1zz.sdlwayland.config.SdlWaylandConfig;
import dev.den1zz.sdlwayland.sdl.Sdl3WindowManager;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class SdlWaylandConfigScreen extends Screen {
    private final Screen parent;

    public SdlWaylandConfigScreen(Screen parent) {
        super(Component.literal("SDL Wayland Tweaks Configuration"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        SdlWaylandConfig cfg = SdlWaylandConfig.get();
        int centerX = this.width / 2;
        int btnWidth = 190;
        int btnHeight = 20;

        // Dynamic spacing to guarantee all widgets fit on lower resolution displays
        int totalRows = 7;
        int availableSpace = this.height - 70;
        int spacing = Math.max(22, Math.min(24, availableSpace / (totalRows + 1)));
        int startY = Math.max(30, (this.height - (totalRows * spacing + 30)) / 2);

        int col1 = centerX - btnWidth - 6;
        int col2 = centerX + 6;
        int y = startY;

        // Row 1
        this.addRenderableWidget(createToggle(col1, y, btnWidth, btnHeight, "Wayland Scaling Hints",
            "Enables SDL3 Wayland-specific hints: 1:1 display scaling, native modes, and clean buffer presentation.",
            cfg.waylandHints, val -> cfg.waylandHints = val));

        this.addRenderableWidget(createToggle(col2, y, btnWidth, btnHeight, "App ID & Metadata",
            "Sets Wayland app_id to 'minecraft' so desktop environments and docks identify the window correctly.",
            cfg.appMetadata, val -> cfg.appMetadata = val));
        y += spacing;

        // Row 2
        this.addRenderableWidget(createToggle(col1, y, btnWidth, btnHeight, "Primary Selection Sync",
            "Syncs regular clipboard with the Linux primary selection buffer (middle-click buffer).",
            cfg.primarySelectionSync, val -> cfg.primarySelectionSync = val));

        this.addRenderableWidget(createToggle(col2, y, btnWidth, btnHeight, "Fix Borderless 1px Gap",
            "Removes Mojang's hardcoded 1-pixel borderless padding that causes an edge gap on Wayland.",
            cfg.fixBorderlessPadding, val -> cfg.fixBorderlessPadding = val));
        y += spacing;

        // Row 3
        this.addRenderableWidget(createToggle(col1, y, btnWidth, btnHeight, "Focus Grab Recovery",
            "Re-engages relative mouse and keyboard grabs immediately when the window regains focus.",
            cfg.focusGrabRecovery, val -> cfg.focusGrabRecovery = val));

        this.addRenderableWidget(createToggle(col2, y, btnWidth, btnHeight, "IME Control Char Filter",
            "Strips unhandled ASCII control codes emitted by Wayland IME engines during composition.",
            cfg.imeControlCharFiltering, val -> cfg.imeControlCharFiltering = val));
        y += spacing;

        // Row 4
        this.addRenderableWidget(createToggle(col1, y, btnWidth, btnHeight, "Async Tearing Flip",
            "Enables immediate presentation (swap interval 0) for low-latency tearing on supported compositors.",
            cfg.asyncPageFlip, val -> {
                cfg.asyncPageFlip = val;
                Sdl3WindowManager.applySwapInterval(val);
            }));

        this.addRenderableWidget(createToggle(col2, y, btnWidth, btnHeight, "Relative Mouse Opts",
            "Configures SDL3 relative pointer centering, warp emulation, and DPI-scaled cursors.",
            cfg.relativeMouseOptimizations, val -> cfg.relativeMouseOptimizations = val));
        y += spacing;

        // Row 5
        this.addRenderableWidget(createToggle(col1, y, btnWidth, btnHeight, "Bypass OS Scale",
            "Bypasses compositor pointer scaling for raw 1:1 mouse input coordinates.",
            cfg.bypassOsScale, val -> {
                cfg.bypassOsScale = val;
                Sdl3WindowManager.updateRuntimeHints();
            }));

        this.addRenderableWidget(createToggle(col2, y, btnWidth, btnHeight, "Bypass Compositor",
            "Requests compositor bypass on X11 and XWayland sessions to minimize display latency.",
            cfg.bypassCompositor, val -> {
                cfg.bypassCompositor = val;
                Sdl3WindowManager.updateRuntimeHints();
            }));
        y += spacing;

        // Row 6
        this.addRenderableWidget(createToggle(col1, y, btnWidth, btnHeight, "Prevent Minimize",
            "Prevents fullscreen Minecraft from automatically minimizing when switching windows.",
            cfg.preventMinimizeOnFocusLoss, val -> {
                cfg.preventMinimizeOnFocusLoss = val;
                Sdl3WindowManager.updateRuntimeHints();
            }));

        this.addRenderableWidget(createToggle(col2, y, btnWidth, btnHeight, "Focus Clickthrough",
            "Allows mouse clicks that focus the window to be delivered directly to the game.",
            cfg.focusClickthrough, val -> {
                cfg.focusClickthrough = val;
                Sdl3WindowManager.updateRuntimeHints();
            }));
        y += spacing;

        // Row 7
        this.addRenderableWidget(createToggle(col1, y, btnWidth, btnHeight, "Middle-Click Paste",
            "Enables pasting primary selection text into text boxes using the middle mouse button.",
            cfg.middleClickPaste, val -> cfg.middleClickPaste = val));

        this.addRenderableWidget(createToggle(col2, y, btnWidth, btnHeight, "Motion Delta Accum.",
            "Accumulates sub-pixel mouse motion remainders and wraps player yaw to prevent camera jitter.",
            cfg.motionDeltaAccumulation, val -> cfg.motionDeltaAccumulation = val));
        y += spacing + 8;

        // Done button
        int doneY = Math.min(y, this.height - 26);
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, btn -> onClose())
            .bounds(centerX - 100, doneY, 200, 20)
            .build());
    }

    private Button createToggle(int x, int y, int w, int h, String label, String tooltipText, boolean initial, java.util.function.Consumer<Boolean> consumer) {
        boolean[] state = new boolean[]{initial};
        return Button.builder(CommonComponents.optionStatus(Component.literal(label), state[0]), btn -> {
            state[0] = !state[0];
            consumer.accept(state[0]);
            btn.setMessage(CommonComponents.optionStatus(Component.literal(label), state[0]));
        })
        .bounds(x, y, w, h)
        .tooltip(Tooltip.create(Component.literal(tooltipText)))
        .build();
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(guiGraphicsExtractor, mouseX, mouseY, partialTick);
        guiGraphicsExtractor.centeredText(this.font, this.title, this.width / 2, 12, 0xFFFFFF);
    }

    @Override
    public void onClose() {
        SdlWaylandConfig.get().save();
        Sdl3WindowManager.updateRuntimeHints();
        if (this.minecraft != null && this.minecraft.gui != null) {
            this.minecraft.gui.setScreen(this.parent);
        }
    }
}
