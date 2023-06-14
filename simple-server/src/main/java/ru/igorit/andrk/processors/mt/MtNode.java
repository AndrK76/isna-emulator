package ru.igorit.andrk.processors.mt;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class MtNode {
    private final MtFormatNodeInfo format;
    private final List<MtBlock> blocks = new ArrayList<>();

    private String currentCode;

    public MtNode(MtFormatNodeInfo format) {
        this.format = format;
        currentCode = format.getNodeName();
    }

    @Override
    public String toString() {
        return "MtNode{" +
                "format=" + format.getNodeName() +
                ", blocks=" + blocks +
                ", currentCode='" + currentCode + '\'' +
                '}';
    }
}
