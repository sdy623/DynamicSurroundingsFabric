package org.orecruncher.dsurround.lib.seasons.compat;

import org.orecruncher.dsurround.config.libraries.IReloadEvent;
import org.orecruncher.dsurround.lib.resources.ResourceUtilities;
import org.orecruncher.dsurround.mixinutils.IBiomeExtended;

import java.util.Optional;
import net.minecraft.util.Language;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class VanillaSeasons extends AbstractSeasonProvider {

    private String currentSeasonString;

    public VanillaSeasons() {
        super("Vanilla");
    }

    @Override
    public Optional<String> getCurrentSeason(World world) {
        return Optional.of(this.currentSeasonString);
    }

    @Override
    public Optional<String> getCurrentSeasonTranslated(World world) {
        return Optional.of(this.currentSeasonString);
    }

    @Override
    public float getTemperature(World world, BlockPos blockPos) {
        var biome = world.getBiome(blockPos).value();
        return ((IBiomeExtended)(Object)biome).dsurround_getTemperature(blockPos);
    }

    @Override
    protected void reloadResources(ResourceUtilities resourceUtilities, IReloadEvent.Scope scope) {
        if (scope == IReloadEvent.Scope.TAGS)
            return;
        this.currentSeasonString = Language.getInstance().get("dsurround.text.seasons.spring");
    }
}
