package ru.igorit.andrk.endpoints;


import kz.bee.bip.syncchannel.v10.types.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import ru.igorit.andrk.service.StoreService;
import ru.igorit.andrk.utils.RequestMapper;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.namespace.QName;

import static java.time.LocalDateTime.now;
import static ru.igorit.andrk.config.Constants.DEFAULT_NAMESPACE;
import static ru.igorit.andrk.utils.DataHandler.toXmlDate;

@Endpoint
public class OpenCloseEndpoint {

    private static final Logger log = LoggerFactory.getLogger(OpenCloseEndpoint.class);

    private StoreService storeService;

    public OpenCloseEndpoint(StoreService storeService) {
        this.storeService = storeService;
    }


    @PayloadRoot(namespace = DEFAULT_NAMESPACE, localPart = "SendMessage")
    @ResponsePayload
    public JAXBElement<SendMessageResponse> sendMessage(
            @RequestPayload JAXBElement<SendMessage> message) throws DatatypeConfigurationException {
        var msg = message.getValue();

        var req = RequestMapper.toModel(msg.getRequest());
        storeService.save(req);


        var msgId = msg.getRequest().getRequestInfo().getMessageId();

        var ret = new SendMessageResponse();
        var resp = new SyncSendMessageResponse();
        var respInfo = new SyncMessageInfoResponse();
        respInfo.setMessageId(msgId);
        respInfo.setResponseDate(toXmlDate((now())));
        var status = new StatusInfo();
        status.setCode("OK");
        status.setMessage("Message processed successfully");
        respInfo.setStatus(status);
        resp.setResponseInfo(respInfo);

        var data = new ResponseData();
        String mtData = "{4:\n" +
                ":20:0818006500009D24\n" +
                ":12:400\n" +
                ":77E:FORMS\n" +
                "/A1C/230317/Подтверждение об открытии и закрытии банковских счетов\n" +
                "/ACCOUNT/KZKOKZKX/KZ224500284070011156/00/1/220615/881762767605/01/0000153/220101-}";
        data.setData(mtData);
        resp.setResponseData(data);

        ret.setResponse(resp);

        return createResponseJaxbElement(ret, SendMessageResponse.class);
    }

    private <T> JAXBElement<T> createResponseJaxbElement(T object, Class<T> clazz) {
        return new JAXBElement<>(new QName(DEFAULT_NAMESPACE, clazz.getSimpleName()), clazz, object);
    }

}
