package ru.igorit.andrk.service.store;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Streamable;
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

import java.util.ArrayList;
import java.util.List;

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
            ret = reqRepo.findAllByIdLessThan(lastId, condition);
        }
        return ret;
    }

    @Override
    public Long getIdForNewestRequestWithOffset(Long currRequestId, int offset) {
        Pageable condition = PageRequest.of(0, offset, Sort.by("id"));
        Page<Request> data = reqRepo.findAllByIdGreaterThan(currRequestId, condition);
        if (data.getTotalElements() < offset) {
            return null;
        } else {
            return data.getContent().get(offset - 1).getId();
        }
    }

    @Override
    public Request getRequestById(Long id) {
        if (id == null) {
            return null;
        }
        return reqRepo.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Response saveResponse(Response response) {
        Request request = response.getRequest();
        if (request.getId() != null) {
            request = reqRepo.getReferenceById(request.getId());
            response.setRequest(request);
        }
        return respRepo.save(response);
    }

    @Override
    public Page<Response> getResponses(Long lastId, int count) {
        Pageable condition = PageRequest.of(0, count, Sort.by("id").descending());
        if (lastId == null) {
            return respRepo.findAll(condition);
        } else {
            return respRepo.findAllByIdLessThan(lastId, condition);
        }
    }

    @Override
    public List<Response> getResponsesForRequests(List<Request> requests) {
        var minRequest = requests.stream().min(Request::compareTo).orElse(null);
        var maxRequest = requests.stream().max(Request::compareTo).orElse(null);
        if (minRequest == null || maxRequest == null) {
            return new ArrayList<>();
        }
        return respRepo.findAllByRequestBetween(minRequest, maxRequest);
    }

    @Override
    public Response getResponse(Long id) {
        if (id == null) {
            return null;
        }
        return respRepo.findById(id).orElse(null);
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
