package ru.igorit.andrk.service;

import org.springframework.data.domain.Page;
import ru.igorit.andrk.model.OpenCloseRequest;
import ru.igorit.andrk.model.OpenCloseResponse;
import ru.igorit.andrk.model.Request;
import ru.igorit.andrk.model.Response;

import java.util.List;

public interface MainStoreService {
    Request saveRequest(Request request);

    Page<Request> getRequests(Long lastId, int count);

    Long getIdForNewestRequestWithOffset(Long currRequestId, int offset);

    Request getRequestById(Long id);

    Response saveResponse(Response response);

    Page<Response> getResponses(Long lastId, int count);

    List<Response> getResponsesForRequests(List<Request> requests);

    Response getResponse(Long id);

    OpenCloseRequest saveOpenCloseRequest(OpenCloseRequest request);

    Page<OpenCloseRequest> getOpenCloseRequests(Long lastId, int count);

    Long getIdForNewestOpenCloseRequestWithOffset(Long currRequestId, int offset);

    OpenCloseRequest getOpenCloseRequestById(Long id, boolean loadAccounts);

    OpenCloseRequest getOpenCloseRequestById(Long id);


    OpenCloseResponse saveOpenCloseResponse(OpenCloseResponse response);

    OpenCloseResponse getOpenCloseResponseById(Long id, boolean loadAccounts);

    OpenCloseResponse getOpenCloseResponseById(Long id);

    Page<OpenCloseResponse> getOpenCloseResponses(Long lastId, int count);

    List<OpenCloseResponse> getOpenCloseResponsesForRequests(List<OpenCloseRequest> requests);


}
