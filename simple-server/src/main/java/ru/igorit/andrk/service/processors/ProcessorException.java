package ru.igorit.andrk.service.processors;

import kz.icode.gov.integration.kgd.ErrorInfo;
import ru.igorit.andrk.service.ServiceFaultException;

public class ProcessorException extends ServiceFaultException {

    public ProcessorException(ErrorInfo errorInfo) {
        super(errorInfo);
    }

    public ProcessorException(ErrorInfo errorInfo, Throwable cause) {
        super(errorInfo, cause);
    }
}
