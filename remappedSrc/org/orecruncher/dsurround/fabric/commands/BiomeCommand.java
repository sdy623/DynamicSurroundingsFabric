package org.orecruncher.dsurround.fabric.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.command.argument.MessageArgumentType.MessageFormat;
import net.minecraft.command.argument.RegistryEntryReferenceArgumentType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import org.orecruncher.dsurround.commands.BiomeCommandHandler;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import static net.minecraft.command.argument.RegistryEntryReferenceArgumentType.INVALID_TYPE_EXCEPTION;

class BiomeCommand extends AbstractClientCommand {

    private static final String COMMAND = "dsbiome";
    private static final String BIOME_PARAMETER = "biome";
    private static final String SCRIPT_PARAMETER = "script";

    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(literal(COMMAND)
                .then(argument(BIOME_PARAMETER, RegistryEntryReferenceArgumentType.registryEntry(registryAccess, RegistryKeys.BIOME))
                .then(argument(SCRIPT_PARAMETER, MessageArgumentType.message()).executes(this::execute))));
    }

    @SuppressWarnings("unchecked")
    public int execute(CommandContext<FabricClientCommandSource> ctx) throws CommandSyntaxException {
        RegistryEntry.Reference<Biome> reference = ctx.getArgument(BIOME_PARAMETER, RegistryEntry.Reference.class);
        RegistryKey<Biome> registryKey = reference.registryKey();
        if (!registryKey.isOf(RegistryKeys.BIOME)) {
            throw INVALID_TYPE_EXCEPTION.create(registryKey.getValue(), registryKey.getRegistry(), RegistryKeys.BIOME.getRegistry());
        }

        var script = ctx.getArgument(SCRIPT_PARAMETER, MessageArgumentType.MessageFormat.class);
        return this.execute(ctx, () -> BiomeCommandHandler.execute(registryKey.getValue(), script.getText()));
    }
}
