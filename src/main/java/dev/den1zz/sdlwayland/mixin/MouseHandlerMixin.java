package dev.den1zz.sdlwayland.mixin;

import dev.den1zz.sdlwayland.config.SdlWaylandConfig;
import dev.den1zz.sdlwayland.sdl.Sdl3WindowManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {

    @Shadow @Final private Minecraft minecraft;
    @Shadow private boolean mouseGrabbed;
    @Shadow private double accumulatedDX;
    @Shadow private double accumulatedDY;

    @Unique private double sdlwt$remainderDX = 0.0;
    @Unique private double sdlwt$remainderDY = 0.0;

    @Inject(method = "turnPlayer", at = @At("HEAD"))
    private void sdlwt$turnPlayer(double d, CallbackInfo ci) {
        if (!SdlWaylandConfig.get().motionDeltaAccumulation || !this.mouseGrabbed || !Sdl3WindowManager.isWaylandDriver()) {
            return;
        }

        LocalPlayer player = this.minecraft.player;
        if (player == null || this.minecraft.options.smoothCamera) {
            return;
        }

        // Prevent large-angle float truncation jitter by wrapping yaw to [-180, 180]
        float wrappedYRot = Mth.wrapDegrees(player.getYRot());
        player.setYRot(wrappedYRot);
        player.yRotO = Mth.wrapDegrees(player.yRotO);

        double sensitivity = (Double) this.minecraft.options.sensitivity().get() * 0.6 + 0.2;
        double factor = sensitivity * sensitivity * sensitivity;
        if (!player.isScoping()) {
            factor *= 8.0;
        }
        if (factor == 0.0) {
            return;
        }

        double turnStep = factor * 0.15;
        double targetRotX = this.accumulatedDX * turnStep;
        double targetRotY = this.accumulatedDY * turnStep;

        float appliedRotX = (float) targetRotX;
        float appliedRotY = (float) targetRotY;

        double remRotX = targetRotX - (double) appliedRotX;
        double remRotY = targetRotY - (double) appliedRotY;

        this.sdlwt$remainderDX = remRotX / turnStep;
        this.sdlwt$remainderDY = remRotY / turnStep;
    }

    @Inject(method = "handleAccumulatedMovement", at = @At("RETURN"))
    private void sdlwt$handleAccumulatedMovement(CallbackInfo ci) {
        if (this.mouseGrabbed && SdlWaylandConfig.get().motionDeltaAccumulation && Sdl3WindowManager.isWaylandDriver()) {
            this.accumulatedDX += this.sdlwt$remainderDX;
            this.accumulatedDY += this.sdlwt$remainderDY;
            this.sdlwt$remainderDX = 0.0;
            this.sdlwt$remainderDY = 0.0;
        }
    }

    @Inject(method = "releaseMouse", at = @At("HEAD"))
    private void sdlwt$onReleaseMouse(CallbackInfo ci) {
        this.sdlwt$remainderDX = 0.0;
        this.sdlwt$remainderDY = 0.0;
    }
}
