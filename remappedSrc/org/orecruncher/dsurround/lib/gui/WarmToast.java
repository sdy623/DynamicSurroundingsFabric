package org.orecruncher.dsurround.lib.gui;

import I;
import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;

@SuppressWarnings("unused")
public class WarmToast  implements Toast {
    private static final Profile DEFAULT_PROFILE = new Profile(new Identifier("toast/advancement"), 5000, ColorPalette.GOLD, ColorPalette.WHITE);

    private static final int MAX_LINE_SIZE = 200;
    private static final int MIN_LINE_SIZE = 100;
    private static final int LINE_SPACING = 12;
    private static final int MARGIN = 10;

    private final Profile profile;

    private Text title;
    private List<OrderedText> messageLines;
    private long lastChanged;
    private boolean changed;
    private final int width;

    public static WarmToast multiline(MinecraftClient minecraft, Text title, Text body) {
        return multiline(minecraft, DEFAULT_PROFILE, title, body);
    }

    public static WarmToast multiline(MinecraftClient minecraft, Profile profile, Text title, Text body) {
        var font = minecraft.textRenderer;
        var list = font.wrapLines(body, MAX_LINE_SIZE);
        var titleSize = Math.min(MAX_LINE_SIZE, Math.max(MIN_LINE_SIZE, font.getWidth(title)));
        var lineSize = list.stream().mapToInt(font::getWidth).max().orElse(MIN_LINE_SIZE);
        int width = Math.max(titleSize, lineSize) + MARGIN * 3;
        return new WarmToast(profile, title, list, width);
    }

    private WarmToast(Profile profile, Text title, List<OrderedText> body, int width) {
        this.profile = profile;
        this.title = title;
        this.messageLines = body;
        this.width = width;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return MARGIN * 2 + Math.max(this.messageLines.size(), 1) * LINE_SPACING;
    }

    public void reset(Text component, @Nullable Text component2) {
        this.title = component;
        this.messageLines = nullToEmpty(component2);
        this.changed = true;
    }

    public @NotNull Toast.Visibility draw(@NotNull DrawContext guiGraphics, @NotNull ToastManager toastComponent, long lastChanged) {
        if (this.changed) {
            this.lastChanged = lastChanged;
            this.changed = false;
        }

        int i = this.getWidth();
        if (i == 160 && this.messageLines.size() <= 1) {
            guiGraphics.drawGuiTexture(this.profile.sprite, 0, 0, i, this.getHeight());
        } else {
            int renderHeight = this.getHeight();
            int lineRenderCount = Math.min(4, renderHeight - 28);
            this.renderBackgroundRow(guiGraphics, i, 0, 0, 28);

            for(int n = 28; n < renderHeight - lineRenderCount; n += 10) {
                this.renderBackgroundRow(guiGraphics, i, 16, n, Math.min(16, renderHeight - n - lineRenderCount));
            }

            this.renderBackgroundRow(guiGraphics, i, 32 - lineRenderCount, renderHeight - lineRenderCount, lineRenderCount);
        }

        if (this.messageLines.isEmpty()) {
            guiGraphics.drawText(toastComponent.getClient().textRenderer, this.title, 18, LINE_SPACING, this.profile.titleColor.getRgb(), false);
        } else {
            guiGraphics.drawText(toastComponent.getClient().textRenderer, this.title, 18, 7, this.profile.titleColor.getRgb(), false);

            for(int j = 0; j < this.messageLines.size(); ++j) {
                guiGraphics.drawText(toastComponent.getClient().textRenderer, this.messageLines.get(j), 18, 18 + j * LINE_SPACING, this.profile.bodyColor.getRgb(), false);
            }
        }

        double d = (double)this.profile.displayTime * toastComponent.getNotificationDisplayTimeMultiplier();
        long o = lastChanged - this.lastChanged;
        return (double)o < d ? Visibility.SHOW : Visibility.HIDE;
    }

    private void renderBackgroundRow(DrawContext guiGraphics, int i, int j, int k, int l) {
        int m = j == 0 ? 20 : 5;
        int n = Math.min(60, i - m);
        guiGraphics.drawGuiTexture(this.profile.sprite, 160, 32, 0, j, 0, k, m, l);

        for(int o = m; o < i - n; o += 64) {
            guiGraphics.drawGuiTexture(this.profile.sprite, 160, 32, 32, j, o, k, Math.min(64, i - o - n), l);
        }

        guiGraphics.drawGuiTexture(this.profile.sprite, 160, 32, 160 - n, j, i - n, k, n, l);
    }

    private static ImmutableList<OrderedText> nullToEmpty(@Nullable Text component) {
        return component == null ? ImmutableList.of() : ImmutableList.of(component.asOrderedText());
    }

    public record Profile(Identifier sprite, int displayTime, TextColor titleColor, TextColor bodyColor) {

    }
}
