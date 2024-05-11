package org.orecruncher.dsurround.processing.accents;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.Precipitation;
import org.orecruncher.dsurround.Configuration;
import org.orecruncher.dsurround.Constants;
import org.orecruncher.dsurround.lib.collections.ObjectArray;
import org.orecruncher.dsurround.sound.ISoundFactory;
import org.orecruncher.dsurround.tags.BlockEffectTags;

class WaterySurfaceAccent implements IFootstepAccentProvider {

    private static final Identifier WETSURFACE_FACTORY = new Identifier(Constants.MOD_ID, "footstep/wetsurface");

    private final Configuration config;

    WaterySurfaceAccent(Configuration config) {
        this.config = config;
    }

    @Override
    public boolean isEnabled() {
        return this.config.footstepAccents.enableWetSurfaceAccents;
    }

    @Override
    public void collect(LivingEntity entity, BlockPos pos, BlockState state, boolean isWaterLogged, ObjectArray<ISoundFactory> acoustics) {

        boolean addAcoustic = isWaterLogged;

        if (!addAcoustic)
            addAcoustic = FootstepAccents.TAG_LIBRARY.is(BlockEffectTags.WATERY_STEP, state);

        // Check the block above because it may be flagged as having a wet effect, like a lily pad.
        if (!addAcoustic) {
            var world = entity.method_48926();
            var up = pos.up();
            addAcoustic = FootstepAccents.TAG_LIBRARY.is(BlockEffectTags.WATERY_STEP, world.getBlockState(up));

            if (!addAcoustic && world.hasRain(up)) {
                // Get the precipitation type at the location
                var precipitation = world.getBiome(up).value().getPrecipitation(up);
                addAcoustic = precipitation == Biome.Precipitation.RAIN;
            }
        }

        if (addAcoustic)
            SOUND_LIBRARY.getSoundFactory(WETSURFACE_FACTORY)
                    .ifPresent(acoustics::add);
    }
}