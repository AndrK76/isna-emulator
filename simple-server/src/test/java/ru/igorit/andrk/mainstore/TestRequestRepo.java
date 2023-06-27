package ru.igorit.andrk.mainstore;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import ru.igorit.andrk.model.Request;
import ru.igorit.andrk.repository.main.RequestRepository;
import ru.igorit.andrk.service.MainStoreService;
import ru.igorit.andrk.service.store.MainStoreServiceJPAImpl;

import javax.sql.DataSource;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(locations = "/testDataJPA.properties")
public class TestRequestRepo {


    @Autowired
    private DataSource dataSource;

    @Autowired
    private RequestRepository repo;
    private MainStoreService svc;

    @BeforeEach
    private void initService() {
        svc = MainStoreServiceJPAImpl.builder()
                .reqRepo(repo)
                .build();
    }

    private Request makeTestRequest() {
        var messageID = UUID.randomUUID();
        var serviceName = "Test";
        var requestDate = OffsetDateTime.now();
        var data = "";
        return new Request(null, messageID, serviceName, requestDate, data);
    }

    @Test
    @DisplayName("Save Request in storage")
    public void testSaveRequest() {
        var couInRepo = repo.findAll().size();
        assertThat(couInRepo).withFailMessage("Request table must be Empty before test")
                .isEqualTo(0);
        var request = makeTestRequest();
        var oldMessageId = UUID.fromString(request.getMessageId().toString());
        request = svc.saveRequest(request);
        assertThat(request.getMessageId()).withFailMessage("Message Id after save must be not change")
                .isEqualByComparingTo(oldMessageId);
        couInRepo = repo.findAll().size();
        assertThat(couInRepo).withFailMessage("After insert one Request Repo Must contain one record")
                .isEqualTo(1);
    }

    @Test
    @DisplayName("Pagination get Request in storage")
    public void testPagination() {
        int totalSize = 95;
        int partitionSize = 10;
        var couInRepo = repo.findAll().size();
        assertThat(couInRepo).withFailMessage("Request table must be Empty before test")
                .isEqualTo(0);
        for (int i = 0; i < totalSize; i++) {
            svc.saveRequest(makeTestRequest());
        }
        couInRepo = repo.findAll().size();
        assertThat(couInRepo)
                .withFailMessage("Request table contain %d records, but must contain %d before test", couInRepo, totalSize)
                .isEqualTo(totalSize);
        Long pos = null;
        Long prevPos = null;
        int lastPartitionSize = 0;
        for (int i = 0; i < Math.ceil((double) totalSize / partitionSize); i++) {
            var page = svc.getRequests(pos, partitionSize);
            pos = page.stream().min((o1, o2) -> o1.getId().compareTo(o2.getId())).get().getId();
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
        svc.saveRequest(makeTestRequest());
        var page = svc.getRequests(prevPos, partitionSize);
        var actualPartitionSize = page.getContent().size();
        assertThat(actualPartitionSize)
                .withFailMessage("Size last partition (%d) after insert must be don't change (%s)"
                        , actualPartitionSize, lastPartitionSize)
                .isEqualTo(lastPartitionSize);
    }

    @ParameterizedTest
    @ValueSource(longs = {0, 1, 8, 10, 19, 20})
    @DisplayName("Validate value with offset ")
    public void testGetIdForNewestRequestWithOffset() {
        int sampleSize = 20;
        int offset = 1;
        Map<Integer, Long> idValues = new HashMap<>();
        for (int i = 0; i < sampleSize; i++) {
            idValues.put(i, svc.saveRequest(makeTestRequest()).getId());
        }
        for (int i = 0; i < sampleSize; i++) {
            var dlt = sampleSize - offset;
            var val = svc.getIdForNewestRequestWithOffset(idValues.get(i), offset);
            if (i < sampleSize - offset) {
                assertThat(val)
                        .withFailMessage("Offset on %d value on step %d of %d must by not null, but is null", offset, i + 1, sampleSize)
                        .isNotNull();
            } else {
                assertThat(val)
                        .withFailMessage("Offset on %d value on step %d of %d must by null, but is %d", offset, i + 1, sampleSize, val)
                        .isNull();
            }
        }
    }

}
