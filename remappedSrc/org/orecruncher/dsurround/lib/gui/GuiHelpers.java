package org.orecruncher.dsurround.lib.gui;

import org.jetbrains.annotations.Nullable;
import org.orecruncher.dsurround.lib.GameUtils;

import java.util.ArrayList;
import java.util.Collection;
import net.minecraft.client.font.TextHandler;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.MutableText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class GuiHelpers {

    private final static String ELLIPSES = "...";

    /**
     * Gets the text associated with the given language key that is formatted so that a line is <= the width
     * specified.
     *
     * @param key       Translation key for the associated text
     * @param width     Maximum width of a line
     * @param style     The style to apply to each of the resulting split lines
     * @return Collection of Components for the given key
     */
    public static Collection<Text> getTrimmedTextCollection(final String key, final int width, final Style style) {
        var text = Text.translatable(key);
        return getTrimmedTextCollection(text, width, style);
    }

    public static Collection<Text> getTrimmedTextCollection(Text text, int width, Style style) {
        var result = new ArrayList<Text>();
        var textHandler = GameUtils.getTextHandler();
        textHandler.wrapLines(text, width, style).forEach(line -> result.add(Text.literal(line.getString()).fillStyle(style)));
        return result;
    }

    /**
     * Gets the text associated with the given language key.  Text is truncated to the specified width and an
     * ellipses append if necessary.
     *
     * @param key        Translation key for the associated text
     * @param width      Maximum width of the text in GUI pixels
     * @param formatting Formatting to apply to the text
     * @return FormattedText fitting the criteria specified
     */
    public static StringVisitable getTrimmedText(final String key, final int width, @Nullable final Formatting... formatting) {
        var fr = GameUtils.getTextRenderer();
        var cm = GameUtils.getTextHandler();

        final Style style = prefixHelper(formatting);
        final StringVisitable text = Text.translatable(key);
        if (fr.getWidth(text) > width) {
            final int ellipsesWidth = fr.getWidth(ELLIPSES);
            final int trueWidth = width - ellipsesWidth;
            final StringVisitable str = cm.trimToWidth(text, trueWidth, style);
            return Text.literal(str.getString() + ELLIPSES);
        }
        final StringVisitable str = cm.trimToWidth(text, width, style);
        return Text.literal(str.getString());
    }

    private static Style prefixHelper(@Nullable final Formatting[] formatting) {
        final Style style;
        if (formatting != null && formatting.length > 0)
            style = Style.EMPTY.withFormatting(formatting);
        else
            style = Style.EMPTY;
        return style;
    }
}