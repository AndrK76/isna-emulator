package ru.igorit.andrk.processors.mt;

public class ConfigFormatException extends RuntimeException{
    public ConfigFormatException(Throwable cause) {
        super(cause);
    }

    public ConfigFormatException(String message) {
        super(message);
    }
}
