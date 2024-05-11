package org.orecruncher.dsurround.tags;

import org.orecruncher.dsurround.Constants;

import java.util.Collection;
import java.util.HashSet;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class EntityEffectTags {

    static final Collection<TagKey<EntityType<?>>> TAGS = new HashSet<>();

    public static final TagKey<EntityType<?>> BOW_PULL = of("bow_pull");
    public static final TagKey<EntityType<?>> FROST_BREATH = of("frost_breath");
    public static final TagKey<EntityType<?>> ITEM_SWING = of("item_swing");
    public static final TagKey<EntityType<?>> TOOLBAR = of("toolbar");
    public static final TagKey<EntityType<?>> BRUSH_STEP = of ("brush_step");

    private static TagKey<EntityType<?>> of(String id) {
        var tagKey = TagKey.of(RegistryKeys.ENTITY_TYPE, new Identifier(Constants.MOD_ID, "effects/" + id));
        TAGS.add(tagKey);
        return tagKey;
    }
}
