package org.orecruncher.dsurround.platform.forge.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import org.orecruncher.dsurround.commands.ReloadCommandHandler;

import static net.minecraft.commands.Commands.literal;

class ReloadCommand extends AbstractClientCommand {

    ReloadCommand() {
        super("dsreload");
    }

    public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(literal(this.command).executes(cmd -> this.execute(cmd, ReloadCommandHandler::execute)));
    }
}