package ru.igorit.andrk.service;

import org.springframework.stereotype.Service;
import ru.igorit.andrk.model.OpenCloseResult;
import ru.igorit.andrk.model.Request;
import ru.igorit.andrk.repository.OpenCloseResultRepository;
import ru.igorit.andrk.repository.RequestRepository;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StoreServiceImpl implements StoreService {

    private final RequestRepository reqRepo;
    private final OpenCloseResultRepository openCloseResRepo;

    public StoreServiceImpl(
            RequestRepository reqRepo,
            OpenCloseResultRepository openCloseResRepo) {
        this.reqRepo = reqRepo;
        this.openCloseResRepo = openCloseResRepo;
    }

    @Override
    public Request save(Request request) {
        var ret = reqRepo.save(request);
        return ret;
    }
    @Override
    public Map<Long, OpenCloseResult> getOpenCloseCodes() {
        return openCloseResRepo.findAll().stream().collect(Collectors.toMap(OpenCloseResult::getId, v->v));
    }
}
