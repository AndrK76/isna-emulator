package ru.igorit.andrk.processors.mt;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MtBlockFormat {
    private final String splitter;
    private final String formatString;

    @Override
    public String toString() {
        return "MtBlockFormat{" +
                "splitter='" + splitter + '\'' +
                ", formatString='" + formatString + '\'' +
                '}';
    }
}
