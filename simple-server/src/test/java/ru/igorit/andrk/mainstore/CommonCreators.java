package ru.igorit.andrk.mainstore;

import ru.igorit.andrk.model.Request;

import java.time.OffsetDateTime;
import java.util.UUID;

public class CommonCreatrors {
    pu Request makeMainRequest() {
        var messageID = UUID.randomUUID();
        var serviceName = "Test";
        var requestDate = OffsetDateTime.now();
        var data = "";
        return new Request(null, messageID, messageID, serviceName, requestDate, data);
    }
}
