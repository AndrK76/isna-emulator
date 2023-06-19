package ru.igorit.andrk.service;

import ru.igorit.andrk.model.OpenCloseRequest;
import ru.igorit.andrk.model.OpenCloseResponse;
import ru.igorit.andrk.model.Request;

public interface StoreService {
    Request saveRequest(Request request);

    OpenCloseRequest saveOpenCloseRequest(OpenCloseRequest request);

    OpenCloseResponse saveOpenCloseResponse(OpenCloseResponse response);

}
