package org.orecruncher.dsurround.platform.forge.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.MessageArgument;
import org.orecruncher.dsurround.commands.ScriptCommandHandler;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

class ScriptCommand extends AbstractClientCommand {

    private static final String SCRIPT_PARAMETER = "script";

    ScriptCommand() {
        super("dsscript");
    }

    public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(literal(this.command)
                .then(argument(SCRIPT_PARAMETER, MessageArgument.message()).executes(this::execute)));
    }

    private int execute(CommandContext<CommandSourceStack> ctx) {
        var script = ctx.getArgument(SCRIPT_PARAMETER, MessageArgument.Message.class);
        return this.execute(ctx, () -> ScriptCommandHandler.execute(script.getText()));
    }
}
