package net.danygames2014.gambac.mixin;

import net.minecraft.src.MouseHelper;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHelper.class)
public class MouseHelperMixin {
    @Inject(method = "ungrabMouseCursor", at = @At(value = "HEAD"), cancellable = true)
    public void ungrabCursor(CallbackInfo ci){
        Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);
        Mouse.setGrabbed(false);
        ci.cancel();
    }
}
