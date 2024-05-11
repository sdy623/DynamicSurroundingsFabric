package org.orecruncher.dsurround.lib;

import java.util.Objects;
import java.util.Optional;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextHandler;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.resource.ResourceManager;

public final class GameUtils {
    private GameUtils() {

    }

    // Client methods
    public static Optional<PlayerEntity> getPlayer() {
        return Optional.ofNullable(getMC().player);
    }

    public static Optional<ClientWorld> getWorld() {
        return Optional.ofNullable(getMC().world);
    }

    public static Optional<DynamicRegistryManager> getRegistryManager() {
        return getWorld().map(ClientWorld::getRegistryManager);
    }

    public static Optional<Screen> getCurrentScreen() {
        return Optional.ofNullable(getMC().currentScreen);
    }

    public static void setScreen(Screen screen) {
        getMC().setScreen(screen);
    }

    public static ParticleManager getParticleManager() {
        return getMC().particleManager;
    }

    public static GameOptions getGameSettings() {
        return getMC().options;
    }

    public static TextRenderer getTextRenderer() {
        return getMC().textRenderer;
    }

    public static TextHandler getTextHandler() {
        return getTextRenderer().getTextHandler();
    }

    public static SoundManager getSoundManager() {
        return getMC().getSoundManager();
    }

    public static ResourceManager getResourceManager() {
        return getMC().getResourceManager();
    }

    public static TextureManager getTextureManager() {
        return getMC().getTextureManager();
    }

    public static boolean isInGame() {
        return getWorld().isPresent() && getPlayer().isPresent();
    }

    public static boolean isPaused()
    {
        return getMC().isPaused();
    }

    public static boolean isSinglePlayer()
    {
        return getMC().isConnectedToLocalServer();
    }

    public static boolean isFirstPersonView() {
        return getGameSettings().getPerspective() == Perspective.FIRST_PERSON;
    }

    public static MinecraftClient getMC() {
        return Objects.requireNonNull(MinecraftClient.getInstance());
    }

    public static Optional<String> getServerBrand() {
        var connection = getMC().getNetworkHandler();
        if (connection != null)
            return Optional.ofNullable(connection.getBrand());
        return Optional.empty();
    }

    public static MinecraftServerType getServerType() {
        return getServerBrand().map(MinecraftServerType::fromBrand).orElse(MinecraftServerType.VANILLA);
    }
}