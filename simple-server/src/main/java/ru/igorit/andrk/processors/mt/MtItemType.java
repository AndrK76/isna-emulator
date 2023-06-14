package ru.igorit.andrk.processors.mt;

import lombok.Getter;

public enum MtItemType {
    STRING("x"),
    DATE("d");
    @Getter
    private String code;

    MtItemType(String code){
        this.code=code;
    }
}
