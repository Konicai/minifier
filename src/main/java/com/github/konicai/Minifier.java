package com.github.konicai;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Minifier {

    private static final Map<Character, String> RAW_TRANSLATIONS = new HashMap<>();
    private static final Map<Character, String> TRANSLATIONS;
    private static final List<Character> DECORATIONS = Arrays.asList('k', 'l', 'm', 'n', 'o');
    private static final Map<Character, String> CLOSERS;
    private static final char RESET = 'r';

    static {
        RAW_TRANSLATIONS.put('0', "black");
        RAW_TRANSLATIONS.put('1', "dark_blue");
        RAW_TRANSLATIONS.put('2', "dark_green");
        RAW_TRANSLATIONS.put('3', "dark_aqua");
        RAW_TRANSLATIONS.put('4', "dark_red");
        RAW_TRANSLATIONS.put('5', "dark_purple");
        RAW_TRANSLATIONS.put('6', "gold");
        RAW_TRANSLATIONS.put('7', "gray");
        RAW_TRANSLATIONS.put('8', "dark_gray");
        RAW_TRANSLATIONS.put('9', "blue");
        RAW_TRANSLATIONS.put('a', "green");
        RAW_TRANSLATIONS.put('b', "aqua");
        RAW_TRANSLATIONS.put('c', "red");
        RAW_TRANSLATIONS.put('d', "light_purple");
        RAW_TRANSLATIONS.put('e', "yellow");
        RAW_TRANSLATIONS.put('f', "white");

        RAW_TRANSLATIONS.put('k', "obf");
        RAW_TRANSLATIONS.put('l', "b");
        RAW_TRANSLATIONS.put('m', "st");
        RAW_TRANSLATIONS.put('n', "u");
        RAW_TRANSLATIONS.put('o', "i");

        TRANSLATIONS = RAW_TRANSLATIONS.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> "<" + e.getValue() + ">"));
        CLOSERS = RAW_TRANSLATIONS.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> "<\\" + e.getValue() + ">"));
    }

    private final char legacyChar;

    public Minifier(char legacyChar) {
        this.legacyChar = legacyChar;
    }

    public static Map<Character, String> translations() {
        return new HashMap<>(TRANSLATIONS);
    }

    public String translate(String string) {
        return translate(string.toCharArray());
    }

    public String translate(char[] chars) {
        final StringBuilder result = new StringBuilder(); // intermediate
        final int maxIndex = chars.length - 1; // max index

        boolean expectingCodes = false;
        StringBuilder colourClosers = new StringBuilder(); // everything that is awaiting closure
        StringBuilder decorationClosers = new StringBuilder(); // specifically decorations awaiting closure
        // this looks until the second last index, as formatting char would have to be at the last index
        for (int i = 0; i < maxIndex; i++) {
            char c = chars[i];
            if (expectingCodes) {
                // Next char can be a formatting code
                if (c == RESET) {
                    // close all previous colours/decorations
                    result.append(colourClosers);
                    result.append(decorationClosers);
                    colourClosers.setLength(0); // everything has been closed
                    decorationClosers.setLength(0);
                    continue; // No further action required
                }

                String replacement = TRANSLATIONS.get(c);
                if (replacement == null) {
                    // not a code, end expecting codes
                    expectingCodes = false;
                    result.append(c);
                } else {
                    if (DECORATIONS.contains(c)) {
                        // decoration character
                        decorationClosers.append(CLOSERS.get(c));
                    } else {
                        colourClosers.append(CLOSERS.get(c));
                        // color char, need to end all previous decorations
                        result.append(decorationClosers);
                        decorationClosers.setLength(0);
                    }

                    // char was a code, add replacement
                    result.append(replacement);
                }
            } else if (c == legacyChar) {
                // char is the start of a formatting sequence. expect codes after this
                expectingCodes = true;
            } else {
                // anything else, non formatting
                result.append(c);
            }
        }

        // Need to append the last char that we didn't iterate over
        result.append(chars[chars.length -1]);
        result.append(colourClosers); // close everything
        result.append(decorationClosers);
        return result.toString();
    }
}
