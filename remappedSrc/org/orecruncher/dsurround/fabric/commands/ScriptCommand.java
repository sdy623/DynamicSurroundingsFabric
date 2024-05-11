package org.orecruncher.dsurround.fabric.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.command.argument.MessageArgumentType.MessageFormat;
import org.orecruncher.dsurround.commands.ScriptCommandHandler;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

class ScriptCommand extends AbstractClientCommand {

    private static final String COMMAND = "dsscript";
    private static final String SCRIPT_PARAMETER = "script";

    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(literal(COMMAND)
                .then(argument(SCRIPT_PARAMETER, MessageArgumentType.message()).executes(this::execute)));
    }

    private int execute(CommandContext<FabricClientCommandSource> ctx) {
        var script = ctx.getArgument(SCRIPT_PARAMETER, MessageArgumentType.MessageFormat.class);
        return this.execute(ctx, () -> ScriptCommandHandler.execute(script.getText()));
    }
}
