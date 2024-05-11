package org.orecruncher.dsurround.lib.gui;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.text.Text;
import org.orecruncher.dsurround.mixins.core.MixinButtonWidget;

public class SilentButton extends ButtonWidget {

    protected SilentButton(ButtonWidget sourceButton) {
        this(sourceButton.getX(), sourceButton.getY(), sourceButton.getWidth(), sourceButton.getHeight(), sourceButton.getMessage(), ((MixinButtonWidget)sourceButton).dsurround_getPressAction(), ((MixinButtonWidget)sourceButton).dsurround_getNarrationSupplier());
    }

    protected SilentButton(int x, int y, int width, int height, Text message, PressAction onPress, NarrationSupplier narrationSupplier) {
        super(x, y, width, height, message, onPress, narrationSupplier);
    }

    @Override
    public void playDownSound(SoundManager ignored) {
        // Do nothing - we are suppressing the down sound
    }

    public static SilentButton from(ButtonWidget buttonWidget) {
        return new SilentButton(buttonWidget);
    }
}
