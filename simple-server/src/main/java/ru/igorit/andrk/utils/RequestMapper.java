package ru.igorit.andrk.utils;

import org.springframework.lang.NonNull;
import ru.igorit.andrk.model.Request;

import java.time.OffsetDateTime;

public class RequestMapper {
    public static Request toModel(@NonNull kz.bee.bip.syncchannel.v10.types.Request soap){
        var soapInfo = soap.getRequestInfo();
        var xmlDate = soapInfo.getMessageDate();
        OffsetDateTime reqDate;
        if (xmlDate==null){
            reqDate = OffsetDateTime.now();
        } else{
            reqDate = DataHandler.toTimeWithTZ(xmlDate);
        }


        var ret = new Request(
                null,
                soapInfo.getMessageId(),
                soapInfo.getServiceId(),
                reqDate,
                (String) soap.getRequestData().getData());
        return ret;
    }

}