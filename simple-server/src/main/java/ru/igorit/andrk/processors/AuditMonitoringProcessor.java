package ru.igorit.andrk.processors;

import org.springframework.stereotype.Component;

@Component
public class AuditMonitoringProcessor implements DataProcessor{

    private static final String DOCUMENT="AUDIT_MONITORING";

    @Override
    public String document() {
        return null;
    }

    @Override
    public String process(String data) {
        return null;
    }
}
