package org.orecruncher.dsurround.effects.entity;

import net.minecraft.block.BlockState;
import net.minecraft.client.option.GameOptions;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.orecruncher.dsurround.effects.particles.FrostBreathParticle;
import org.orecruncher.dsurround.lib.GameUtils;
import org.orecruncher.dsurround.lib.di.ContainerManager;
import org.orecruncher.dsurround.lib.seasons.ISeasonalInformation;
import org.orecruncher.dsurround.lib.system.ITickCount;
import org.orecruncher.dsurround.lib.random.MurmurHash3;
import org.orecruncher.dsurround.lib.world.WorldUtils;

public class BreathEffect extends EntityEffectBase {

    private static final ISeasonalInformation SEASONAL_INFORMATION = ContainerManager.resolve(ISeasonalInformation.class);

    private final ITickCount tickCount;
    private int seed;

    public BreathEffect(ITickCount tickCount) {
        this.tickCount = tickCount;
    }

    @Override
    public void activate(final EntityEffectInfo info) {
        if (info.isRemoved())
            this.seed = 0;
        else
            this.seed = MurmurHash3.hash(info.getEntity().getId()) & 0xFFFF;
    }

    @Override
    public void tick(final EntityEffectInfo info) {
        if (info.isRemoved())
            return;

        var entity = info.getEntity();
        if (!this.isBreathVisible(entity))
            return;

        final int c = (int) (this.tickCount.getTickCount() + this.seed);
        final BlockPos headPos = getHeadPosition(entity);
        final BlockState state = entity.method_48926().getBlockState(headPos);
        if (showWaterBubbles(state)) {
            final int air = entity.getAir();
            if (air > 0) {
                final int interval = c % 3;
                if (interval == 0) {
                    createBubbleParticle(false);
                }
            } else if (air == 0) {
                // Need to generate a bunch of bubbles due to drowning
                for (int i = 0; i < 8; i++) {
                    createBubbleParticle(true);
                }
            }
        } else {
            final int interval = (c / 10) % 8;
            if (interval < 3 && showFrostBreath(entity, state, headPos)) {
                createFrostParticle(entity);
            }
        }
    }

    protected boolean isBreathVisible(final LivingEntity entity) {
        final var player = GameUtils.getPlayer().orElseThrow();
        var settings = GameUtils.getGameSettings();
        if (entity.getId() == player.getId()) {
            return !(player.isSpectator() || settings.hudHidden);
        }
        return !entity.isInvisibleTo(player) && player.canSee(entity);
    }

    protected BlockPos getHeadPosition(final LivingEntity entity) {
        return BlockPos.ofFloored(entity.getEyePos());
    }

    protected boolean showWaterBubbles(final BlockState headBlock) {
        return !headBlock.getFluidState().isEmpty();
    }

    protected boolean showFrostBreath(final LivingEntity entity, final BlockState headBlock, final BlockPos pos) {
        if (headBlock.isAir()) {
            final World world = entity.method_48926();
            return SEASONAL_INFORMATION.isColdTemperature(world, pos);
        }
        return false;
    }

    protected void createBubbleParticle(boolean isDrowning) {
        //final BubbleBreathParticle p = new BubbleBreathParticle(getEntity(), isDrowning);
        //addParticle(p);
    }

    protected void createFrostParticle(LivingEntity entity) {
        var particle = new FrostBreathParticle(entity);
        this.addParticle(particle);
    }

}