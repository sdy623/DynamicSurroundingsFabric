package org.orecruncher.dsurround;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.util.Collection;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

/**
 * Values that are constant throughout the mod source.
 */
public final class Constants {

    /**
     * The ID of the current mod
     */
    public static final String MOD_ID = "dsurround";

    /**
     * ID's of mods that Dynamic Surroundings has an interest for disabling certain features.
     */
    // Brush step sound effect
    public static final String MOD_PRESENCE_FOOTSTEPS = "presencefootsteps";
    // Enhanced sound processing.  Check AudioUtilities.java as well.
    public static final String MOD_SOUND_PHYSICS_REMASTERED = "sound_physics_remastered";
    public static final String CLOTH_CONFIG = "cloth-config";
    public static final String YACL = "yet_another_config_lib_v3";
    public static final String SINYTRA_CONNECTOR = "connectormod";
    public static final String SERENE_SEASONS = "sereneseasons";
    public static final String QUILTED_LOADER = "quilt_loader";

    /**
     * Collection of MOD IDs that are of interest.  Ease of iteration.
     */
    public static final Collection<String> SPECIAL_MODS;

    /**
     * Blocks that will be ignored by the system during configuration
     * and processing.
     */
    public static final Set<Block> BLOCKS_TO_IGNORE = new ReferenceOpenHashSet<>(5);

    static {
        SPECIAL_MODS = ImmutableList.of(
                MOD_PRESENCE_FOOTSTEPS,
                MOD_SOUND_PHYSICS_REMASTERED,
                CLOTH_CONFIG,
                YACL,
                SINYTRA_CONNECTOR,
                SERENE_SEASONS,
                QUILTED_LOADER);

        BLOCKS_TO_IGNORE.add(Blocks.VOID_AIR);
        BLOCKS_TO_IGNORE.add(Blocks.CAVE_AIR);
        BLOCKS_TO_IGNORE.add(Blocks.AIR);
        BLOCKS_TO_IGNORE.add(Blocks.BARRIER);
        BLOCKS_TO_IGNORE.add(Blocks.COMMAND_BLOCK);
    }

}
