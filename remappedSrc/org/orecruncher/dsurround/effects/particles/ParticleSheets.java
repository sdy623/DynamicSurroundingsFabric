package org.orecruncher.dsurround.effects.particles;

import org.orecruncher.dsurround.Constants;
import org.orecruncher.dsurround.Configuration;
import org.orecruncher.dsurround.lib.GameUtils;
import org.orecruncher.dsurround.lib.di.ContainerManager;
import org.orecruncher.dsurround.mixins.core.MixinParticleManager;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;

public final class ParticleSheets {

    private static Configuration.BlockEffects CONFIG;

    public static final Identifier TEXTURE_WATER_RIPPLE_PIXELATED_CIRCLE = new Identifier(Constants.MOD_ID, "textures/particles/pixel_ripples.png");

    public static final net.minecraft.client.particle.ParticleTextureSheet RIPPLE_RENDER =
            new DsurroundParticleRenderType(TEXTURE_WATER_RIPPLE_PIXELATED_CIRCLE) {
                @Override
                protected Identifier getTexture() {
                    return CONFIG.waterRippleStyle.getTexture();
                }
            };

    public static void register() {

        CONFIG = ContainerManager.resolve(Configuration.BlockEffects.class);
        var manager = GameUtils.getTextureManager();
        manager.registerTexture(TEXTURE_WATER_RIPPLE_PIXELATED_CIRCLE, new ResourceTexture(TEXTURE_WATER_RIPPLE_PIXELATED_CIRCLE));

        var existingSheets = MixinParticleManager.dsurround_getParticleTextureSheets();
        assert existingSheets != null;
        existingSheets = new ArrayList<>(existingSheets);
        existingSheets.add(RIPPLE_RENDER);
        MixinParticleManager.dsurround_setParticleTextureSheets(existingSheets);
    }
}
