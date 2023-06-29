package ru.igorit.andrk.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "stored_settings")
public class StoredSetting {
    @EmbeddedId
    private StoredSettingKey id;
    @Column(name = "value_type")
    private Class<?> valueType;
    @Column(name = "setting_value")
    private String value;

    @Transient
    private static final ObjectMapper objectMapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();

    public StoredSetting(StoredSettingKey id, Object value) {
        this.id = id;
        setValue(value);
    }

    public void setValue(Object value) {
        try {
            this.valueType = value.getClass();
            this.value = objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public Object getValue()  {
        try {
            return objectMapper.readValue(this.value, this.valueType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
