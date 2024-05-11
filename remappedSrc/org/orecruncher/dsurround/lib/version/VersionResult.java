package org.orecruncher.dsurround.lib.version;

import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.orecruncher.dsurround.lib.gui.ColorPalette;

public record VersionResult(String version, String modId, String displayName, String downloadLocation, String downloadLocationModrinth, String releaseNotesLink) {

    public Text getChatText() {
        var space = Text.literal(" ");
        var openBracket = Text.literal("[").withColor(ColorPalette.SILVER_SAND.getRgb());
        var closeBracket = Text.literal("]").withColor(ColorPalette.SILVER_SAND.getRgb());

        var downloadPage = Text.translatable(this.modId + ".newversion.downloadpage")
                .withColor(ColorPalette.CORN_FLOWER_BLUE.getRgb());
        var downloadHoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, downloadPage);

        var releaseNotesPage = Text.translatable(this.modId + ".newversion.releasenotespage")
                .withColor(ColorPalette.CORN_FLOWER_BLUE.getRgb());
        var releaseNotesHoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, releaseNotesPage);

        var downloadStyleCurse = Style.EMPTY
                .withHoverEvent(downloadHoverEvent)
                .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, this.downloadLocation));
        var curseHover = Text.translatable(this.modId + ".newversion.curseforge")
                .withColor(ColorPalette.CURSEFORGE.getRgb())
                .fillStyle(downloadStyleCurse);

        var releaseNotesStyle = Style.EMPTY
                .withHoverEvent(releaseNotesHoverEvent)
                .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, this.releaseNotesLink));
        var releaseNotesHover = Text.translatable(this.modId + ".newversion.releasenotes")
                .withColor(ColorPalette.BRIGHT_CERULEAN.getRgb())
                .fillStyle(releaseNotesStyle);

        var downloadStyleModrinth = Style.EMPTY
                .withHoverEvent(downloadHoverEvent)
                .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, this.downloadLocationModrinth));
        var modrinthHover = Text.translatable(this.modId + ".newversion.modrinth")
                .withColor(ColorPalette.MODRINTH.getRgb())
                .fillStyle(downloadStyleModrinth);

        var modDisplayNameAndVersion = Text.literal(this.displayName)
                .append(" v").append(this.version)
                .withColor(ColorPalette.SUN_GLOW.getRgb());

        return Text.translatable(this.modId + ".newversion.update")
                .withColor(ColorPalette.AQUAMARINE.getRgb())
                .append(modDisplayNameAndVersion)
                .append(space)
                .append(openBracket)
                .append(releaseNotesHover)
                .append(closeBracket)
                .append(space)
                .append(openBracket)
                .append(curseHover)
                .append(closeBracket)
                .append(space)
                .append(openBracket)
                .append(modrinthHover)
                .append(closeBracket);
    }
}
