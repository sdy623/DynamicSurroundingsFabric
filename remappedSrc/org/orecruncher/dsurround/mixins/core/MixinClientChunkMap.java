package org.orecruncher.dsurround.mixins.core;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.concurrent.atomic.AtomicReferenceArray;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.world.chunk.WorldChunk;

@Mixin(ClientChunkManager.ClientChunkMap.class)
public interface MixinClientChunkMap {

    @Accessor("chunks")
    AtomicReferenceArray<WorldChunk> dsurround_getChunks();
}
