package ru.igorit.andrk.service.processors.openclose;


import lombok.*;
import ru.igorit.andrk.service.processors.ProcessResult;


@Getter
@EqualsAndHashCode(of={"id"})
@RequiredArgsConstructor
@ToString
public class OpenCloseResult {

    private final String id;
    private final String code;
    @Setter
    private String text;

    public static ProcessResult toProcessResult(OpenCloseResult res){
        return new ProcessResult(res.getCode(),res.getText());
    }

}
