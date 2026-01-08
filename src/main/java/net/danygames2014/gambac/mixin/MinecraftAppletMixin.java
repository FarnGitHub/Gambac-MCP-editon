package net.danygames2014.gambac.mixin;

import net.danygames2014.gambac.BrnoMinecraft;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MinecraftApplet;
import net.minecraft.src.Session;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings({"removal", "StringBufferReplaceableByString"})
@Mixin(MinecraftApplet.class)
public class MinecraftAppletMixin {

    @Shadow
    private Minecraft mc;

    private MinecraftApplet self = (MinecraftApplet) (Object) this;

    /**
     * @author Proudly overwritten by DanyGames2014
     * @reason because i don't give a shit
     */
    @Overwrite(remap = false)
    public void init() {
        //System.out.println("Properties:");
        //System.getProperties().forEach( (k, v) -> System.out.println(k + " : " + v));

        // PrismLauncher Window Size
        if (System.getProperty("org.prismlauncher.window.dimensions") != null) {
            String[] dimensions = System.getProperty("org.prismlauncher.window.dimensions").split("x");
            if (dimensions.length == 2) {
                try {
                    int prismWidth = Integer.parseInt(dimensions[0]);
                    int prismHeight = Integer.parseInt(dimensions[1]);
                    self.setSize(prismWidth, prismHeight);
                } catch (NumberFormatException ignored) {

                }
            }
        }

        boolean fullscreen = false;
        if (self.getParameter("fullscreen") != null) {
            fullscreen = self.getParameter("fullscreen").equalsIgnoreCase("true");
        }

        this.mc = new BrnoMinecraft(self.getWidth(), self.getHeight(), fullscreen);

        this.mc.minecraftUri = self.getDocumentBase().getHost();
        if (self.getDocumentBase().getPort() > 0) {
            StringBuilder hostAdressBuilder = new StringBuilder();
            Minecraft mc = this.mc;
            mc.minecraftUri = hostAdressBuilder.append(mc.minecraftUri).append(":").append(self.getDocumentBase().getPort()).toString();
        }

        if (self.getParameter("username") != null && self.getParameter("sessionid") != null) {
            this.mc.session = new Session(self.getParameter("username"), self.getParameter("sessionid"));
            System.out.println("Setting user: " + this.mc.session.username);
            if (self.getParameter("mppass") != null) {
                this.mc.session.mpPassParameter = self.getParameter("mppass");
            }
        } else {
            this.mc.session = new Session("Player" + System.currentTimeMillis() % 10000, "");
        }

        if (self.getParameter("server") != null && self.getParameter("port") != null) {
            this.mc.setServer(self.getParameter("server"), Integer.parseInt(self.getParameter("port")));
        }

        self.startMainThread();

        SwingUtilities.invokeLater(() -> {
            hideThemAll(self.getParent().getParent().getParent());
            hideThemAll(self.getParent().getParent());
            hideThemAll(self.getParent());
            hideThemAll(self);
        });
    }

    @Unique
    private void hideThemAll(Container container) {
        try {
            if (container instanceof Frame) {
                ((Frame) container).dispose();
            }
            for (Component component : container.getComponents()) {
                component.setVisible(false);
            }
        } catch (NullPointerException ignored) {
        }
    }

    /**
     * @author DanyGames2014
     * @reason because i don't give a shit
     */
    @Overwrite
    public void startMainThread() { // startMainThread
        this.mc.run();
    }

    @Inject(method = "destroy", at = @At(value = "HEAD"), remap = false, cancellable = true)
    public void destroy(CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "stop", at = @At(value = "HEAD"), cancellable = true)
    public void stopThread(CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "clearApplet", at = @At(value = "HEAD"), cancellable = true)
    public void clearMemory(CallbackInfo ci) {
        ci.cancel();
    }
}
