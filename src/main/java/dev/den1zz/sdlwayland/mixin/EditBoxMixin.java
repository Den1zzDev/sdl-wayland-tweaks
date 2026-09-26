package dev.den1zz.sdlwayland.mixin;

import dev.den1zz.sdlwayland.config.SdlWaylandConfig;
import dev.den1zz.sdlwayland.sdl.Sdl3WindowManager;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.CharacterEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EditBox.class)
public abstract class EditBoxMixin {

    @Inject(method = "charTyped(Lnet/minecraft/client/input/CharacterEvent;)Z", at = @At("HEAD"), cancellable = true)
    private void sdlwt$filterWaylandImeControlChars(CharacterEvent event, CallbackInfoReturnable<Boolean> cir) {
        if (SdlWaylandConfig.get().imeControlCharFiltering && Sdl3WindowManager.isWaylandDriver()) {
            int codePoint = event.codepoint();
            if (codePoint < 32 && codePoint != 8 && codePoint != 10 && codePoint != 13) {
                cir.setReturnValue(false);
            }
        }
    }
}
