package ru.igorit.andrk.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.igorit.andrk.model.StoredSetting;
import ru.igorit.andrk.service.MainStoreService;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class StoredSettingDTO {
    private String key;
    private Object value;

    public StoredSettingDTO(StoredSetting src){
        this();
        this.key = src.getId().getSetting();
        this.value = src.getValue();
    }

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public static List<StoredSettingDTO> create(List<StoredSetting> data){
        return data.stream().map(StoredSettingDTO::new).collect(Collectors.toList());
    }
}
