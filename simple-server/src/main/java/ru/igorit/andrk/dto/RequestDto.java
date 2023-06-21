package ru.igorit.andrk.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import ru.igorit.andrk.model.Request;

import java.time.OffsetDateTime;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class RequestDto {
    private Long id;

    private UUID messageId;

    private String serviceId;

    private OffsetDateTime messageDate;

    private String data;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public RequestDto(UUID messageId, String serviceId, OffsetDateTime messageDate, String data) {
        this(null, messageId, serviceId, messageDate, data);
    }

    public static RequestDto toDto(Request request){
        return new RequestDto(request.getId(),request.getMessageId(), request.getServiceId(),
                request.getMessageDate(), request.getData());
    }

    public static Request fromDto(RequestDto request){
        return new Request(request.getId(),request.getMessageId(), request.getServiceId(),
                request.getMessageDate(), request.getData());
    }

    //public static Converter<Request, RequestDto> toDtoConverter
}
