package ru.igorit.andrk.service.processor.openclose;

import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.igorit.andrk.dto.StoredSettingDTO;
import ru.igorit.andrk.model.StoredSetting;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter
public class OpenCloseDynamicSettings {
    private boolean checkUniqueMessageId = false;
    private boolean checkUniqueResponseId = false;
    private boolean validateAccountState = false;

    public static OpenCloseDynamicSettings create(List<StoredSetting> storedSettings) {
        var ret = new OpenCloseDynamicSettings();
        var stored = storedSettings.stream()
                .map(StoredSettingDTO::new)
                .collect(Collectors.toMap(StoredSettingDTO::getKey, StoredSettingDTO::getValue));
        ret.checkUniqueMessageId = getStoredBool(stored, "CheckUniqueMessageId", ret.checkUniqueMessageId);
        ret.checkUniqueResponseId = getStoredBool(stored, "CheckUniqueResponseId", ret.checkUniqueMessageId);
        ret.validateAccountState = getStoredBool(stored, "ValidateAccountState", ret.checkUniqueMessageId);
        return ret;
    }

    private static boolean getStoredBool(Map<String, Object> values, String propertyName, boolean defaultValue) {
        return (Boolean) values.getOrDefault(propertyName, defaultValue);
    }
}
