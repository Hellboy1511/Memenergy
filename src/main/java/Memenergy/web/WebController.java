package Memenergy.web;

import Memenergy.data.ExceptionEntity;
import Memenergy.data.User;
import Memenergy.database.services.ExceptionEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class WebController implements ErrorController {
    @Autowired
    Header header;
    @Autowired
    ExceptionEntityService exceptionEntityService;
    @Autowired
    ErrorAttributes errorAttributes;

    //Check if visitor is logged to change about page display accordingly
    @GetMapping("/about")
    public String about(Model model, HttpServletRequest request) {
        header.header(model,request);
        return "about";
    }

    @RequestMapping("/error")
    public String error(Model model, HttpServletRequest request, WebRequest webRequest) {
        User loggedUser =header.header(model,request);
        Map<String,Object> map = this.errorAttributes.getErrorAttributes(webRequest, ErrorAttributeOptions.defaults());
        this.exceptionEntityService.create(new ExceptionEntity(this.errorAttributes.getError(webRequest),loggedUser,
                (int) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE),
                (String) map.get("error"),
                (String) map.get("path")));
        model.addAttribute("url","/assets/images/error.jpeg");
        model.addAttribute("fault"," There has been an error  ( ͡° ͜ʖ ͡°)");
        return "error";
    }

    @RequestMapping("/errors")
    public String errors(Model model, HttpServletRequest request){
        header.header(model, request);
        model.addAttribute("errors",this.exceptionEntityService.getAll());
        return "exceptions";
    }

    @RequestMapping("/forbidden")
    public String forbidden(Model model, HttpServletRequest request) {
        header.header(model,request);
        model.addAttribute("url","/assets/images/forbidden.jpg");
        model.addAttribute("fault"," Forbidden access (⩾﹏⩽)");
        return "error";
    }

    @Override
    public String getErrorPath() {
        return null;
    }
}
