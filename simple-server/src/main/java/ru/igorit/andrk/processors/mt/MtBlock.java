package ru.igorit.andrk.processors.mt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@RequiredArgsConstructor
public class MtBlock {

    private final int id;
    private final String text;

    private final Map<MtItem, String> values = new HashMap<>();

    @Override
    public String toString() {
        return "MtBlock{" +
                "id=" + id +
                ", Text='" + text + '\'' +
                '}';
    }
}
