package org.orecruncher.dsurround.mixins.audio;

import net.minecraft.client.sound.MusicTracker;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.MusicSound;
import org.jetbrains.annotations.Nullable;
import org.orecruncher.dsurround.gui.sound.SoundToast;
import org.orecruncher.dsurround.mixinutils.IMusicManager;
import org.orecruncher.dsurround.mixinutils.MixinHelpers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MusicTracker.class)
public class MixinMusicManager implements IMusicManager {
    @Shadow
    @Nullable
    private SoundInstance currentMusic;
    @Shadow
    private int nextSongDelay;

    @Unique
    private boolean dsurround_pauseTicking;

    @Override
    public String dsurround_getDiagnosticText() {
        String playingSound = "Nothing playing";
        if (this.currentMusic != null)
            playingSound = this.currentMusic.getId().toString();
        var result = "Music Manager: %d (%s)".formatted(this.nextSongDelay, playingSound);
        if (this.dsurround_pauseTicking)
            result += " (PAUSED)";
        return result;
    }

    @Override
    public void dsurround_reset() {
        MusicTracker self = (MusicTracker) (Object) this;
        self.stop();
        this.nextSongDelay = 100;
        this.dsurround_pauseTicking = false;
    }

    @Override
    public void dsurround_setPaused(boolean flag) {
        var self = (MusicTracker) (Object) this;
        if (flag) {
            MixinHelpers.LOGGER.info("Stopping MusicManager");
            this.dsurround_pauseTicking = true;
            self.stop();
        } else {
            MixinHelpers.LOGGER.info("Starting MusicManager");
            this.nextSongDelay = 100;
            this.dsurround_pauseTicking = false;
        }
    }

    @Inject(method = "tick()V", at = @At("HEAD"), cancellable = true)
    public void dsurround_pauseTickCheck(CallbackInfo ci) {
        if (this.dsurround_pauseTicking)
            ci.cancel();
    }

    @Inject(method = "startPlaying(Lnet/minecraft/sounds/Music;)V", at = @At("RETURN"))
    public void dsurround_startPlaying(MusicSound music, CallbackInfo ci) {
        MixinHelpers.LOGGER.debug("Play Music: %s", music.getSound().value());
        if (MixinHelpers.soundOptions.displayToastMessagesForMusic)
            SoundToast.create(music);
    }
}
