package ru.igorit.andrk.mainstore;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import ru.igorit.andrk.model.Request;
import ru.igorit.andrk.model.Response;
import ru.igorit.andrk.repository.main.RequestRepository;
import ru.igorit.andrk.repository.main.ResponseRepository;
import ru.igorit.andrk.service.MainStoreService;
import ru.igorit.andrk.service.store.MainStoreServiceJPAImpl;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

@DataJpaTest
@TestPropertySource(locations = "/testDataJPA.properties")
public class TestResponseRepo {


    @Autowired
    private RequestRepository reqRepo;

    @Autowired
    private ResponseRepository respRepo;
    private MainStoreService svc;

    @BeforeEach
    private void initService() {
        svc = new MainStoreServiceJPAImpl(reqRepo, respRepo, null, null);
    }

    private Response makeTestRequestWithResponse() {
        var messageID = UUID.randomUUID();
        var serviceName = "Test";
        var requestDate = OffsetDateTime.now();
        var data = "";
        var request = new Request(null, messageID, serviceName, requestDate, data);
        var resp = new Response(request);
        resp.setIsSuccess(true);
        resp.setStatusMessage("status message");
        resp.setStatusCode("code");
        return resp;
    }

    private Request makeTestRequest() {
        var messageID = UUID.randomUUID();
        var serviceName = "Test";
        var requestDate = OffsetDateTime.now();
        var data = "";
        return new Request(null, messageID, serviceName, requestDate, data);
    }

    @Test
    @DisplayName("Save Response in storage")
    public void testSaveResponse() {
        var couInRepo = respRepo.findAll().size();
        assertThat(couInRepo).withFailMessage("Response table must be Empty before test")
                .isEqualTo(0);
        var response = makeTestRequestWithResponse();
        var oldMessageId = UUID.fromString(response.getMessageId().toString());
        response = svc.saveResponse(response);
        assertThat(response.getRequest().getId()).withFailMessage("Request Must be saved when saving Response")
                .isNotNull();
        assertThat(response.getMessageId()).withFailMessage("Message Id after save must be not change")
                .isEqualByComparingTo(oldMessageId);
        couInRepo = respRepo.findAll().size();
        assertThat(couInRepo).withFailMessage("After insert one Request Repo Must contain one record")
                .isEqualTo(1);
        response = makeTestRequestWithResponse();
        svc.saveRequest(response.getRequest());
        couInRepo = reqRepo.findAll().size();
        var reqId = response.getRequest().getId();
        svc.saveResponse(response);
        assertThat(reqRepo.findAll().size())
                .withFailMessage("Save response with persisted request must don't create new request ")
                .isEqualTo(couInRepo);
        assertThat(response.getRequest().getId().longValue())
                .withFailMessage("Save response with persisted request must don't change request ")
                .isEqualTo(reqId);
    }

    //@Test
    @DisplayName("Pagination get Response in storage")
    public void testPagination() {
        int totalSize = 95;
        int partitionSize = 10;
        var couInRepo = respRepo.findAll().size();
        assertThat(couInRepo).withFailMessage("Response table must be Empty before test")
                .isEqualTo(0);
        for (int i = 0; i < totalSize; i++) {
            svc.saveResponse(makeTestRequestWithResponse());
        }
        couInRepo = respRepo.findAll().size();
        assertThat(couInRepo)
                .withFailMessage("Response table contain %d records, but must contain %d before test", couInRepo, totalSize)
                .isEqualTo(totalSize);
        Long pos = null;
        Long prevPos = null;
        int lastPartitionSize = 0;
        for (int i = 0; i < Math.ceil((double) totalSize / partitionSize); i++) {
            var page = svc.getResponses(pos, partitionSize);
            pos = page.stream().min(Comparator.comparing(Response::getId)).get().getId();
            var actualPartitionSize = page.getContent().size();
            if (i == 0) {
                var actualTotal = page.getTotalElements();
                assertThat(actualTotal).withFailMessage("Total must be equal" + totalSize)
                        .isEqualTo(totalSize);
            }
            if (i < Math.ceil((double) totalSize / partitionSize) - 1) {
                assertThat(actualPartitionSize)
                        .withFailMessage("Each partition size except last must be equal" + partitionSize)
                        .isEqualTo(partitionSize);
                prevPos = pos;
            } else {
                lastPartitionSize = actualPartitionSize;
            }
        }
        svc.saveResponse(makeTestRequestWithResponse());
        var page = svc.getRequests(prevPos, partitionSize);
        var actualPartitionSize = page.getContent().size();
        assertThat(actualPartitionSize)
                .withFailMessage("Size last partition (%d) after insert must be don't change (%s)"
                        , actualPartitionSize, lastPartitionSize)
                .isEqualTo(lastPartitionSize);
    }


    @Test
    @DisplayName("Find Responses by Request range")
    public void testFindResponsesByRequestRange() {
        int totalSize = 95;
        int minBoundStep = 10, maxBoundStep = 80;
        long minBound = 0L, maxBound = 0L;
        for (int i = 1; i <= totalSize; i++) {
            long curReqId = 0;
            if (i % 2 == 0) {
                var res = svc.saveResponse(makeTestRequestWithResponse());
                curReqId = res.getRequest().getId();
            } else {
                var res = svc.saveRequest(makeTestRequest());
                curReqId = res.getId();
            }
            if (i == minBoundStep){
                minBound=curReqId;
            }
            if (i == maxBoundStep){
                maxBound=curReqId;
            }
        }
        var reqMin = reqRepo.findById(minBound).get();
        var reqMax = reqRepo.findById(maxBound).get();
        var ret = respRepo.findAllByRequestBetween(reqMin, reqMax)
                .stream().map(Response::getRequest).collect(Collectors.toList());
        var min = ret.stream().min(Request::compareTo).get().getId();
        var max = ret.stream().max(Request::compareTo).get().getId();
        assertThatNoException().isThrownBy(() -> {
            if (min != reqMin.getId().longValue() || max != reqMax.getId().longValue()) {
                throw new RuntimeException("Incorrect range bounds");
            }
        });

    }

}
