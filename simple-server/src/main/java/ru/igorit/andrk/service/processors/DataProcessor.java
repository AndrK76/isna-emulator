package ru.igorit.andrk.service.processors;

import java.util.UUID;

public interface DataProcessor {
    String document();

    ProcessResult process(String data, UUID messageId);

    void configure(byte[] config);
}
