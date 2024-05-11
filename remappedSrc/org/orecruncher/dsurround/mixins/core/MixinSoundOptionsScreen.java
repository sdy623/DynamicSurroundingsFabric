package org.orecruncher.dsurround.mixins.core;

import I;
import Z;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.SoundOptionsScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.orecruncher.dsurround.gui.sound.IndividualSoundControlScreen;
import org.orecruncher.dsurround.lib.GameUtils;
import org.orecruncher.dsurround.lib.gui.ColorPalette;
import org.orecruncher.dsurround.mixinutils.IMusicManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundOptionsScreen.class)
public abstract class MixinSoundOptionsScreen extends Screen {

    protected MixinSoundOptionsScreen(Text component) {
        super(component);
    }

    @Inject(method = "init()V", at = @At("RETURN"))
    public void dsurround_addSoundConfigButton(CallbackInfo ci) {
        // This will add a button in the lower left corner of the sound options menu
        var toolTip = Tooltip.of(Text.translatable("dsurround.text.config.soundconfiguration.tooltip"));
        var style = Style.EMPTY.withColor(ColorPalette.GOLD);
        var buttonText = Text.translatable("dsurround.text.config.soundconfiguration").fillStyle(style);
        var textWidth = GameUtils.getTextRenderer().getWidth(buttonText) + 10;
        this.addDrawableChild(ButtonWidget.builder(buttonText, (button) -> {
                var enablePlayButtons = GameUtils.getMC().world == null || GameUtils.isSinglePlayer();

                // If play buttons are enabled, we need to prevent the MusicManager from
                // ticking.
                var musicManager = (IMusicManager)GameUtils.getMC().getMusicTracker();
                if (enablePlayButtons) {
                    musicManager.dsurround_setPaused(true);
                }

                var screen = new IndividualSoundControlScreen(
                        this,
                        enablePlayButtons,
                        ignore -> {
                            // Stop any sounds left hanging for whatever reason, and restart the MusicManager
                            GameUtils.getSoundManager().stopAll();
                            if (enablePlayButtons)
                                musicManager.dsurround_setPaused(false);
                        });
                this.client.setScreen(screen);
            })
            .tooltip(toolTip)
            .dimensions(5, this.height - 27, textWidth, 20).build());
    }
}
