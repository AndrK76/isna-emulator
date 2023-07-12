package ru.igorit.andrk.parser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import ru.igorit.andrk.mt.structure.MtFormat;
import ru.igorit.andrk.mt.utils.MtConfigParser;
import ru.igorit.andrk.mt.utils.MtParser;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class MtParserTests {

    private static final String SAMPLE_CFG = "sample_parser.cfg";

    @ParameterizedTest
    @MethodSource("clearStringParameters")
    void clearString_shouldReturnCorrectResult(String srcString, String resString){
        assertThat(MtParser.clearMtString(srcString)).isEqualTo(resString);
    }

    private static Stream<Arguments> clearStringParameters() {
        return Stream.of(
                Arguments.of("{test}", "test"),
                Arguments.of("{test\n-}", "test"),
                Arguments.of("{test\ntest}", "test\ntest"),
                Arguments.of("{test\ntest\n-}", "test\ntest"),
                Arguments.of("{test\ntest\n\n\n-}", "test\ntest"),
                Arguments.of("{test\r\ntest}", "test\r\ntest"),
                Arguments.of("{test\ntest\r\n\r\n\r\n\r\n\r\n-}", "test\ntest"),
                Arguments.of("{{test\ntest\r\n\r\n\r\n\r\n}\r\n-}", "{test\ntest\r\n\r\n\r\n\r\n}")
        );
    }


    @ParameterizedTest
    @MethodSource("mtDataSource")
    void parsePreview_shouldReturnCorrectResult(String srcData) throws IOException {
        MtFormat inputFormat = new MtFormat();
        MtConfigParser.parseInputFormatFromXML(getConfig(), inputFormat);
        var content = MtParser.parsePreview(srcData, inputFormat);
        var nodeKeys = content.getNodes().keySet().stream().map(r->r.getNodeName()).collect(Collectors.joining(","));
        assertThat(nodeKeys).isEqualTo("SUBJECT,ID,ACCOUNT");
        var itemKeys = content.getItems().keySet();
        assertThat(itemKeys).containsAll(Arrays.asList(new String[]{"reference", "code_form", "notify_date"}));
        System.out.println();
    }
    private byte[] getConfig() throws IOException {
        return this.getClass().getClassLoader().getResourceAsStream(SAMPLE_CFG).readAllBytes();
    }

    private static Stream<Arguments> mtDataSource() {
        return Stream.of(
                Arguments.of("{4:\n" +
                        ":20:V306154735451685\n" +
                        ":12:400\n" +
                        ":77E:FORMS/A01/202306151309/Увед. об откр. и закр. банковских счетов\n" +
                        "/ACCOUNT/VTBAKZKZ/KZ484324302398A00006/05/1/20230301/450509833484//\n" +
                        "/ACCOUNT/VTBAKZKZ/KZ164322204398R09704/20/2/20210406/143346407250//\n" +
                        "-}"),
                Arguments.of("{4:\n" +
                        ":20:V306197160121274\n" +
                        ":12:400\n" +
                        ":77E:FORMS/A03/202306191953/Увед. об изменении банковских счетов\n" +
                        "/ACCOUNT/VTBAKZKZ/KZ484324302398A00006/05/20230301/450509833484/VTBAKZKZ/398A00006/20230301\n" +
                        "-}")
        );
    }
}
