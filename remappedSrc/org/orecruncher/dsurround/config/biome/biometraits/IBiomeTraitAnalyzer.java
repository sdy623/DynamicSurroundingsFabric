package org.orecruncher.dsurround.config.biome.biometraits;

import org.orecruncher.dsurround.config.BiomeTrait;

import java.util.Collection;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

public interface IBiomeTraitAnalyzer {
    Collection<BiomeTrait> evaluate(Identifier id, Biome biome);
}
