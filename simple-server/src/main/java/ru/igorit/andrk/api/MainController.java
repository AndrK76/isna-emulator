package ru.igorit.andrk.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.igorit.andrk.config.services.Constants;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class MainController implements ErrorController {

    private static Map<String, String> listModes = new HashMap<>();

    static {
        listModes.put("general", "Все запросы");
        listModes.put("open-close", "Уведомления об открытии, закрытии и изменении счетов");
    }

    private final int docPerPage;

    public MainController(@Value("${api.doc_per_page}") int docPerPage) {
        this.docPerPage = docPerPage;
    }

    @GetMapping({"/", "/index"})
    public String index() {
        return "index";
    }

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        HttpStatus statusCode = HttpStatus.resolve(Integer.parseInt(status.toString()));


        model.addAttribute("code", status.toString());
        model.addAttribute("name", statusCode.getReasonPhrase());
        return "error";
    }

    @RequestMapping("/list")
    public String showstatistics(
            @RequestParam(name = "mode", required = false, defaultValue = "general") String mode,
            Model model) {
        model.addAttribute("mode", mode);
        model.addAttribute("modeName", listModes.getOrDefault(mode, "Не определено"));
        model.addAttribute("restApiVersion", Constants.API_VERSION);
        model.addAttribute("perPage", docPerPage);
        return "list";
    }


}
