package ru.igorit.andrk.mainstore;

import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import ru.igorit.andrk.model.*;
import ru.igorit.andrk.repository.main.OpenCloseRequestRepository;
import ru.igorit.andrk.repository.main.OpenCloseResponseRepository;
import ru.igorit.andrk.repository.main.RequestRepository;
import ru.igorit.andrk.service.MainStoreService;
import ru.igorit.andrk.service.store.MainStoreServiceJPAImpl;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatNoException;

@DataJpaTest
@TestPropertySource(locations = "/testDataJPA.properties")
public class TestOpenCloseResponseRepo {
    @Autowired
    private DataSource dataSource;

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private RequestRepository mainReqRepo;

    @Autowired
    private OpenCloseRequestRepository ocReqRepo;

    @Autowired
    private OpenCloseResponseRepository ocRespRepo;

    private MainStoreService svc;

    @BeforeEach
    private void initService() {
        svc =  MainStoreServiceJPAImpl.builder()
                .reqRepo(mainReqRepo)
                .ocReqRepo(ocReqRepo)
                .ocRespRepo(ocRespRepo)
                .build();
    }

    @Test
    @DisplayName("Save open/close response in storage")
    public void testSaveResponse(){
        int accountCounts = 3;
        var request = makeMainRequest();
        request = svc.saveRequest(request);
        var ocReq = makeOCRequest(request, accountCounts);
        ocReq = svc.saveOpenCloseRequest(ocReq);
        Long reqId = ocReq.getId();
        var ocResp = makeOCResponse(ocReq);
        ocResp = svc.saveOpenCloseResponse(ocResp);

        assertThat(ocResp.getRequest().getId())
                .withFailMessage("Id request in stored response %d must me equal %d", ocResp.getRequest().getId(), reqId)
                .isEqualTo(reqId);
        assertThat(ocResp.getId())
                .withFailMessage("Id stored response must be not null")
                .isNotNull();
        assertThat(ocResp.getAccounts().size())
                .withFailMessage("Stored request contain %d account, but mus be %d", ocResp.getAccounts().size(), accountCounts)
                .isEqualTo(accountCounts);
        Long ocRespId = ocResp.getId();
        assertThat(ocResp.getAccounts())
                .withFailMessage("Each account in stored request must be have id")
                .allMatch(r -> r.getId() != null)
                .withFailMessage("Request id in each account must be equal %d", ocRespId)
                .allMatch(r -> r.getResponse().getId().equals(ocRespId));
        assertThat(ocRespRepo.findAll().size()).isEqualTo(1);

    }

    @Test
    @DisplayName("Load open/close response from storage")
    public void testLoadResponse() {
        int accountCounts = 3;
        var request = makeMainRequest();
        request = svc.saveRequest(request);
        var ocReq = makeOCRequest(request, accountCounts);
        ocReq = svc.saveOpenCloseRequest(ocReq);
        var ocResp = makeOCResponse(ocReq);
        var ocRespId = svc.saveOpenCloseResponse(ocResp).getId();

        entityManager.flush();
        entityManager.clear();


        assertThatThrownBy(() -> {
            var ocResp1 = svc.getOpenCloseResponseById(ocRespId);
            entityManager.detach(ocResp1);
            var size = ocResp1.getAccounts().size();
        }).isInstanceOf(LazyInitializationException.class);

        assertThatNoException().isThrownBy(() -> {
            var ocResp1 = svc.getOpenCloseResponseById(ocRespId);
            var size = ocResp1.getAccounts().size();
            entityManager.detach(ocResp1);
            if (size != 3){
                throw new RuntimeException("Incorrect account Size after Lazy load");
            }
        });

        assertThatNoException().isThrownBy(() -> {
            var ocResp1 = svc.getOpenCloseResponseById(ocRespId, true);
            entityManager.detach(ocResp1);
            var size = ocResp1.getAccounts().size();
            if (size != 3){
                throw new RuntimeException("Incorrect account Size after Lazy load");
            }
        });

    }




    private Request makeMainRequest() {
        var messageID = UUID.randomUUID();
        var serviceName = "Test";
        var requestDate = OffsetDateTime.now();
        var data = "";
        return new Request(null, messageID, serviceName, requestDate, data);
    }

    private OpenCloseRequest makeOCRequest(Request request, int accountCounts) {
        OpenCloseRequest ocRequest = new OpenCloseRequest(request);
        ocRequest.setCodeForm("TEST");
        var accounts = ocRequest.getAccounts();
        for (int i = 0; i < accountCounts; i++) {
            var account = new OpenCloseRequestAccount();
            account.setRequest(ocRequest);
            account.setSort(i);
            account.setAccount("QWERTY123456");
            accounts.add(account);
        }
        return ocRequest;
    }

    private OpenCloseResponse makeOCResponse(OpenCloseRequest request) {
        OpenCloseResponse resp = new OpenCloseResponse(request);
        request.getAccounts().forEach(r ->
        {
            var acc = new OpenCloseResponseAccount();
            acc.setResponse(resp);
            resp.getAccounts().add(acc);
        });
        return resp;
    }
}
