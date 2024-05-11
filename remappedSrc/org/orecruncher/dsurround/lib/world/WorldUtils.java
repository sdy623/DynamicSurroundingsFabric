package org.orecruncher.dsurround.lib.world;

import org.orecruncher.dsurround.mixins.core.MixinClientWorldProperties;
import org.orecruncher.dsurround.mixinutils.IClientWorld;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.WorldProperties;

public class WorldUtils {
    public static boolean isSuperFlat(final World world) {
        final WorldProperties info = world.getLevelProperties();
        return info instanceof MixinClientWorldProperties && ((MixinClientWorldProperties) info).dsurround_isFlatWorld();
    }

    public static BlockPos getTopSolidOrLiquidBlock(final World world, final BlockPos pos) {
        return world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, pos);
    }

    public static int getPrecipitationHeight(final World world, final BlockPos pos) {
        return world.getTopY(Heightmap.Type.MOTION_BLOCKING, pos.getX(), pos.getZ());
    }

    public static List<BlockEntity> getLoadedBlockEntities(World world, Predicate<BlockEntity> predicate) {
        var accessor = (IClientWorld) world;
        return accessor.dsurround_getLoadedChunks()
                .flatMap(chunk -> chunk.getBlockEntities().values().stream())
                .filter(predicate)
                .collect(Collectors.toList());
    }

    public static boolean isChunkLoaded(World world, BlockPos pos) {
        return world.canSetBlock(pos);
    }
}
