package de.crazypokemondev.pixelmongenerations.bingo.client.gui;

import de.crazypokemondev.pixelmongenerations.bingo.common.config.PixelmonBingoConfig;
import de.crazypokemondev.pixelmongenerations.bingo.common.tasks.BingoTask;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class BingoCardScreen extends GuiScreen {
    private final Map<Integer, BingoTask> card;
    @Nullable
    private final LocalDateTime expirationTime;

    public static final int WIDTH = 256;
    public static final int HEIGHT = 256;
    public static final int GRID_OFFSET_X = 48;
    public static final int GRID_OFFSET_Y = 64;
    public static final int COL_WIDTH = 32;
    public static final int ROW_HEIGHT = 32;
    public static final int EXPIRATION_ICON_OFFSET_X = 229;
    public static final int EXPIRATION_ICON_OFFSET_Y = 226;

    public BingoCardScreen(Map<Integer, BingoTask> card, @Nullable LocalDateTime expirationTime) {
        this.card = card;
        this.expirationTime = expirationTime;
        zLevel = -0.5f;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawWorldBackground(0);
        mc.getTextureManager().bindTexture(GuiResources.background);
        int x = (width - WIDTH) / 2;
        int y = (height - HEIGHT) / 2;
        drawTexturedModalRect(x, y, 0, 0, WIDTH, HEIGHT);
        Optional<List<String>> toolTip = Optional.empty();
        for (int i = 0; i < 25; i++) {
            int row = i / 5;
            int col = i % 5;
            BingoTask task = card.get(i);
            if (task == null) continue;
            int iconX = x + GRID_OFFSET_X + col * COL_WIDTH;
            int iconY = y + GRID_OFFSET_Y + row * ROW_HEIGHT;
            task.drawIcon(this, iconX, iconY,
                    COL_WIDTH, ROW_HEIGHT, zLevel);
            if (mouseX >= iconX && mouseX < iconX + COL_WIDTH
                    && mouseY >= iconY && mouseY < iconY + ROW_HEIGHT) {
                toolTip = task.getToolTip();
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);

        if (expirationTime != null) {
            mc.getTextureManager().bindTexture(GuiResources.guiIcons);
            int expirationIconX = x + EXPIRATION_ICON_OFFSET_X;
            int expirationIconY = y + EXPIRATION_ICON_OFFSET_Y;
            drawTexturedModalRect(expirationIconX, expirationIconY,
                    GuiResources.EXPIRATION_ICON_X, GuiResources.EXPIRATION_ICON_Y,
                    GuiResources.EXPIRATION_ICON_WIDTH, GuiResources.EXPIRATION_ICON_HEIGHT);
            if (mouseX >= expirationIconX && mouseX < expirationIconX + GuiResources.EXPIRATION_ICON_WIDTH
                    && mouseY >= expirationIconY && mouseY < expirationIconY + GuiResources.EXPIRATION_ICON_HEIGHT) {
                toolTip = Optional.of(Collections.singletonList(
                        I18n.format("gui.pixelmongenerationsbingo.expires",
                                expirationTime.format(DateTimeFormatter.ofPattern(
                                        PixelmonBingoConfig.Formatting.dateTimeFormat, Locale.getDefault())))));
            }
        }

        toolTip.ifPresent(strings -> drawHoveringText(strings, mouseX, mouseY, fontRenderer));
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
