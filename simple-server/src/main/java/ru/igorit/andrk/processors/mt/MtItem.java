package ru.igorit.andrk.processors.mt;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@EqualsAndHashCode(of = {"code"})
public class MtItem {
    private final String code;
    private boolean required;
    private boolean strongLength;
    private MtItemType type;
    private int length;
    private String formatString;

    public static MtItem parse(String format){
        if (format==null || format.length()==0){
            throw new ConfigFormatException(String.format("Обнаружен пустой формат",format));
        }
        var fields = format.split(":");
        if (fields.length < 5){
            throw new ConfigFormatException(String.format("Некорректный формат {%s}",format));
        }
        var ret = new MtItem(fields[0]);
        ret.setRequired(fields[1].equals("1"));
        ret.setStrongLength(fields[2].equals("1"));
        ret.setType(MtParser.getItemTypes().get(fields[3]));
        ret.setLength(Integer.parseInt(fields[4]));
        if (fields.length > 5){
            ret.setFormatString(fields[5]);
        }
        return ret;
    }

    public Object getFormattedValue(String value){
        return value;
    }


    @Override
    public String toString() {
        return "MtItem{" +
                "code='" + code + '\'' +
                ", required=" + required +
                ", strongLength=" + strongLength +
                ", type=" + type +
                ", length=" + length +
                ", formatString='" + formatString + '\'' +
                '}';
    }
}
