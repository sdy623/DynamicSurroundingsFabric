package org.orecruncher.dsurround.commands;

import net.minecraft.text.Text;
import org.orecruncher.dsurround.lib.GameUtils;
import org.orecruncher.dsurround.mixinutils.IMusicManager;

public class MusicManagerCommandHandler {

    public static Text reset() {
        try {
            ((IMusicManager)(GameUtils.getMC().getMusicTracker())).dsurround_reset();
            return Text.translatable("dsurround.command.dsmm.reset.success");
        } catch (Throwable t) {
            return Text.translatable("dsurround.command.dsmm.reset.failure", t.getMessage());
        }
    }
}
