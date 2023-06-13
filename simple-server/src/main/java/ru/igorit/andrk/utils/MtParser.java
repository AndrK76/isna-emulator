package ru.igorit.andrk.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MtParser {
    private static final Logger log = LoggerFactory.getLogger(MtParser.class);

    public static String Clear(String origText) {
        List<Character> smbRes = new ArrayList<>();
        String retText = "";
        char[] symbols = origText.toCharArray();

        //remove { | {-
        clearAtStart(symbols, smbRes, '{', '-');

        Collections.reverse(smbRes);
        symbols = getChars(smbRes);

        //remove } | }-
        clearAtStart(symbols, smbRes, '}', '-');
        symbols = getChars(smbRes);

        //remove LF | CR
        while (clearAtStart(symbols, smbRes, '\n', '\r')) {
            symbols = getChars(smbRes);
        }

        //remove LF | LF
        while (clearAtStart(symbols, smbRes, '\n', '\n')) {
            symbols = getChars(smbRes);
        }

        Collections.reverse(smbRes);
        retText = smbRes.stream().map(String::valueOf).collect(Collectors.joining());
        return retText;
    }

    private static char[] getChars(List<Character> smbRes) {
        char[] symbols;
        symbols = smbRes.stream().map(String::valueOf).collect(Collectors.joining()).toCharArray();
        return symbols;
    }

    private static boolean clearAtStart(char[] symbols, List<Character> smbRes, char first, char second) {
        smbRes.clear();
        boolean cleared = false;
        boolean inFindFirst = true;
        boolean inFindSecond = false;
        for (int i = 0; i < symbols.length; i++) {
            if (inFindFirst && symbols[i] == first) {
                inFindSecond = true;
                cleared = true;
            } else if (inFindSecond && symbols[i] == second) {
                inFindSecond = false;
            } else if (inFindSecond) {
                inFindSecond = false;
                smbRes.add(symbols[i]);
            } else {
                smbRes.add(symbols[i]);
            }
            inFindFirst = false;
        }
        return cleared;
    }
}
