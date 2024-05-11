package org.orecruncher.dsurround.effects.entity;

import net.minecraft.client.sound.EntityTrackingSoundInstance;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.orecruncher.dsurround.Constants;
import org.orecruncher.dsurround.tags.ItemEffectTags;

public class BowUseEffect extends EntityEffectBase {

    private static final Identifier BOW_PULL_FACTORY = new Identifier(Constants.MOD_ID, "bow_pull");

    protected ItemStack lastActiveStack = ItemStack.EMPTY;

    @Override
    public void tick(EntityEffectInfo info) {
        if (info.isRemoved()) {
            this.lastActiveStack = ItemStack.EMPTY;
            return;
        }

        var entity = info.getEntity();
        final ItemStack currentStack = entity.getActiveItem();
        if (isApplicable(currentStack)) {
            if (!ItemStack.areEqual(currentStack, this.lastActiveStack)) {
                SOUND_LIBRARY.getSoundFactory(BOW_PULL_FACTORY)
                        .ifPresent(f -> {
                            var sound = f.attachToEntity(entity);
                            this.playSound(sound);
                        });
                this.lastActiveStack = currentStack;
            }
        } else {
            this.lastActiveStack = ItemStack.EMPTY;
        }
    }

    private static boolean isApplicable(ItemStack stack) {
        return TAG_LIBRARY.is(ItemEffectTags.BOWS, stack);
    }
}