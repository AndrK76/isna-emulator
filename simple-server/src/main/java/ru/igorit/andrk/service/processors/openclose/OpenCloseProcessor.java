package ru.igorit.andrk.service.processors.openclose;

import kz.icode.gov.integration.kgd.ErrorInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ru.igorit.andrk.config.ConfigFormatException;
import ru.igorit.andrk.model.OpenCloseRequest;
import ru.igorit.andrk.model.OpenCloseRequestAccount;
import ru.igorit.andrk.mt.structure.MtBlock;
import ru.igorit.andrk.mt.structure.MtContent;
import ru.igorit.andrk.mt.structure.MtFormat;
import ru.igorit.andrk.mt.structure.MtNode;
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
    private final MtFormat outputFormat = new MtFormat();
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
        MtContent inputContent, outputContent;
        if (inputFormat.getNodes().size() == 0) {
            throw processError(new ConfigFormatException("Пустая конфигурация сервиса"), "SVC_CONFIG_ERROR");
        }

        try {
            inputContent = MtParser.parsePreview(data, inputFormat);
            validateContentOnFatalErrors(inputContent);

            var codeForm = (String) inputContent.getValue("code_form");
            if (codeForm.equals("A03")) {
                inputContent.getNode("ACCOUNT").setCurrentCode("ACCOUNT_CHANGE");
            }
            MtParser.parseFinal(inputContent, inputFormat);
            log.debug(inputContent.dumpValues());
        } catch (DataFormatFatalException e) {
            throw processError(e, "SVC_DATAFORMAT_ERROR");
        }

        OpenCloseRequest request;
        Map<Integer, OpenCloseResult> accountResult;
        try {
            request = makeRequestEntity(inputContent, messageId);
            accountResult = makeRequestAccounts(request, inputContent);
        } catch (Exception e) {
            throw processError(e, "SVC_DATAFORMAT_ERROR");
        }

        request = storeService.saveOpenCloseRequest(request);

        try {
            outputContent = new MtContent(outputFormat);
            fillOutContent(outputContent, outputFormat, request, accountResult);
        } catch (Exception e) {
            throw processError(e, "SVC_DATACOMPOSE_ERROR");
        }


        var successResult = OpenCloseResult.toProcessResult(results.get("SUCCESS"));
        successResult.setData("l;kl;kl;kl;kl;\r\nkljkljkljlkjklhjkkljkl");

        return successResult;
    }

    @Override
    public void configure(byte[] config) {
        log.debug("apply config");
        MtConfigParser.parseInputFormatFromXML(config, inputFormat);
        MtConfigParser.parseOutputFormatFromXML(config, outputFormat);

        log.trace("Input Config: {}", inputFormat);
        results = initResultValues(config);
        log.trace("Results: {}", results);
    }

    private Map<String, OpenCloseResult> initResultValues(byte[] config) {
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
                        List<Node> valueNodes = new ArrayList<>();
                        for (int j = 0; j < node.getChildNodes().getLength(); j++) {
                            valueNodes.add(node.getChildNodes().item(j));
                        }
                        Optional<Node> valNode = valueNodes.stream()
                                .filter(r -> r.getNodeType() == Node.CDATA_SECTION_NODE).findFirst();
                        if (!valNode.isPresent()) {
                            valNode = Optional.of(valueNodes.get(0));
                        }
                        res.setText(valNode.get().getNodeValue());
                    }
                    resList.put(res.getId(), res);
                }
            }
        }
        return resList;
    }

    private ProcessorException processError(Exception e, String code) {
        ErrorInfo info = new ErrorInfo();
        info.setErrorCode(code);
        info.setErrorMessage(e.getMessage());
        return new ProcessorException(info, e);
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
        Map<Integer, OpenCloseResult> parseDataResult = new HashMap<>();
        try {
            var accountBlocks = content.getBlocks().stream()
                    .filter(r -> r.getOwnerNode().getFormat().getNodeName().equals("ACCOUNT"))
                    .sorted(Comparator.comparing(MtBlock::getId))
                    .collect(Collectors.toList());
            for (var block : accountBlocks) {
                Set<String> emptyItems = new HashSet<>();
                var id = block.getId();
                OpenCloseRequestAccount account = new OpenCloseRequestAccount();
                request.getAccounts().add(account);
                account.setRequest(request);
                account.setSort(id);
                account.setAccount((String) getBlockValue("account", content, emptyItems, id));
                account.setOperType((Integer) getBlockValue("oper_type", content, emptyItems, id));
                account.setBic((String) getBlockValue("bic", content, emptyItems, id));
                account.setAccountType((String) getBlockValue("account_type", content, emptyItems, id));
                account.setOperDate((LocalDateTime) getBlockValue("oper_date", content, emptyItems, id));
                account.setRnn((String) getBlockValue("rnn", content, emptyItems, id));
                account.setDog((String) getBlockValue("dog", content, emptyItems, id));

                OpenCloseResult parseRowResult;
                if (emptyItems.size() > 0) {
                    parseRowResult = new OpenCloseResult(results.get("EMPTY_FIELD"));
                    parseRowResult.setText(
                            parseRowResult.getText().replace("%%{FIELD}%%",
                                    emptyItems.stream().collect(Collectors.joining(", "))));
                } else if (!checkTypeOper(account)) {
                    parseRowResult = new OpenCloseResult(results.get("INVALID_OPER_TYPE"));
                    parseRowResult.setText(
                            parseRowResult.getText().replace("%%{TYPE_OPER}%%",
                                    account.getOperType().toString()));
                    parseRowResult.setText(
                            parseRowResult.getText().replace("%%{CODE_FORM}%%",
                                    account.getRequest().getCodeForm()));
                } else if (!checkTypeAccount(account)) {
                    parseRowResult = new OpenCloseResult(results.get("INVALID_ACC_TYPE"));
                    parseRowResult.setText(
                            parseRowResult.getText().replace("%%{ACC_TYPE}%%",
                                    account.getAccountType()));
                } else {
                    parseRowResult = new OpenCloseResult(results.get("SUCCESS"));
                }
                parseDataResult.put(id, parseRowResult);
            }

            return parseDataResult;
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

    //TODO: захардкожено, можно тоже в настройки вынести секцией
    private boolean checkTypeOper(OpenCloseRequestAccount accountInfo) {
        String codeForm = accountInfo.getRequest().getCodeForm();
        int operType = accountInfo.getOperType();
        if (codeForm.equals("A01") && (operType == 1 || operType == 2)) {
            return true;
        } else if (codeForm.equals("A03") && operType == 9) {
            return true;
        }
        return false;
    }

    //TODO: захардкожено, можно тоже в настройки вынести секцией
    private boolean checkTypeAccount(OpenCloseRequestAccount accountInfo) {
        String accType = accountInfo.getAccountType();
        var validTypes = new String[]{"00", "05", "09", "20"};
        return Arrays.asList(validTypes).contains(accType);
    }

    private void fillOutContent(MtContent content,
                                MtFormat format,
                                OpenCloseRequest data,
                                Map<Integer, OpenCloseResult> processResult) {
        String[] constantNodeNames = new String[]{"HEAD", "ID", "MT_FORM", "SUBJECT"};
        Arrays.stream(constantNodeNames).forEach(n -> {
            var node = content.getNode(n, MtContent.FindNodeType.ByOrigCode);
            var block = new MtBlock(0, "", node);
            node.getBlocks().add(block);
        });
        String respCodeForm = data.getCodeForm().equals("A01") ? "A1C" : "A3C";

        var idBlock = content.getNode("ID").getBlocks().get(0);
        idBlock.setItem(format.getItem("reference"), data.getReference());

        var subjBlock = content.getNode("SUBJECT").getBlocks().get(0);
        subjBlock.setItem(format.getItem("code_form"), respCodeForm);
        subjBlock.setItem(format.getItem("notify_date"), LocalDateTime.now());
        subjBlock.setItem(format.getItem("name_form"),
                respCodeForm.equals("A1C")
                        ? "Подтв. о получ. увед. об откр. и закр. банк. счетов"
                        : "Подтв.о получ.увед.об измен.номеров банк.счетов");

        MtNode accNode = respCodeForm.equals("A1C")
                ? content.getNode("ACCOUNT")
                : content.getNode("ACCOUNT_CHANGE");
        for (var account : data.getAccounts()) {
            var accBlock = new MtBlock(account.getSort(), "", accNode);
            accNode.getBlocks().add(accBlock);
            accBlock.setItem(format.getItem("bic"),account.getBic());
            accBlock.setItem(format.getItem("account"),account.getAccount());
            accBlock.setItem(format.getItem("account_type"),account.getAccountType());
            accBlock.setItem(format.getItem("oper_type"),account.getOperType());
            accBlock.setItem(format.getItem("oper_date"),account.getOperDate());
            accBlock.setItem(format.getItem("rnn"),account.getRnn());
            var res = processResult.get(account.getSort());
            accBlock.setItem(format.getItem("result_code"),res.getCode());
            accBlock.setItem(format.getItem("result_name"),res.getText());
            accBlock.setItem(format.getItem("dog"),account.getDog());
            accBlock.setItem(format.getItem("dog_date"),account.getDogDate());
        }


        log.debug("start make data");
    }

}
