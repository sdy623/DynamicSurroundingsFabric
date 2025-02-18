package org.orecruncher.dsurround.mixins.core;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;

@Mixin(ParticleEngine.class)
public interface MixinParticleManager {

    @Accessor("spriteSets")
    Map<ResourceLocation, SpriteSet> dsurround_getSpriteAwareFactories();

    @Invoker("createParticle")
    <T extends ParticleOptions> Particle dsurround_createParticle(T parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ);
}
