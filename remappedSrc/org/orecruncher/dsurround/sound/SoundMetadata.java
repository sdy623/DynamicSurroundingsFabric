package org.orecruncher.dsurround.sound;

import com.google.common.collect.ImmutableList;
import org.orecruncher.dsurround.config.data.SoundMetadataConfig;
import org.orecruncher.dsurround.config.data.SoundMetadataConfig.CreditEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public final class SoundMetadata {

    private final Text title;
    private final Text subTitle;
    private final List<Credit> credits;
    private final SoundCategory category;
    private final boolean isDefault;

    public SoundMetadata() {
        this.title = Text.empty();
        this.subTitle = Text.empty();
        this.credits = ImmutableList.of();
        this.category = SoundCategory.AMBIENT;
        this.isDefault = true;
    }

    public SoundMetadata(Identifier location) {
        this.title = Text.empty();
        this.subTitle = Text.empty();
        this.credits = ImmutableList.of();
        this.isDefault = false;
        this.category = this.estimateSoundSource(location);
    }

    public SoundMetadata(Identifier location, SoundMetadataConfig cfg) {
        Objects.requireNonNull(cfg);

        this.isDefault = false;

        this.title = cfg.title().map(Text::translatable).orElse(Text.empty());
        this.subTitle = cfg.subtitle().map(Text::translatable).orElse(Text.empty());

        if (cfg.credits() == null || cfg.credits().isEmpty()) {
            this.credits = ImmutableList.of();
        } else {
            var temp = new ArrayList<Credit>(cfg.credits().size());
            for (var entry : cfg.credits()) {
                var name = Text.of(Formatting.strip(entry.name()));
                var author = Text.of(Formatting.strip(entry.author()));
                var webSite = entry.website().map(website -> Text.of(Formatting.strip(website)));
                var license = Text.of(Formatting.strip(entry.license()));
                var creditEntry = new Credit(name, author, webSite, license);
                temp.add(creditEntry);
            }
            this.credits = ImmutableList.copyOf(temp);
        }

        this.category = cfg.category().orElseGet(() -> this.estimateSoundSource(location));
    }

    public boolean isDefault() {
        return this.isDefault;
    }

    private SoundCategory estimateSoundSource(Identifier location) {
        var path = location.getPath();
        if (path.startsWith("music"))
            return SoundCategory.MUSIC;
        if (path.startsWith("block"))
            return SoundCategory.BLOCKS;
        if (path.startsWith("entity"))
            return SoundCategory.HOSTILE;
        if (path.startsWith("weather"))
            return SoundCategory.WEATHER;
        if (path.startsWith("ambient"))
            return SoundCategory.AMBIENT;
        return SoundCategory.AMBIENT;
    }

    public record Credit(Text name, Text author, Optional<Text> webSite, Text license) {

    }

    /**
     * Gets the title configured in sounds.json, or EMPTY if not present.
     *
     * @return Configured title, or EMPTY if not present.
     */
    public Text getTitle() {
        return this.title;
    }

    /**
     * Gets the subtitle (subtitle) configured in sounds.json, or EMPTY if not present.
     *
     * @return Configured subtitle, or EMPTY if not present.
     */
    public Text getSubTitle() {
        return this.subTitle;
    }

    /**
     * Gets the credits configured for the sound event in sounds.json, or an empty list if not present.
     *
     * @return List containing zero or more strings describing the sound credits.
     */
    public List<Credit> getCredits() {
        return this.credits;
    }

    /**
     * Gets the sound category that has been configured or estimated from the location ID.
     */
    public SoundCategory getCategory() {
        return this.category;
    }
}