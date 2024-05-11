package org.orecruncher.dsurround.lib.config;

import org.orecruncher.dsurround.lib.config.ConfigElement;
import org.orecruncher.dsurround.lib.config.ConfigOptions;
import org.orecruncher.dsurround.lib.config.ConfigurationData;
import org.orecruncher.dsurround.lib.gui.ColorPalette;

import java.util.Collection;
import java.util.function.BiFunction;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

public abstract class AbstractConfigScreenFactory implements BiFunction<MinecraftClient, Screen, Screen> {

    private static final Style STYLE_RESTART = Style.EMPTY.withColor(ColorPalette.RED);
    private static final Text CLIENT_RESTART_REQUIRED = Text.translatable("dsurround.config.tooltip.clientRestartRequired").fillStyle(STYLE_RESTART);
    private static final Text WORLD_RESTART_REQUIRED = Text.translatable("dsurround.config.tooltip.worldRestartRequired").fillStyle(STYLE_RESTART);
    private static final Text ASSET_RELOAD_REQUIRED = Text.translatable("dsurround.config.tooltip.assetReloadRequired").fillStyle(STYLE_RESTART);
    // Use a string with a single space as an empty line. Some config UI frameworks elide
    // Component.empty() entries and the tooltip logic uses empty lines as part of its
    // formatting.
    private static final Text EMPTY_LINE = Text.literal(" ");

    protected final ConfigOptions options;
    protected final ConfigurationData configData;

    public AbstractConfigScreenFactory(ConfigOptions options, final ConfigurationData config) {
        this.options = options;
        this.configData = config;
    }

    protected Collection<Text> generateToolTipCollection(ConfigElement.PropertyValue<?> pv) {
        var toolTipEntries = this.options.transformTooltip(pv.getTooltip(this.options.getTooltipStyle()));
        toolTipEntries.add(EMPTY_LINE);
        toolTipEntries.add(pv.getDefaultValueTooltip());

        if (pv instanceof ConfigElement.IRangeTooltip rt && rt.hasRange())
            toolTipEntries.add(rt.getRangeTooltip());

        if (pv.isClientRestartRequired()) {
            toolTipEntries.add(EMPTY_LINE);
            toolTipEntries.add(CLIENT_RESTART_REQUIRED);
        } else if (pv.isWorldRestartRequired()) {
            toolTipEntries.add(EMPTY_LINE);
            toolTipEntries.add(WORLD_RESTART_REQUIRED);
        } else if (pv.isAssetReloadRequired()) {
            toolTipEntries.add(EMPTY_LINE);
            toolTipEntries.add(ASSET_RELOAD_REQUIRED);
        }

        return toolTipEntries;
    }

    @Override
    public abstract Screen apply(MinecraftClient minecraft, Screen screen);
}
