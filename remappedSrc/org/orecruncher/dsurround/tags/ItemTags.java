package org.orecruncher.dsurround.tags;

import org.orecruncher.dsurround.Constants;

import java.util.Collection;
import java.util.HashSet;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ItemTags {

    static final Collection<TagKey<Item>> TAGS = new HashSet<>();

    public static final TagKey<Item> ENTITY_WATER_BUCKETS = of("entity_water_buckets");
    public static final TagKey<Item> LAVA_BUCKETS = of("lava_buckets");
    public static final TagKey<Item> MILK_BUCKETS = of("milk_buckets");
    public static final TagKey<Item> WATER_BUCKETS = of("water_buckets");

    private static TagKey<Item> of(String id) {
        var tagKey = TagKey.of(RegistryKeys.ITEM, new Identifier(Constants.MOD_ID, id));
        TAGS.add(tagKey);
        return tagKey;
    }

}
