package org.orecruncher.dsurround.processing.scanner;

import java.util.List;
import net.minecraft.block.entity.BellBlockEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.orecruncher.dsurround.lib.GameUtils;
import org.orecruncher.dsurround.lib.world.WorldUtils;

public class VillageScanner extends AbstractScanner {

    private static final double VILLAGE_RANGE = 64;
    private static final int SCAN_INTERVAL = 20;

    private boolean isInVillage;

    public void tick(long tickCount) {
        // Only check once a second
        if (tickCount % SCAN_INTERVAL != 0)
            return;

        this.isInVillage = false;
        var world = GameUtils.getWorld().orElseThrow();
        PlayerEntity player = GameUtils.getPlayer().orElseThrow();

        // Only for surface worlds.  Other types of worlds are interpreted as not having villages.
        if (world.getDimension().natural()) {
            var playerEyes = player.getEyePos();
            Box box = Box.from(playerEyes).expand(VILLAGE_RANGE);

            var villagerEntities = world.getNonSpectatingEntities(VillagerEntity.class, box);

            if (!villagerEntities.isEmpty()) {
                // We have villagers.  Now find a bell!
                var bell = WorldUtils.getLoadedBlockEntities(world, blockEntity -> blockEntity instanceof BellBlockEntity && blockEntity.getPos().isWithinDistance(playerEyes, VILLAGE_RANGE));
                this.isInVillage = !bell.isEmpty();
            }
        }
    }

    public boolean isInVillage() {
        return this.isInVillage;
    }
}
