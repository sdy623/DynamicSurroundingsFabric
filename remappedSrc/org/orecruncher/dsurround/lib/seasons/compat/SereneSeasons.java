package org.orecruncher.dsurround.lib.seasons.compat;

import org.orecruncher.dsurround.config.libraries.IReloadEvent;
import org.orecruncher.dsurround.lib.resources.ResourceUtilities;
import sereneseasons.api.season.ISeasonState;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.Season.SubSeason;
import sereneseasons.api.season.Season.TropicalSeason;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.season.SeasonHooks;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Language;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class SereneSeasons extends AbstractSeasonProvider {

    private final Map<Season.SubSeason, String> subSeasonStringMap = new EnumMap<>(Season.SubSeason.class);
    private final Map<Season.TropicalSeason, String> tropicalSeasonMap = new EnumMap<>(Season.TropicalSeason.class);

    public SereneSeasons() {
        super("Serene Seasons");
    }

    @Override
    public Optional<String> getCurrentSeason(World world) {
        var helper = SeasonHelper.getSeasonState(world);
        var subSeason = helper.getSubSeason();
        return Optional.of(subSeason.toString());
    }

    @Override
    public Optional<String> getCurrentSeasonTranslated(World world) {
        var helper = SeasonHelper.getSeasonState(world);
        var subSeason = this.subSeasonStringMap.get(helper.getSubSeason());
        var tropicalSeason = this.tropicalSeasonMap.get(helper.getTropicalSeason());
        var result = "%s (%s)".formatted(subSeason, tropicalSeason);
        return Optional.of(result);
    }

    public boolean isSpring(World world) {
        var helper = SeasonHelper.getSeasonState(world);
        return helper.getSeason() == Season.SPRING;
    }

    public  boolean isSummer(World world) {
        var helper = SeasonHelper.getSeasonState(world);
        return helper.getSeason() == Season.SUMMER;
    }

    public  boolean isAutumn(World world) {
        var helper = SeasonHelper.getSeasonState(world);
        return helper.getSeason() == Season.AUTUMN;
    }

    public  boolean isWinter(World world) {
        var helper = SeasonHelper.getSeasonState(world);
        return helper.getSeason() == Season.WINTER;
    }

    @Override
    public Biome.Precipitation getPrecipitationAt(World world, BlockPos blockPos) {
        var biome = world.getBiome(blockPos);
        return SeasonHooks.getPrecipitationAtSeasonal(world, biome, blockPos);
    }

    @Override
    public float getTemperature(World world, BlockPos blockPos) {
        var biome = world.getBiome(blockPos);
        return SeasonHooks.getBiomeTemperature(world, biome, blockPos);
    }

    @Override
    protected void reloadResources(ResourceUtilities resourceUtilities, IReloadEvent.Scope scope) {
        if (scope == IReloadEvent.Scope.TAGS)
            return;
        for (var subSeason : Season.SubSeason.values())
            this.subSeasonStringMap.put(subSeason, Language.getInstance().get("desc.sereneseasons." + subSeason.toString().toLowerCase()));
        for (var tropicalSeason : Season.TropicalSeason.values())
            this.tropicalSeasonMap.put(tropicalSeason, Language.getInstance().get("desc.sereneseasons." + tropicalSeason.toString().toLowerCase()));
    }
}
