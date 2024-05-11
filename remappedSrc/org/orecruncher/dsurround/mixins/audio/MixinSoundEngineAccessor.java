package org.orecruncher.dsurround.mixins.audio;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import net.minecraft.client.sound.Channel;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundListener;
import net.minecraft.client.sound.SoundSystem;

@Mixin(SoundSystem.class)
public interface MixinSoundEngineAccessor {

    @Accessor("instanceToChannel")
    Map<SoundInstance, Channel.SourceManager> dsurround_getSources();

    @Accessor("listener")
    SoundListener dsurround_getListener();
}
