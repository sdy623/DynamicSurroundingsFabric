package org.orecruncher.dsurround.mixins.audio;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import javax.sound.sampled.AudioFormat;
import net.minecraft.client.sound.StaticSound;
import java.nio.ByteBuffer;

@Mixin(StaticSound.class)
public interface MixinSoundBuffer {

    @Accessor("data")
    ByteBuffer dsurround_getSample();

    @Accessor("format")
    AudioFormat dsurround_getFormat();

    @Accessor("format")
    @Mutable
    void dsurround_setFormat(AudioFormat format);

}
