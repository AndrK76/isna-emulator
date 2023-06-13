package ru.igorit.andrk.processors;

public interface DataProcessor {
    String document();

    String process(String data);

    void configure(byte[] config);
}
