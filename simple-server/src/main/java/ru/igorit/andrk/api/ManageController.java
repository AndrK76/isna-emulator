package ru.igorit.andrk.api;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.igorit.andrk.config.Constants;
import ru.igorit.andrk.dto.RequestDto;
import ru.igorit.andrk.service.MainStoreService;

@RestController
@RequestMapping("/api/" + Constants.API_VERSION)
public class ManageController {


    private final MainStoreService mainStore;

    public ManageController(MainStoreService mainStore) {
        this.mainStore = mainStore;
    }

    @GetMapping("/request")
    public Page<RequestDto> getRequests(
            @RequestParam(required = false) Long after,
            @RequestParam(required = false, defaultValue = "10") int perPage) {
        return mainStore.getRequests(after, perPage).map(RequestDto::toDto);
    }

}
