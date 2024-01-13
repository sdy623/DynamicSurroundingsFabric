package org.orecruncher.dsurround.forge.services;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.GameShuttingDownEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.orecruncher.dsurround.lib.GameUtils;
import org.orecruncher.dsurround.lib.Library;
import org.orecruncher.dsurround.lib.platform.IClientEventRegistrations;
import org.orecruncher.dsurround.lib.platform.events.ClientState;

import java.util.function.Consumer;

@SuppressWarnings("unused")
public class ClientEventRegistrationsImpl implements IClientEventRegistrations {

    public void register() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::clientStarted);

        MinecraftForge.EVENT_BUS.addListener(this::clientTick);
        MinecraftForge.EVENT_BUS.addListener(this::login);
        MinecraftForge.EVENT_BUS.addListener(this::logout);
        MinecraftForge.EVENT_BUS.addListener(this::shutdown);
        MinecraftForge.EVENT_BUS.addListener(this::recipesChanged);
    }

    public void registerClientTickStart(Consumer<Minecraft> handler) {
        var h = new ClientEventTickEventAdaptor(handler, TickEvent.Phase.START);
        MinecraftForge.EVENT_BUS.register(h);
    }

    public void registerClientTickEnd(Consumer<Minecraft> handler) {
        var h = new ClientEventTickEventAdaptor(handler, TickEvent.Phase.END);
        MinecraftForge.EVENT_BUS.register(h);
    }

    @SubscribeEvent
    public void clientTick(TickEvent.ClientTickEvent event) {
        if (event.type != TickEvent.Type.CLIENT)
            return;

        var client = GameUtils.getMC();
        if (event.phase == TickEvent.Phase.START)
            ClientState.TICK_START.raise(client);
        else
            ClientState.TICK_END.raise(client);
    }

    @SubscribeEvent
    public void login(ClientPlayerNetworkEvent.LoggingIn event) {
        Library.getLogger().info("Player logging in");
        ClientState.ON_CONNECT.raise(GameUtils.getMC());
    }

    @SubscribeEvent
    public void logout(ClientPlayerNetworkEvent.LoggingOut event) {
        Library.getLogger().info("Player logging out");
        ClientState.ON_DISCONNECT.raise(GameUtils.getMC());
    }

    @SubscribeEvent
    public void clientStarted(FMLLoadCompleteEvent event) {
        Library.getLogger().info("Client started");
        ClientState.STARTED.raise(GameUtils.getMC());
    }

    @SubscribeEvent
    public void shutdown(GameShuttingDownEvent event) {
        Library.getLogger().info("Client stopped");
        ClientState.STOPPING.raise(GameUtils.getMC());
    }

    @SubscribeEvent
    public void recipesChanged(RecipesUpdatedEvent event) {
        Library.getLogger().info("Recipes changed");
        ClientState.TAG_SYNC.raise(new ClientState.TagSyncEvent(null));
    }

    private record ClientEventTickEventAdaptor(Consumer<Minecraft> consumer, TickEvent.Phase phase) {
        @SubscribeEvent
        public void onEvent(TickEvent.ClientTickEvent event) {
            if (event.phase == this.phase)
                this.consumer.accept(GameUtils.getMC());
        }
    }
}
