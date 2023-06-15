package ru.igorit.andrk.service.processors;

import lombok.Getter;

@Getter
public class DataContentException extends RuntimeException {
    private String dataErrorMessage;

    public DataContentException(String message, Throwable cause) {
        super(cause);
        this.dataErrorMessage=message;
    }
}
