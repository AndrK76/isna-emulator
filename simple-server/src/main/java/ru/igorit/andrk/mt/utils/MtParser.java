package ru.igorit.andrk.mt.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.igorit.andrk.config.services.ConfigFormatException;
import ru.igorit.andrk.mt.structure.*;
import ru.igorit.andrk.service.processor.DataFormatFatalException;

import java.util.*;
import java.util.stream.Collectors;

public class MtParser {
    private static final Logger log = LoggerFactory.getLogger(MtParser.class);

    private static final Map<String, MtItemType> itemTypes = new HashMap<>();

    static {
        for (var val : MtItemType.values()) {
            itemTypes.put(val.getCode(), val);
        }
    }

    public static Map<String, MtItemType> getItemTypes() {
        return itemTypes;
    }

    public static MtContent parsePreview(String rawData, MtFormat format) {
        var content = new MtContent(rawData,format);
        log.debug("parse ");
        log.trace(String.format("on start: %n%s%n", rawData));
        String clearedData = MtParser.clear(rawData);
        log.trace(String.format("after clear:%n%s%n", clearedData));
        var strings = Arrays.stream(clearedData.split("\n"))
                .map(s -> s.replace("\r", ""))
                .filter(f -> f.length() > 0)
                .collect(Collectors.toList());
        int curFmtIdx = -1;

        for (String str : strings) {
            curFmtIdx = MtParser.parseDataRow(str, content.getNodes(), format.getNodes(), curFmtIdx);
        }

        for (var node : content.getNodes().values()) {
            var nodeName = node.getFormat().getNodeName();
            if (format.getPreviewFormats().containsKey(nodeName)) {
                var blockFmt = format.getPreviewFormats().get(nodeName);
                for (var block : node.getBlocks()) {
                    processBlockFormat(block, blockFmt, content.getItems());
                }
            }
        }
        return content;
    }

    public static void parseFinal(MtContent content, MtFormat format) {
        for (var node : content.getNodes().values()) {
            var nodeName = node.getCurrentCode();
            if (format.getDetailFormats().containsKey(nodeName)) {
                var blockFmt = format.getDetailFormats().get(nodeName);
                for (var block : node.getBlocks()) {
                    processBlockFormat(block, blockFmt, content.getItems());
                }
            }
        }
    }


    private static String clear(String origText) {
        List<Character> smbRes = new ArrayList<>();
        String retText = "";
        char[] symbols = origText.toCharArray();

        //remove { | {-
        clearAtStart(symbols, smbRes, '{', '-');

        Collections.reverse(smbRes);
        symbols = getChars(smbRes);

        //remove } | }-
        clearAtStart(symbols, smbRes, '}', '-');
        symbols = getChars(smbRes);

        //remove LF | CR
        while (clearAtStart(symbols, smbRes, '\n', '\r')) {
            symbols = getChars(smbRes);
        }

        //remove LF | LF
        while (clearAtStart(symbols, smbRes, '\n', '\n')) {
            symbols = getChars(smbRes);
        }

        Collections.reverse(smbRes);
        retText = buf2str(smbRes);
        return retText;
    }

    private static int parseDataRow(String str, Map<MtFormatNodeInfo, MtNode> content,
                                    List<MtFormatNodeInfo> formats, int prevFmtIdx) {
        int curFmtIdx = prevFmtIdx;
        //log.debug("Str: {}", str);
        for (int i = (curFmtIdx == -1 ? 0 : curFmtIdx); i < formats.size(); i++) {
            var fmt = formats.get(i);
            var mask = fmt.getSearchMask();
            var testStr = str.substring(0, (Math.min(mask.length(), str.length())));
            if (testStr.equals(mask)) {
                curFmtIdx = i;
                var idx = content.get(fmt).getBlocks().size();
                content.get(fmt).getBlocks().add(new MtBlock(idx, str, content.get(fmt)));
            }
            //log.debug("fmt={}, mask={}, val={}", formats.get(i).getNodeName(), mask, testStr);
        }
        return curFmtIdx;
    }

    private static char[] getChars(List<Character> smbRes) {
        char[] symbols;
        symbols = buf2str(smbRes).toCharArray();
        return symbols;
    }

    private static boolean clearAtStart(char[] symbols, List<Character> smbRes, char first, char second) {
        smbRes.clear();
        boolean cleared = false;
        boolean inFindFirst = true;
        boolean inFindSecond = false;
        for (int i = 0; i < symbols.length; i++) {
            if (inFindFirst && symbols[i] == first) {
                inFindSecond = true;
                cleared = true;
            } else if (inFindSecond && symbols[i] == second) {
                inFindSecond = false;
            } else if (inFindSecond) {
                inFindSecond = false;
                smbRes.add(symbols[i]);
            } else {
                smbRes.add(symbols[i]);
            }
            inFindFirst = false;
        }
        return cleared;
    }

    private static void processBlockFormat(MtBlock block, MtBlockFormat format, Map<String, MtItem> items) {
        if (block.getText() == null || block.getText().length() == 0
                || format == null || format.getFormatString() == null || format.getFormatString().length() == 0) {
            return;
        }
        int dataPos = 0;
        int fmtPos = 0;
        boolean inFormat = false;
        List<Character> fmtBuf = new ArrayList<>();
        char fmtChar, dataChar;
        while (dataPos < block.getText().length() && fmtPos < format.getFormatString().length()) {
            fmtChar = format.getFormatString().charAt(fmtPos);
            dataChar = block.getText().charAt(dataPos);
            if (!inFormat) {
                if (fmtChar != '{') {
                    dataPos++;
                    if (fmtChar != dataChar) {
                        var errText ="Несоответствие строки "+block.getText()+ " формату "+ format.getFormatString();
                        log.error(errText);
                        throw new DataFormatFatalException(errText);
                    }
                } else {
                    inFormat = true;
                }
            } else {
                if (fmtChar != '}') {
                    fmtBuf.add(fmtChar);
                } else {
                    var fmtString = buf2str(fmtBuf);
                    fmtBuf.clear();
                    var item = MtItem.parse(fmtString);
                    if (!items.containsKey(item.getCode())) {
                        items.put(item.getCode(), item);
                    }
                    var value = extractDataByFormat(block, format.getSplitter(), item, dataPos);
                    if (block.getValues().containsKey(item)) {
                        block.getValues().remove(item);
                    }
                    block.getValues().put(item, value);
                    dataPos += value.length();
                    inFormat = false;
                }
            }
            fmtPos++;
        }
        if (fmtBuf.size() > 0) {
            throw new ConfigFormatException("Некорректный формат: " + format.getFormatString());
        }
    }

    private static String extractDataByFormat(MtBlock block, String splitter, MtItem item, int dataPos) {
        List<Character> dataBuf = new ArrayList<>();
        String srcStr = block.getText();
        boolean isComplete = false;
        int curPos = dataPos;
        int charProcessed = 0;
        while (curPos < srcStr.length() && !isComplete && charProcessed < item.getLength()) {
            char curChar = srcStr.charAt(curPos);
            if (!item.isStrongLength() && splitter.charAt(0) == curChar) {
                isComplete = true;
            } else {
                dataBuf.add(curChar);
                curPos++;
                charProcessed++;
            }
        }
        return buf2str(dataBuf);
    }

    public static String buf2str(List<Character> buf) {
        return buf.stream().map(String::valueOf).collect(Collectors.joining());
    }

}
