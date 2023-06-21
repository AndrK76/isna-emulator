package ru.igorit.andrk.service;

import org.springframework.data.domain.Page;
import ru.igorit.andrk.model.OpenCloseRequest;
import ru.igorit.andrk.model.OpenCloseResponse;
import ru.igorit.andrk.model.Request;
import ru.igorit.andrk.model.Response;

public interface MainStoreService {
    Request saveRequest(Request request);
    Page<Request> getRequests(Long lastId, int count);



    Response saveResponse(Response response);

    OpenCloseRequest saveOpenCloseRequest(OpenCloseRequest request);

    OpenCloseResponse saveOpenCloseResponse(OpenCloseResponse response);



}
