package org.orecruncher.dsurround.commands;

import java.util.Optional;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.orecruncher.dsurround.config.libraries.IBiomeLibrary;
import org.orecruncher.dsurround.lib.GameUtils;
import org.orecruncher.dsurround.lib.di.ContainerManager;
import org.orecruncher.dsurround.lib.scripting.Script;

public class BiomeCommandHandler {

    public static Text execute(Identifier biomeIdentifier, String script) {
        return GameUtils.getRegistryManager()
                .map(rm -> {
                    var biome = rm.getOptional(RegistryKeys.BIOME).map(r -> r.get(biomeIdentifier));
                    if (biome.isEmpty()) {
                        return Text.translatable("dsurround.command.dsbiome.failure.unknown_biome", biomeIdentifier.toString());
                    }
                    var result = ContainerManager.resolve(IBiomeLibrary.class).eval(biome.get(), new Script(script));
                    return Text.literal(result.toString());
                })
                .orElse(Text.literal("Unable to locate registry manager"));
    }
}
