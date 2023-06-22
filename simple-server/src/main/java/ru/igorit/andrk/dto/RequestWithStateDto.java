package ru.igorit.andrk.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import ru.igorit.andrk.model.Request;
import ru.igorit.andrk.model.Response;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public class RequestWithStateDto {
    private Long id;

    private UUID messageId;

    private String serviceId;

    private OffsetDateTime messageDate;

    private String data;

    @Setter
    private ResponseForRequestDTO response;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public RequestWithStateDto(UUID messageId, String serviceId, OffsetDateTime messageDate, String data) {
        this(null, messageId, serviceId, messageDate, data, null);
    }

    public static RequestWithStateDto create(Request request) {
        return new RequestWithStateDto(request.getId(), request.getMessageId(), request.getServiceId(),
                request.getMessageDate(), request.getData(), null);
    }

    public static Request fromDto(RequestWithStateDto request) {
        return new Request(request.getId(), request.getMessageId(), request.getServiceId(),
                request.getMessageDate(), request.getData());
    }

    public static void setResponseData(Iterable<RequestWithStateDto> requests, List<Response> responses) {
        Map<Long, Response> responseMap = responses.stream()
                .collect(Collectors.toMap(k -> k.getRequest().getId(), v -> v));
        requests.forEach(r -> {
            var response = responseMap.get(r.getId());
            if (response != null) {
                r.setResponse(ResponseForRequestDTO.create(response));
            }
        });
    }
}
