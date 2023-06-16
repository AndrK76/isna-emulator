package ru.igorit.andrk.mt.structure;

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

    private final MtNode ownerNode;

    private final Map<MtItem, String> values = new HashMap<>();

    public void setItem(MtItem item, Object value) {
        String formattedVal = item.formatValue(value);
        values.put(item, formattedVal);
    }

    @Override
    public String toString() {
        return "MtBlock{" +
                "id=" + id +
                ", Text='" + text + '\'' +
                '}';
    }
}
