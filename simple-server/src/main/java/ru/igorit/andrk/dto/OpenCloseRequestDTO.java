package ru.igorit.andrk.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.igorit.andrk.model.OpenCloseRequest;
import ru.igorit.andrk.model.OpenCloseRequestAccount;
import ru.igorit.andrk.model.OpenCloseResponse;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OpenCloseRequestDTO {
    private Long id;

    private Long rawRequestId;

    private UUID messageId;

    private String reference;

    private String codeForm;
    private LocalDateTime notifyDate;

    private List<OpenCloseRequestAccountDTO> accounts = new ArrayList<>();

    @Setter
    private OpenCloseResponseForRequestDTO response;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public static OpenCloseRequestDTO create(OpenCloseRequest request, boolean loadAccounts) {
        var ret = new OpenCloseRequestDTO();
        ret.id = request.getId();
        ret.rawRequestId = request.getRawRequest().getId();
        ret.messageId = request.getMessageId();
        ret.reference = request.getReference();
        ret.codeForm = request.getCodeForm();
        ret.notifyDate = request.getNotifyDate();
        if (loadAccounts) {
            request.getAccounts().stream()
                    .sorted(Comparator.comparing(OpenCloseRequestAccount::getSort))
                    .forEach(r -> ret.getAccounts().add(OpenCloseRequestAccountDTO.create(r)));
        }
        return ret;
    }

    public static OpenCloseRequestDTO create(OpenCloseRequest request) {
        return create(request, false);
    }


    public static void setResponseData(Iterable<OpenCloseRequestDTO> requests, List<OpenCloseResponse> responses) {
        Map<Long, OpenCloseResponse> responseMap = responses.stream()
                .collect(Collectors.toMap(k -> k.getRequest().getId(), v -> v));
        requests.forEach(r -> {
            var response = responseMap.get(r.getId());
            if (response != null) {
                r.setResponse(OpenCloseResponseForRequestDTO.create(response));
            }
        });
    }

}
