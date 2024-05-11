package org.orecruncher.dsurround.gui.overlay;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import org.orecruncher.dsurround.lib.di.Cacheable;

@Cacheable
public abstract class AbstractOverlay {

    public AbstractOverlay() {

    }

    public abstract void render(DrawContext context, float partialTick);

    public void tick(MinecraftClient client) {

    }

}
