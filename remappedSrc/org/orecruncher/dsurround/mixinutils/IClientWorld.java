package org.orecruncher.dsurround.mixinutils;

import java.util.stream.Stream;
import net.minecraft.world.chunk.WorldChunk;

public interface IClientWorld {
    Stream<WorldChunk> dsurround_getLoadedChunks();
}
