package ru.igorit.andrk.processors.mt;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MtContent {
    @Getter
    private final Map<MtFormatNodeInfo, MtNode> nodes = new HashMap<>();

    @Getter
    private final Map<String, MtItem> items = new HashMap<>();

    public MtContent(MtFormat format) {
        for (var fmt : format.getNodes()) {
            nodes.put(fmt, new MtNode(fmt));
        }
    }

    public List<MtNode> getNodeList() {
        return new ArrayList<>(nodes.values());
    }

    public List<MtBlock> getBlocks() {
        return getNodeList().stream().flatMap(r -> r.getBlocks().stream()).collect(Collectors.toList());
    }

    public Object getValue(String itemCode, int level) {
        if (!getItems().containsKey(itemCode)) {
            return null;
        }
        var item = getItems().get(itemCode);
        for (var block : getBlocks().stream().filter(r -> r.getId() == level).collect(Collectors.toList())) {
            if (block.getValues().containsKey(item)) {
                var val = block.getValues().get(item);
                return item.getFormattedValue(val);
            }
        }
        return null;
    }

    public Object getValue(String itemCode) {
        return getValue(itemCode, 0);
    }

    public MtNode getNode(String code) {
        var ret = getNodeList().stream().filter(r -> r.getCurrentCode().equals(code)).findFirst();
        if (ret.isPresent()) {
            return ret.get();
        }
        return null;
    }


    @Override
    public String toString() {
        return "MtContent{" +
                "nodes=" + nodes +
                '}';
    }
}
