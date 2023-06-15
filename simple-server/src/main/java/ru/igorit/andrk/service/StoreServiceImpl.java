package ru.igorit.andrk.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.igorit.andrk.model.Request;
import ru.igorit.andrk.repository.RequestRepository;

@Service
public class StoreServiceImpl implements StoreService {

    private final RequestRepository reqRepo;

    public StoreServiceImpl(RequestRepository reqRepo) {
        this.reqRepo = reqRepo;
    }

    @Override
    @Transactional
    public Request saveRequest(Request request) {
        var ret = reqRepo.save(request);
        return ret;
    }

}
