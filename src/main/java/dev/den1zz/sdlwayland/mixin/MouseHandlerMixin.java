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

        // Prevent large-angle float truncation jitter by wrapping yaw to [-180, 180].
        // Both yRot and yRotO must receive identical offset to avoid an interpolation snap across the 180 boundary.
        float currentYRot = player.getYRot();
        float wrappedYRot = Mth.wrapDegrees(currentYRot);
        if (wrappedYRot != currentYRot) {
            float diff = wrappedYRot - currentYRot;
            player.setYRot(wrappedYRot);
            player.yRotO += diff;
        }

        // Only compute sub-pixel remainder if there is actual input movement
        if (Math.abs(this.accumulatedDX) < 1e-7 && Math.abs(this.accumulatedDY) < 1e-7) {
            return;
        }

        double sensitivity = (Double) this.minecraft.options.sensitivity().get() * 0.6 + 0.2;
        double factor = sensitivity * sensitivity * sensitivity;
        if (this.minecraft.options.getCameraType().isFirstPerson() && player.isScoping()) {
            // Unmultiplied factor during first-person scoping
        } else {
            factor *= 8.0;
        }

        if (factor == 0.0) {
            return;
        }

        boolean invertX = this.minecraft.options.invertMouseX().get();
        boolean invertY = this.minecraft.options.invertMouseY().get();

        double d3 = this.accumulatedDX * factor * (invertX ? -1.0 : 1.0);
        double d5 = this.accumulatedDY * factor * (invertY ? -1.0 : 1.0);

        // Minecraft's Entity.turn multiplies by 0.15F after casting to float
        double targetYaw = d3 * 0.15;
        double targetPitch = d5 * 0.15;

        float appliedYaw = (float) d3 * 0.15F;
        float appliedPitch = (float) d5 * 0.15F;

        double remYaw = targetYaw - (double) appliedYaw;
        double remPitch = targetPitch - (double) appliedPitch;

        double divisor = factor * 0.15;
        this.sdlwt$remainderDX = (remYaw / divisor) * (invertX ? -1.0 : 1.0);
        this.sdlwt$remainderDY = (remPitch / divisor) * (invertY ? -1.0 : 1.0);
    }

    @Inject(method = "handleAccumulatedMovement", at = @At("RETURN"))
    private void sdlwt$handleAccumulatedMovement(CallbackInfo ci) {
        if (this.mouseGrabbed && SdlWaylandConfig.get().motionDeltaAccumulation && Sdl3WindowManager.isWaylandDriver()) {
            // Only feed back remainder if the accumulated residual is meaningful
            if (Math.abs(this.sdlwt$remainderDX) > 1e-5) {
                this.accumulatedDX += this.sdlwt$remainderDX;
            }
            if (Math.abs(this.sdlwt$remainderDY) > 1e-5) {
                this.accumulatedDY += this.sdlwt$remainderDY;
            }
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
