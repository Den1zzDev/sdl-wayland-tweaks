package dev.den1zz.sdlwayland.mixin;

import com.mojang.blaze3d.platform.ClipboardManager;
import dev.den1zz.sdlwayland.config.SdlWaylandConfig;
import dev.den1zz.sdlwayland.sdl.Sdl3WindowManager;
import net.minecraft.util.StringDecomposer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClipboardManager.class)
public abstract class ClipboardManagerMixin {

    @Inject(method = "setClipboard", at = @At("RETURN"))
    private void sdlwt$syncPrimarySelection(String text, CallbackInfo ci) {
        if (text != null && SdlWaylandConfig.get().primarySelectionSync) {
            Sdl3WindowManager.setPrimarySelectionText(text);
        }
    }

    @Inject(method = "getClipboard", at = @At("RETURN"), cancellable = true)
    private void sdlwt$fallbackPrimarySelection(CallbackInfoReturnable<String> cir) {
        if (cir.getReturnValue() != null && cir.getReturnValue().isEmpty() && SdlWaylandConfig.get().primarySelectionSync) {
            String primary = Sdl3WindowManager.getPrimarySelectionText();
            if (primary != null && !primary.isEmpty()) {
                cir.setReturnValue(StringDecomposer.filterBrokenSurrogates(primary));
            }
        }
    }
}
