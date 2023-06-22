package ru.igorit.andrk.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.igorit.andrk.model.Response;

import java.time.OffsetDateTime;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class ResponseForRequestDTO {
    private Long id;
    private UUID messageId;
    private String serviceId;
    private Boolean isSuccess;
    private OffsetDateTime responseDate;
    private String statusCode;
    private String statusMessage;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public ResponseForRequestDTO(UUID messageId, String serviceId, Boolean isSuccess, OffsetDateTime responseDate, String statusCode, String statusMessage) {
        this(null, messageId, serviceId, isSuccess, responseDate, statusCode, statusMessage);
    }

    public static ResponseForRequestDTO create(Response response) {
        return new ResponseForRequestDTO(response.getId(), response.getMessageId(), response.getServiceId(), response.getIsSuccess(),
                response.getResponseDate(), response.getStatusCode(), response.getStatusMessage());
    }

}
