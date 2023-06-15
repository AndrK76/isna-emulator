package ru.igorit.andrk.service;

import kz.bee.bip.syncchannel.v10.types.ErrorInfo;
import lombok.Getter;
import lombok.Setter;

public class ServiceFaultException extends RuntimeException{

    @Getter
    @Setter
    protected ErrorInfo errorInfo;


    public ServiceFaultException(ErrorInfo errorInfo,Throwable cause) {
        super(cause);
        this.errorInfo = errorInfo;
    }

    public ServiceFaultException(ErrorInfo errorInfo) {
        super();
        this.errorInfo = errorInfo;
    }
}
