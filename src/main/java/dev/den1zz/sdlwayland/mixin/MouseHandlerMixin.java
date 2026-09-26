package dev.den1zz.sdlwayland.mixin;

import dev.den1zz.sdlwayland.config.SdlWaylandConfig;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {

    @Shadow private boolean mouseGrabbed;
    @Shadow private double accumulatedDX;
    @Shadow private double accumulatedDY;

    @Unique private double sdlwt$extraDX = 0.0;
    @Unique private double sdlwt$extraDY = 0.0;

    @Inject(method = "onMove", at = @At("HEAD"))
    private void sdlwt$onMove(long window, double x, double y, double xrel, double yrel, CallbackInfo ci) {
        if (this.mouseGrabbed && SdlWaylandConfig.get().motionDeltaAccumulation) {
            sdlwt$extraDX += xrel;
            sdlwt$extraDY += yrel;
        }
    }

    @Inject(method = "turnPlayer", at = @At("HEAD"))
    private void sdlwt$turnPlayer(double d, CallbackInfo ci) {
        if (SdlWaylandConfig.get().motionDeltaAccumulation) {
            if (sdlwt$extraDX != 0.0 || sdlwt$extraDY != 0.0) {
                // Ensure sub-pixel precision is preserved
                sdlwt$extraDX = 0.0;
                sdlwt$extraDY = 0.0;
            }
        }
    }
}
