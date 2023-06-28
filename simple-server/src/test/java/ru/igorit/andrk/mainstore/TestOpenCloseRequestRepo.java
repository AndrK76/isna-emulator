package ru.igorit.andrk.mainstore;

import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import ru.igorit.andrk.model.OpenCloseRequest;
import ru.igorit.andrk.model.OpenCloseRequestAccount;
import ru.igorit.andrk.model.Request;
import ru.igorit.andrk.repository.main.OpenCloseRequestRepository;
import ru.igorit.andrk.repository.main.RequestRepository;
import ru.igorit.andrk.service.MainStoreService;
import ru.igorit.andrk.service.store.MainStoreServiceJPAImpl;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@TestPropertySource(locations = "/testDataJPA.properties")
public class TestOpenCloseRequestRepo {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private RequestRepository mainReqRepo;

    @Autowired
    private OpenCloseRequestRepository ocReqRepo;

    private MainStoreService svc;

    @BeforeEach
    private void initService() {
        svc = MainStoreServiceJPAImpl.builder()
                .reqRepo(mainReqRepo)
                .ocReqRepo(ocReqRepo)
                .build();
    }

    @Test
    @DisplayName("Save open/close request in storage")
    public void testSaveRequest() {
        int accountCounts = 3;
        var request = makeMainRequest();
        request = svc.saveRequest(request);
        Long mainId = request.getId();
        var ocReq = makeOCRequest(request, accountCounts);
        ocReq = svc.saveOpenCloseRequest(ocReq);
        assertThat(ocReq.getRawRequest().getId())
                .withFailMessage("Id owner request %d must me equal %d", ocReq.getRawRequest().getId(), mainId)
                .isEqualTo(mainId);
        assertThat(ocReq.getId())
                .withFailMessage("Id stored request must be not null")
                .isNotNull();
        assertThat(ocReq.getAccounts().size())
                .withFailMessage("Stored request contain %d account, but mus be %d", ocReq.getAccounts().size(), accountCounts)
                .isEqualTo(accountCounts);
        Long ocReqId = ocReq.getId();
        assertThat(ocReq.getAccounts())
                .withFailMessage("Each account in stored request must be have id")
                .allMatch(r -> r.getId() != null)
                .withFailMessage("Request id in each account must be equal %d", ocReq.getId())
                .allMatch(r -> r.getRequest().getId().equals(ocReqId));
        assertThat(ocReqRepo.findAll().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Load open/close request from storage")
    public void testLoadRequest() {
        int accountCounts = 3;
        var request = makeMainRequest();
        request = svc.saveRequest(request);
        var ocReq = makeOCRequest(request, accountCounts);
        ocReq = svc.saveOpenCloseRequest(ocReq);
        Long ocReqId = ocReq.getId();

        entityManager.flush();
        entityManager.clear();


        assertThatThrownBy(() -> {
            var ocReq1 = svc.getOpenCloseRequestById(ocReqId);
            entityManager.detach(ocReq1);
            var size = ocReq1.getAccounts().size();
        }).isInstanceOf(LazyInitializationException.class);

        assertThatNoException().isThrownBy(() -> {
            var ocReq1 = svc.getOpenCloseRequestById(ocReqId);
            var size = ocReq1.getAccounts().size();
            entityManager.detach(ocReq1);
            if (size != 3) {
                throw new RuntimeException("Incorrect account Size after Lazy load");
            }
        });

        assertThatNoException().isThrownBy(() -> {
            var ocReq1 = svc.getOpenCloseRequestById(ocReqId, true);
            entityManager.detach(ocReq1);
            var size = ocReq1.getAccounts().size();
            if (size != 3) {
                throw new RuntimeException("Incorrect account Size after Lazy load");
            }
        });

    }

    @Test
    @DisplayName("Test Exist by Reference")
    public void testExistByReference() {
        var request = makeMainRequest();
        request = svc.saveRequest(request);
        var ocRequest = makeOCRequest(request, 3);
        svc.saveOpenCloseRequest(ocRequest);
        var reference = ocRequest.getReference();
        request = makeMainRequest();
        request = svc.saveRequest(request);
        svc.saveOpenCloseRequest(makeOCRequest(request, 4));
        request = makeMainRequest();
        request = svc.saveRequest(request);
        ocRequest = OpenCloseRequest.builder()
                .rawRequest(request)
                .messageId(ocRequest.getMessageId())
                .codeForm(ocRequest.getCodeForm())
                .reference(reference)
                .notifyDate(ocRequest.getNotifyDate())
                .build();

        assertThat(svc.containOpenCloseRequestWithReference(ocRequest)).isTrue();
        svc.saveOpenCloseRequest(ocRequest);
        assertThat(svc.containOpenCloseRequestWithReference(ocRequest)).isTrue();
        request = makeMainRequest();
        ocRequest = makeOCRequest(request, 5);
        assertThat(svc.containOpenCloseRequestWithReference(ocRequest)).isFalse();
        svc.saveOpenCloseRequest(ocRequest);
        assertThat(svc.containOpenCloseRequestWithReference(ocRequest)).isFalse();
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
        ocRequest.setNotifyDate(LocalDateTime.now());
        byte[] array = new byte[10];
        new Random().nextBytes(array);
        String generatedString = new String(array, StandardCharsets.UTF_8);
        ocRequest.setReference(generatedString);
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


}
