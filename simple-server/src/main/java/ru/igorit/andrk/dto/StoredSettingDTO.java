package ru.igorit.andrk.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.*;
import lombok.extern.jackson.Jacksonized;
import ru.igorit.andrk.model.StoredSetting;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Jacksonized
public class StoredSettingDTO {

    @JsonIgnore
    private static final ObjectMapper objectMapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();

    private String key;

    private Class<?> valueType;

    @Setter
    private Object value;

    public StoredSettingDTO(StoredSetting src) {
        this();
        this.key = src.getId().getSetting();
        this.valueType = src.getValueType();
        this.value = src.getValue();
    }

    public static List<StoredSettingDTO> create(List<StoredSetting> data) {
        return data.stream().map(StoredSettingDTO::new).collect(Collectors.toList());
    }

    public void actualizeValue(){
        try {
            String str = objectMapper.writeValueAsString(this.getValue());
            this.setValue(objectMapper.readValue(str, this.getValueType()));
        } catch (JsonProcessingException e) {
        }
    }

}
