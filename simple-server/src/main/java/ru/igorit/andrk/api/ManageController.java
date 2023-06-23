package ru.igorit.andrk.api;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import ru.igorit.andrk.config.Constants;
import ru.igorit.andrk.dto.RequestWithStateDto;
import ru.igorit.andrk.model.Request;
import ru.igorit.andrk.service.MainStoreService;

@RestController
@RequestMapping("/api/" + Constants.API_VERSION)
public class ManageController {


    private final MainStoreService mainStore;

    public ManageController(MainStoreService mainStore) {
        this.mainStore = mainStore;
    }

    @GetMapping("/request")
    public Page<RequestWithStateDto> getRequests(
            @RequestParam(required = false) Long after,
            @RequestParam(required = false, defaultValue = "10") int perPage) {
        Page<Request> data = mainStore.getRequests(after, perPage);
        Page<RequestWithStateDto> ret = data.map(RequestWithStateDto::create);
        var responses = mainStore.getResponsesForRequests(data.getContent());
        RequestWithStateDto.setResponseData(ret, responses);
        return ret;
    }

    @GetMapping("/request/{id}/getnewest")
    public Long getIdForNewestRequestWithOffset(
            @PathVariable(name = "id") Long currRequestId,
            @RequestParam(name = "offset") int offset) {
        return mainStore.getIdForNewestRequestWithOffset(currRequestId, offset);
    }
    @GetMapping("/request/{id}/data")
    public String getRequestData(@PathVariable(name = "id") Long id){
        var request = mainStore.getRequestById(id);
        if (request==null){
            return null;
        }
        return request.getData();
    }

    @GetMapping("/response/{id}/data")
    public String getResponseData(@PathVariable(name = "id") Long id){
        var response = mainStore.getResponse(id);
        if (response==null){
            return null;
        }
        return response.getData();
    }

}
