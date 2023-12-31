package ru.igorit.andrk.utils;

import org.springframework.lang.NonNull;
import ru.igorit.andrk.model.Request;

import java.time.OffsetDateTime;
import java.util.UUID;

public class RequestMapper {
    public static Request toModel(@NonNull kz.icode.gov.integration.kgd.Request soap) {
        var soapInfo = soap.getRequestInfo();
        var xmlDate = soapInfo.getMessageDate();
        OffsetDateTime reqDate;
        if (xmlDate == null) {
            reqDate = OffsetDateTime.now();
        } else {
            reqDate = DataHandler.toTimeWithTZ(xmlDate);
        }

        return new Request(
                null,
                UUID.fromString(soapInfo.getMessageId()),
                soapInfo.getCorrelationId() == null ? null : UUID.fromString(soapInfo.getCorrelationId()),
                soapInfo.getServiceId(),
                reqDate,
                //оставляем (String) - В исходно XSD это был не String
                (String) soap.getRequestData().getData());
    }

}
