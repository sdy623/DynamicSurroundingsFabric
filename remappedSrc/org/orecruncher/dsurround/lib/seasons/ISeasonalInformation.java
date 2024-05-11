package org.orecruncher.dsurround.lib.seasons;

import I;
import java.util.Optional;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public interface ISeasonalInformation {

    /**
     * Name of the seasonal provider
     */
    String getProviderName();

    /**
     * Gets the name of the current season, if any
     */
    Optional<String> getCurrentSeason(World world);

    /**
     * Gets the translated season name from the provider resources.
     */
    Optional<String> getCurrentSeasonTranslated(World world);

    /**
     * Indicates if the current season is considered Spring.
     */
    default boolean isSpring(World world) {
        return true;
    }

    /**
     * Indicates if the current season is considered Summer.
     */
    default boolean isSummer(World world) {
        return false;
    }

    /**
     * Indicates if the current season is considered Autumn/Fall.
     */
    default boolean isAutumn(World world) {
        return false;
    }

    /**
     * Indicates if the current season is considered Winter.
     */
    default boolean isWinter(World world) {
        return false;
    }

    /**
     * Gets the temperature at the specified block location taking into account any seasonal variance.
     */
    float getTemperature(World world, BlockPos blockPos);

    /**
     * Indicates whether the temperature at the given position is considered cold. For example, if the temp
     * is cold, the frost breath effect can be produced.
     */
    default boolean isColdTemperature(World world, BlockPos blockPos) {
        return this.getTemperature(world, blockPos) < 0.2F;
    }

    /**
     * Indicates whether the temperature at the given position is considered cold enough for snow.
     */
    default boolean isSnowTemperature(World world, BlockPos blockPos) {
        return this.getTemperature(world, blockPos) < 0.15F;
    }

    /**
     * Gets the Y on the XZ plane at which precipitation will strike.
     */
    default int getPrecipitationHeight(World world, BlockPos pos) {
        return world.getTopY(Heightmap.Type.MOTION_BLOCKING, pos.getX(), pos.getZ());
    }

    /**
     * Gets the possible precipitation that can occur in the biome at the specified position.
     */
    default Biome.Precipitation getPrecipitationAt(World world, BlockPos blockPos) {
        return world.getBiome(blockPos).value().getPrecipitation(blockPos);
    }

    /**
     * Gets the active precipitation occurring at the specified position.
     */
    default Biome.Precipitation getActivePrecipitation(World world, BlockPos pos) {
        if (!world.isRaining()) {
            // Not currently raining
            return Biome.Precipitation.NONE;
        }

        // If the biome has no rain...
        if (this.getPrecipitationAt(world, pos) == Biome.Precipitation.NONE)
            return Biome.Precipitation.NONE;

        // Is there a block above that is blocking the rainfall?
        var p = this.getPrecipitationHeight(world, pos);
        if (p > pos.getY()) {
            return Biome.Precipitation.NONE;
        }

        // Use the temperature of the biome to get whether it is raining or snowing
        return this.isSnowTemperature(world, pos) ? Biome.Precipitation.SNOW : Biome.Precipitation.RAIN;
    }
}
