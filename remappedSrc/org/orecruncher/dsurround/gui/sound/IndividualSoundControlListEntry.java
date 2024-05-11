package org.orecruncher.dsurround.gui.sound;

import I;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.orecruncher.dsurround.config.IndividualSoundConfigEntry;
import org.orecruncher.dsurround.config.libraries.ISoundLibrary;
import org.orecruncher.dsurround.lib.GameUtils;
import org.orecruncher.dsurround.lib.Library;
import org.orecruncher.dsurround.lib.di.ContainerManager;
import org.orecruncher.dsurround.lib.gui.ColorPalette;
import org.orecruncher.dsurround.lib.gui.GuiHelpers;
import org.orecruncher.dsurround.lib.gui.TextWidget;
import org.orecruncher.dsurround.lib.platform.IPlatform;
import org.orecruncher.dsurround.sound.IAudioPlayer;
import org.orecruncher.dsurround.sound.SoundMetadata;
import org.orecruncher.dsurround.sound.SoundMetadata.Credit;
import java.util.*;

public class IndividualSoundControlListEntry extends ElementListWidget.Entry<IndividualSoundControlListEntry> implements AutoCloseable {

    private static final ISoundLibrary SOUND_LIBRARY = ContainerManager.resolve(ISoundLibrary.class);
    private static final IAudioPlayer AUDIO_PLAYER = ContainerManager.resolve(IAudioPlayer.class);
    private static final IPlatform PLATFORM = Library.PLATFORM;

    private static final int TOOLTIP_WIDTH = 300;

    private static final Style STYLE_MOD_NAME = Style.EMPTY.withColor(ColorPalette.GOLD);
    private static final Style STYLE_ID = Style.EMPTY.withColor(ColorPalette.SLATEGRAY);
    private static final Style STYLE_CATEGORY = Style.EMPTY.withColor(ColorPalette.FRESH_AIR);
    private static final Style STYLE_SUBTITLE = Style.EMPTY.withColor(ColorPalette.APRICOT).withItalic(true);
    private static final Style STYLE_CREDIT_NAME = Style.EMPTY.withColor(ColorPalette.GREEN);
    private static final Style STYLE_CREDIT_AUTHOR = Style.EMPTY.withColor(ColorPalette.WHITE);
    private static final Style STYLE_CREDIT_LICENSE = Style.EMPTY.withItalic(true).withColor(ColorPalette.MC_DARKAQUA);
    private static final Style STYLE_HELP = Style.EMPTY.withItalic(true).withColor(ColorPalette.KEY_LIME);

    private static final OrderedText VANILLA_CREDIT = Text.translatable("dsurround.text.soundconfig.vanilla").asOrderedText();
    private static final Collection<Text> VOLUME_HELP = GuiHelpers.getTrimmedTextCollection("dsurround.text.soundconfig.volume.help", TOOLTIP_WIDTH, STYLE_HELP);
    private static final Collection<Text> PLAY_HELP = GuiHelpers.getTrimmedTextCollection("dsurround.text.soundconfig.play.help", TOOLTIP_WIDTH, STYLE_HELP);
    private static final Collection<Text> CULL_HELP = GuiHelpers.getTrimmedTextCollection("dsurround.text.soundconfig.cull.help", TOOLTIP_WIDTH, STYLE_HELP);
    private static final Collection<Text> BLOCK_HELP = GuiHelpers.getTrimmedTextCollection("dsurround.text.soundconfig.block.help", TOOLTIP_WIDTH, STYLE_HELP);
    private static final int CONTROL_SPACING = 3;

    private final IndividualSoundConfigEntry config;
    private final TextWidget label;
    private final VolumeSliderControl volume;
    private final BlockButton blockButton;
    private final CullButton cullButton;
    private final @Nullable SoundPlayButton playButton;

    private final List<ClickableWidget> children = new ArrayList<>();
    private final List<OrderedText> cachedToolTip = new ArrayList<>();

    private ConfigSoundInstance soundPlay;

    public IndividualSoundControlListEntry(final IndividualSoundConfigEntry data, final boolean enablePlay) {
        this.config = data;

        this.label = new TextWidget(0, 0, 200, GameUtils.getTextRenderer().fontHeight, Text.literal(data.soundEventId.toString()), GameUtils.getTextRenderer());
        this.children.add(this.label);

        this.volume = new VolumeSliderControl(this, 0, 0);
        this.children.add(this.volume);

        this.blockButton = new BlockButton(this.config.block, this::toggleBlock);
        this.children.add(this.blockButton);

        this.cullButton = new CullButton(this.config.cull, this::toggleCull);
        this.children.add(this.cullButton);

        if (enablePlay) {
            this.playButton = new SoundPlayButton(this::play);
            this.children.add(this.playButton);
        } else {
            this.playButton = null;
        }
    }

    public int getWidth() {
        int width = this.label.getWidth();
        width += this.cullButton.getWidth() + this.blockButton.getWidth() + this.volume.getWidth() + 4 * CONTROL_SPACING;
        if (this.playButton != null)
            width += this.playButton.getWidth() + CONTROL_SPACING;
        return width;
    }

    public void setWidth(int width) {
        var fixedWidth = this.cullButton.getWidth() + this.blockButton.getWidth() + this.volume.getWidth() + 4 * CONTROL_SPACING;
        if (this.playButton != null)
            fixedWidth += this.playButton.getWidth() + CONTROL_SPACING;
        width -= fixedWidth;
        if (width < 100)
            width = 100;
        this.label.setWidth(width);
    }

    public void mouseMoved(double mouseX, double mouseY) {
        ClickableWidget child = this.findChild(mouseX, mouseY);
        if (child != null)
            child.mouseMoved(mouseX, mouseY);
    }

    @Override
    public @NotNull List<? extends Element> children() {
        // TODO:  What?
        return this.children;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        ClickableWidget child = this.findChild(mouseX, mouseY);
        if (child != null)
            return child.mouseClicked(mouseX, mouseY, button);
        return false;
    }

    @Override
    public @NotNull List<? extends Selectable> selectableChildren() {
        return ImmutableList.of();
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        ClickableWidget child = this.findChild(mouseX, mouseY);
        if (child != null)
            return child.mouseReleased(mouseX, mouseY, button);
        return false;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        ClickableWidget child = this.findChild(mouseX, mouseY);
        if (child != null)
            return child.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        return false;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double hAmount, double vAmount) {
        ClickableWidget child = this.findChild(mouseX, mouseY);
        if (child != null)
            return child.mouseScrolled(mouseX, mouseY, hAmount, vAmount);
        return false;
    }

    private ClickableWidget findChild(double mouseX, double mouseY) {
        if (this.isMouseOver(mouseX, mouseY)) {
            for (ClickableWidget e : this.children) {
                if (e.isMouseOver(mouseX, mouseY)) {
                    return e;
                }
            }
        }
        return null;
    }

    @Override
    public void render(final @NotNull DrawContext context, int index, int rowTop, int rowLeft, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean mouseOver, float partialTick_) {
        final var font = GameUtils.getTextRenderer();
        final int labelY = rowTop + (rowHeight - font.fontHeight) / 2;

        this.label.method_46421(rowLeft);
        this.label.method_46419(labelY);

        // Need to position the other controls appropriately
        int rightMargin = rowLeft + rowWidth;
        this.volume.method_46421(rightMargin - this.volume.getWidth());
        this.volume.method_46419(rowTop);
        this.volume.setHeight(rowHeight);
        rightMargin -= this.volume.getWidth() + CONTROL_SPACING;

        if (this.playButton != null) {
            this.playButton.method_46421(rightMargin - this.playButton.getWidth());
            this.playButton.method_46419(rowTop);
            this.playButton.setHeight(rowHeight);
            rightMargin -= this.playButton.getWidth() + CONTROL_SPACING;
        }

        this.blockButton.method_46421(rightMargin - this.blockButton.getWidth());
        this.blockButton.method_46419(rowTop);
        this.blockButton.setHeight(rowHeight);
        rightMargin -= this.blockButton.getWidth() + CONTROL_SPACING;

        this.cullButton.method_46421(rightMargin - this.cullButton.getWidth());
        this.cullButton.setHeight(rowHeight);
        this.cullButton.method_46419(rowTop);

        for (final ClickableWidget w : this.children)
            w.render(context, mouseX, mouseY, partialTick_);
    }

    protected void toggleBlock(ButtonWidget button) {
        if (button instanceof BlockButton bb) {
            this.config.block = bb.toggle();
        }
    }

    protected void toggleCull(ButtonWidget button) {
        if (button instanceof CullButton cb)
            this.config.cull = cb.toggle();
    }

    protected void play(final ButtonWidget button) {
        if (button instanceof SoundPlayButton sp) {
            if (this.soundPlay == null) {
                this.soundPlay = this.playSound(this.config);
                sp.play();
            } else {
                AUDIO_PLAYER.stop(this.soundPlay);
                this.soundPlay = null;
                sp.stop();
            }
        }
    }

    protected ConfigSoundInstance playSound(IndividualSoundConfigEntry entry) {
        var metadata = SOUND_LIBRARY.getSoundMetadata(entry.soundEventId);
        ConfigSoundInstance sound = ConfigSoundInstance.create(entry.soundEventId, metadata.getCategory(), () -> entry.volumeScale / 100F);
        AUDIO_PLAYER.play(sound);
        return sound;
    }

    @Override
    public void close() {
        if (this.soundPlay != null) {
            AUDIO_PLAYER.stop(this.soundPlay);
            this.soundPlay = null;
        }
    }

    public void tick() {
        if (this.soundPlay != null && this.playButton != null) {
            if (!AUDIO_PLAYER.isPlaying(this.soundPlay)) {
                this.soundPlay = null;
                this.playButton.stop();
            }
        }
    }

    protected List<OrderedText> getToolTip(final int mouseX, final int mouseY) {
        // Cache the static part of the tooltip if needed
        if (this.cachedToolTip.isEmpty()) {
            Identifier id = this.config.soundEventId;
            this.resolveDisplayName(id.getNamespace())
                    .ifPresent(name -> {
                        OrderedText modName = OrderedText.styledForwardsVisitedString(Objects.requireNonNull(Formatting.strip(name)), STYLE_MOD_NAME);
                        this.cachedToolTip.add(modName);
                    });

            @SuppressWarnings("ConstantConditions")
            OrderedText soundLocationId = OrderedText.styledForwardsVisitedString(id.toString(), STYLE_ID);

            this.cachedToolTip.add(soundLocationId);

            SoundMetadata metadata = SOUND_LIBRARY.getSoundMetadata(id);
            if (metadata != null) {
                if (!metadata.getTitle().equals(Text.empty()))
                    this.cachedToolTip.add(metadata.getTitle().asOrderedText());

                this.cachedToolTip.add(Text.literal(metadata.getCategory().toString()).fillStyle(STYLE_CATEGORY).asOrderedText());

                if (!metadata.getSubTitle().equals(Text.empty())) {
                    this.cachedToolTip.add(metadata.getSubTitle().copy().fillStyle(STYLE_SUBTITLE).asOrderedText());
                }

                if (!metadata.getCredits().isEmpty()) {
                    for (var credit : metadata.getCredits()) {
                        this.cachedToolTip.add(Text.empty().asOrderedText());
                        this.cachedToolTip.add(credit.name().copy().fillStyle(STYLE_CREDIT_NAME).asOrderedText());
                        this.cachedToolTip.add(credit.author().copy().fillStyle(STYLE_CREDIT_AUTHOR).asOrderedText());
                        if (credit.webSite().isPresent()) {
                            this.cachedToolTip.add(credit.webSite().get().copy().fillStyle(STYLE_CREDIT_AUTHOR).asOrderedText());
                        }
                        this.cachedToolTip.add(credit.license().copy().fillStyle(STYLE_CREDIT_LICENSE).asOrderedText());
                    }
                }
            }

            if (id.getNamespace().equals("minecraft")) {
                this.cachedToolTip.add(VANILLA_CREDIT);
            }
        }

        List<OrderedText> generatedTip = new ArrayList<>(this.cachedToolTip);

        Collection<Text> toAppend = null;
        if (this.volume.isMouseOver(mouseX, mouseY)) {
            toAppend = VOLUME_HELP;
        } else if (this.blockButton.isMouseOver(mouseX, mouseY)) {
            toAppend = BLOCK_HELP;
        } else if (this.cullButton.isMouseOver(mouseX, mouseY)) {
            toAppend = CULL_HELP;
        } else if (this.playButton != null && this.playButton.isMouseOver(mouseX, mouseY)) {
            toAppend = PLAY_HELP;
        }

        if (toAppend != null) {
            generatedTip.add(OrderedText.EMPTY);
            toAppend.forEach(e -> generatedTip.add(e.asOrderedText()));
        }

        return generatedTip;
    }

    private Optional<String> resolveDisplayName(String namespace) {
        var displayName = PLATFORM.getModDisplayName(namespace);
        if (displayName.isPresent())
            return displayName;

        // Could be a resource pack
        return GameUtils.getResourceManager().streamResourcePacks()
                .filter(pack -> pack.getNamespaces(ResourceType.CLIENT_RESOURCES).contains(namespace))
                .map(ResourcePack::getId)
                .findAny();
    }

    /**
     * Retrieves the updated data from the entry
     *
     * @return Updated IndividualSoundControl data
     */
    public IndividualSoundConfigEntry getData() {
        return this.config;
    }

}