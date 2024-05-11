package org.orecruncher.dsurround.mixins.core;

import org.orecruncher.dsurround.mixinutils.IClientWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.stream.Stream;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.client.world.ClientChunkManager.ClientChunkMap;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.chunk.WorldChunk;

@Mixin(ClientWorld.class)
public class MixinClientWorld implements IClientWorld {

    @Final
    @Shadow
    private ClientChunkManager chunkSource;

    @Unique
    public Stream<WorldChunk> dsurround_getLoadedChunks() {
        var x = (MixinClientChunkManager) this.chunkSource;
        var chunkMap = x.dsurround_getClientChunkMap();
        var chunks = ((MixinClientChunkMap)((Object)chunkMap)).dsurround_getChunks();

        List<WorldChunk> resultChunks = new ArrayList<>();
        for (int i = 0; i < chunks.length(); i++) {
            var chunk = chunks.get(i);
            if (chunk != null)
                resultChunks.add(chunk);
        }

        return resultChunks.stream();
    }
}
