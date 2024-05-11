package org.orecruncher.dsurround.mixinutils;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import org.orecruncher.dsurround.config.biome.BiomeInfo;

public interface IBiomeExtended {

    BiomeInfo dsurround_getInfo();

    void dsurround_setInfo(BiomeInfo info);

    float dsurround_getTemperature(BlockPos pos);

    Biome.Weather dsurround_getWeather();

    BiomeEffects dsurround_getSpecialEffects();

}
