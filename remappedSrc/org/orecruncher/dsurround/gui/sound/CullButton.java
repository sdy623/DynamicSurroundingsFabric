package org.orecruncher.dsurround.gui.sound;

import net.minecraft.util.Identifier;
import org.orecruncher.dsurround.lib.Library;
import org.orecruncher.dsurround.lib.gui.ToggleButton;

public class CullButton extends ToggleButton {

    // These are 20x20 sprites
    private static final Identifier CULL_ON_SYMBOL = new Identifier(Library.MOD_ID, "controls/cull_on");
    private static final Identifier CULL_OFF_SYMBOL = new Identifier(Library.MOD_ID, "controls/cull_off");

    public CullButton(boolean initialState, PressAction onPress) {
        super(initialState, CULL_ON_SYMBOL, CULL_OFF_SYMBOL, onPress);
    }
}
