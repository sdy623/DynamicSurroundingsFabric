package org.orecruncher.dsurround.config.libraries;

import org.orecruncher.dsurround.config.IndividualSoundConfigEntry;
import org.orecruncher.dsurround.sound.ISoundFactory;
import org.orecruncher.dsurround.sound.SoundMetadata;

import java.util.Collection;
import java.util.Optional;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public interface ISoundLibrary extends ILibrary {

    SoundEvent getSound(final String sound);
    SoundEvent getSound(final Identifier sound);
    Collection<SoundEvent> getRegisteredSoundEvents();
    SoundMetadata getSoundMetadata(final Identifier sound);
    Optional<ISoundFactory> getSoundFactory(Identifier factoryLocation);
    ISoundFactory getSoundFactoryOrDefault(Identifier factoryLocation);
    ISoundFactory getSoundFactoryForMusic(MusicSound music);

    boolean isBlocked(final Identifier id);
    boolean isCulled(final Identifier id);
    float getVolumeScale(SoundCategory category, Identifier id);
    Optional<SoundEvent> getRandomStartupSound();
    Collection<IndividualSoundConfigEntry> getIndividualSoundConfigs();
    void saveIndividualSoundConfigs(Collection<IndividualSoundConfigEntry> configs);
}
