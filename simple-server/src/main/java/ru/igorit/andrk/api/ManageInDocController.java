package ru.igorit.andrk.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.igorit.andrk.api.model.NewUploadFile;
import ru.igorit.andrk.config.services.Constants;

import java.util.Map;

@RestController
@RequestMapping("/api/" + Constants.API_VERSION)
public class ManageInDocController {

    private final Map<String, String> fileModes = Map.of(
            "audit-monitoring", "Аудит и мониторинг",
            "reab-bankrot", "Реабилитация и банкротство"
    );

    @GetMapping("/send-file/file-modes")
    public Map<String, String> getFileModes() {
        return fileModes;
    }


    @PostMapping("/send-file/add-new")
    public String uploadFile(@ModelAttribute NewUploadFile file) {
        String res = "descr: " + file.getDescription() + "<br/>"
                + "type: " + fileModes.get(file.getFileType()) + "<br/>"
                + "name: " + file.getFile().getOriginalFilename() + "<br/>"
                + "size: " + file.getFile().getSize() + "<br/>";
        return res;
    }
}
