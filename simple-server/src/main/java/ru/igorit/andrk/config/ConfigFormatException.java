package ru.igorit.andrk.config;

public class ConfigFormatException extends RuntimeException{
    public ConfigFormatException(Throwable cause) {
        super(cause);
    }

    public ConfigFormatException(String message) {
        super(message);
    }
}
