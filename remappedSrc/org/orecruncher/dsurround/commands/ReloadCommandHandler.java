package org.orecruncher.dsurround.commands;

import net.minecraft.text.Text;
import org.orecruncher.dsurround.config.libraries.AssetLibraryEvent;
import org.orecruncher.dsurround.config.libraries.IReloadEvent;
import org.orecruncher.dsurround.lib.resources.ResourceUtilities;

public class ReloadCommandHandler {

    public static Text execute() {
        try {
            var resourceUtilities = ResourceUtilities.createForCurrentState();
            AssetLibraryEvent.RELOAD.raise().onReload(resourceUtilities, IReloadEvent.Scope.ALL);
            return Text.translatable("dsurround.command.dsreload.success");
        } catch (Throwable t) {
            return Text.translatable("dsurround.command.dsreload.failure", t.getMessage());
        }
    }
}
