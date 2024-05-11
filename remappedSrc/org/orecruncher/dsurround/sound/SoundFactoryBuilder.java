package org.orecruncher.dsurround.sound;

import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.floatprovider.ConstantFloatProvider;
import net.minecraft.util.math.floatprovider.FloatProvider;
import net.minecraft.util.math.floatprovider.UniformFloatProvider;
import org.orecruncher.dsurround.config.libraries.ISoundLibrary;
import org.orecruncher.dsurround.lib.di.ContainerManager;

@SuppressWarnings("unused")
public final class SoundFactoryBuilder {

    final SoundEvent soundEvent;
    FloatProvider volume;
    FloatProvider pitch;
    SoundCategory category;
    boolean isRepeatable = false;
    int repeatDelay = 0;
    boolean global = false;
    SoundInstance.AttenuationType attenuation;

    int musicMinDelay = SoundFactory.MusicSettings.DEFAULT.minDelay();
    int musicMaxDelay = SoundFactory.MusicSettings.DEFAULT.maxDelay();
    boolean musicReplaceMusic = SoundFactory.MusicSettings.DEFAULT.replaceCurrentMusic();

    SoundFactoryBuilder(SoundEvent soundEvent) {
        this.soundEvent = soundEvent;
        this.volume = ConstantFloatProvider.create(1F);
        this.pitch = ConstantFloatProvider.create(1F);
        this.category = SoundCategory.AMBIENT;
        this.attenuation = SoundInstance.AttenuationType.LINEAR;
    }

    public SoundFactoryBuilder volume(float vol) {
        return this.volume(ConstantFloatProvider.create(vol));
    }

    public SoundFactoryBuilder volume(float min, float max) {
        return this.volume(Float.compare(min, max) == 0 ? ConstantFloatProvider.create(min) : UniformFloatProvider.create(min, max));
    }

    public SoundFactoryBuilder volume(FloatProvider provider) {
        this.volume = provider;
        return this;
    }

    public SoundFactoryBuilder pitch(float pitch) {
        return this.pitch(ConstantFloatProvider.create(pitch));
    }

    public SoundFactoryBuilder pitch(float min, float max) {
        return this.pitch(Float.compare(min, max) == 0 ? ConstantFloatProvider.create(min) : UniformFloatProvider.create(min, max));
    }

    public SoundFactoryBuilder pitch(FloatProvider provider) {
        this.pitch = provider;
        return this;
    }

    public SoundFactoryBuilder category(SoundCategory category) {
        this.category = category;
        return this;
    }

    public SoundFactoryBuilder repeatable() {
        this.isRepeatable = true;
        this.repeatDelay = 0;
        return this;
    }

    public SoundFactoryBuilder repeatable(int delay) {
        this.isRepeatable = true;
        this.repeatDelay = delay;
        return this;
    }

    public SoundFactoryBuilder attenuation(SoundInstance.AttenuationType attenuation) {
        this.attenuation = attenuation;
        this.global = attenuation == SoundInstance.AttenuationType.NONE;
        return this;
    }

    public SoundFactoryBuilder global() {
        this.attenuation = SoundInstance.AttenuationType.NONE;
        this.global = true;
        return this;
    }

    public SoundFactoryBuilder setMusicMinDelay(int delay) {
        this.musicMinDelay = delay;
        return this;
    }

    public SoundFactoryBuilder setMusicMaxDelay(int delay) {
        this.musicMaxDelay = delay;
        return this;
    }

    public SoundFactoryBuilder setMusicReplaceCurrentMusic(boolean flag) {
        this.musicReplaceMusic = flag;
        return this;
    }

    public ISoundFactory build() {
        return SoundFactory.from(this);
    }

    public static SoundFactoryBuilder create(String soundEventId) {
        var se = ContainerManager.resolve(ISoundLibrary.class).getSound(soundEventId);
        return create(se);
    }

    public static SoundFactoryBuilder create(Identifier soundEventId) {
        var se = ContainerManager.resolve(ISoundLibrary.class).getSound(soundEventId);
        return create(se);
    }

    public static SoundFactoryBuilder create(SoundEvent soundEvent) {
        return new SoundFactoryBuilder(soundEvent);
    }

    public static SoundFactoryBuilder create(MusicSound music) {
        return new SoundFactoryBuilder(music.getSound().value())
                .setMusicMinDelay(music.getMinDelay())
                .setMusicMaxDelay(music.getMaxDelay())
                .setMusicReplaceCurrentMusic(music.shouldReplaceCurrentMusic());
    }

}
