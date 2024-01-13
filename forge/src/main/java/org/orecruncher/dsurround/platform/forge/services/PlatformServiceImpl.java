package org.orecruncher.dsurround.platform.forge.services;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.forgespi.language.IModInfo;
import org.orecruncher.dsurround.Client;
import org.orecruncher.dsurround.lib.Library;
import org.orecruncher.dsurround.lib.collections.ObjectArray;
import org.orecruncher.dsurround.lib.platform.IPlatform;
import org.orecruncher.dsurround.lib.platform.ModInformation;
import org.orecruncher.dsurround.lib.version.SemanticVersion;

import java.net.URL;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class PlatformServiceImpl implements IPlatform {

    private final ObjectArray<KeyMapping> keyMappings = new ObjectArray<>();

    public PlatformServiceImpl() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::onRegistration);
    }

    @Override
    public String getPlatformName() {
        return "Forge";
    }

    @Override
    public Optional<ModInformation> getModInformation(String modId) {
        var container = ModList.get().getModContainerById(modId);
        if (container.isPresent()) {
            try {
                var modInfo = container.get().getModInfo();
                var displayName = modInfo.getDisplayName();
                var version = SemanticVersion.parse(modInfo.getVersion().toString());
                var updateURL = modInfo.getUpdateURL().map(URL::toString).orElse("");
                var curseForgeLink = ""; //data.get("curseForgeLink").getAsString();
                var modrinthLink = ""; //data.get("modrinthLink").getAsString();
                var result = new ModInformation(modId, displayName, version, updateURL, curseForgeLink, modrinthLink);
                return Optional.of(result);
            } catch(Exception ex) {
                Client.LOGGER.error(ex, "What?");
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<String> getModDisplayName(String namespace) {
        return ModList.get()
                .getModContainerById(namespace)
                .map(c -> c.getModInfo().getDisplayName());
    }

    @Override
    public Optional<SemanticVersion> getModVersion(String namespace) {
        var container = ModList.get().getModContainerById(namespace);
        if (container.isPresent()) {
            try {
                // TODO: Check to see if we can use ArtifactVersion instead of our own rolled instance
                return Optional.of(SemanticVersion.parse(container.get().getModInfo().getVersion().toString()));
            } catch (Exception ignored) {

            }
        }
        return Optional.empty();
    }

    @Override
    public boolean isModLoaded(String namespace) {
        return ModList.get().isLoaded(namespace);
    }

    @Override
    public Collection<String> getModIdList(boolean loadedOnly) {
        return ModList.get().getMods().stream()
                .map(IModInfo::getModId)
                .collect(Collectors.toList());
    }

    @Override
    public Path getConfigPath() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public KeyMapping registerKeyBinding(String translationKey, int code, String category) {
        var mapping = new KeyMapping(translationKey, code, category);
        keyMappings.add(mapping);
        return mapping;
    }

    public void onRegistration(RegisterKeyMappingsEvent event) {
        Library.getLogger().debug("Forge key bind event received");
        keyMappings.forEach(event::register);
    }
}
