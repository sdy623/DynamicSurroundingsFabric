package org.orecruncher.dsurround.sound;

import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import org.orecruncher.dsurround.Constants;
import org.orecruncher.dsurround.Configuration;
import org.orecruncher.dsurround.config.libraries.ISoundLibrary;
import org.orecruncher.dsurround.gui.sound.ConfigSoundInstance;
import org.orecruncher.dsurround.lib.GameUtils;
import org.orecruncher.dsurround.lib.system.ITickCount;
import org.orecruncher.dsurround.lib.di.ContainerManager;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.minecraft.client.sound.ElytraSoundInstance;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

/**
 * Handles sound block and culling.
 */
public final class SoundInstanceHandler {

    private static final ISoundLibrary SOUND_LIBRARY = ContainerManager.resolve(ISoundLibrary.class);
    private static final IAudioPlayer AUDIO_PLAYER = ContainerManager.resolve(IAudioPlayer.class);
    private static final ITickCount TICK_COUNT = ContainerManager.resolve(ITickCount.class);
    private static final Configuration.SoundSystem SOUND_SYSTEM_CONFIG = ContainerManager.resolve(Configuration.SoundSystem.class);
    private static final Configuration.SoundOptions THUNDERSTORM_CONFIG = ContainerManager.resolve(Configuration.SoundOptions.class);

    private static final Object2LongOpenHashMap<Identifier> SOUND_CULL = new Object2LongOpenHashMap<>(32);
    private static final Set<Identifier> THUNDER_SOUNDS = new HashSet<>();
    private static final Identifier THUNDER_SOUND_FACTORY = new Identifier(Constants.MOD_ID, "thunder");

    static {
        THUNDER_SOUNDS.add(SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER.getId());
    }

    private static boolean isSoundBlocked(final Identifier id) {
        return SOUND_LIBRARY.isBlocked(id);
    }

    private static boolean isSoundCulled(final Identifier id) {
        return SOUND_LIBRARY.isCulled(id);
    }

    private static boolean isSoundCulledLogical(final Identifier sound) {
        int cullInterval = SOUND_SYSTEM_CONFIG.cullInterval;
        if (cullInterval > 0 && isSoundCulled(sound)) {
            final long lastOccurrence = SOUND_CULL.getLong(Objects.requireNonNull(sound));
            final long currentTick = TICK_COUNT.getTickCount();
            if ((currentTick - lastOccurrence) < cullInterval) {
                return true;
            } else {
                // Set when it happened and fall through for remapping and stuff
                SOUND_CULL.put(sound, currentTick);
            }
        }
        return false;
    }

    /**
     * Special hook in the Minecraft SoundSystem that will be invoked when a sound is played.
     * Based on configuration, the sound play will be discarded if it is blocked or if it is
     * within its culling interval.
     *
     * @param theSound The sound that is being played
     * @return True if the sound play is to be blocked, false otherwise
     */
    public static boolean shouldBlockSoundPlay(final SoundInstance theSound) {
        // Don't block ConfigSoundInstances.  They are triggered from the individual sound config
        // options, and though it may be blocked, the player may wish to hear.
        if (theSound instanceof ConfigSoundInstance)
            return false;

        final Identifier id = theSound.getId();

        if (THUNDERSTORM_CONFIG.replaceThunderSounds && THUNDER_SOUNDS.contains(id)) {
            // Yeah - a bit reentrant but it should be good
            var soundFactory = SOUND_LIBRARY.getSoundFactory(THUNDER_SOUND_FACTORY);
            if (soundFactory.isPresent()) {
                var sound = soundFactory.get().createAtLocation(new Vec3d(theSound.getX(), theSound.getY(), theSound.getZ()));
                AUDIO_PLAYER.play(sound);
                return true;
            }
        }

        return isSoundBlocked(id) || isSoundCulledLogical(id);
    }

    /**
     * Determines if a sound is in range of a listener based on the sound's properties.
     *
     * @param listener Location of the listener
     * @param sound    The sound that is to be evaluated
     * @param pad      Additional distance to add when evaluating
     * @return true if the sound is within the attenuation distance; false otherwise
     */
    public static boolean inRange(final Vec3d listener, final SoundInstance sound, final int pad) {
        // Do not cancel if:
        // - The sound is global. Distance is not a factor.
        // - Weather related (thunder, lightning strike)
        if (sound.isRelative() || sound.getAttenuationType() == SoundInstance.AttenuationType.NONE || sound.getCategory() == SoundCategory.WEATHER)
            return true;

        // Do not cancel if it is the elytra flying sound. Due to the derpy implementation, the location of the
        // sound is at Origin after construction. Could mixin the class to correct, but this is safer.
        if (sound instanceof ElytraSoundInstance)
            return true;

        // If for some reason the sound is at origin let it through. Some mods submit sound instances attached to a
        // location, but do not initialize the location until it starts ticking.
        if (sound.getX() == 0 && sound.getY() == 0 && sound.getZ() == 0)
            return true;

        // Make sure a sound is assigned so that the volume check can work
        sound.getSoundSet(GameUtils.getSoundManager());

        // If it is a loud sound, let it through
        if (sound.getVolume() > 1F)
            return true;

        // Get the max sound range. Pad is added because a player may move into hearing
        // range before the sound terminates.
        int distSq = sound.getSound().getAttenuation() + pad;
        distSq *= distSq;
        return listener.squaredDistanceTo(sound.getX(), sound.getY(), sound.getZ()) < distSq;
    }

    public static boolean inRange(final Vec3d listener, final SoundInstance sound) {
        return inRange(listener, sound, 0);
    }
}
