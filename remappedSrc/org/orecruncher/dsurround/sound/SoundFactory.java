package org.orecruncher.dsurround.sound;

import com.google.common.base.MoreObjects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;
import org.orecruncher.dsurround.lib.IdentityUtils;
import org.orecruncher.dsurround.lib.random.Randomizer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.sound.EntityTrackingSoundInstance;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.Entity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.floatprovider.ConstantFloatProvider;
import net.minecraft.util.math.floatprovider.FloatProvider;

import static org.orecruncher.dsurround.sound.SoundCodecHelpers.SOUND_PROPERTY_RANGE;

public record SoundFactory(
        Optional<Identifier> location,
        SoundEvent soundEvent,
        FloatProvider volume,
        FloatProvider pitch,
        SoundCategory category,
        boolean isRepeatable,
        int repeatDelay,
        boolean global,
        SoundInstance.AttenuationType attenuation,
        MusicSettings musicSettings) implements Comparable<ISoundFactory>, ISoundFactory {

    public static final Codec<SoundFactory> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(
                    IdentityUtils.CODEC.optionalFieldOf("location").forGetter(SoundFactory::location),
                    SoundCodecHelpers.SOUND_EVENT_CODEC.fieldOf("soundEvent").forGetter(SoundFactory::soundEvent),
                    SOUND_PROPERTY_RANGE.optionalFieldOf("volume", ConstantFloatProvider.create(1F)).forGetter(SoundFactory::volume),
                    SOUND_PROPERTY_RANGE.optionalFieldOf("pitch", ConstantFloatProvider.create(1F)).forGetter(SoundFactory::pitch),
                    SoundCodecHelpers.SOUND_CATEGORY_CODEC.optionalFieldOf("category", SoundCategory.AMBIENT).forGetter(SoundFactory::category),
                    Codec.BOOL.optionalFieldOf("isRepeatable", false).forGetter(SoundFactory::isRepeatable),
                    Codec.INT.optionalFieldOf("repeatDelay", 0).forGetter(SoundFactory::repeatDelay),
                    Codec.BOOL.optionalFieldOf("global", false).forGetter(SoundFactory::global),
                    SoundCodecHelpers.ATTENUATION_CODEC.optionalFieldOf("attenuation", SoundInstance.AttenuationType.LINEAR).forGetter(SoundFactory::attenuation),
                    MusicSettings.CODEC.optionalFieldOf("music", MusicSettings.DEFAULT).forGetter(SoundFactory::musicSettings)
            ).apply(instance, SoundFactory::new));

    private static final Map<SoundEvent, MusicSound> MUSIC_MAP = new HashMap<>();

    @Override
    public Identifier getLocation() {
        return this.location.orElse(this.soundEvent.getId());
    }

    @Override
    public BackgroundSoundLoop createBackgroundSoundLoop() {
        return new BackgroundSoundLoop(this.soundEvent)
                .setVolume(this.getVolume())
                .setPitch(this.getPitch());
    }

    @Override
    public BackgroundSoundLoop createBackgroundSoundLoopAt(BlockPos pos) {
        return new BackgroundSoundLoop(this.soundEvent, pos)
                .setVolume(this.getVolume())
                .setPitch(this.getPitch());
    }

    @Override
    public PositionedSoundInstance createAsAdditional() {
        return new PositionedSoundInstance(
                this.soundEvent.getId(),
                this.category,
                this.getVolume(),
                this.getPitch(),
                Randomizer.current(),
                this.isRepeatable,
                this.repeatDelay,
                this.attenuation,
                0.0D,
                0.0D,
                0.0D,
                true);
    }

    @Override
    public EntityTrackingSoundInstance attachToEntity(Entity entity) {
        return new EntityTrackingSoundInstance(
                this.soundEvent,
                this.category,
                this.getVolume(),
                this.getPitch(),
                entity,
                Randomizer.current().nextLong()
        );
    }

    @Override
    public PositionedSoundInstance createAtLocation(Vec3d position, float volumeScale) {
        return new PositionedSoundInstance(
                this.soundEvent.getId(),
                this.category,
                this.getVolume() * volumeScale,
                this.getPitch(),
                Randomizer.current(),
                this.isRepeatable,
                this.repeatDelay,
                this.attenuation,
                position.getX(),
                position.getY(),
                position.getZ(),
                this.global);
    }

    @Override
    public MusicSound createAsMusic() {
        return MUSIC_MAP.computeIfAbsent(this.soundEvent, key -> {
            var holder = RegistryEntry.of(key);
            return new MusicSound(holder, this.musicSettings.minDelay, this.musicSettings.maxDelay, this.musicSettings.replaceCurrentMusic);
        });
    }

    private float getVolume() {
        return this.volume.get(Randomizer.current());
    }

    private float getPitch() {
        return this.pitch.get(Randomizer.current());
    }

    @Override
    public int hashCode() {
        return this.getLocation().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        return obj instanceof SoundFactory f && this.getLocation().equals(f.getLocation());
    }

    @Override
    public int compareTo(@NotNull ISoundFactory o) {
        return this.getLocation().compareTo(o.getLocation());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("location", this.getLocation())
                .add("soundEvent", this.soundEvent.getId())
                .toString();
    }

    static ISoundFactory from(SoundFactoryBuilder builder) {
        return new SoundFactory(
                Optional.empty(),
                builder.soundEvent,
                builder.volume,
                builder.pitch,
                builder.category,
                builder.isRepeatable,
                builder.repeatDelay,
                builder.global,
                builder.attenuation,
                new MusicSettings(builder.musicMinDelay, builder.musicMaxDelay, builder.musicReplaceMusic));
    }

    public record MusicSettings(int minDelay, int maxDelay, boolean replaceCurrentMusic) {
        public static final MusicSettings DEFAULT = new MusicSettings(6000, 24000, false);

        public static final Codec<MusicSettings> CODEC = RecordCodecBuilder.create((instance) ->
                instance.group(
                        Codec.INT.optionalFieldOf("min_delay", DEFAULT.minDelay()).forGetter(MusicSettings::minDelay),
                        Codec.INT.optionalFieldOf("max_delay", DEFAULT.maxDelay()).forGetter(MusicSettings::maxDelay),
                        Codec.BOOL.optionalFieldOf("replace_current_music", DEFAULT.replaceCurrentMusic()).forGetter(MusicSettings::replaceCurrentMusic)
                ).apply(instance, MusicSettings::new));
    }
}
