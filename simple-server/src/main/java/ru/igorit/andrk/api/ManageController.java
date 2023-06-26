package ru.igorit.andrk.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import ru.igorit.andrk.config.services.Constants;
import ru.igorit.andrk.dto.*;
import ru.igorit.andrk.model.OpenCloseRequest;
import ru.igorit.andrk.model.OpenCloseRequestAccount;
import ru.igorit.andrk.model.OpenCloseResponse;
import ru.igorit.andrk.model.Request;
import ru.igorit.andrk.service.MainStoreService;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/" + Constants.API_VERSION)
public class ManageController {


    private final MainStoreService mainStore;

    private final int docPerPage;

    public ManageController(MainStoreService mainStore,
                            @Value("${api.doc_per_page}") int docPerPage) {
        this.mainStore = mainStore;
        this.docPerPage = docPerPage;
    }

    @GetMapping("/request")
    public Page<RequestDto> getRequests(
            @RequestParam(required = false) Long after,
            @RequestParam(required = false, defaultValue = "0") int perPage) {
        Page<Request> data = mainStore.getRequests(after, perPage == 0 ? docPerPage : perPage);
        Page<RequestDto> ret = data.map(RequestDto::create);
        var responses = mainStore.getResponsesForRequests(data.getContent());
        RequestDto.setResponseData(ret, responses);
        return ret;
    }

    @GetMapping("/request/{id}")
    public RequestDto getRequests(
            @PathVariable(name = "id") Long id) {
        Request request = mainStore.getRequestById(id);
        if (request == null) {
            return null;
        }
        RequestDto ret = RequestDto.create(request, true);
        var responses = mainStore.getResponsesForRequests(List.of(request));
        if (responses.size() > 0) {
            ret.setResponse(ResponseForRequestDTO.create(responses.get(0), true));
        }
        return ret;
    }


    @GetMapping("/request/{id}/getnewest")
    public Long getIdForNewestRequestWithOffset(
            @PathVariable(name = "id") Long currRequestId,
            @RequestParam(name = "offset") int offset) {
        return mainStore.getIdForNewestRequestWithOffset(currRequestId, offset);
    }

    @GetMapping("/request/{id}/data")
    public String getRequestData(@PathVariable(name = "id") Long id) {
        var request = mainStore.getRequestById(id);
        if (request == null) {
            return null;
        }
        return request.getData();
    }

    @GetMapping("/response/{id}/data")
    public String getResponseData(@PathVariable(name = "id") Long id) {
        var response = mainStore.getResponse(id);
        if (response == null) {
            return null;
        }
        return response.getData();
    }

    @GetMapping("/opencloserequest")
    public Page<OpenCloseRequestDTO> getOpenCloseRequests(
            @RequestParam(required = false) Long after,
            @RequestParam(required = false, defaultValue = "0") int perPage) {
        Page<OpenCloseRequest> data = mainStore.getOpenCloseRequests(after, perPage == 0 ? docPerPage : perPage);
        Page<OpenCloseRequestDTO> ret = data.map(OpenCloseRequestDTO::create);
        var responses = mainStore.getOpenCloseResponsesForRequests(data.getContent());
        OpenCloseRequestDTO.setResponseData(ret, responses);
        return ret;
    }

    @GetMapping("/opencloserequest/{id}/account")
    public OpenCloseRequestDTO getOpenCloseRequest(
            @PathVariable(name = "id") Long id) {
        OpenCloseRequest request = mainStore.getOpenCloseRequestById(id, true);
        OpenCloseRequestDTO ret = OpenCloseRequestDTO.create(request, true);
        return ret;
    }

    @GetMapping("/opencloserequest/{id}/getnewest")
    public Long getIdForNewestOpenCloseRequestWithOffset(
            @PathVariable(name = "id") Long currRequestId,
            @RequestParam(name = "offset") int offset) {
        return mainStore.getIdForNewestOpenCloseRequestWithOffset(currRequestId, offset);
    }

    @GetMapping("/opencloseresponse/{id}/account")
    public OpenCloseResponseForRequestDTO getOpenCloseResponse(
            @PathVariable(name = "id") Long id) {
        OpenCloseResponse response = mainStore.getOpenCloseResponseById(id, true);
        OpenCloseResponseForRequestDTO ret = OpenCloseResponseForRequestDTO.create(response, true);
        return ret;
    }

}
