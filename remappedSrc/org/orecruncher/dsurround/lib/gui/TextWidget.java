package org.orecruncher.dsurround.lib.gui;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AbstractTextWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public class TextWidget extends AbstractTextWidget {

    public TextWidget(int x, int y, int width, int height, Text component, TextRenderer font) {
        super(x, y, width, height, component, font);
    }

    @Override
    protected void renderWidget(@NotNull DrawContext guiGraphics, int i, int j, float f) {
        int y = getY();

        int nameWidth = this.getTextRenderer().getWidth(this.getMessage());
        if (nameWidth > getWidth()) {
            drawScrollableText(guiGraphics, this.getTextRenderer(), this.getMessage(), getX(), y, getX() + getWidth(), y + this.getTextRenderer().fontHeight, -1);
        } else {
            guiGraphics.drawTextWithShadow(this.getTextRenderer(), this.getMessage(), getX(), y, 0xFFFFFF);
        }
    }
}
