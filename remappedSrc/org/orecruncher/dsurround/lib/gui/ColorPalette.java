package org.orecruncher.dsurround.lib.gui;

import I;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;

@SuppressWarnings("unused")
public final class ColorPalette {
    // Branding colors
    public static final TextColor CURSEFORGE = of("#f16436");
    public static final TextColor MODRINTH = of("#1bd96a");

    // Minecraft colors mapped to codes
    public static final TextColor MC_BLACK = of(Formatting.BLACK);
    public static final TextColor MC_DARKBLUE = of(Formatting.DARK_BLUE);
    public static final TextColor MC_DARKGREEN = of(Formatting.DARK_GREEN);
    public static final TextColor MC_DARKAQUA = of(Formatting.DARK_AQUA);
    public static final TextColor MC_DARKRED = of(Formatting.DARK_RED);
    public static final TextColor MC_DARKPURPLE = of(Formatting.DARK_PURPLE);
    public static final TextColor MC_GOLD = of(Formatting.GOLD);
    public static final TextColor MC_GRAY = of(Formatting.GRAY);
    public static final TextColor MC_DARKGRAY = of(Formatting.DARK_GRAY);
    public static final TextColor MC_BLUE = of(Formatting.BLUE);
    public static final TextColor MC_GREEN = of(Formatting.GREEN);
    public static final TextColor MC_AQUA = of(Formatting.AQUA);
    public static final TextColor MC_RED = of(Formatting.RED);
    public static final TextColor MC_LIGHTPURPLE = of(Formatting.LIGHT_PURPLE);
    public static final TextColor MC_YELLOW = of(Formatting.YELLOW);
    public static final TextColor MC_WHITE = of(Formatting.WHITE);

    public static final TextColor CRIMSON = of("#8D230F");

    public static final TextColor RED = of(255, 0, 0);
    public static final TextColor ORANGE = of(255, 127, 0);
    public static final TextColor YELLOW = of(255, 255, 0);
    public static final TextColor LGREEN = of(127, 255, 0);
    public static final TextColor GREEN = of(0, 255, 0);
    public static final TextColor TURQUOISE = of(0, 255, 127);
    public static final TextColor CYAN = of(0, 255, 255);
    public static final TextColor AQUAMARINE = of(127,255,212);
    public static final TextColor BLUE = of(0, 0, 255);
    public static final TextColor VIOLET = of(127, 0, 255);
    public static final TextColor MAGENTA = of(255, 0, 255);
    public static final TextColor RASPBERRY = of(255, 0, 127);
    public static final TextColor BLACK = of(0, 0, 0);
    public static final TextColor WHITE = of(255, 255, 255);
    public static final TextColor PURPLE = of(80, 0, 80);
    public static final TextColor INDIGO = of(75, 0, 130);
    public static final TextColor NAVY = of(0, 0, 128);
    public static final TextColor TAN = of(210, 180, 140);
    public static final TextColor GOLD = of(255, 215, 0);
    public static final TextColor GRAY = of(128, 128, 128);
    public static final TextColor LGRAY = of(192, 192, 192);
    public static final TextColor SLATEGRAY = of(112, 128, 144);
    public static final TextColor DARKSLATEGRAY = of(47, 79, 79);
    public static final TextColor SILVER_SAND = of(191,193,194);
    public static final TextColor SUN_GLOW = of(255,204,51);
    public static final TextColor CORN_FLOWER_BLUE = of(100,149,237);
    public static final TextColor APRICOT = of(251,206,177);
    public static final TextColor KEY_LIME = of(232,244,140);
    public static final TextColor BRIGHT_CERULEAN = of(29,172,214);
    public static final TextColor BURNT_UMBER = of(110, 38, 14);
    public static final TextColor GOLDENROD = of(218, 165, 32);
    public static final TextColor WHEAT = of(245, 222, 179);
    public static final TextColor PUMPKIN_ORANGE = of(255, 117, 24);
    public static final TextColor DESERT = of(250, 213, 165);
    public static final TextColor CORNSILK = of(255, 248, 220);
    public static final TextColor BRASS = of(225, 193, 110);
    public static final TextColor ECRU = of(194, 178, 128);
    public static final TextColor SEASHELL = of(255, 245, 238);
    public static final TextColor ORCHID = of(218,112,214);
    public static final TextColor PALE_BROWN = of(152,118,84);
    public static final TextColor DARK_VIOLET = of(148,0,211);
    public static final TextColor ANTIQUE_WHITE = of(250,235,215);
    public static final TextColor PEARLY_PURPLE = of(183,104,162);
    public static final TextColor FRESH_AIR = of(166,231,255);

    public static int getRed(int rgb) {
        return (rgb >> 16) & 0xFF;
    }

    public static int getGreen(int rgb) {
        return (rgb >> 8) & 0xFF;
    }

    public static int getBlue(int rgb) {
        return rgb & 0xFF;
    }

    private static TextColor of(Formatting formatColor) {
        return TextColor.fromFormatting(formatColor);
    }

    private static TextColor of(String formatColor) {
        return TextColor.parse(formatColor).getOrThrow(false, (msg)-> {});
    }

    private static TextColor of(int red, int green, int blue) {
        return TextColor.fromRgb(toRGB(red, green, blue));
    }

    static int toRGB(int red, int green, int blue) {
        return ((red & 0xFF) << 16) |
                ((green & 0xFF) << 8)  |
                ((blue & 0xFF));
    }

    public static TextColor lerp(float scale, TextColor start, TextColor end) {
        var startRed = getRed(start.getRgb());
        var startGreen = getGreen(start.getRgb());
        var startBlue = getBlue(start.getRgb());
        var endRed = getRed(end.getRgb());
        var endGreen = getGreen(end.getRgb());
        var endBlue = getBlue(end.getRgb());

        var red = (int)MathHelper.lerp(scale, startRed, endRed);
        var green = (int)MathHelper.lerp(scale, startGreen, endGreen);
        var blue = (int)MathHelper.lerp(scale, startBlue, endBlue);

        return of(red, green, blue);
    }
}