package ru.igorit.andrk.processors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.igorit.andrk.processors.mt.MtConfigParser;
import ru.igorit.andrk.processors.mt.MtFormat;
import ru.igorit.andrk.processors.mt.MtParser;

@Component
public class OpenCloseProcessor implements DataProcessor {
    private static final Logger log = LoggerFactory.getLogger(OpenCloseProcessor.class);
    private static final String DOCUMENT = "ISNA_BVU_BA_OPEN_CLOSE";

    private final MtFormat inputFormat = new MtFormat();

    @Override
    public String document() {
        return DOCUMENT;
    }

    @Override
    public String process(String data) {
        var content = MtParser.parsePreview(data, inputFormat);
        var format = (String)content.getValue("codeform");
        if (format.equals("A03")){
            content.getNode("ACCOUNT").setCurrentCode("ACCOUNT_CHANGE");
        }
        MtParser.parseFinal(content,inputFormat);
        log.debug(content.dumpValues());

        return "";
    }

    @Override
    public void configure(byte[] config) {
        log.debug("apply config");
        MtConfigParser.parseInputFormatFromXML(config, inputFormat);
        log.trace("Input Config: {}", inputFormat);
    }




}
