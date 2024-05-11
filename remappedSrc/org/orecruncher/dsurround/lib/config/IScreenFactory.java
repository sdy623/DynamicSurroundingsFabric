package org.orecruncher.dsurround.lib.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

@FunctionalInterface
public interface IScreenFactory<S extends Screen> {
    S create(MinecraftClient client, Screen parent);
}
