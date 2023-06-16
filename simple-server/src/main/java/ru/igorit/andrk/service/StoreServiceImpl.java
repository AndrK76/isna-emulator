package ru.igorit.andrk.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.igorit.andrk.model.OpenCloseRequest;
import ru.igorit.andrk.model.Request;
import ru.igorit.andrk.repository.OpenCloseRequestRepository;
import ru.igorit.andrk.repository.RequestRepository;

@Service
public class StoreServiceImpl implements StoreService {

    private final RequestRepository reqRepo;

    private final OpenCloseRequestRepository ocReqRepo;

    public StoreServiceImpl(RequestRepository reqRepo, OpenCloseRequestRepository ocReqRepo) {
        this.reqRepo = reqRepo;
        this.ocReqRepo = ocReqRepo;
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
        var ret = ocReqRepo.save(request);
        return ret;
    }

}
