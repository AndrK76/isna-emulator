package ru.igorit.andrk.service.store;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.igorit.andrk.model.OpenCloseRequest;
import ru.igorit.andrk.model.OpenCloseResponse;
import ru.igorit.andrk.model.Request;
import ru.igorit.andrk.model.Response;
import ru.igorit.andrk.repository.main.OpenCloseRequestRepository;
import ru.igorit.andrk.repository.main.OpenCloseResponseRepository;
import ru.igorit.andrk.repository.main.RequestRepository;
import ru.igorit.andrk.repository.main.ResponseRepository;
import ru.igorit.andrk.service.MainStoreService;

@Service
public class MainStoreServiceJPAImpl implements MainStoreService {

    private final RequestRepository reqRepo;
    private final ResponseRepository respRepo;
    private final OpenCloseRequestRepository ocReqRepo;
    private final OpenCloseResponseRepository ocRespRepo;

    public MainStoreServiceJPAImpl(
            RequestRepository reqRepo,
            ResponseRepository respRepo,
            OpenCloseRequestRepository ocReqRepo,
            OpenCloseResponseRepository ocRespRepo) {
        this.reqRepo = reqRepo;
        this.respRepo = respRepo;
        this.ocReqRepo = ocReqRepo;
        this.ocRespRepo = ocRespRepo;
    }

    @Override
    @Transactional
    public Request saveRequest(Request request) {
        return reqRepo.save(request);
    }

    @Override
    public Page<Request> getRequests(Long lastId, int count) {
        Pageable condition = PageRequest.of(0, count, Sort.by("id").descending());
        Page<Request> ret;
        if (lastId == null) {
            ret = reqRepo.findAll(condition);
        } else {
            ret = reqRepo.findAllByIdLessThan(lastId,condition);
        }
        return ret;
    }

    @Override
    @Transactional
    public Response saveResponse(Response response) {
        return respRepo.save(response);
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
