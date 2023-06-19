package ru.igorit.andrk.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.igorit.andrk.model.OpenCloseRequest;
import ru.igorit.andrk.model.OpenCloseResponse;
import ru.igorit.andrk.model.Request;
import ru.igorit.andrk.repository.OpenCloseRequestRepository;
import ru.igorit.andrk.repository.OpenCloseResponseRepository;
import ru.igorit.andrk.repository.RequestRepository;

@Service
public class StoreServiceImpl implements StoreService {

    private final RequestRepository reqRepo;
    private final OpenCloseRequestRepository ocReqRepo;
    private final OpenCloseResponseRepository ocRespRepo;

    public StoreServiceImpl(RequestRepository reqRepo, OpenCloseRequestRepository ocReqRepo, OpenCloseResponseRepository ocRespRepo) {
        this.reqRepo = reqRepo;
        this.ocReqRepo = ocReqRepo;
        this.ocRespRepo = ocRespRepo;
    }

    @Override
    @Transactional
    public Request saveRequest(Request request) {
        var ret = reqRepo.save(request);
        return ret;
    }

    @Override
    @Transactional
    public OpenCloseRequest saveOpenCloseRequest(OpenCloseRequest request) {
        return ocReqRepo.save(request);
    }

    @Override
    @Transactional
    public OpenCloseResponse saveOpenCloseResponse(OpenCloseResponse response) {
        return ocRespRepo.save(response);
    }

}
