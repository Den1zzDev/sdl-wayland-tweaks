package dev.den1zz.sdlwayland.mixin;

import dev.den1zz.sdlwayland.config.SdlWaylandConfig;
import dev.den1zz.sdlwayland.sdl.Sdl3WindowManager;
import net.minecraft.client.gui.components.EditBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EditBox.class)
public abstract class EditBoxMixin {

    @Inject(method = "charTyped", at = @At("HEAD"), cancellable = true)
    private void sdlwt$filterWaylandImeControlChars(char codePoint, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (SdlWaylandConfig.get().imeControlCharFiltering && Sdl3WindowManager.isWaylandDriver()) {
            if (codePoint < 32 && codePoint != 8 && codePoint != 10 && codePoint != 13) {
                cir.setReturnValue(false);
            }
        }
    }
}
