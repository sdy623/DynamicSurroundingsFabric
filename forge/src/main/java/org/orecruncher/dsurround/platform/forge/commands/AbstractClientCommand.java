package org.orecruncher.dsurround.platform.forge.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import org.orecruncher.dsurround.lib.Library;

import java.util.function.Supplier;

abstract class AbstractClientCommand {

    protected final String command;

    protected AbstractClientCommand(String command) {
        this.command = command;
    }

    public abstract void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess);

    protected int execute(CommandContext<CommandSourceStack> ctx, Supplier<Component> commandHandler) {
        try {
            ctx.getSource().sendSuccess(commandHandler, false);
            return 0;
        } catch(Exception ex) {
            Library.getLogger().error(ex, "Unable to execute command %s", ctx.getCommand().toString());
            ctx.getSource().sendFailure(Component.literal(ex.getMessage()));
            return 1;
        }
    }
}
