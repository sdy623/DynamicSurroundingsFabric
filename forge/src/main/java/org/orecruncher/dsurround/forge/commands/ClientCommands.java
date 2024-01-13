package org.orecruncher.dsurround.forge.commands;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.orecruncher.dsurround.Constants;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class ClientCommands {

    public static void register() {
        // Do nothing.  The event handler will do the registration when invoked.
    }

    @SubscribeEvent
    public static void clientCommandRegisterEvent(RegisterClientCommandsEvent event) {
        var dispatcher = event.getDispatcher();
        var registryAccess = event.getBuildContext();
        new BiomeCommand().register(dispatcher, registryAccess);
        new DumpCommand().register(dispatcher, registryAccess);
        new ReloadCommand().register(dispatcher, registryAccess);
        new ScriptCommand().register(dispatcher, registryAccess);
    }
}
