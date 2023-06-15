package ru.igorit.andrk.service.processors.auditmonitoring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.igorit.andrk.service.processors.DataProcessor;
import ru.igorit.andrk.service.processors.ProcessResult;

import java.util.UUID;

@Service
public class AuditMonitoringProcessor implements DataProcessor {

    private static final String DOCUMENT="AUDIT_MONITORING";
    private static final Logger log = LoggerFactory.getLogger(AuditMonitoringProcessor.class);

    @Override
    public String document() {
        return DOCUMENT;
    }

    @Override
    public ProcessResult process(String data, UUID messageId) {
        return null;
    }

    @Override
    public void configure(byte[] config) {
        log.debug("apply config");
    }
}
