package org.orecruncher.dsurround.lib;

import net.minecraft.world.World;
import org.orecruncher.dsurround.Constants;

public enum DayCycle {

    NO_SKY("NoSky"),
    SUNRISE("Sunrise"),
    SUNSET("Sunset"),
    DAYTIME("Daytime"),
    NIGHTTIME("Nighttime");

    // Thresholds are in degrees.  Noon is 0 degrees, and midnight is 180
    private static final float DAYTIME_THRESHOLD = 274;
    private static final float SUNRISE_THRESHOLD = DAYTIME_THRESHOLD - 15F;
    private static final float NIGHTTIME_THRESHOLD = 94F;
    private static final float SUNSET_THRESHOLD = NIGHTTIME_THRESHOLD - 15F;

    private final String localizeString;

    DayCycle(final String localName) {
        this.localizeString = Constants.MOD_ID + ".format." + localName;
    }

    public static boolean isDaytime(final World world) {
        return getCycle(world) == DayCycle.DAYTIME;
    }

    public static boolean isNighttime(final World world) {
        return getCycle(world) == DayCycle.NIGHTTIME;
    }

    public static boolean isSunrise(final World world) {
        return getCycle(world) == DayCycle.SUNRISE;
    }

    public static boolean isSunset(final World world) {
        return getCycle(world) == DayCycle.SUNSET;
    }

    public static DayCycle getCycle(final World world) {
        if (world.getDimension().hasCeiling() || !world.getDimension().hasSkyLight())
            return DayCycle.NO_SKY;

        final float angleDegrees = world.getSkyAngle(0) * 360F;

        if (angleDegrees > DAYTIME_THRESHOLD)
            return DayCycle.DAYTIME;
        if (angleDegrees > SUNRISE_THRESHOLD)
            return DayCycle.SUNRISE;
        if (angleDegrees > NIGHTTIME_THRESHOLD)
            return DayCycle.NIGHTTIME;
        if (angleDegrees > SUNSET_THRESHOLD)
            return DayCycle.SUNSET;
        return DayCycle.DAYTIME;
    }

    public static float getMoonSize(final World world) {
        return world.getMoonSize();
    }

    public String getFormattedName() {
        return Localization.load(this.localizeString);
    }

}