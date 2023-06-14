package ru.igorit.andrk.processors.mt;

import lombok.Getter;

@Getter
public enum MtNodeCountMode {
    ZeroOrOne("0..1"),
    One("1..1"),
    Many("0..N");
    private String name;
    MtNodeCountMode(String name){
        this.name=name;
    }

    @Override
    public String toString() {
        return "'" + name + "'";
    }
}
