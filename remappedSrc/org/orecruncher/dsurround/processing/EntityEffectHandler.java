package org.orecruncher.dsurround.processing;

import I;
import Z;
import java.util.List;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.orecruncher.dsurround.Configuration;
import org.orecruncher.dsurround.config.libraries.IEntityEffectLibrary;
import org.orecruncher.dsurround.effects.entity.EntityEffectInfo;
import org.orecruncher.dsurround.eventing.CollectDiagnosticsEvent;
import org.orecruncher.dsurround.lib.logging.IModLog;

public class EntityEffectHandler  extends AbstractClientHandler {

    private final IEntityEffectLibrary entityEffectLibrary;
    private int entityCount;
    private int entityEffectsTicked;

    public EntityEffectHandler(Configuration config, IEntityEffectLibrary entityEffectLibrary, IModLog logger) {
        super("EntityEffect Handler", config, logger);

        this.entityEffectLibrary = entityEffectLibrary;
    }

    private int effectRange() {
        return this.config.entityEffects.entityEffectRange;
    }

    private int scanRange() {
        var range = this.effectRange();
        return range + (range >> 1);
    }

    @Override
    public void process(final PlayerEntity player) {

        var world = player.method_48926();

        this.entityCount = 0;
        this.entityEffectsTicked = 0;

        // Get living entities in the world. Since the API does some fancy tracking of entities, we create a box
        // larger than the normal range size.
        var worldBox = Box.from(player.getEyePos()).expand(this.scanRange());
        var loadedEntities = world.getNonSpectatingEntities(LivingEntity.class, worldBox);

        for (var entity : loadedEntities) {
            this.entityCount++;
            var hasInfo = this.entityEffectLibrary.doesEntityEffectInfoExist(entity);
            var inRange = entity.isInRange(player, this.effectRange());
            EntityEffectInfo info = null;

            if (!hasInfo && entity.isAlive()) {
                // If it does not have info, but is alive, and is not a spectator, get info for it.
                if (inRange) {
                    info = this.entityEffectLibrary.getEntityEffectInfo(entity);
                }
            } else if (hasInfo) {
                // If it does have info, get whatever is currently cached
                info = this.entityEffectLibrary.getEntityEffectInfo(entity);
            }

            if (info != null) {
                if (inRange && info.isAlive() && !entity.isSpectator()) {
                    if (!info.isDefault()) {
                        this.entityEffectsTicked++;
                        info.tick();
                    }
                } else {
                    info.deactivate();
                    this.entityEffectLibrary.clearEntityEffectInfo(entity);
                }
            }
        }
    }

    @Override
    protected void gatherDiagnostics(CollectDiagnosticsEvent event) {
        event.add(CollectDiagnosticsEvent.Section.Systems, "Entity effects (range %d/%d): entities %d, ticked %d".formatted(this.effectRange(), this.scanRange(), this.entityCount, this.entityEffectsTicked));
    }
}
