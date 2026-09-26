package dev.den1zz.sdlwayland.mixin;

import com.mojang.blaze3d.platform.DisplayData;
import com.mojang.blaze3d.platform.MonitorManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.platform.WindowEventHandler;
import com.mojang.renderpearl.api.device.GpuBackend;
import dev.den1zz.sdlwayland.config.SdlWaylandConfig;
import dev.den1zz.sdlwayland.sdl.Sdl3WindowManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Window.class)
public abstract class WindowMixin {

    @Shadow @Final private long handle;

    @Inject(
        method = "<init>(Lcom/mojang/blaze3d/platform/WindowEventHandler;Lcom/mojang/blaze3d/platform/DisplayData;Ljava/lang/String;ZLjava/lang/String;Lcom/mojang/blaze3d/platform/MonitorManager;Lcom/mojang/renderpearl/api/device/GpuBackend;I)V",
        at = @At("HEAD")
    )
    private static void sdlwt$preInitHints(
        WindowEventHandler windowEventHandler,
        DisplayData displayData,
        String string,
        boolean bl,
        String string2,
        MonitorManager monitorManager,
        GpuBackend gpuBackend,
        int i,
        CallbackInfo ci
    ) {
        Sdl3WindowManager.preInitHints();
    }

    @Inject(
        method = "<init>(Lcom/mojang/blaze3d/platform/WindowEventHandler;Lcom/mojang/blaze3d/platform/DisplayData;Ljava/lang/String;ZLjava/lang/String;Lcom/mojang/blaze3d/platform/MonitorManager;Lcom/mojang/renderpearl/api/device/GpuBackend;I)V",
        at = @At("RETURN")
    )
    private void sdlwt$onWindowCreated(
        WindowEventHandler windowEventHandler,
        DisplayData displayData,
        String string,
        boolean bl,
        String string2,
        MonitorManager monitorManager,
        GpuBackend gpuBackend,
        int i,
        CallbackInfo ci
    ) {
        Sdl3WindowManager.onWindowCreated(this.handle);
    }

    @Inject(method = "framebufferWidthPadding", at = @At("HEAD"), cancellable = true)
    private void sdlwt$fixBorderlessPadding(CallbackInfoReturnable<Integer> cir) {
        if (SdlWaylandConfig.get().fixBorderlessPadding && Sdl3WindowManager.isWaylandDriver()) {
            cir.setReturnValue(0);
        }
    }

    @Inject(method = "onFocus", at = @At("RETURN"))
    private void sdlwt$onFocus(boolean focused, CallbackInfo ci) {
        if (focused && SdlWaylandConfig.get().focusGrabRecovery && Sdl3WindowManager.isWaylandDriver()) {
            Sdl3WindowManager.reassertGrabs();
        }
    }
}
