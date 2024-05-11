package org.orecruncher.dsurround.config.biome;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;
import org.orecruncher.dsurround.config.data.AcousticConfig;
import org.orecruncher.dsurround.config.libraries.ISoundLibrary;
import org.orecruncher.dsurround.config.libraries.ITagLibrary;
import org.orecruncher.dsurround.config.SoundEventType;
import org.orecruncher.dsurround.config.BiomeTrait;
import org.orecruncher.dsurround.config.biome.biometraits.BiomeTraits;
import org.orecruncher.dsurround.config.data.BiomeConfigRule;
import org.orecruncher.dsurround.lib.random.IRandomizer;
import org.orecruncher.dsurround.lib.registry.RegistryUtils;
import org.orecruncher.dsurround.lib.WeightTable;
import org.orecruncher.dsurround.lib.collections.ObjectArray;
import org.orecruncher.dsurround.lib.di.ContainerManager;
import org.orecruncher.dsurround.lib.logging.IModLog;
import org.orecruncher.dsurround.lib.scripting.Script;
import org.orecruncher.dsurround.mixinutils.IBiomeExtended;
import org.orecruncher.dsurround.runtime.IConditionEvaluator;
import org.orecruncher.dsurround.sound.ISoundFactory;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.MusicSound;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

public final class BiomeInfo implements Comparable<BiomeInfo>, IBiomeSoundProvider {

    public static final int DEFAULT_ADDITIONAL_SOUND_CHANCE = 1000 / 4;
    public static final Script DEFAULT_SOUND_CHANCE = new Script(String.valueOf(1D / DEFAULT_ADDITIONAL_SOUND_CHANCE));
    private static final IModLog LOGGER = ContainerManager.resolve(IModLog.class);
    private static final ISoundLibrary SOUND_LIBRARY = ContainerManager.resolve(ISoundLibrary.class);
    private static final ITagLibrary TAG_LIBRARY = ContainerManager.resolve(ITagLibrary.class);
    private static final IConditionEvaluator CONDITION_EVALUATOR = ContainerManager.resolve(IConditionEvaluator.class);

    private final int version;
    private final Identifier biomeId;
    private final String biomeName;
    private final Optional<Biome> biome;
    private final BiomeTraits traits;
    private final boolean isRiver;
    private final boolean isOcean;
    private final boolean isDeepOcean;
    private final boolean isCave;
    private Collection<AcousticEntry> loopSounds = new ObjectArray<>();
    private Collection<AcousticEntry> moodSounds = new ObjectArray<>();
    private Collection<AcousticEntry> additionalSounds = new ObjectArray<>();
    private Collection<AcousticEntry> musicSounds = new ObjectArray<>();
    private Collection<String> comments = new ObjectArray<>();
    private TextColor fogColor;
    private Script additionalSoundChance = DEFAULT_SOUND_CHANCE;
    private Script moodSoundChance = DEFAULT_SOUND_CHANCE;

    public BiomeInfo(final int version, final Identifier id, final String name, BiomeTraits traits) {
        this(version, id, name, traits, null);
    }

    public BiomeInfo(final int version, final Identifier id, final String name, BiomeTraits traits, Biome biome) {
        this.version = version;
        this.biomeId = id;
        this.biomeName = name;
        this.biome = Optional.ofNullable(biome);

        this.traits = traits;
        this.isRiver = this.traits.contains(BiomeTrait.RIVER);
        this.isOcean = this.traits.contains(BiomeTrait.OCEAN);
        this.isDeepOcean = this.isOcean && this.traits.contains(BiomeTrait.DEEP);
        this.isCave = this.traits.contains(BiomeTrait.CAVES);

        // Check to see if the biome has a soundtrack. If so, add it to
        // the music list.
        if (biome != null) {
            var accessor = (IBiomeExtended)(Object)biome;
            accessor.dsurround_getSpecialEffects().getMusic()
                .ifPresent(m -> {
                    var factory = SOUND_LIBRARY.getSoundFactoryForMusic(m);
                    var entry = new AcousticEntry(factory, null);
                    this.musicSounds.add(entry);
                });
        }
    }

    public int getVersion() {
        return this.version;
    }

    public boolean isRiver() {
        return this.isRiver;
    }

    public boolean isOcean() {
        return this.isOcean;
    }

    public boolean isDeepOcean() {
        return this.isDeepOcean;
    }

    public boolean isCave() {
        return this.isCave;
    }

    public Identifier getBiomeId() {
        return this.biomeId;
    }

    void addComment(final String comment) {
        if (!StringUtils.isEmpty(comment)) {
            this.comments.add(comment);
        }
    }

    public String getBiomeName() {
        return this.biomeName;
    }

    public TextColor getFogColor() {
        return this.fogColor;
    }

    void setFogColor(final TextColor color) {
        this.fogColor = color;
    }

    void setAdditionalSoundChance(final Script chance) {
        this.additionalSoundChance = chance;
    }

    void setMoodSoundChance(final Script chance) {
        this.moodSoundChance = chance;
    }

    public BiomeTraits getTraits() {
        return this.traits;
    }

    public void mergeTraits(BiomeConfigRule configRule) {
        this.traits.mergeTraits(configRule.traits());
        configRule.comment().ifPresent(this::addComment);
    }

    public boolean hasTrait(String trait) {
        return this.traits.contains(trait);
    }

    @Override
    public Collection<ISoundFactory> findBiomeSoundMatches() {
        return this.loopSounds.stream()
                .filter(AcousticEntry::matches)
                .map(AcousticEntry::getAcoustic)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ISoundFactory> getExtraSound(final SoundEventType type, final IRandomizer random) {

        Collection<AcousticEntry> sourceList = null;

        switch (type) {
            case ADDITION -> {
                var chance = CONDITION_EVALUATOR.eval(this.additionalSoundChance);
                if (chance instanceof Double c) {
                    sourceList = random.nextDouble() < c ? this.additionalSounds : null;
                }
            }
            case MOOD -> {
                var chance = CONDITION_EVALUATOR.eval(this.moodSoundChance);
                if (chance instanceof Double c) {
                    sourceList = random.nextDouble() < c ? this.moodSounds : null;
                }
            }
            case MUSIC -> sourceList = this.musicSounds;
        }

        if (sourceList == null || sourceList.isEmpty())
            return Optional.empty();

        var candidates = sourceList.stream().filter(AcousticEntry::matches);
        return WeightTable.makeSelection(candidates);
    }

    @Override
    public Optional<MusicSound> getBackgroundMusic(IRandomizer random) {
        return this.getExtraSound(SoundEventType.MUSIC, random).map(ISoundFactory::createAsMusic);
    }

    void clearSounds() {
        this.loopSounds.clear();
        this.additionalSounds.clear();
        this.musicSounds.clear();
        this.moodSounds.clear();
        this.moodSoundChance = DEFAULT_SOUND_CHANCE;
        this.additionalSoundChance = DEFAULT_SOUND_CHANCE;
    }

    public void update(final BiomeConfigRule entry) {

        entry.comment().ifPresent(this::addComment);
        entry.fogColor().ifPresent(this::setFogColor);
        entry.additionalSoundChance().ifPresent(this::setAdditionalSoundChance);
        entry.moodSoundChance().ifPresent(this::setMoodSoundChance);

        // NOTE: We do not merge in traits here - it has already
        // been done prior to this point.

        if (entry.clearSounds()) {
            addComment("> Sound Clear");
            clearSounds();
        }

        for (final AcousticConfig sr : entry.acoustics()) {
            var factory = SOUND_LIBRARY.getSoundFactoryOrDefault(sr.factory());

            switch (sr.type()) {
                case LOOP -> {
                    final AcousticEntry acousticEntry = new AcousticEntry(factory, sr.conditions());
                    this.loopSounds.add(acousticEntry);
                }
                case MUSIC, MOOD, ADDITION -> {
                    final int weight = sr.weight();
                    final AcousticEntry acousticEntry = new AcousticEntry(factory, sr.conditions(), weight);

                    if (sr.type() == SoundEventType.ADDITION)
                        this.additionalSounds.add(acousticEntry);
                    else if (sr.type() == SoundEventType.MOOD)
                        this.moodSounds.add(acousticEntry);
                    else
                        this.musicSounds.add(acousticEntry);
                }
                default -> LOGGER.warn("Unknown SoundEventType %s", sr.type());
            }
        }
    }

    public void trim() {
        if (this.loopSounds.isEmpty())
            this.loopSounds = ImmutableList.of();
        if (this.moodSounds.isEmpty())
            this.moodSounds = ImmutableList.of();
        if (this.additionalSounds.isEmpty())
            this.additionalSounds = ImmutableList.of();
        if (this.musicSounds.isEmpty())
            this.musicSounds = ImmutableList.of();
        if (this.comments.isEmpty())
            this.comments = ImmutableList.of();
    }

    @Override
    public String toString() {
        final String indent = "    ";

        var tags = this.biome.map(b -> {
                    var holder = RegistryUtils.getRegistryEntry(RegistryKeys.BIOME, b);
                    if (holder.isEmpty())
                        return "null";
                    return TAG_LIBRARY.asString(TAG_LIBRARY.streamTags(holder.get()));
                }).orElse("null");

        final StringBuilder builder = new StringBuilder();
        builder.append("Biome [").append(getBiomeName()).append('/').append(this.biomeId).append("]");
        builder.append("\nTags: ").append(tags);
        builder.append("\n").append(getTraits().toString());

        if (this.fogColor != null) {
            builder.append("\nfogColor: ").append(this.fogColor.getHexCode());
        }

        if (!this.loopSounds.isEmpty()) {
            builder.append("\nLOOP sounds [\n");
            builder.append(this.loopSounds.stream().map(c -> indent + c.toString()).collect(Collectors.joining("\n")));
            builder.append("\n]");
        }

        if (!this.musicSounds.isEmpty()) {
            builder.append("\nMUSIC sounds [\n");
            builder.append(this.musicSounds.stream().map(c -> indent + c.toString()).collect(Collectors.joining("\n")));
            builder.append("\n]");
        }

        if (!this.additionalSounds.isEmpty()) {
            builder.append("\nADDITIONAL chance: ").append(this.additionalSoundChance);
            builder.append("\nADDITIONAL sounds [\n");
            builder.append(
                    this.additionalSounds.stream().map(c -> indent + c.toString()).collect(Collectors.joining("\n")));
            builder.append("\n]");
        }

        if (!this.moodSounds.isEmpty()) {
            builder.append("\nMOOD chance: ").append(this.additionalSoundChance);
            builder.append("\nMOOD sounds [\n");
            builder.append(this.moodSounds.stream().map(c -> indent + c.toString()).collect(Collectors.joining("\n")));
            builder.append("\n]");
        }

        if (!this.comments.isEmpty()) {
            builder.append("\ncomments:\n");
            builder.append(this.comments.stream().map(c -> indent + c).collect(Collectors.joining("\n")));
            builder.append('\n');
        }

        builder.append("\n");

        return builder.toString();
    }

    @Override
    public int compareTo(final BiomeInfo o) {
        return getBiomeId().compareTo(o.getBiomeId());
    }
}