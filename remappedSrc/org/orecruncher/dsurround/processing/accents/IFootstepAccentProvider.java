package org.orecruncher.dsurround.processing.accents;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import org.orecruncher.dsurround.config.libraries.ISoundLibrary;
import org.orecruncher.dsurround.lib.collections.ObjectArray;
import org.orecruncher.dsurround.lib.di.ContainerManager;
import org.orecruncher.dsurround.sound.ISoundFactory;

public interface IFootstepAccentProvider {

    ISoundLibrary SOUND_LIBRARY = ContainerManager.resolve(ISoundLibrary.class);

    void collect(LivingEntity entity, BlockPos pos, BlockState posState, boolean isWaterLogged, ObjectArray<ISoundFactory> acoustics);

    boolean isEnabled();
}
