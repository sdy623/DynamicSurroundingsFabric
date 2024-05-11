package org.orecruncher.dsurround.tags;

import org.orecruncher.dsurround.Constants;

import java.util.Collection;
import java.util.HashSet;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ItemEffectTags {

    static final Collection<TagKey<Item>> TAGS = new HashSet<>();

    public static final TagKey<Item> AXES = of("axes");
    public static final TagKey<Item> BOOKS = of("books");
    public static final TagKey<Item> BOWS = of("bows");
    public static final TagKey<Item> CROSSBOWS = of("crossbows");
    public static final TagKey<Item> POTIONS = of("potions");
    public static final TagKey<Item> SHIELDS = of("shields");
    public static final TagKey<Item> SWORDS = of("swords");
    public static final TagKey<Item> TOOLS = of("tools");
    public static final TagKey<Item> COMPASSES = of("compasses");
    public static final TagKey<Item> CLOCKS = of("clocks");

    private static TagKey<Item> of(String id) {
        var tagKey = TagKey.of(RegistryKeys.ITEM, new Identifier(Constants.MOD_ID, "effects/" + id));
        TAGS.add(tagKey);
        return tagKey;
    }

}
