package org.orecruncher.dsurround.gui.overlay.plugins;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import joptsimple.internal.Strings;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.orecruncher.dsurround.Configuration;
import org.orecruncher.dsurround.Constants;
import org.orecruncher.dsurround.config.block.BlockInfo;
import org.orecruncher.dsurround.config.libraries.IBlockLibrary;
import org.orecruncher.dsurround.config.libraries.IEntityEffectLibrary;
import org.orecruncher.dsurround.config.libraries.ITagLibrary;
import org.orecruncher.dsurround.effects.entity.EntityEffectInfo;
import org.orecruncher.dsurround.eventing.ClientEventHooks;
import org.orecruncher.dsurround.eventing.CollectDiagnosticsEvent;
import org.orecruncher.dsurround.gui.overlay.IDiagnosticPlugin;
import org.orecruncher.dsurround.lib.Comparers;
import org.orecruncher.dsurround.lib.GameUtils;
import org.orecruncher.dsurround.lib.gui.ColorPalette;
import org.orecruncher.dsurround.lib.registry.RegistryUtils;

import java.util.*;
import java.util.stream.Stream;

public class ViewerPlugin implements IDiagnosticPlugin {

    // Mod packs have a lot of tags. We are only interested in
    // tags for the various frameworks and ourselves.
    private static final Object2ObjectOpenHashMap<String, Style> TAG_STYLES = new Object2ObjectOpenHashMap<>();

    static {
        TAG_STYLES.defaultReturnValue(Style.EMPTY.withColor(ColorPalette.GRAY));
        TAG_STYLES.put(Constants.MOD_ID, Style.EMPTY.withColor(ColorPalette.GOLD));
        TAG_STYLES.put("minecraft", Style.EMPTY.withColor(ColorPalette.FRESH_AIR));
        TAG_STYLES.put("forge", Style.EMPTY.withColor(ColorPalette.AQUAMARINE));
        TAG_STYLES.put("fabric", Style.EMPTY.withColor(ColorPalette.CORNSILK));
        TAG_STYLES.put("c", Style.EMPTY.withColor(ColorPalette.CORNSILK));
    }

    private final Configuration.Logging config;
    private final IBlockLibrary blockLibrary;
    private final ITagLibrary tagLibrary;
    private final IEntityEffectLibrary entityEffectLibrary;

    public ViewerPlugin(Configuration.Logging config, IBlockLibrary blockLibrary, ITagLibrary tagLibrary, IEntityEffectLibrary entityEffectLibrary) {
        this.config = config;
        this.blockLibrary = blockLibrary;
        this.tagLibrary = tagLibrary;
        this.entityEffectLibrary = entityEffectLibrary;
        ClientEventHooks.COLLECT_DIAGNOSTICS.register(this::onCollect);
    }

    private void processBlockHitResult(World world, BlockHitResult result, Collection<Text> data) {
        if (result.getType() != HitResult.Type.BLOCK)
            return;

        var state = world.getBlockState(result.getBlockPos());
        data.add(Text.literal(state.toString()));

        this.processTags(state.getRegistryEntry(), data);
        if (!state.getFluidState().isEmpty()) {
            data.add(Text.literal("Fluid Tags"));
            this.processTags(state.getFluidState().getRegistryEntry(), data);
        }

        var info = this.blockLibrary.getBlockInfo(state);
        var wallOfText = info.toString();
        Arrays.stream(wallOfText.split("\n"))
            .map(l -> l.replaceAll("[\\[\\]]", "").strip())
            .filter(s -> !Strings.isNullOrEmpty(s))
            .map(Text::literal)
            .forEach(data::add);
    }

    private void processEntityHitResult(Entity entity, Collection<Text> data) {

        data.add(Text.literal(String.valueOf(Registries.ENTITY_TYPE.getId(entity.getType()))));

        var holderResult = RegistryUtils.getRegistryEntry(RegistryKeys.ENTITY_TYPE, entity.getType());
        if (holderResult.isEmpty())
            return;

        this.processTags(holderResult.get(), data);

        if (entity instanceof LivingEntity le) {
            var info = this.entityEffectLibrary.getEntityEffectInfo(le);
            if (info.isDefault()) {
                data.add(Text.literal("Default Effects"));
            } else {
                info.getEffects().forEach(effect -> data.add(Text.literal(effect.toString())));
            }
        } else {
            data.add(Text.literal("Not a LivingEntity"));
        }
    }

    private void processHeldItem(ItemStack stack, Collection<Text> data) {
        if (stack.isEmpty())
            return;
        var holder = stack.getRegistryEntry();
        holder.getKey().ifPresent(key -> data.add(Text.literal(key.getValue().toString())));
        this.processTags(holder, data);
    }

    private <T> void processTags(RegistryEntry<T> holder, Collection<Text> data) {
        var query = this.tagLibrary.streamTags(holder)
                .map(TagKey::id);

        if (this.config.filteredTagView)
            query = query.filter(loc -> TAG_STYLES.containsKey(loc.getNamespace()));

        query.sorted(Comparers.IDENTIFIER_NATURAL_COMPARABLE)
            .map(l -> {
                var formatting = TAG_STYLES.get(l.getNamespace());
                return Text.literal("#" + l).fillStyle(formatting);
            })
            .forEach(data::add);
    }

    public void onCollect(CollectDiagnosticsEvent event) {
        // Get the block info from the normal diagnostics
        Entity entity = GameUtils.getMC().getCameraEntity();
        if (entity == null)
            return;

        if (entity instanceof LivingEntity le) {
            var stack = le.getStackInHand(Hand.MAIN_HAND);
            if (!stack.isEmpty()) {
                var panelText = event.getSectionText(CollectDiagnosticsEvent.Section.HeldItem);
                this.processHeldItem(stack, panelText);
            }
        }

        var blockHit = (BlockHitResult)entity.raycast(20.0D, 0.0F, false);
        var fluidHit = (BlockHitResult)entity.raycast(20.0D, 0.0F, true);
        var entityHit = GameUtils.getMC().targetedEntity;

        var panelText = event.getSectionText(CollectDiagnosticsEvent.Section.BlockView);
        processBlockHitResult(entity.method_48926(), blockHit, panelText);

        if (!blockHit.getBlockPos().equals(fluidHit.getBlockPos())) {
            panelText = event.getSectionText(CollectDiagnosticsEvent.Section.FluidView);
            processBlockHitResult(entity.method_48926(), fluidHit, panelText);
        }

        if (entityHit != null) {
            panelText = event.getSectionText(CollectDiagnosticsEvent.Section.EntityView);
            processEntityHitResult(entityHit, panelText);
        }
    }
}
