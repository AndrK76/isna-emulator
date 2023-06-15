package ru.igorit.andrk.service.processors;

public class DataFormatFatalException extends RuntimeException{
    public DataFormatFatalException(String message) {
        super(message);
    }

    public DataFormatFatalException(Throwable cause) {
        super(cause);
    }
}
