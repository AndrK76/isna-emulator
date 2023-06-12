package ru.igorit.andrk.service;

import ru.igorit.andrk.model.OpenCloseResult;
import ru.igorit.andrk.model.Request;

import java.util.List;
import java.util.Map;

public interface StoreService {
    Request save(Request request);

    Map<Long, OpenCloseResult> getOpenCloseCodes();
}
