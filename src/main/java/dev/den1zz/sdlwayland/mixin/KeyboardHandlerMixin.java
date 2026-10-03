package dev.den1zz.sdlwayland.mixin;

import dev.den1zz.sdlwayland.config.SdlWaylandConfig;
import dev.den1zz.sdlwayland.sdl.Sdl3WindowManager;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.input.CharacterEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public abstract class KeyboardHandlerMixin {

    @Inject(method = "charTyped", at = @At("HEAD"), cancellable = true)
    private void sdlwt$filterGlobalImeControlChars(long handle, CharacterEvent event, CallbackInfo ci) {
        if (SdlWaylandConfig.get().imeControlCharFiltering && Sdl3WindowManager.isWaylandDriver()) {
            int codePoint = event.codepoint();
            if (codePoint < 32 && codePoint != 8 && codePoint != 10 && codePoint != 13) {
                ci.cancel();
            }
        }
    }
}
