package org.orecruncher.dsurround.effects;


import org.orecruncher.dsurround.lib.random.IRandomizer;

import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@FunctionalInterface
public interface IBlockEffectProducer {
    Optional<IBlockEffect> produce(World world, BlockState state, BlockPos pos, IRandomizer rand);
}
