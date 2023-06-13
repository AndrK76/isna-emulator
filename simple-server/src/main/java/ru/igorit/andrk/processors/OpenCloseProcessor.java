package ru.igorit.andrk.processors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.igorit.andrk.utils.MtParser;

import java.nio.charset.StandardCharsets;

@Component
public class OpenCloseProcessor implements DataProcessor {
    private static final Logger log = LoggerFactory.getLogger(OpenCloseProcessor.class);
    private static final String DOCUMENT = "ISNA_BVU_BA_OPEN_CLOSE";

    @Override
    public String document() {
        return DOCUMENT;
    }

    @Override
    public String process(String data) {
        log.debug(String.format("on start:%n%s%n", data));
        String clearedData = MtParser.Clear(data);
        log.debug(String.format("after clear:%n%s%n", clearedData));
        return clearedData;
    }

    @Override
    public void configure(byte[] config) {
        String cfgData = new String(config, StandardCharsets.UTF_8);
        log.debug(String.format("apply config:%n%s", cfgData));

    }

}
