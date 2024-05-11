package org.orecruncher.dsurround.gui.overlay.plugins;

import org.orecruncher.dsurround.eventing.ClientEventHooks;
import org.orecruncher.dsurround.eventing.CollectDiagnosticsEvent;
import org.orecruncher.dsurround.gui.overlay.IDiagnosticPlugin;
import org.orecruncher.dsurround.lib.GameUtils;
import org.orecruncher.dsurround.lib.events.HandlerPriority;
import org.orecruncher.dsurround.lib.gui.ColorPalette;
import org.orecruncher.dsurround.mixins.audio.MixinSoundManagerAccessor;
import org.orecruncher.dsurround.mixins.audio.MixinSoundEngineAccessor;
import F;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class SoundEngineDiagnosticsPlugin implements IDiagnosticPlugin {

    private static final String FMT_DBG_SOUND = "%s: %d";

    public SoundEngineDiagnosticsPlugin() {
        ClientEventHooks.COLLECT_DIAGNOSTICS.register(this::onCollect, HandlerPriority.LOW);
    }

    public void onCollect(CollectDiagnosticsEvent event) {
        var soundManager = GameUtils.getSoundManager();
        var panelText = event.getSectionText(CollectDiagnosticsEvent.Section.Sounds);

        // Check the sound source volume settings because they can disable sounds
        for (var category : SoundCategory.values()) {
            var volumeSettings = GameUtils.getGameSettings().getSoundVolume(category);
            if (Float.compare(volumeSettings, 0F) == 0) {
                var text = Text.literal("%s is OFF".formatted(category.name())).withColor(ColorPalette.RED.getRgb());
                panelText.add(text);
            }
        }

        MixinSoundManagerAccessor manager = (MixinSoundManagerAccessor) soundManager;
        MixinSoundEngineAccessor accessors = (MixinSoundEngineAccessor) manager.dsurround_getSoundSystem();
        var sources = accessors.dsurround_getSources();
        var str = Text.literal(soundManager.getDebugString());
        panelText.add(str);

        if (!sources.isEmpty()) {
            accessors.dsurround_getSources().keySet().stream()
                    .map(SoundInstance::getId)
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                    .entrySet().stream()
                    .map(e -> FMT_DBG_SOUND.formatted(e.getKey(), e.getValue()))
                    .sorted()
                    .map(Text::literal)
                    .forEach(panelText::add);
        } else {
            panelText.add(Text.literal("No sounds playing"));
        }
    }
}
