package ru.igorit.andrk.controllertests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import ru.igorit.andrk.api.ManageController;
import ru.igorit.andrk.dto.OpenCloseRequestDTO;
import ru.igorit.andrk.dto.OpenCloseResponseForRequestDTO;
import ru.igorit.andrk.dto.RequestDto;
import ru.igorit.andrk.model.*;
import ru.igorit.andrk.service.MainStoreService;
import ru.igorit.andrk.utils.RestPage;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.igorit.andrk.config.services.Constants.API_VERSION;

@WebMvcTest(ManageController.class)
public class ManageControllerListTest {

    private static final String PATH = "/api/" + API_VERSION;
    private static final int COUNT_REQUESTS = 10;


    @MockBean
    private MainStoreService mainStoreService;

    @Autowired
    private MockMvc client;

    @Autowired
    private ObjectMapper objectMapper;

    private final List<Request> requests = new ArrayList<>();
    private final List<Response> responses = new ArrayList<>();
    private final List<OpenCloseRequest> ocRequests = new ArrayList<>();
    private final List<OpenCloseResponse> ocResponses = new ArrayList<>();


    @ParameterizedTest
    @ValueSource(ints = {0, COUNT_REQUESTS})
    void checkThatGetAllRequestQuery_ReturnCorrectData(int reqCount) throws Exception {
        if (reqCount != 0) {
            initFullMockStorage();
        }
        initFullMockStoreService();
        var requestRes = client.perform(get(PATH + "/request"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(reqCount))
                .andDo(print())
                .andReturn();

        var responseString = requestRes.getResponse().getContentAsString();

        var typeDef = TypeFactory.defaultInstance().constructParametricType(RestPage.class, RequestDto.class);
        var responseContent = (RestPage<RequestDto>) objectMapper.readValue(responseString, typeDef);
        var expectedRequests = requests.stream().map(r -> RequestDto.create(r, true)).collect(Collectors.toList());
        RequestDto.setResponseData(expectedRequests, responses);
        assertThat(responseContent.getContent())
                .hasSize(reqCount)
                .containsExactlyElementsOf(expectedRequests);
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, COUNT_REQUESTS - 5, COUNT_REQUESTS + 10})
    void checkThatGetOneRequestQuery_ReturnCorrectData(Long id) throws Exception {
        initFullMockStorage();
        initFullMockStoreService();
        var requestRes = client.perform(get(PATH + "/request/{id}", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        var responseString = requestRes.getResponse().getContentAsString();
        if (id < COUNT_REQUESTS) {
            var actualId = (Integer) JsonPath.read(responseString, "$.id");
            assertThat(actualId.longValue()).isEqualTo(id);
        } else {
            assertThat(responseString).isNullOrEmpty();
        }
    }

    @ParameterizedTest
    @MethodSource("getNewestRequestParameters")
    void checkThatGetNewestRequest_CallStorageWhenNeeded_andReturnResultFromStorage
            (Long id, Integer offset, Long answer, int statusCode, boolean callStorage) throws Exception {
        AtomicBoolean actualCallStorage = new AtomicBoolean(false);
        if (offset != null) {
            when(mainStoreService.getIdForNewestRequestWithOffset(id, offset))
                    .thenAnswer((Answer<Long>) invocation -> {
                        actualCallStorage.set(true);
                        return answer;
                    });
        }
        var requestRes = client.perform(get(PATH + "/request/{id}/getnewest", id)
                        .param("offset", "" + offset))
                .andDo(print())
                .andReturn();

        assertThat(requestRes.getResponse().getStatus()).isEqualTo(statusCode);

        var responseString = requestRes.getResponse().getContentAsString();
        if (answer == null) {
            assertThat(responseString).isNullOrEmpty();
        } else {
            AtomicReference<Long> actualId = new AtomicReference<>();
            assertThatNoException().isThrownBy(() -> actualId.set(Long.parseLong(responseString)));
            assertThat(actualId.get()).isEqualTo(answer);
        }

        assertThat(actualCallStorage.get()).isEqualTo(callStorage);
    }

    private static Stream<Arguments> getNewestRequestParameters() {
        return Stream.of(
                Arguments.of(2L, 1, 5L, 200, true)
                , Arguments.of(null, 8, null, 404, false)
                , Arguments.of(10L, null, null, 400, false)
                , Arguments.of(null, null, null, 404, false)
                , Arguments.of(1L, 5, 88L, 200, true)
                , Arguments.of(0L, 5, 18L, 200, true)
                , Arguments.of(55L, 100, 77L, 200, true)

        );
    }

    @ParameterizedTest
    @MethodSource("getRequestDataAndResponseData_Parameters")
    void checkThatGetRequestData_CallStorageWhenCalled_andReturnResultFromStorage
            (Long id, String answer, int statusCode, boolean callStorage) throws Exception {
        AtomicBoolean actualCallStorage = new AtomicBoolean(false);
        when(mainStoreService.getRequestById(id))
                .thenAnswer((Answer<Request>) invocation -> {
                    actualCallStorage.set(true);
                    return Request.builder()
                            .messageDate(OffsetDateTime.now())
                            .data(answer)
                            .build();
                });
        var requestRes = client.perform(get(PATH + "/request/{id}/data", id))
                .andDo(print())
                .andReturn();

        assertThat(requestRes.getResponse().getStatus()).isEqualTo(statusCode);

        var responseString = requestRes.getResponse().getContentAsString();
        if (answer==null || answer.isEmpty()) {
            assertThat(responseString).isNullOrEmpty();
        } else {
            assertThat(responseString).isEqualTo(answer);
        }

        assertThat(actualCallStorage.get()).isEqualTo(callStorage);
    }

    private static Stream<Arguments> getRequestDataAndResponseData_Parameters() {
        return Stream.of(
                Arguments.of(2L, "Текст\nтекст", 200, true)
                , Arguments.of(null, null, 404, false)
                , Arguments.of(55L, "", 200, true)

        );
    }

    @ParameterizedTest
    @MethodSource("getRequestDataAndResponseData_Parameters")
    void checkThatGetResponseData_CallStorageWhenCalled_andReturnResultFromStorage
            (Long id, String answer, int statusCode, boolean callStorage) throws Exception {
        AtomicBoolean actualCallStorage = new AtomicBoolean(false);
        when(mainStoreService.getResponse(id))
                .thenAnswer((Answer<Response>) invocation -> {
                    actualCallStorage.set(true);
                    return Response.builder()
                            .request(
                                    Request.builder().messageDate(OffsetDateTime.now()).build()
                            )
                            .messageId(UUID.randomUUID())
                            .responseDate(OffsetDateTime.now())
                            .data(answer)
                            .isSuccess(true)
                            .build();
                });
        var requestRes = client.perform(get(PATH + "/response/{id}/data", id))
                .andDo(print())
                .andReturn();

        assertThat(requestRes.getResponse().getStatus()).isEqualTo(statusCode);

        var responseString = requestRes.getResponse().getContentAsString();
        if (answer==null || answer.isEmpty()) {
            assertThat(responseString).isNullOrEmpty();
        } else {
            assertThat(responseString).isEqualTo(answer);
        }

        assertThat(actualCallStorage.get()).isEqualTo(callStorage);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, COUNT_REQUESTS})
    void checkThatGetAllOpenCloseRequestQuery_ReturnCorrectData(int reqCount) throws Exception {
        if (reqCount != 0) {
            initFullMockStorage();
        }
        int ocReqCount = ocRequests.size();
        initFullMockStoreService();
        var requestRes = client.perform(get(PATH + "/opencloserequest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(ocReqCount))
                .andDo(print())
                .andReturn();

        var responseString = requestRes.getResponse().getContentAsString();

        var typeDef = TypeFactory.defaultInstance().constructParametricType(RestPage.class, OpenCloseRequestDTO.class);
        var responseContent = (RestPage<OpenCloseRequestDTO>) objectMapper.readValue(responseString, typeDef);
        var expectedRequests = ocRequests.stream().map(r -> OpenCloseRequestDTO.create(r, true)).collect(Collectors.toList());
        OpenCloseRequestDTO.setResponseData(expectedRequests, ocResponses);
        assertThat(responseContent.getContent())
                .hasSize(ocReqCount)
                .containsExactlyElementsOf(expectedRequests)
                .allMatch(r-> r.getAccounts()==null || r.getAccounts().size()==0);
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, 1L, 2L, COUNT_REQUESTS - 5, COUNT_REQUESTS + 10})
    void checkThatGetOneOpenCloseRequestQuery_ReturnCorrectData(Long id) throws Exception {
        initFullMockStorage();
        initFullMockStoreService();
        var requestRes = client.perform(get(PATH + "/opencloserequest/{id}/account", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        var responseString = requestRes.getResponse().getContentAsString();

        if (id < COUNT_REQUESTS && ocRequests.stream().filter(r->r.getId().equals(id)).findFirst().isPresent()) {
            var actualId = (Integer) JsonPath.read(responseString, "$.id");
            assertThat(actualId.longValue()).isEqualTo(id);
            var responseContent = (OpenCloseRequestDTO) objectMapper.readValue(responseString, OpenCloseRequestDTO.class);
            assertThat(responseContent.getAccounts()).isNotNull().hasSize(1);
        } else {
            assertThat(responseString).isNullOrEmpty();
        }
    }

    @ParameterizedTest
    @MethodSource("getNewestRequestParameters")
    void checkThatGetNewestOpenCloseRequest_CallStorageWhenNeeded_andReturnResultFromStorage
            (Long id, Integer offset, Long answer, int statusCode, boolean callStorage) throws Exception {
        AtomicBoolean actualCallStorage = new AtomicBoolean(false);
        if (offset != null) {
            when(mainStoreService.getIdForNewestOpenCloseRequestWithOffset(id, offset))
                    .thenAnswer((Answer<Long>) invocation -> {
                        actualCallStorage.set(true);
                        return answer;
                    });
        }
        var requestRes = client.perform(get(PATH + "/opencloserequest/{id}/getnewest", id)
                        .param("offset", "" + offset))
                .andDo(print())
                .andReturn();

        assertThat(requestRes.getResponse().getStatus()).isEqualTo(statusCode);

        var responseString = requestRes.getResponse().getContentAsString();
        if (answer == null) {
            assertThat(responseString).isNullOrEmpty();
        } else {
            AtomicReference<Long> actualId = new AtomicReference<>();
            assertThatNoException().isThrownBy(() -> actualId.set(Long.parseLong(responseString)));
            assertThat(actualId.get()).isEqualTo(answer);
        }

        assertThat(actualCallStorage.get()).isEqualTo(callStorage);
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, 1L, 2L, COUNT_REQUESTS - 5, COUNT_REQUESTS + 10})
    void checkThatGetOneOpenCloseResponseQuery_ReturnCorrectData(Long id) throws Exception {
        initFullMockStorage();
        initFullMockStoreService();
        var requestRes = client.perform(get(PATH + "/opencloseresponse/{id}/account", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        var responseString = requestRes.getResponse().getContentAsString();

        if (id < COUNT_REQUESTS && ocResponses.stream().filter(r->r.getId().equals(id)).findFirst().isPresent()) {
            var actualId = (Integer) JsonPath.read(responseString, "$.id");
            assertThat(actualId.longValue()).isEqualTo(id);
            var responseContent = (OpenCloseResponseForRequestDTO) objectMapper.readValue(responseString, OpenCloseResponseForRequestDTO.class);
            assertThat(responseContent.getAccounts()).isNotNull().hasSize(1);
        } else {
            assertThat(responseString).isNullOrEmpty();
        }
    }



    void initFullMockStoreService() {
        when(mainStoreService.getRequests(any(), anyInt()))
                .thenAnswer((Answer<Page<Request>>) invocation -> {
                    var count = (int) invocation.getArguments()[1];
                    return new PageImpl<>(requests, PageRequest.of(0, count), requests.size());
                });
        when(mainStoreService.getResponsesForRequests(any()))
                .thenAnswer((Answer<List<Response>>) invocation -> {
                    var reqs = (List<Request>) invocation.getArguments()[0];
                    return responses.stream()
                            .filter(r -> reqs.contains(r.getRequest()))
                            .collect(Collectors.toList());
                });
        when(mainStoreService.getRequestById(any()))
                .thenAnswer((Answer<Request>) invocation -> {
                    Long id = (Long) invocation.getArguments()[0];
                    return requests.stream()
                            .filter(r -> r.getId() == id)
                            .findFirst().orElse(null);
                });
        when(mainStoreService.getIdForNewestRequestWithOffset(any(), anyInt()))
                .thenAnswer((Answer<Long>) invocation -> {
                    Long currRequestId = (Long) invocation.getArguments()[0];
                    int offset = (int) invocation.getArguments()[1];
                    var retRequest = requests.stream()
                            .filter(r -> r.getId() > currRequestId)
                            .sorted(Comparator.comparing(Request::getId))
                            .skip(offset).findFirst();
                    return retRequest.isPresent() ? retRequest.get().getId() : null;
                });
        when(mainStoreService.getOpenCloseRequests(any(), anyInt()))
                .thenAnswer((Answer<Page<OpenCloseRequest>>) invocation -> {
                    var count = (int) invocation.getArguments()[1];
                    return new PageImpl<>(ocRequests, PageRequest.of(0, count), ocRequests.size());
                });
        when(mainStoreService.getOpenCloseResponsesForRequests(any()))
                .thenAnswer((Answer<List<OpenCloseResponse>>) invocation -> {
                    var reqs = (List<OpenCloseRequest>) invocation.getArguments()[0];
                    return ocResponses.stream()
                            .filter(r -> reqs.contains(r.getRequest()))
                            .collect(Collectors.toList());
                });
        when(mainStoreService.getOpenCloseRequestById(any(), anyBoolean()))
                .thenAnswer((Answer<OpenCloseRequest>) invocation -> {
                    Long id = (Long) invocation.getArguments()[0];
                    return ocRequests.stream()
                            .filter(r -> r.getId() == id)
                            .findFirst().orElse(null);
                });
        when(mainStoreService.getIdForNewestOpenCloseRequestWithOffset(any(), anyInt()))
                .thenAnswer((Answer<Long>) invocation -> {
                    Long currRequestId = (Long) invocation.getArguments()[0];
                    int offset = (int) invocation.getArguments()[1];
                    var retRequest = ocRequests.stream()
                            .filter(r -> r.getId() > currRequestId)
                            .sorted(Comparator.comparing(OpenCloseRequest::getId))
                            .skip(offset).findFirst();
                    return retRequest.isPresent() ? retRequest.get().getId() : null;
                });
        when(mainStoreService.getOpenCloseResponseById(any(), anyBoolean()))
                .thenAnswer((Answer<OpenCloseResponse>) invocation -> {
                    Long id = (Long) invocation.getArguments()[0];
                    return ocResponses.stream()
                            .filter(r -> r.getId() == id)
                            .findFirst().orElse(null);
                });
    }

    private void initFullMockStorage() {
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
                var ocRequest = OpenCloseRequest.builder()
                        .id((long)i)
                        .reference("Qwertiop_"+ ((long)i*5))
                        .codeForm("A000")
                        .accounts(new ArrayList<>())
                        .notifyDate(LocalDateTime.now().withSecond(0).withNano(0))
                        .rawRequest(request)
                        .build();
                ocRequests.add(ocRequest);
                var ocRequestAccount = OpenCloseRequestAccount.builder()
                        .id((long)i*8)
                        .sort(0)
                        .account("000000000000001")
                        .operType(1)
                        .accountType("05")
                        .build();
                ocRequest.getAccounts().add(ocRequestAccount);
                var ocResponse = OpenCloseResponse.builder()
                        .id((long)i)
                        .request(ocRequest)
                        .reference(ocRequest.getReference())
                        .codeForm("AC0")
                        .notifyDate(LocalDateTime.now().withSecond(0).withNano(0))
                        .accounts(new ArrayList<>())
                        .build();
                ocResponses.add(ocResponse);
                var OcResponseAccount = OpenCloseResponseAccount.builder()
                        .id((long)i*7)
                        .response(ocResponse)
                        .sort(0)
                        .account(ocRequestAccount.getAccount())
                        .resultCode("00")
                        .build();
                ocResponse.getAccounts().add(OcResponseAccount);
            }
            requests.add(request);
        }

    }

}
