package org.orecruncher.dsurround.effects.entity;

import J;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.orecruncher.dsurround.Constants;
import org.orecruncher.dsurround.config.libraries.ITagLibrary;
import org.orecruncher.dsurround.lib.system.ITickCount;
import org.orecruncher.dsurround.tags.BlockEffectTags;
import org.orecruncher.dsurround.mixinutils.ILivingEntityExtended;

public class StepThroughBrushEffect extends EntityEffectBase {

    private static final long BRUSH_INTERVAL = 2;
    private static final Identifier BRUSH_SOUND = new Identifier(Constants.MOD_ID, "brush_step/brush");
    private static final Identifier STRAW_SOUND = new Identifier(Constants.MOD_ID, "brush_step/straw");

    private final ITickCount tickCount;
    private final ITagLibrary tagLibrary;
    private long lastBrushCheck;

    public StepThroughBrushEffect(ITickCount tickCount, ITagLibrary tagLibrary) {
        this.tickCount = tickCount;
        this.tagLibrary = tagLibrary;
    }

    @Override
    public void tick(final EntityEffectInfo info) {
        var currentCount = this.tickCount.getTickCount();
        if (currentCount > this.lastBrushCheck) {
            this.lastBrushCheck = currentCount + BRUSH_INTERVAL;
            if (info.isRemoved())
                return;
            var entity = info.getEntity();
            if (shouldProcess(entity)) {
                var world = entity.method_48926();
                var pos = entity.getBlockPos();
                var feetPos = BlockPos.ofFloored(pos.getX(), pos.getY() + 0.25D, pos.getZ());

                if (!this.process(BlockEffectTags.STRAW_STEP, STRAW_SOUND, world, feetPos))
                    this.process(BlockEffectTags.BRUSH_STEP, BRUSH_SOUND, world, feetPos);
            }
        }
    }

    private boolean process(TagKey<Block> effectTag, Identifier factory, World world, BlockPos blockPos) {
        var block = world.getBlockState(blockPos);
        if (this.tagLibrary.is(effectTag, block)) {
            this.playSoundEffect(blockPos, factory, getVolumeScaling(world, blockPos, block));
            return true;
        } else {
            var headPos = blockPos.up();
            block = world.getBlockState(headPos);
            if (this.tagLibrary.is(effectTag, block)) {
                this.playSoundEffect(headPos, factory, getVolumeScaling(world, headPos, block));
                return true;
            }
        }

        return false;
    }

    private static float getVolumeScaling(World world, BlockPos pos, BlockState state) {
        final VoxelShape shape = state.getOutlineShape(world, pos);
        return shape.isEmpty() ? 1F : (float) shape.getBoundingBox().maxY;
    }

    private static boolean shouldProcess(LivingEntity entity) {
        if (entity.isSilent() || entity.isSpectator())
            return false;
        if (entity.sidewaysSpeed != 0 || entity.forwardSpeed != 0 || entity.upwardSpeed != 0)
            return true;
        return ((ILivingEntityExtended)entity).dsurround_isJumping();
    }

    private void playSoundEffect(BlockPos pos, Identifier factory, float volumeScale) {
       SOUND_LIBRARY.getSoundFactory(factory)
               .ifPresent(f -> {
                   var soundInstance = f.createAtLocation(pos, volumeScale);
                   this.playSound(soundInstance);
               });
    }
}
