package org.orecruncher.dsurround.lib.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public abstract class ToggleButton extends ButtonWidget {

    private final Identifier onSprite;
    private final Identifier offSprite;

    private boolean isOn;

    protected ToggleButton(boolean initialState, Identifier onSprite, Identifier offSprite, PressAction onPress) {
        super(0, 0, 20, 20, Text.empty(), onPress, DEFAULT_NARRATION_SUPPLIER);

        this.isOn = initialState;
        this.onSprite = onSprite;
        this.offSprite = offSprite;
    }

    public void setOn(boolean flag) {
        this.isOn = flag;
    }

    public boolean toggle() {
        return this.isOn = !this.isOn;
    }

    // Basically what ImageButton does but simplified.
    @Override
    public void renderWidget(DrawContext guiGraphics, int i, int j, float f) {
        Identifier resourceLocation = this.getSpriteToRender();
        guiGraphics.drawGuiTexture(resourceLocation, this.getX(), this.getY(), this.width, this.height);
    }

    private Identifier getSpriteToRender() {
        return this.isOn ? this.onSprite : this.offSprite;
    }
}
