package ru.igorit.andrk.service.processors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
public class ProcessResult {
    private final String statusCode;
    @Setter
    private String statusMessage;
    @Setter
    private String data;
    public ProcessResult(String statusCode, String statusMessage) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }
    public static ProcessResult successResult(){
        return new ProcessResult("OK","Message processed successfully");
    }

    public String getDataIgnoreCR(){
        return data.replace("\r","");
    }
}
