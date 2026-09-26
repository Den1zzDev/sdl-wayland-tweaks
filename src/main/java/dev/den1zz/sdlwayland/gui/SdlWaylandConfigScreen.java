package dev.den1zz.sdlwayland.gui;

import dev.den1zz.sdlwayland.config.SdlWaylandConfig;
import dev.den1zz.sdlwayland.sdl.Sdl3WindowManager;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
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
        int y = 35;
        int btnWidth = 190;
        int btnHeight = 20;
        int spacing = 24;

        // Column 1
        int col1 = centerX - btnWidth - 5;
        // Column 2
        int col2 = centerX + 5;

        // 1. Wayland Hints
        this.addRenderableWidget(createToggle(col1, y, btnWidth, btnHeight, "Wayland Scaling Hints", cfg.waylandHints, val -> cfg.waylandHints = val));
        // 2. App Metadata
        this.addRenderableWidget(createToggle(col2, y, btnWidth, btnHeight, "App ID & Metadata", cfg.appMetadata, val -> cfg.appMetadata = val));
        y += spacing;

        // 3. Primary Selection
        this.addRenderableWidget(createToggle(col1, y, btnWidth, btnHeight, "Primary Selection Sync", cfg.primarySelectionSync, val -> cfg.primarySelectionSync = val));
        // 4. Borderless Padding Fix
        this.addRenderableWidget(createToggle(col2, y, btnWidth, btnHeight, "Fix Borderless 1px Gap", cfg.fixBorderlessPadding, val -> cfg.fixBorderlessPadding = val));
        y += spacing;

        // 5. Focus Grab Recovery
        this.addRenderableWidget(createToggle(col1, y, btnWidth, btnHeight, "Focus Grab Recovery", cfg.focusGrabRecovery, val -> cfg.focusGrabRecovery = val));
        // 6. IME Filtering
        this.addRenderableWidget(createToggle(col2, y, btnWidth, btnHeight, "IME Control Char Filter", cfg.imeControlCharFiltering, val -> cfg.imeControlCharFiltering = val));
        y += spacing;

        // 7. Async Page Flip (Tearing)
        this.addRenderableWidget(createToggle(col1, y, btnWidth, btnHeight, "Async Tearing Flip", cfg.asyncPageFlip, val -> {
            cfg.asyncPageFlip = val;
            Sdl3WindowManager.applySwapInterval(val);
        }));
        // 8. Relative Mouse Opts
        this.addRenderableWidget(createToggle(col2, y, btnWidth, btnHeight, "Relative Mouse Opts", cfg.relativeMouseOptimizations, val -> cfg.relativeMouseOptimizations = val));
        y += spacing;

        // 9. Bypass OS Scale
        this.addRenderableWidget(createToggle(col1, y, btnWidth, btnHeight, "Bypass OS Scale", cfg.bypassOsScale, val -> cfg.bypassOsScale = val));
        // 10. Bypass Compositor
        this.addRenderableWidget(createToggle(col2, y, btnWidth, btnHeight, "Bypass Compositor", cfg.bypassCompositor, val -> cfg.bypassCompositor = val));
        y += spacing;

        // 11. Prevent Minimize
        this.addRenderableWidget(createToggle(col1, y, btnWidth, btnHeight, "Prevent Minimize", cfg.preventMinimizeOnFocusLoss, val -> cfg.preventMinimizeOnFocusLoss = val));
        // 12. Focus Clickthrough
        this.addRenderableWidget(createToggle(col2, y, btnWidth, btnHeight, "Focus Clickthrough", cfg.focusClickthrough, val -> cfg.focusClickthrough = val));
        y += spacing;

        // 13. Middle Click Paste
        this.addRenderableWidget(createToggle(col1, y, btnWidth, btnHeight, "Middle-Click Paste", cfg.middleClickPaste, val -> cfg.middleClickPaste = val));
        // 14. Motion Delta Accumulation
        this.addRenderableWidget(createToggle(col2, y, btnWidth, btnHeight, "Motion Delta Accum.", cfg.motionDeltaAccumulation, val -> cfg.motionDeltaAccumulation = val));
        y += spacing + 10;

        // Done button
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, btn -> {
            cfg.save();
            Sdl3WindowManager.preInitHints();
            onClose();
        }).bounds(centerX - 100, y, 200, 20).build());
    }

    private Button createToggle(int x, int y, int w, int h, String label, boolean initial, java.util.function.Consumer<Boolean> consumer) {
        boolean[] state = new boolean[]{initial};
        return Button.builder(Component.literal(label + ": " + (state[0] ? "ON" : "OFF")), btn -> {
            state[0] = !state[0];
            consumer.accept(state[0]);
            btn.setMessage(Component.literal(label + ": " + (state[0] ? "ON" : "OFF")));
        }).bounds(x, y, w, h).build();
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(guiGraphicsExtractor, mouseX, mouseY, partialTick);
        guiGraphicsExtractor.centeredText(this.font, this.title, this.width / 2, 14, 0xFFFFFF);
    }

    @Override
    public void onClose() {
        SdlWaylandConfig.get().save();
        if (this.minecraft != null && this.minecraft.gui != null) {
            this.minecraft.gui.setScreen(this.parent);
        }
    }
}
