package org.orecruncher.dsurround.platform.forge.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import org.orecruncher.dsurround.commands.DumpCommandHandler;

import static net.minecraft.commands.Commands.literal;

class DumpCommand extends AbstractClientCommand {

    DumpCommand() {
        super("dsdump");
    }

    public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(literal(this.command)
                .then(
                        literal("biomes")
                                .executes(cmd -> this.execute(cmd, DumpCommandHandler::dumpBiomes)))
                .then(
                        literal("sounds")
                                .executes(cmd -> this.execute(cmd, DumpCommandHandler::dumpSounds)))
                .then(
                        literal("dimensions")
                                .executes(cmd -> this.execute(cmd, DumpCommandHandler::dumpDimensions)))
                .then(
                        literal("blocks")
                                .executes(cmd -> this.execute(cmd, () -> DumpCommandHandler.dumpBlocks(false)))
                            .then(literal("nostates")
                                .executes(cmd -> this.execute(cmd, () -> DumpCommandHandler.dumpBlocks(true)))))
                .then(
                        literal("blocksbytag")
                                .executes(cmd -> this.execute(cmd, DumpCommandHandler::dumpBlocksByTag)))
                .then(
                        literal("blockconfigrules")
                                .executes(cmd -> this.execute(cmd, DumpCommandHandler::dumpBlockConfigRules)))
                .then(
                        literal("blockstates")
                                .executes(cmd -> this.execute(cmd, DumpCommandHandler::dumpBlockState)))
                .then(
                        literal("items")
                                .executes(cmd -> this.execute(cmd, DumpCommandHandler::dumpItems)))
                .then(
                        literal("tags")
                                .executes(cmd -> this.execute(cmd, DumpCommandHandler::dumpTags)))
        );
    }
}
