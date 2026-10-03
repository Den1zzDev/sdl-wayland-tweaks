package dev.den1zz.sdlwayland.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import dev.den1zz.sdlwayland.config.SdlWaylandConfig;
import dev.den1zz.sdlwayland.sdl.Sdl3WindowManager;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.util.StringDecomposer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractWidget.class)
public abstract class AbstractWidgetMixin {

    @Shadow public abstract boolean isMouseOver(double x, double y);
    @Shadow public abstract boolean isActive();
    @Shadow public boolean visible;

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void sdlwt$middleClickPaste(MouseButtonEvent event, boolean doubleClick, CallbackInfoReturnable<Boolean> cir) {
        if (!SdlWaylandConfig.get().middleClickPaste || !Sdl3WindowManager.isWaylandDriver()) {
            return;
        }

        if (event.button() == InputConstants.MOUSE_BUTTON_MIDDLE && this.isActive() && this.visible && this.isMouseOver(event.x(), event.y())) {
            if ((Object) this instanceof EditBox editBox) {
                editBox.setFocused(true);
                editBox.onClick(event, doubleClick);
                String primary = Sdl3WindowManager.getPrimarySelectionText();
                if (primary != null && !primary.isEmpty()) {
                    editBox.insertText(StringDecomposer.filterBrokenSurrogates(primary));
                }
                cir.setReturnValue(true);
            } else if ((Object) this instanceof MultiLineEditBox multiLineEditBox) {
                multiLineEditBox.setFocused(true);
                multiLineEditBox.onClick(event, doubleClick);
                String primary = Sdl3WindowManager.getPrimarySelectionText();
                if (primary != null && !primary.isEmpty()) {
                    ((MultiLineEditBoxAccessor) multiLineEditBox).sdlwt$getTextField().insertText(StringDecomposer.filterBrokenSurrogates(primary));
                }
                cir.setReturnValue(true);
            }
        }
    }
}
