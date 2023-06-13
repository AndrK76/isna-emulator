package ru.igorit.andrk.processors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AuditMonitoringProcessor implements DataProcessor{

    private static final String DOCUMENT="AUDIT_MONITORING";
    private static final Logger log = LoggerFactory.getLogger(AuditMonitoringProcessor.class);

    @Override
    public String document() {
        return DOCUMENT;
    }

    @Override
    public String process(String data) {
        return null;
    }

    @Override
    public void configure(byte[] config) {
        log.debug("apply config");
    }
}
