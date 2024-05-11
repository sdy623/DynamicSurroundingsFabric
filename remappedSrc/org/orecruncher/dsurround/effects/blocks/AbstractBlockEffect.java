package org.orecruncher.dsurround.effects.blocks;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.orecruncher.dsurround.config.libraries.ISoundLibrary;
import org.orecruncher.dsurround.effects.IBlockEffect;
import org.orecruncher.dsurround.lib.GameUtils;
import org.orecruncher.dsurround.lib.di.ContainerManager;
import org.orecruncher.dsurround.lib.random.IRandomizer;
import org.orecruncher.dsurround.lib.random.Randomizer;
import org.orecruncher.dsurround.mixins.core.MixinParticleManager;
import org.orecruncher.dsurround.sound.IAudioPlayer;

import java.util.Optional;

public abstract class AbstractBlockEffect implements IBlockEffect {

    protected static final IRandomizer RANDOM = Randomizer.current();
    protected static final ISoundLibrary SOUND_LIBRARY = ContainerManager.resolve(ISoundLibrary.class);
    protected static final IAudioPlayer AUDIO_PLAYER = ContainerManager.resolve(IAudioPlayer.class);


    protected final World world;
    protected final double posX;
    protected final double posY;
    protected final double posZ;
    protected final BlockPos position;
    private boolean removed = false;

    protected AbstractBlockEffect(final World worldIn, final double posXIn, final double posYIn, final double posZIn) {
        this.world = worldIn;
        this.posX = posXIn;
        this.posY = posYIn;
        this.posZ = posZIn;
        this.position = BlockPos.ofFloored(posXIn, posYIn, posZIn);
    }

    public BlockPos getPos() {
        return this.position;
    }

    /**
     * Adds a particle to the Minecraft particle system
     */
    public void addParticle(final Particle particle) {
        GameUtils.getParticleManager().addParticle(particle);
    }

    /**
     * Creates a particle effect but does not queue.  Allows for the particle to be manipulated prior to handing
     * it to the particle manager.
     */
    public <T extends ParticleEffect> Optional<Particle> createParticle(T parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        var pm = GameUtils.getParticleManager();
        var t = (MixinParticleManager) pm;
        return Optional.ofNullable(t.dsurround_createParticle(parameters, x, y, z, velocityX, velocityY, velocityZ));
    }

    public boolean isDone() {
        return this.removed;
    }

    public void remove() {
        this.removed = true;
        cleanUp();
    }

    /**
     * Override to provide the necessary signal so the system will start the process of removal.
     */
    public abstract boolean shouldRemove();

    /**
     * Perform any cleanup activities prior to being removed from tracking collections.
     */
    protected void cleanUp() {

    }

    /**
     * Update the state of the effect.
     */
    public final void tick() {
        if (shouldRemove()) {
            remove();
            return;
        }

        // Let the system mull over what it wants to do
        think();

        if (!isDone())
            // Update any sounds
            soundUpdate();
    }

    /**
     * Override to provide sound for the particle effect. Will be invoked whenever
     * the particle system is updated by the particle manager.
     */
    protected void soundUpdate() {

    }

    /**
     * Override to provide some sort of intelligence to the system. The logic can do
     * things like add new particles, remove old ones, update positions, etc. Will
     * be invoked during the systems onUpdate() call.
     */
    public abstract void think();

}