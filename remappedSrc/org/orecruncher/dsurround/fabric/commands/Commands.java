package org.orecruncher.dsurround.fabric.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;

public final class Commands {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        if (dispatcher == null)
            return;

        new BiomeCommand().register(dispatcher, registryAccess);
        new DumpCommand().register(dispatcher, registryAccess);
        new ReloadCommand().register(dispatcher, registryAccess);
        new ScriptCommand().register(dispatcher, registryAccess);
        new MusicManagerCommand().register(dispatcher, registryAccess);
    }
}
