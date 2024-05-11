package org.orecruncher.dsurround.lib.random;

import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.random.RandomSplitter;
import org.jetbrains.annotations.NotNull;

class MinecraftRandomizer implements IRandomizer {

    private final Random source;

    public MinecraftRandomizer() {
        this(Random.create());
    }

    public MinecraftRandomizer(Random source) {
        this.source = source;
    }

    @Override
    public @NotNull Random split() {
        return this.source.split();
    }

    @Override
    public @NotNull RandomSplitter nextSplitter() {
        return this.source.nextSplitter();
    }

    @Override
    public void setSeed(long seed) {
        this.source.setSeed(seed);
    }

    @Override
    public int nextInt() {
        return this.source.nextInt();
    }

    @Override
    public int nextInt(int bound) {
        return this.source.nextInt(bound);
    }

    @Override
    public boolean nextBoolean() {
        return this.source.nextBoolean();
    }

    @Override
    public double nextDouble() {
        return this.source.nextDouble();
    }

    @Override
    public float nextFloat() {
        return this.source.nextFloat();
    }

    @Override
    public double nextGaussian() {
        return this.source.nextGaussian();
    }

    @Override
    public long nextLong() {
        return this.source.nextLong();
    }
}
