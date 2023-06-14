package ru.igorit.andrk.processors.mt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;
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

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private class DumpInfo {
        private String name;
        private String value;
        private int order;
        private long count;
    }

    public String dumpValues() {
        var countInfo = getBlocks().stream().flatMap(r -> r.getValues().entrySet().stream())
                .collect(Collectors.groupingBy(r -> r.getKey().getCode(), Collectors.counting()));
        var ret = new ArrayList<DumpInfo>();
        for (var block : getBlocks()) {
            block.getValues().entrySet().stream().forEach(r ->
                    ret.add(new DumpInfo(
                            r.getKey().getCode(),
                            r.getValue(),
                            block.getId(),
                            countInfo.get(r.getKey().getCode()))));
        }
        var nl$ = System.lineSeparator();
        StringBuilder sb = new StringBuilder("Value:" + nl$);
        ret.stream()
                .sorted(Comparator.comparing(DumpInfo::getCount)
                        .thenComparing(DumpInfo::getName)).forEach(r->{
                            sb.append(r.getName()+"\t"+r.getValue()+"\t"+r.order+nl$);
                });

        return sb.toString();
    }
}
