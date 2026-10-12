package dev.den1zz.sdlwayland.mixin;

import com.mojang.renderpearl.api.device.GpuSurface;
import dev.den1zz.sdlwayland.config.SdlWaylandConfig;
import dev.den1zz.sdlwayland.sdl.Sdl3WindowManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Collection;

@Mixin(GpuSurface.PresentMode.class)
public abstract class PresentModeMixin {

    @ModifyVariable(
        method = "getSupportedVsyncMode(Ljava/util/Collection;Z)Lcom/mojang/renderpearl/api/device/GpuSurface$PresentMode;",
        at = @At("HEAD"),
        argsOnly = true,
        ordinal = 0
    )
    private static boolean sdlwt$forceImmediatePresent(boolean vsync) {
        if (SdlWaylandConfig.get().asyncPageFlip && Sdl3WindowManager.isWaylandDriver()) {
            return false;
        }
        return vsync;
    }
}
