package ru.igorit.andrk.processors.mt;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class MtFormat {
    private List<MtFormatNodeInfo> nodes = new ArrayList<>();
    private Map<String,MtBlockFormat> previewFormats = new HashMap<>();
    private Map<String,MtBlockFormat> detailFormats = new HashMap<>();

    @Override
    public String toString() {
        return "MtFormat{" +
                "nodes=" + nodes +
                ", previewFormats=" + previewFormats +
                ", detailFormats=" + detailFormats +
                '}';
    }
}
