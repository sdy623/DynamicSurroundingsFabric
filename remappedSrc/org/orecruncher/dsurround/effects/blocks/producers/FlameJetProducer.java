package org.orecruncher.dsurround.effects.blocks.producers;

import org.orecruncher.dsurround.effects.blocks.FlameJetEffect;
import org.orecruncher.dsurround.effects.IBlockEffect;
import org.orecruncher.dsurround.lib.random.IRandomizer;
import org.orecruncher.dsurround.lib.scripting.Script;

import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.block.SideShapeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;

import static org.orecruncher.dsurround.effects.BlockEffectUtils.IS_LAVA;

public class FlameJetProducer extends BlockEffectProducer {

    public FlameJetProducer(final Script chance, final Script conditions) {
        super(chance, conditions);
    }

    @Override
    protected boolean canTrigger(final World world, final BlockState state,
                                 final BlockPos pos, final IRandomizer random) {
        return world.getBlockState(pos.up()).isAir() && super.canTrigger(world, state, pos, random);
    }

    @Override
    public Optional<IBlockEffect> produceImpl(final World world, final BlockState state,
                                              final BlockPos pos, final IRandomizer random) {
        final int blockCount;
        final float spawnHeight;
        final boolean isNonLiquidBlock;

        if (!state.getFluidState().isEmpty()) {
            blockCount = countVerticalBlocks(world, pos, IS_LAVA, -1);
            spawnHeight = pos.getY() + state.getFluidState().getHeight() + 0.1F;
            isNonLiquidBlock = false;
        } else {
            final VoxelShape shape = state.getOutlineShape(world, pos);
            if (shape.isEmpty()) {
                return Optional.empty();
            }
            final double blockHeight = shape.getBoundingBox().maxY;
            spawnHeight = (float) (pos.getY() + blockHeight);
            isNonLiquidBlock = true;
            if (state.isSideSolid(world, pos, Direction.UP, SideShapeType.FULL)) {
                blockCount = 2;
            } else {
                blockCount = 1;
            }
        }

        if (blockCount > 0) {
            var effect = new FlameJetEffect(blockCount, world, pos.getX() + 0.5D, spawnHeight, pos.getZ() + 0.5D, isNonLiquidBlock);
            return Optional.of(effect);
        }

        return Optional.empty();
    }
}