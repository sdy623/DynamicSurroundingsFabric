package org.orecruncher.dsurround.lib;

import java.util.Comparator;
import net.minecraft.util.Identifier;

public class Comparers {

    /**
     * Because the comparison that Identifier uses compares the path prior to namespace, thus making sorting
     * for visual representation sucky.  That's a technical term.
     */
    public static final Comparator<Identifier> IDENTIFIER_NATURAL_COMPARABLE = Comparator.comparing(Identifier::getNamespace).thenComparing(Identifier::getPath);
}
