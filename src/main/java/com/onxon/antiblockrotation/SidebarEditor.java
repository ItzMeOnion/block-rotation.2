package com.onxon.antiblockrotation;

import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Optional;

/**
 * Client-side only. Rewrites the text of the sidebar scoreboard on your own screen.
 */
public final class SidebarEditor {
    private SidebarEditor() {}

    private static boolean active = false;
    private static int index = 0;

    public static void begin() {
        active = true;
        index = 0;
    }

    public static void end() {
        active = false;
    }

    /** Called for each sidebar line, in display order (top to bottom). */
    public static MutableText line(MutableText original) {
        if (!active) return original;
        Config c = Config.INSTANCE;
        int i = index++;
        if (!c.enabled) return original;
        String[] lines = c.lines();
        if (i >= lines.length) return original;
        String custom = lines[i];
        if (custom == null || custom.isEmpty()) return original;
        return replaceValue(original, custom);
    }

    /** Called for the sidebar title (your name). */
    public static Text title(Text original) {
        if (!active) return original;
        Config c = Config.INSTANCE;
        if (!c.enabled || c.name == null || c.name.isEmpty()) return original;
        return parse(c.name, original.getStyle());
    }

    /**
     * Keeps everything before the first digit (the symbol and the space after it)
     * exactly as the server sent it, and swaps the value after it for your text.
     */
    private static MutableText replaceValue(Text original, String custom) {
        String plain = original.getString();
        int cut = cutPoint(plain);

        MutableText prefix = Text.empty();
        Style[] valueStyle = {null};
        int[] seen = {0};

        original.visit((style, str) -> {
            int start = seen[0];
            if (start < cut) {
                int take = Math.min(str.length(), cut - start);
                if (take > 0) {
                    prefix.append(Text.literal(str.substring(0, take)).setStyle(style));
                }
                if (take < str.length() && valueStyle[0] == null) {
                    valueStyle[0] = style;
                }
            } else if (valueStyle[0] == null) {
                valueStyle[0] = style;
            }
            seen[0] = start + str.length();
            return Optional.empty();
        }, Style.EMPTY);

        Style base = valueStyle[0] != null ? valueStyle[0] : Style.EMPTY;
        MutableText result = Text.empty();
        result.append(prefix);
        result.append(parse(custom, base));
        return result;
    }

    /** Index where the value starts: the first digit, or after the first symbol if there are no digits. */
    private static int cutPoint(String s) {
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch >= '0' && ch <= '9') return i;
        }
        int i = 0;
        while (i < s.length() && Character.isWhitespace(s.charAt(i))) i++;
        if (i < s.length()) i += Character.charCount(s.codePointAt(i));
        while (i < s.length() && Character.isWhitespace(s.charAt(i))) i++;
        return i;
    }

    /** Turns "&aHello &lworld" into styled text. */
    public static MutableText parse(String input, Style base) {
        MutableText out = Text.empty();
        Style style = base;
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            if ((ch == '&' || ch == '§') && i + 1 < input.length()) {
                Formatting f = Formatting.byCode(Character.toLowerCase(input.charAt(i + 1)));
                if (f != null) {
                    if (buf.length() > 0) {
                        out.append(Text.literal(buf.toString()).setStyle(style));
                        buf.setLength(0);
                    }
                    style = f == Formatting.RESET ? base : style.withFormatting(f);
                    i++;
                    continue;
                }
            }
            buf.append(ch);
        }
        if (buf.length() > 0) out.append(Text.literal(buf.toString()).setStyle(style));
        return out;
    }
}
