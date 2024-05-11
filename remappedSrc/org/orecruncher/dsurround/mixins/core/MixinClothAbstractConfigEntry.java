package org.orecruncher.dsurround.mixins.core;

import me.shedaniel.clothconfig2.api.AbstractConfigEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

/**
 * NOTE: This mixin will fail application if Cloth Config is not present. Not harmful, just emits noise into logs
 * and can make some folks concerned.
 */
@Mixin(AbstractConfigEntry.class)
public class MixinClothAbstractConfigEntry {

    /**
     * @author OreCruncher
     * @reason Preserve style of Component.  The current implementation overrides color settings to force Gray.
     */
    @Overwrite
    public Text getDisplayedFieldName() {
        var self = (AbstractConfigEntry)((Object)this);
        MutableText text = self.getFieldName().copy();
        boolean hasError = self.getConfigError().isPresent();
        boolean isEdited = self.isEdited();

        if (!hasError && !isEdited) {
            // If the text entry does not have a color set, force
            // to gray.
            var color = text.getStyle().getColor();
            if (color == null)
                text = text.formatted(Formatting.GRAY);
        }

        if (hasError) {
            text = text.formatted(Formatting.RED);
        }

        if (isEdited) {
            text = text.formatted(Formatting.ITALIC);
        }

        if (!self.isEnabled()) {
            text = text.formatted(Formatting.DARK_GRAY);
        }

        return text;
    }

}
