package ru.igorit.andrk.service.processor;

import ru.igorit.andrk.service.DataProcessor;

public interface ProcessorFactory {

    DataProcessor getProcessor(String document);

    byte[] getProcCfg(String procKey);

}
