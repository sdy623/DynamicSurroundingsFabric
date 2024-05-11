package org.orecruncher.dsurround.lib;


import java.util.Optional;
import net.minecraft.util.Language;

public final class Localization {

    public static String load(String key) {
        return Language.getInstance().get(key);
    }

    public static Optional<String> loadIfPresent(String key) {
        var result = Language.getInstance().get(key, null);
        return Optional.ofNullable(result);
    }
}
