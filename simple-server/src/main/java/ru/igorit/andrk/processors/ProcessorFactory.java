package ru.igorit.andrk.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProcessorFactory {

    @Autowired
    private List<DataProcessor> processors;

    private Map<String,DataProcessor> processorMap;
    //private Map<DataProcessor> processorState;

    public ProcessorFactory(List<DataProcessor> processors) {
        this.processors = processors;
        processorMap = processors.stream().collect(Collectors.toMap(DataProcessor::document, p->p));
    }

    public DataProcessor getProcessor(String document){
        return processorMap.get(document);
    }
}
