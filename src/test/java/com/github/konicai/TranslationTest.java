package com.github.konicai;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class TranslationTest {

    private static final char SECTION = '§';
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacySection();
    private static final MiniMessage MINI = MiniMessage.miniMessage();
    private static final Minifier minifier = new Minifier(SECTION);

    private static void assertMiniEquals(String expected, String actual) {
        Assertions.assertEquals(MINI.deserialize(expected), MINI.deserialize(actual));
    }

    @Test
    public void testTranslations() {
        final String text = "alphabet";
        Map<Character, String> translations = Minifier.translations();
        for (char c : translations.keySet()) {
            // need to add empty string to force adding two chars as string
            String legacy = SECTION + "" + c + " " + text;
            Assertions.assertEquals(MINI.deserialize(translations.get(c) + " " + text), LEGACY.deserialize(legacy));
        }
    }

    @Test
    public void testColours() {
        String legacy = "§6Hello §0there.";
        String actual = minifier.translate(legacy);
        String expected = "<gold>Hello <black>there.";
        assertMiniEquals(expected, actual);
    }

    @Test
    public void testReset() {
        String legacy = "§6oHello §rthere.";
        String actual = minifier.translate(legacy);
        assertMiniEquals("<gold><i>Hello </i></gold>there.", actual);
    }

    @Test
    public void testFormat() {
        String legacy = "§lHello §othere.";
        String actual = minifier.translate(legacy);
        assertMiniEquals("<b>Hello <i>there.</i></b>", actual);
    }

    @Test
    public void testColourAfterFormat() {
        String legacy = "§nThe§l quick§o brown§6 fox...";
        String actual = minifier.translate(legacy);
        String expected = "<u>The<b> quick<i> brown</i></b></u><gold> fox...</gold>";
        assertMiniEquals(expected, actual);
    }

    @Test
    public void testImplicitCosing() {
        String legacy = "§nThe§l quick§o brown§6 fox...";
        String actual = minifier.translate(legacy);
        String explicit = "<u>The<b> quick<i> brown</i></b></u><gold> fox...</gold>";
        String implicit = "<u>The<b> quick<i> brown</u><gold> fox...</gold>";
        assertMiniEquals(explicit, actual);
        assertMiniEquals(implicit, actual);
    }

    @Test
    public void stringBuilderAppendingTest() {
        StringBuilder a = new StringBuilder("a");
        StringBuilder b = new StringBuilder("b");
        a.append(b);
        String before = a.toString();
        b.append(5);
        String after = a.toString();
        Assertions.assertEquals(before, after);
    }
}
