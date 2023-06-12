package ru.igorit.andrk.processors;

import org.springframework.stereotype.Component;

@Component
public class OpenCloseProcessor implements DataProcessor{
    private static final String DOCUMENT="ISNA_BVU_BA_OPEN_CLOSE";

    @Override
    public String document() {
        return DOCUMENT;
    }

    @Override
    public String process(String data) {
        return null;
    }
}
