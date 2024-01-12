package org.orecruncher.dsurround.platform.forge.services;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.GameShuttingDownEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import org.orecruncher.dsurround.Constants;
import org.orecruncher.dsurround.lib.GameUtils;
import org.orecruncher.dsurround.lib.Library;
import org.orecruncher.dsurround.lib.platform.IClientEventRegistrations;
import org.orecruncher.dsurround.lib.platform.events.ClientState;

import java.util.function.Consumer;

@SuppressWarnings("unused")
public class ClientEventRegistrationsImpl implements IClientEventRegistrations {

    public void register() {
        // Do nothing - handled by the static constructors
    }

    public void registerClientTickStart(Consumer<Minecraft> handler) {
        var h = new ClientEventTickEventAdaptor(handler, TickEvent.Phase.START);
        MinecraftForge.EVENT_BUS.register(h);
    }

    public void registerClientTickEnd(Consumer<Minecraft> handler) {
        var h = new ClientEventTickEventAdaptor(handler, TickEvent.Phase.END);
        MinecraftForge.EVENT_BUS.register(h);
    }

    @Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    private static class ClientTickEventHandler {
        @SubscribeEvent
        public static void clientTick(TickEvent.ClientTickEvent event) {
            if (event.type != TickEvent.Type.CLIENT)
                return;

            var client = GameUtils.getMC();
            if (event.phase == TickEvent.Phase.START)
                ClientState.TICK_START.raise(client);
            else
                ClientState.TICK_END.raise(client);
        }
    }

    @Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    private static class ClientLoginEventHandler {
        @SubscribeEvent
        public static void login(ClientPlayerNetworkEvent.LoggingIn event) {
            ClientState.ON_CONNECT.raise(GameUtils.getMC());
        }
    }

    @Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    private static class ClientLogoutEventHandler {
        @SubscribeEvent
        public static void logout(ClientPlayerNetworkEvent.LoggingOut event) {
            ClientState.ON_DISCONNECT.raise(GameUtils.getMC());
        }
    }

    @Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    private static class ClientStartedEventHandler {
        @SubscribeEvent
        public static void clientStarted(FMLLoadCompleteEvent event) {
            Library.getLogger().info("Client started");
            ClientState.STARTED.raise(GameUtils.getMC());
        }
    }

    @Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    private static class GameShuttingDownEventHandler {
        @SubscribeEvent
        public static void shutdown(GameShuttingDownEvent event) {
            Library.getLogger().info("Client stopped");
            ClientState.STOPPING.raise(GameUtils.getMC());
        }
    }

    @Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    private static class RecipesChangedEventHandler {
        @SubscribeEvent
        public static void recipesChanged(RecipesUpdatedEvent event) {
            ClientState.TAG_SYNC.raise(new ClientState.TagSyncEvent(null));
        }
    }

    private record ClientEventTickEventAdaptor(Consumer<Minecraft> consumer, TickEvent.Phase phase) {
        @SubscribeEvent
        public void onEvent(TickEvent.ClientTickEvent event) {
            if (event.phase == this.phase)
                this.consumer.accept(GameUtils.getMC());
        }
    }
}
