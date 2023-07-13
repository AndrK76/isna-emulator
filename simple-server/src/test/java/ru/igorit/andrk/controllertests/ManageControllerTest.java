package ru.igorit.andrk.controllertests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import ru.igorit.andrk.api.ManageController;
import ru.igorit.andrk.dto.RequestDto;
import ru.igorit.andrk.dto.ResponseForRequestDTO;
import ru.igorit.andrk.model.Request;
import ru.igorit.andrk.model.Response;
import ru.igorit.andrk.service.MainStoreService;
import ru.igorit.andrk.utils.RestPage;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.igorit.andrk.config.services.Constants.API_VERSION;

@WebMvcTest(ManageController.class)
public class ManageControllerTest {

    private static final String PATH = "/api/" + API_VERSION;
    private static final int COUNT_REQUESTS = 10;

    @MockBean
    private MainStoreService mainStoreService;

    @Autowired
    private MockMvc client;

    @Autowired
    private ObjectMapper objectMapper;

    List<Request> requests = new ArrayList<>();
    List<Response> responses = new ArrayList<>();


    @Test
    void checkThatGetRequestQuery_ReturnCorrectData() throws Exception {
        initRequestData();
        var requestRes = client.perform(get(PATH + "/request"))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        var responseString = requestRes.getResponse().getContentAsString();
        var typeDef = TypeFactory.defaultInstance().constructParametricType(RestPage.class, RequestDto.class);
        var responseContent = (RestPage<RequestDto>) objectMapper.readValue(responseString, typeDef);
        var expectedRequests = requests.stream().map(r -> RequestDto.create(r, true)).collect(Collectors.toList());
        RequestDto.setResponseData(expectedRequests,responses);
        assertThat(responseContent.getContent())
                .hasSize(COUNT_REQUESTS)
                .containsExactlyElementsOf(expectedRequests);
    }

    @BeforeEach
    void initMockStoreService() {
        when(mainStoreService.getRequests(any(), anyInt()))
                .thenAnswer((Answer<Page<Request>>) invocation -> {
                    var count = (int) invocation.getArguments()[1];
                    return new PageImpl<>(requests, PageRequest.of(0, count), requests.size());
                });
        when(mainStoreService.getResponsesForRequests(any()))
                .thenAnswer((Answer<List<Response>>) invocation -> {
                    var reqs =(List<Request>)invocation.getArguments()[0];
                    return responses.stream()
                            .filter(r->reqs.contains(r.getRequest()))
                            .collect(Collectors.toList());
                });
    }

    private void initRequestData() {
        for (int i = 0; i < COUNT_REQUESTS; i++) {
            var request = Request.builder()
                    .id((long) i)
                    .messageId(UUID.randomUUID())
                    .serviceId("TEST")
                    .messageDate(OffsetDateTime.now().withNano(0))
                    .data("запрос" + i)
                    .build();
            if (i % 2 == 0) {
                var response = Response.builder()
                        .id((long) i * 3)
                        .request(request)
                        .messageId(UUID.randomUUID())
                        .correlationId(request.getMessageId())
                        .serviceId(request.getServiceId())
                        .isSuccess(true)
                        .responseDate(OffsetDateTime.now().withNano(0))
                        .statusCode("Code")
                        .statusMessage("Сообщение")
                        .data("Ответ" + (i * 3))
                        .build();
                responses.add(response);
            }
            requests.add(request);
        }

    }

}
