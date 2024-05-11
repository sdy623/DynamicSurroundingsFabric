package org.orecruncher.dsurround.config.biome.biometraits;

import org.orecruncher.dsurround.config.BiomeTrait;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

public class BiomeMysticalAnalyzer implements IBiomeTraitAnalyzer {

    @Override
    public Collection<BiomeTrait> evaluate(Identifier id, Biome biome) {
        List<BiomeTrait> results = new ArrayList<>();

        var path = id.getPath();

        if (path.contains("dark") || path.contains("ominous"))
            results.add(BiomeTrait.SPOOKY);

        if (path.contains("magic") || path.contains("magik"))
            results.add(BiomeTrait.MAGICAL);

        return results;
    }
}
