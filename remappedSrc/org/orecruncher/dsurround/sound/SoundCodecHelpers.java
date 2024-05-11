package org.orecruncher.dsurround.sound;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundInstance.AttenuationType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.floatprovider.ConstantFloatProvider;
import net.minecraft.util.math.floatprovider.FloatProvider;
import net.minecraft.util.math.floatprovider.UniformFloatProvider;
import org.orecruncher.dsurround.lib.IdentityUtils;

public class SoundCodecHelpers {

    public static final Codec<SoundCategory> SOUND_CATEGORY_CODEC = Codec.STRING.xmap(SoundCodecHelpers::lookupSoundSource, SoundCategory::getName);
    public static final Codec<SoundInstance.AttenuationType> ATTENUATION_CODEC = Codec.STRING.xmap(SoundCodecHelpers::lookupAttenuation, SoundInstance.AttenuationType::name);
    public static final Codec<SoundEvent> SOUND_EVENT_CODEC = Codec.either(IdentityUtils.CODEC, SoundEvent.CODEC)
            .xmap(either -> either.map(SoundEvent::of, x -> x), Either::right);

    public static final Codec<FloatProvider> SOUND_PROPERTY_RANGE = Codec.either(Codec.FLOAT, RangeProperty.CODEC)
            .xmap((either) -> either.map(ConstantFloatProvider::create, rangeProperty -> UniformFloatProvider.create(rangeProperty.min, rangeProperty.max)),
                    floatProvider -> {
                        throw new RuntimeException("Not gonna happen");
                    });

    public record RangeProperty(float min, float max) {
        static Codec<RangeProperty> CODEC = RecordCodecBuilder.create((instance) ->
                instance.group(
                        Codec.FLOAT.optionalFieldOf("min", 1.0F).forGetter(RangeProperty::min),
                        Codec.FLOAT.optionalFieldOf("max", 1.0F).forGetter(RangeProperty::max)
                ).apply(instance, RangeProperty::new));
    }

    private static SoundCategory lookupSoundSource(String string) {
        for (var c : SoundCategory.values())
            if (c.getName().equalsIgnoreCase(string))
                return c;
        return SoundCategory.AMBIENT;
    }

    private static SoundInstance.AttenuationType lookupAttenuation(String string) {
        for (var c : SoundInstance.AttenuationType.values())
            if (c.name().equalsIgnoreCase(string))
                return c;
        return SoundInstance.AttenuationType.LINEAR;
    }

}
