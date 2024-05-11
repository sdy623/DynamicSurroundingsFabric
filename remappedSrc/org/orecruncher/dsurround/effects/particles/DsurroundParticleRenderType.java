package org.orecruncher.dsurround.effects.particles;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;

public class DsurroundParticleRenderType implements ParticleTextureSheet {

    private final Identifier texture;

    public DsurroundParticleRenderType(final Identifier texture) {
        this.texture = texture;
    }

    protected VertexFormat getVertexFormat() {
        return VertexFormats.POSITION_TEXTURE_COLOR_LIGHT; //.POSITION_TEXTURE_COLOR_LIGHT;
    }

    @Override
    public void begin(final BufferBuilder buffer, final TextureManager textureManager) {
        RenderSystem.depthMask(true);
        RenderSystem.setShaderTexture(0, this.getTexture());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        buffer.begin(VertexFormat.DrawMode.QUADS, this.getVertexFormat());
    }

    @Override
    public void draw(Tessellator tesselator) {
        tesselator.draw();
    }

    protected Identifier getTexture() {
        return this.texture;
    }

    @Override
    public String toString() {
        return this.texture.toString();
    }
}