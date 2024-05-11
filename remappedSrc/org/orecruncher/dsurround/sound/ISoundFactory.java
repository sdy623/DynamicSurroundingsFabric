package org.orecruncher.dsurround.sound;

import net.minecraft.client.sound.EntityTrackingSoundInstance;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.Entity;
import net.minecraft.sound.MusicSound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.orecruncher.dsurround.lib.math.MathStuff;

public interface ISoundFactory {

    /**
     * Gets the resource location of this sound factory instance.
     */
    Identifier getLocation();

    /**
     * Creates an attenuated sound instance that is attached to the entity and will move as
     * the entity moves.  The sound instance is not repeated.
     */
    EntityTrackingSoundInstance attachToEntity(Entity entity);

    /**
     * Creates a sound instance that has no attenuation and that will repeat without any
     * delays.
     */
    BackgroundSoundLoop createBackgroundSoundLoop();

    /**
     * Creates a sound instance that has attenuation at the specified location.  The sound will
     * repeat without any delays.
     */
    BackgroundSoundLoop createBackgroundSoundLoopAt(BlockPos pos);

    /**
     * Creates an attenuated sound instance that will play at a location defined by the range
     * parameters around the specified entity player.  This sound instance will be tied to
     * a fixed location.  Mimics a biomes "mood" sound as defined in Minecraft.
     */
    default PositionedSoundInstance createAsMood(Entity entity, int minRange, int maxRange) {
        var offset = MathStuff.randomPoint(minRange, maxRange);
        var position = entity.getEyePos().add(offset);
        return this.createAtLocation(position, 1F);
    }

    /**
     * Creates a non-attenuated sound instance that will not repeat. Mimics the sound profile of
     * a biome "additional" sound as defined in Minecraft.
     */
    PositionedSoundInstance createAsAdditional();

    /**
     * Creates an attenuated sound instance at the center of the specified block location. The
     * sound instance is not repeated.
     */
    default PositionedSoundInstance createAtLocation(BlockPos pos) {
        return this.createAtLocation(Vec3d.ofCenter(pos), 1F);
    }

    /**
     * Creates an attenuated sound instance at the center of the specified block location. The provided factor
     * scales the volume of the sound. The sound instance is not repeated.
     */
    default PositionedSoundInstance createAtLocation(BlockPos pos, float volumeScale) {
        return this.createAtLocation(Vec3d.ofCenter(pos), volumeScale);
    }

    /**
     * Creates a sound instance at the eye location of the specified entity.  This sound instance is not attached
     * to the entity and will be at a fixed location.  The properties of the sound instance are defined by the
     * underlying factory settings.
     */
    default PositionedSoundInstance createAtLocation(Entity entity) {
        return this.createAtLocation(entity.getEyePos(), 1F);
    }

    /**
     * Creates a sound instance at the specified location. This sound instance is not attached. The properties
     * of the sound instance are defined by the underlying factory settings.
     */
    default PositionedSoundInstance createAtLocation(Vec3d position) {
        return this.createAtLocation(position, 1F);
    }

    /**
     * Creates a sound instance at the specified location. The provided factor scales the sound volume.
     * This sound instance is not attached.  The properties of the sound instance are defined by the underlying
     * factory settings.
     */
    PositionedSoundInstance createAtLocation(Vec3d position, float volumeScale);

    /**
     * Creates a Music instance to be used with Minecraft's music manager
     */
    MusicSound createAsMusic();

}
