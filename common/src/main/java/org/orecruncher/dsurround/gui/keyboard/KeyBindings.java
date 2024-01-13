package org.orecruncher.dsurround.gui.keyboard;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.orecruncher.dsurround.gui.overlay.DiagnosticsOverlay;
import org.orecruncher.dsurround.gui.sound.IndividualSoundControlScreen;
import org.orecruncher.dsurround.lib.GameUtils;
import org.orecruncher.dsurround.lib.Library;
import org.orecruncher.dsurround.lib.config.factories.FactoryResolver;
import org.orecruncher.dsurround.lib.di.ContainerManager;
import org.orecruncher.dsurround.lib.platform.events.ClientState;
import org.orecruncher.dsurround.sound.IAudioPlayer;

public class KeyBindings {

    public static final KeyMapping modConfigurationMenu;
    public static final KeyMapping individualSoundConfigBinding;
    public static final KeyMapping diagnosticHud;

    static {
        var platform = Library.getPlatform();

        var modMenuKey = platform.isModLoaded("modmenu") ? InputConstants.UNKNOWN.getValue() : InputConstants.KEY_EQUALS;
        modConfigurationMenu = platform.registerKeyBinding(
                "dsurround.text.keybind.modConfigurationMenu",
                modMenuKey,
                "dsurround.text.keybind.section");

        individualSoundConfigBinding = platform.registerKeyBinding(
                "dsurround.text.keybind.individualSoundConfig",
                InputConstants.UNKNOWN.getValue(),
                "dsurround.text.keybind.section");

        diagnosticHud = platform.registerKeyBinding(
                "dsurround.text.keybind.diagnosticHud",
                InputConstants.UNKNOWN.getValue(),
                "dsurround.text.keybind.section");
    }

    public static void register() {
        ClientState.TICK_END.register(KeyBindings::handleMenuKeyPress);
    }

    private static void handleMenuKeyPress(Minecraft client) {
        if (GameUtils.getCurrentScreen().isPresent() || GameUtils.getPlayer().isEmpty())
            return;

        if (modConfigurationMenu.consumeClick()) {
            Library.getLogger().debug("Activating mod configuration menu");
            var screenFactory = FactoryResolver.getModConfigScreenFactory();
            if (screenFactory != null)
                GameUtils.setScreen(screenFactory.create(null));
            else
                Library.getLogger().info("Configuration GUI libraries not present");
        }

        if (diagnosticHud.consumeClick()) {
            var result = ContainerManager.resolve(DiagnosticsOverlay.class).toggleCollection();
            Library.getLogger().debug("Toggling diagnostic overlay: %s", result);
        }

        if (individualSoundConfigBinding.consumeClick()) {
            Library.getLogger().debug("Activating individual sound configuration menu");
            final boolean singlePlayer = GameUtils.isSinglePlayer();
            GameUtils.setScreen(new IndividualSoundControlScreen(null, singlePlayer));
            if (singlePlayer)
                ContainerManager.resolve(IAudioPlayer.class).stopAll();
        }
    }
}
