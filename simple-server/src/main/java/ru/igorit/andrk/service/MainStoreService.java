package ru.igorit.andrk.service;

import org.springframework.data.domain.Page;
import org.springframework.data.util.Streamable;
import ru.igorit.andrk.model.OpenCloseRequest;
import ru.igorit.andrk.model.OpenCloseResponse;
import ru.igorit.andrk.model.Request;
import ru.igorit.andrk.model.Response;

import java.util.List;

public interface MainStoreService {
    Request saveRequest(Request request);
    Page<Request> getRequests(Long lastId, int count);

    Response saveResponse(Response response);

    Page<Response> getResponses(Long lastId, int count);
    List<Response> getResponsesForRequests(List<Request> requests);


    OpenCloseRequest saveOpenCloseRequest(OpenCloseRequest request);

    OpenCloseResponse saveOpenCloseResponse(OpenCloseResponse response);




}
