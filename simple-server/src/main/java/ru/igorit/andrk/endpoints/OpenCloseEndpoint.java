package ru.igorit.andrk.endpoints;


import kz.icode.gov.integration.kgd.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import ru.igorit.andrk.service.processors.ProcessResult;
import ru.igorit.andrk.service.processors.ProcessorException;
import ru.igorit.andrk.service.processors.ProcessorFactory;
import ru.igorit.andrk.service.StoreService;
import ru.igorit.andrk.utils.RequestMapper;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.namespace.QName;

import java.util.UUID;

import static java.time.LocalDateTime.now;
import static ru.igorit.andrk.config.Constants.DEFAULT_NAMESPACE;
import static ru.igorit.andrk.utils.DataHandler.toXmlDate;

@Endpoint
public class OpenCloseEndpoint {

    private static final Logger log = LoggerFactory.getLogger(OpenCloseEndpoint.class);

    private final StoreService storeService;
    private final ProcessorFactory processors;


    public OpenCloseEndpoint(
            StoreService storeService,
            ProcessorFactory processors) {
        this.storeService = storeService;
        this.processors = processors;
    }


    @PayloadRoot(namespace = DEFAULT_NAMESPACE, localPart = "SendMessage")
    @ResponsePayload
    public JAXBElement<SendMessageResponse> sendMessage(
            @RequestPayload JAXBElement<SendMessage> message) throws DatatypeConfigurationException {
        var msg = message.getValue();

        var req = RequestMapper.toModel(msg.getRequest());
        //TODO: to task
        storeService.saveRequest(req);

        var processor = processors.getProcessor(req.getServiceId());
        if (processor==null){
            ErrorInfo err = new ErrorInfo();
            err.setErrorCode("SE_PROCESSOR");
            err.setErrorMessage("Не найден сервис обработки "+ req.getServiceId());
            throw new ProcessorException(err);
        }

        UUID messageId;
        try{
            var msgId = msg.getRequest().getRequestInfo().getMessageId();
            messageId = UUID.fromString(msgId);
            if (messageId==null){
                throw new IllegalArgumentException("Message id is empty");
            }
        } catch (IllegalArgumentException e){
            ErrorInfo err = new ErrorInfo();
            err.setErrorCode("SVC_ERRFORMAT");
            err.setErrorMessage("Ошибка в номере сообщения");
            throw new ProcessorException(err,e);
        }

        ProcessResult res = processor.process(req.getData(), messageId);



        var ret = new SendMessageResponse();
        var resp = new SyncSendMessageResponse();
        var respInfo = new SyncMessageInfoResponse();
        respInfo.setMessageId(messageId.toString());
        respInfo.setResponseDate(toXmlDate((now())));
        var status = new StatusInfo();
        status.setCode(res.getStatusCode());
        status.setMessage(res.getStatusMessage());
        respInfo.setStatus(status);
        resp.setResponseInfo(respInfo);

        var data = new ResponseData();
        data.setData(res.getDataIgnoreCR());
        resp.setResponseData(data);

        ret.setResponse(resp);

        return createResponseJaxbElement(ret, SendMessageResponse.class);
    }

    private <T> JAXBElement<T> createResponseJaxbElement(T object, Class<T> clazz) {
        return new JAXBElement<>(new QName(DEFAULT_NAMESPACE, clazz.getSimpleName()), clazz, object);
    }


}
