package org.orecruncher.dsurround.processing.accents;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.orecruncher.dsurround.Configuration;
import org.orecruncher.dsurround.Constants;
import org.orecruncher.dsurround.lib.collections.ObjectArray;
import org.orecruncher.dsurround.lib.random.Randomizer;
import org.orecruncher.dsurround.sound.ISoundFactory;
import org.orecruncher.dsurround.tags.BlockEffectTags;

class FloorSqueakAccent  implements IFootstepAccentProvider {

    private static final Identifier FLOOR_SQUEAK = new Identifier(Constants.MOD_ID, "footstep/floorsqueak");

    private final Configuration config;

    FloorSqueakAccent(Configuration config) {
        this.config = config;
    }

    @Override
    public void collect(LivingEntity entity, BlockPos pos, BlockState state, boolean isWaterLogged, ObjectArray<ISoundFactory> acoustics) {
        // Check for waterlogged.  Don't want to squeak if waterlogged.
        if (isWaterLogged)
            return;

        if (FootstepAccents.TAG_LIBRARY.is(BlockEffectTags.FLOOR_SQUEAKS, state)) {
            // 1 in 10 chance of a squeak
            if (Randomizer.current().nextInt(10) == 0) {
                SOUND_LIBRARY.getSoundFactory(FLOOR_SQUEAK)
                        .ifPresent(acoustics::add);
            }
        }
    }

    @Override
    public boolean isEnabled() {
        return this.config.footstepAccents.enableFloorSqueaks;
    }
}
