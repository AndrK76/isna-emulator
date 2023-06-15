package ru.igorit.andrk.service.processors.openclose;

import kz.bee.bip.syncchannel.v10.types.ErrorInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ru.igorit.andrk.model.OpenCloseRequest;
import ru.igorit.andrk.model.OpenCloseRequestAccount;
import ru.igorit.andrk.mt.structure.MtBlock;
import ru.igorit.andrk.mt.structure.MtContent;
import ru.igorit.andrk.mt.structure.MtFormat;
import ru.igorit.andrk.mt.utils.MtConfigParser;
import ru.igorit.andrk.mt.utils.MtParser;
import ru.igorit.andrk.service.StoreService;
import ru.igorit.andrk.service.processors.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OpenCloseProcessor implements DataProcessor {
    private static final Logger log = LoggerFactory.getLogger(OpenCloseProcessor.class);
    private static final String DOCUMENT = "ISNA_BVU_BA_OPEN_CLOSE";
    private final MtFormat inputFormat = new MtFormat();
    private final StoreService storeService;
    private Map<String, OpenCloseResult> results = null;

    public OpenCloseProcessor(StoreService storeService) {
        this.storeService = storeService;
    }

    @Override
    public String document() {
        return DOCUMENT;
    }

    @Override
    public ProcessResult process(String data, UUID messageId) {
        MtContent content;

        try {
            content = MtParser.parsePreview(data, inputFormat);
            validateContentOnFatalErrors(content);

            var codeForm = (String) content.getValue("code_form");
            if (codeForm.equals("A03")) {
                content.getNode("ACCOUNT").setCurrentCode("ACCOUNT_CHANGE");
            }
            MtParser.parseFinal(content, inputFormat);
            log.debug(content.dumpValues());
        } catch (DataFormatFatalException e) {
            ErrorInfo info = new ErrorInfo();
            info.setErrorCode("SVC_ERRFORMAT");
            info.setErrorMessage(e.getMessage());
            throw new ProcessorException(info, e);
        }

        OpenCloseRequest request;
        try {
            request = makeRequestEntity(content, messageId);
        } catch (RuntimeException e) {
            ErrorInfo info = new ErrorInfo();
            info.setErrorCode("SVC_ERRFORMAT");
            info.setErrorMessage(e.getMessage());
            throw new ProcessorException(info, e);
        }

        try {
            makeRequestAccounts(request, content);
        } catch (DataContentException e) {
            log.error(e.getDataErrorMessage() +
                    (e.getCause() == null
                            ? ""
                            : " " + e.getCause().getClass().getSimpleName() + ": " + e.getCause().getMessage()));
        }


        var successResult = OpenCloseResult.toProcessResult(results.get("SUCCESS"));
        successResult.setData("l;kl;kl;kl;kl;\r\nkljkljkljlkjklhjkkljkl");

        return successResult;
    }

    @Override
    public void configure(byte[] config) {
        log.debug("apply config");
        MtConfigParser.parseInputFormatFromXML(config, inputFormat);
        log.trace("Input Config: {}", inputFormat);
        results = getResultValues(config);
        log.trace("Results: {}", results);
    }

    private Map<String, OpenCloseResult> getResultValues(byte[] config) {
        Map<String, OpenCloseResult> resList = new HashMap<>();
        NodeList resCfg = MtConfigParser.getCustomSection(config, "results");
        if (resCfg.getLength() != 0) {
            var childNodes = resCfg.item(0).getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                var node = childNodes.item(i);
                if (node.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }
                var attributes = node.getAttributes();
                Map<String, String> attrs = new HashMap<>();
                for (int j = 0; j < attributes.getLength(); j++) {
                    attrs.put(attributes.item(j).getNodeName(), attributes.item(j).getNodeValue());
                }
                if (attrs.containsKey("id")) {
                    var res = new OpenCloseResult(attrs.get("id"),
                            attrs.getOrDefault("code", ""));
                    if (node.getChildNodes().getLength() > 0) {
                        res.setText(node.getChildNodes().item(0).getNodeValue());
                    }
                    resList.put(res.getId(), res);
                }
            }
        }
        return resList;
    }

    private void validateContentOnFatalErrors(MtContent content) {
        String[] requiredItems = new String[]{"reference", "code_form", "notify_date"};
        if (!Arrays.stream(requiredItems).map(content::getValue).allMatch(Objects::nonNull)) {
            String reqToStr = Arrays.stream(requiredItems).collect(Collectors.joining(", "));
            throw new DataFormatFatalException("Не найдены поля определяющие формат: " + reqToStr);
        }
        String[] validCodeForms = new String[]{"A01", "A03"};
        var codeForm = (String) content.getValue("code_form");
        if (!Arrays.stream(requiredItems).map(content::getValue).anyMatch(codeForm::equals)) {
            throw new DataFormatFatalException("Некорректная форма: " + codeForm);
        }
    }

    private OpenCloseRequest makeRequestEntity(MtContent content, UUID messageId) {
        var ret = new OpenCloseRequest();
        ret.setMessageId(messageId);
        ret.setReference((String) content.getValue("reference"));
        ret.setCodeForm((String) content.getValue("code_form"));
        ret.setNotifyDate((LocalDateTime) content.getValue("notify_date"));
        return ret;
    }

    private Map<Integer, OpenCloseResult> makeRequestAccounts(OpenCloseRequest request, MtContent content) {
        try {
            var accountBlocks = content.getBlocks().stream()
                    .filter(r -> r.getOwnerNode().getFormat().getNodeName().equals("ACCOUNT"))
                    .sorted(Comparator.comparing(MtBlock::getId))
                    .collect(Collectors.toList());
            Set<String> emptyItems = new HashSet<>();
            for (var block : accountBlocks) {
                var id = block.getId();
                OpenCloseRequestAccount account = new OpenCloseRequestAccount();
                request.getAccounts().add(account);
                account.setRequest(request);
                account.setSort(id);
                account.setAccount((String) getBlockValue("account", content, emptyItems, id));
                account.setOperType((Integer) getBlockValue("oper_type", content, emptyItems, id));
            }

            return null;
        } catch (Exception e) {
            if (e instanceof DataContentException) {
                throw e;
            } else {
                throw new DataContentException(e.getMessage(), e);
            }
        }
    }

    private Object getBlockValue(String itemName, MtContent content, Set<String> emptyItems, int id) {
        if (!content.checkOnEmpty(itemName, id)) {
            emptyItems.add(itemName);
            return null;
        }
        try {
            var ret = content.getValue(itemName, id);
            return ret;
        } catch (RuntimeException e) {
            if (content.getItems().get(itemName).isRequired()) {
                emptyItems.add(itemName);
            }
            return null;
        }
    }


}
