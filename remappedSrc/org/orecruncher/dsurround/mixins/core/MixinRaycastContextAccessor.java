package org.orecruncher.dsurround.mixins.core;

import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RaycastContext.class)
public interface MixinRaycastContextAccessor {

    @Accessor("from")
    Vec3d dsurround_getStartPoint();

    @Accessor("from")
    @Mutable
    void dsurround_setStartPoint(Vec3d point);

    @Accessor("to")
    Vec3d dsurround_getEndPoint();

    @Accessor("to")
    @Mutable
    void dsurround_setEndPoint(Vec3d point);

    @Accessor("collisionContext")
    @Mutable
    void dsurround_setShapeContext(ShapeContext context);
}
