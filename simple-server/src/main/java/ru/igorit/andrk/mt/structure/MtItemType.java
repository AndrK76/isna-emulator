package ru.igorit.andrk.mt.structure;

import lombok.Getter;

public enum MtItemType {
    STRING("x"),
    DATE("d"),
    INTEGER("i");
    @Getter
    private final String code;

    MtItemType(String code){
        this.code=code;
    }
}
