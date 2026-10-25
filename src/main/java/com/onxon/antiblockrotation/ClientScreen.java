package com.onxon.antiblockrotation;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.awt.Color;
import java.util.function.Consumer;

public class ClientScreen extends Screen {
    private static final String TITLE = "Onxon client";
    private static final String[] LABELS = {"Name", "Balance", "Shards", "Kills", "Deaths", "Playtime"};

    private final Screen parent;
    private ButtonWidget toggle;

    private int panelX, panelY, panelW, panelH;

    public ClientScreen(Screen parent) {
        super(Text.literal(TITLE));
        this.parent = parent;
    }

    @Override
    protected void init() {
        Config c = Config.INSTANCE;
        panelW = 300;
        panelH = 290;
        panelX = width / 2 - panelW / 2;
        panelY = Math.max(6, height / 2 - panelH / 2);

        String[] values = {c.name, c.balance, c.shards, c.kills, c.deaths, c.playtime};
        @SuppressWarnings("unchecked")
        Consumer<String>[] setters = new Consumer[]{
                (Consumer<String>) s -> c.name = s,
                (Consumer<String>) s -> c.balance = s,
                (Consumer<String>) s -> c.shards = s,
                (Consumer<String>) s -> c.kills = s,
                (Consumer<String>) s -> c.deaths = s,
                (Consumer<String>) s -> c.playtime = s
        };

        int rowY = panelY + 74;
        for (int i = 0; i < LABELS.length; i++) {
            TextFieldWidget field = new TextFieldWidget(textRenderer, panelX + 90, rowY + i * 24, 190, 18, Text.literal(LABELS[i]));
            field.setMaxLength(48);
            field.setText(values[i] == null ? "" : values[i]);
            field.setPlaceholder(Text.literal("Original").formatted(net.minecraft.util.Formatting.DARK_GRAY));
            Consumer<String> setter = setters[i];
            field.setChangedListener(setter);
            addDrawableChild(field);
        }

        int btnY = rowY + LABELS.length * 24 + 8;
        toggle = ButtonWidget.builder(toggleText(), b -> {
            c.enabled = !c.enabled;
            b.setMessage(toggleText());
        }).dimensions(panelX + 20, btnY, 125, 20).build();
        addDrawableChild(toggle);

        addDrawableChild(ButtonWidget.builder(Text.literal("Done"), b -> close())
                .dimensions(panelX + panelW - 145, btnY, 125, 20).build());
    }

    private Text toggleText() {
        return Text.literal("Display: " + (Config.INSTANCE.enabled ? "ON" : "OFF"));
    }

    /** Own background (no blur) with a clean panel. */
    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, width, height, 0xA0050509);
        // panel
        context.fill(panelX - 1, panelY - 1, panelX + panelW + 1, panelY + panelH + 1, 0xFF2A2A40);
        context.fill(panelX, panelY, panelX + panelW, panelY + panelH, 0xF0101018);
        // top accent bar (animated gradient)
        float t = (System.currentTimeMillis() % 6000L) / 6000f;
        int segs = 30;
        int segW = panelW / segs + 1;
        for (int i = 0; i < segs; i++) {
            int col = gradient(t, i / (float) segs);
            int x = panelX + i * panelW / segs;
            context.fill(x, panelY, Math.min(x + segW, panelX + panelW), panelY + 3, col);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        drawFancyTitle(context);

        for (int i = 0; i < LABELS.length; i++) {
            int y = panelY + 74 + i * 24 + 5;
            context.drawText(textRenderer, Text.literal(LABELS[i]), panelX + 20, y, 0xFFB8B8D0, true);
        }

        context.drawCenteredTextWithShadow(textRenderer,
                Text.literal("Leave a box empty to keep the original. Use &a, &l etc. for colours."),
                width / 2, panelY + panelH - 16, 0xFF6C6C85);
    }

    private void drawFancyTitle(DrawContext context) {
        float scale = 3f;
        float t = (System.currentTimeMillis() % 6000L) / 6000f;

        int total = 0;
        int[] widths = new int[TITLE.length()];
        for (int i = 0; i < TITLE.length(); i++) {
            Text letter = Text.literal(String.valueOf(TITLE.charAt(i))).setStyle(Style.EMPTY.withBold(true));
            widths[i] = textRenderer.getWidth(letter);
            total += widths[i];
        }
        float startX = width / 2f - (total * scale) / 2f;
        float y = panelY + 18;

        float cursor = 0;
        for (int i = 0; i < TITLE.length(); i++) {
            int col = gradient(t, i / (float) TITLE.length());
            Text letter = Text.literal(String.valueOf(TITLE.charAt(i)))
                    .setStyle(Style.EMPTY.withBold(true).withColor(col & 0xFFFFFF));
            float wave = (float) Math.sin((t * Math.PI * 2) + i * 0.6) * 1.5f;

            context.getMatrices().pushMatrix();
            context.getMatrices().translate(startX + cursor * scale, y + wave);
            context.getMatrices().scale(scale, scale);
            context.drawText(textRenderer, letter, 0, 0, 0xFF000000 | (col & 0xFFFFFF), true);
            context.getMatrices().popMatrix();

            cursor += widths[i];
        }

        // underline accent
        int lineW = (int) (total * scale);
        int lx = width / 2 - lineW / 2;
        int ly = panelY + 18 + (int) (9 * scale) + 6;
        for (int i = 0; i < lineW; i++) {
            context.fill(lx + i, ly, lx + i + 1, ly + 1, gradient(t, i / (float) lineW));
        }
    }

    /** Cyan -> violet -> pink gradient that slowly drifts. */
    private static int gradient(float t, float pos) {
        float hue = 0.52f + 0.30f * (0.5f + 0.5f * (float) Math.sin((pos + t) * Math.PI * 2));
        return 0xFF000000 | (Color.HSBtoRGB(hue, 0.65f, 1f) & 0xFFFFFF);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void removed() {
        Config.save();
    }

    @Override
    public void close() {
        Config.save();
        if (client != null) client.setScreen(parent);
    }
}
