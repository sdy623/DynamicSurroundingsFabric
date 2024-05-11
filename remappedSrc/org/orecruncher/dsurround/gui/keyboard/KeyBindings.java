package org.orecruncher.dsurround.gui.keyboard;

import I;
import java.util.Optional;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.orecruncher.dsurround.Configuration;
import org.orecruncher.dsurround.gui.overlay.DiagnosticsOverlay;
import org.orecruncher.dsurround.gui.sound.IndividualSoundControlScreen;
import org.orecruncher.dsurround.lib.GameUtils;
import org.orecruncher.dsurround.lib.Library;
import org.orecruncher.dsurround.lib.di.ContainerManager;
import org.orecruncher.dsurround.lib.platform.IPlatform;
import org.orecruncher.dsurround.lib.platform.Services;
import org.orecruncher.dsurround.eventing.ClientState;
import org.orecruncher.dsurround.sound.IAudioPlayer;

public class KeyBindings {

    public static final KeyBinding modConfigurationMenu;
    public static final KeyBinding individualSoundConfigBinding;
    public static final KeyBinding diagnosticHud;

    static {
        var platform = Library.PLATFORM;

        var modMenuKey = platform.isModLoaded("modmenu") ? InputUtil.UNKNOWN_KEY.getCode() : InputUtil.GLFW_KEY_EQUAL;
        modConfigurationMenu = platform.registerKeyBinding(
                "dsurround.text.keybind.modConfigurationMenu",
                modMenuKey,
                "dsurround.text.keybind.section");

        individualSoundConfigBinding = platform.registerKeyBinding(
                "dsurround.text.keybind.individualSoundConfig",
                InputUtil.UNKNOWN_KEY.getCode(),
                "dsurround.text.keybind.section");

        diagnosticHud = platform.registerKeyBinding(
                "dsurround.text.keybind.diagnosticHud",
                InputUtil.UNKNOWN_KEY.getCode(),
                "dsurround.text.keybind.section");
    }

    public static void register() {
        ClientState.TICK_END.register(KeyBindings::handleMenuKeyPress);
    }

    private static void handleMenuKeyPress(MinecraftClient client) {
        if (GameUtils.getCurrentScreen().isPresent() || GameUtils.getPlayer().isEmpty())
            return;

        if (modConfigurationMenu.wasPressed()) {
            var factory = Services.PLATFORM.getModConfigScreenFactory(Configuration.class);
            if (factory.isPresent()) {
                GameUtils.setScreen(factory.get().create(GameUtils.getMC(), null));
            } else {
                Library.LOGGER.info("Configuration GUI libraries not present");
            }
        }

        if (diagnosticHud.wasPressed())
            ContainerManager.resolve(DiagnosticsOverlay.class).toggleCollection();

        if (individualSoundConfigBinding.wasPressed()) {
            final boolean singlePlayer = GameUtils.isSinglePlayer();
            GameUtils.setScreen(new IndividualSoundControlScreen(null, singlePlayer));
            if (singlePlayer)
                ContainerManager.resolve(IAudioPlayer.class).stopAll();
        }
    }
}
