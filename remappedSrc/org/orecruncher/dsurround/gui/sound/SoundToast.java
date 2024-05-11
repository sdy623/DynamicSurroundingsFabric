package org.orecruncher.dsurround.gui.sound;

import net.minecraft.sound.MusicSound;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.orecruncher.dsurround.config.libraries.ISoundLibrary;
import org.orecruncher.dsurround.lib.GameUtils;
import org.orecruncher.dsurround.lib.di.ContainerManager;
import org.orecruncher.dsurround.lib.gui.ColorPalette;
import org.orecruncher.dsurround.lib.gui.WarmToast;
import org.orecruncher.dsurround.sound.SoundMetadata;

public class SoundToast {

    private static final WarmToast.Profile SOUND_TOAST_PROFILE = new WarmToast.Profile(new Identifier("toast/advancement"), 5000, ColorPalette.PUMPKIN_ORANGE, ColorPalette.WHEAT);

    public static void create(MusicSound music) {
        var soundLibrary = ContainerManager.resolve(ISoundLibrary.class);
        var metadata = soundLibrary.getSoundMetadata(music.getSound().value().getId());
        if (metadata != null && !metadata.getCredits().isEmpty()) {
            var title = metadata.getTitle();
            if (!Text.empty().equals(title)) {
                var author = metadata.getCredits().get(0).author();
                var titleLine = Text.translatable("dsurround.text.toast.music.title", title);
                var authorLine = Text.translatable("dsurround.text.toast.music.author", author);
                var toast = WarmToast.multiline(GameUtils.getMC(), SOUND_TOAST_PROFILE, titleLine, authorLine);
                GameUtils.getMC().getToastManager().add(toast);
            }
        }
    }
}
