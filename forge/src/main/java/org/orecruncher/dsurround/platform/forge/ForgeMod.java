package org.orecruncher.dsurround.platform.forge;

import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.orecruncher.dsurround.Client;
import org.orecruncher.dsurround.Constants;
import org.orecruncher.dsurround.platform.forge.commands.ClientCommands;

/**
 * Implements the Forge-specific binding to initialize the mod
 */
@SuppressWarnings("unused")
@Mod(Constants.MOD_ID)
public class ForgeMod extends Client {

    public ForgeMod() {
        // Since we are 100% client side
        ModLoadingContext.get().registerExtensionPoint(
                IExtensionPoint.DisplayTest.class,
                () -> new IExtensionPoint.DisplayTest(() -> "dQw4w9WgXcQ", (remoteVersion, isFromServer) -> true));

        this.bootStrap();

        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::clientSetup);

        ClientCommands.register();
    }

    private void clientSetup(FMLClientSetupEvent event) {
        this.initialize();
    }
}
