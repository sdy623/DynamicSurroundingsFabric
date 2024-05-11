package org.orecruncher.dsurround.gui.sound;

import org.orecruncher.dsurround.lib.random.Randomizer;

import java.util.function.Supplier;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.TickableSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;

/**
 * Special sound instance created by the sound configuration option menu.  The type is detected through the pipeline
 * to avoid applying behaviors like blocking and volume scaling.
 */
public class ConfigSoundInstance extends PositionedSoundInstance implements TickableSoundInstance {

    private final Supplier<Float> volumeScale;

    ConfigSoundInstance(Identifier id, SoundCategory category, Supplier<Float> volumeScale) {
        super(id, category, volumeScale.get(), 1F, Randomizer.current(), false, 0, SoundInstance.AttenuationType.NONE, 0.0D, 0.0D, 0.0D, true);

        this.volumeScale = volumeScale;
    }

    @Override
    public float getVolume() {
        return super.getVolume() * this.volumeScale.get();
    }

    public static ConfigSoundInstance create(Identifier location, SoundCategory category, Supplier<Float> volumeScale) {
        return new ConfigSoundInstance(location, category, volumeScale);
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public void tick() {

    }
}
