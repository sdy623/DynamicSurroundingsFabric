package org.orecruncher.dsurround.lib.config;

import joptsimple.internal.Strings;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.orecruncher.dsurround.lib.Localization;
import org.orecruncher.dsurround.lib.gui.GuiHelpers;

import java.util.ArrayList;
import java.util.Collection;

public class ConfigOptions {

    private String translationRoot = Strings.EMPTY;
    private Style titleStyle = Style.EMPTY;
    private Style propertyGroupStyle = Style.EMPTY;
    private Style propertyStyle = Style.EMPTY;
    private Style tooltipStyle = Style.EMPTY;
    private boolean wrapToolTip = false;
    private int toolTipWidth = 300;

    public ConfigOptions() {

    }

    public ConfigOptions setTitleStyle(Style style) {
        this.titleStyle = style;
        return this;
    }

    public ConfigOptions setPropertyGroupStyle(Style style) {
        this.propertyGroupStyle = style;
        return this;
    }

    public ConfigOptions setPropertyStyle(Style style) {
        this.propertyStyle = style;
        return this;
    }

    public ConfigOptions setTooltipStyle(Style style) {
        this.tooltipStyle = style;
        return this;
    }

    public ConfigOptions setTooltipWidth(int width) {
        this.toolTipWidth = width;
        return this;
    }

    public ConfigOptions wrapToolTip(boolean flag) {
        this.wrapToolTip = flag;
        return this;
    }

    public Style getTooltipStyle() {
        return this.tooltipStyle;
    }

    public ConfigOptions setTranslationRoot(String root) {
        this.translationRoot = root;
        return this;
    }

    public Text transformTitle() {
        var txt = Localization.load(this.translationRoot + ".title");
        return Text.literal(txt).fillStyle(this.titleStyle);
    }

    public Text transformPropertyGroup(String langKey) {
        var txt = Localization.load(langKey);
        return Text.literal(txt).fillStyle(this.propertyGroupStyle);
    }

    public Text transformProperty(String langKey) {
        var txt = Localization.load(langKey);
        return Text.literal(txt).fillStyle(this.propertyStyle);
    }

    public Collection<Text> transformTooltip(Text tooltip) {
        if (this.wrapToolTip)
            return GuiHelpers.getTrimmedTextCollection(tooltip, toolTipWidth, this.tooltipStyle);
        var result = new ArrayList<Text>();
        result.add(tooltip);
        return result;
    }
}