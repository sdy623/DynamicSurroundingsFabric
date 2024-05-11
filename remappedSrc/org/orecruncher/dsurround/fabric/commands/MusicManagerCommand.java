package org.orecruncher.dsurround.fabric.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import org.orecruncher.dsurround.commands.MusicManagerCommandHandler;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class MusicManagerCommand extends AbstractClientCommand {
    private static final String COMMAND = "dsmm";
    private static final String RESET = "reset";

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(literal(COMMAND)
                .then(subCommand(RESET, MusicManagerCommandHandler::reset)));
    }
}
