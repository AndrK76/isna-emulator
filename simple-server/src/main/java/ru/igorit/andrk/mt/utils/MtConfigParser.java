package ru.igorit.andrk.mt.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import ru.igorit.andrk.config.ConfigFormatException;
import ru.igorit.andrk.mt.structure.MtBlockFormat;
import ru.igorit.andrk.mt.structure.MtFormat;
import ru.igorit.andrk.mt.structure.MtFormatNodeInfo;
import ru.igorit.andrk.mt.structure.MtNodeCountMode;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class MtConfigParser {
    private static final Logger log = LoggerFactory.getLogger(MtConfigParser.class);

    public static void parseInputFormatFromXML(byte[] configData, MtFormat format) {
        try {
            Document cfgDoc = getXmlDoc(configData);
            format.getNodes().clear();

            var nodeList = cfgDoc.getElementsByTagName("input");
            if (nodeList.getLength() == 0) {
                throw new Exception("Не найдено описание входного формата");
            }
            var inputConfig = nodeList.item(0).getFirstChild().getNodeValue();
            if (inputConfig == null || inputConfig.length() == 0) {
                throw new Exception("Пустое описание входного формата");
            }

            var sectionCfgData = Arrays.stream(inputConfig.split("\n"))
                    .map(s -> s.replace("\r", ""))
                    .map(MtConfigParser::parseFormatNodeRow)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
            log.trace("nodes: {}", sectionCfgData);
            format.getNodes().addAll(sectionCfgData);

            var previewFormatsData = Arrays.stream(inputConfig.split("\n"))
                    .map(s -> s.replace("\r", ""))
                    .map(s -> parseItemFormatRow(s, "2"))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
            for (var previewFormat : previewFormatsData) {
                format.getPreviewFormats().put(previewFormat.getFirst(), previewFormat.getSecond());
            }

            var detailFormatsData = Arrays.stream(inputConfig.split("\n"))
                    .map(s -> s.replace("\r", ""))
                    .map(s -> parseItemFormatRow(s, "3"))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
            for (var detailFormat : detailFormatsData) {
                format.getDetailFormats().put(detailFormat.getFirst(), detailFormat.getSecond());
            }
        } catch (Exception e) {
            throw new ConfigFormatException(e);
        }
    }

    private static Document getXmlDoc(byte[] configData) throws ParserConfigurationException, IOException, SAXException {
        Document cfgDoc = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder bld = factory.newDocumentBuilder();
        try (InputStream is = new ByteArrayInputStream(configData)) {
            cfgDoc = bld.parse(is);
        }
        return cfgDoc;
    }

    public static NodeList getCustomSection(byte[] configData, String sectionName) {
        Document cfgDoc = null;
        try {
            cfgDoc = getXmlDoc(configData);
            return cfgDoc.getElementsByTagName(sectionName);
        } catch (Exception e) {
            throw new ConfigFormatException(e);
        }
    }


    private static Optional<MtFormatNodeInfo> parseFormatNodeRow(String str) {
        var fields = str.split("~");
        if (fields.length > 1 && fields[0].equals("1")) {
            if (fields.length > 3) {
                var nodeInfo = new MtFormatNodeInfo(
                        fields[1],
                        Integer.parseInt(fields[2]),
                        fields[3], MtNodeCountMode.One);
                if (fields.length > 4) {
                    if (fields[4].equals("N")) {
                        nodeInfo.setCountMode(MtNodeCountMode.Many);
                    }
                }
                return Optional.of(nodeInfo);
            }
        }
        return Optional.empty();
    }

    private static Optional<Pair<String, MtBlockFormat>> parseItemFormatRow(String str, String levelKey) {
        var fields = str.split("~");
        if (fields.length > 3 && fields[0].equals(levelKey)) {
            Pair<String, MtBlockFormat> fmtInfo = Pair.of(fields[1], new MtBlockFormat(fields[2], fields[3]));
            return Optional.of(fmtInfo);
        }
        return Optional.empty();
    }


}
