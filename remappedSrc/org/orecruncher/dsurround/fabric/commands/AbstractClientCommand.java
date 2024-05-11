package org.orecruncher.dsurround.fabric.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;
import org.orecruncher.dsurround.lib.Library;

import java.util.function.Supplier;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

abstract class AbstractClientCommand {

    protected AbstractClientCommand() {

    }

    public abstract void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess);

    protected int execute(CommandContext<FabricClientCommandSource> ctx, Supplier<Text> commandHandler) {
        try {
            var result = commandHandler.get();
            ctx.getSource().sendFeedback(result);
            return 0;
        } catch(Exception ex) {
            Library.LOGGER.error(ex, "Unable to execute command %s", ctx.getCommand().toString());
            ctx.getSource().sendFeedback(Text.literal(ex.getMessage()));
            return 1;
        }
    }

    protected LiteralArgumentBuilder<FabricClientCommandSource> subCommand(String command, Supplier<Text> supplier) {
        return literal(command).executes(ctx -> this.execute(ctx, supplier));
    }
}
