package Memenergy.exceptions.controllers;

import Memenergy.data.ExceptionEntity;
import Memenergy.database.services.ExceptionEntityService;
import Memenergy.exceptions.exceptions.web.BadRequestException;
import Memenergy.exceptions.exceptions.web.ForbiddenException;
import Memenergy.exceptions.exceptions.web.NotFoundException;
import Memenergy.web.Header;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class WebExceptionController {
    @Autowired
    Header header;

    @ExceptionHandler(value = NotFoundException.class)
    public String notFound(Model model, HttpServletRequest request){
        header.header(model,request);
        model.addAttribute("url","/assets/images/error.jpeg");
        model.addAttribute("fault"," Error 404 page not found. ");
        return "error";
    }

    @ExceptionHandler(value = ForbiddenException.class)
    public String forbidden(Model model,HttpServletRequest request){
        header.header(model,request);
        model.addAttribute("url","/assets/images/forbidden.jpg");
        model.addAttribute("fault"," Forbidden access (⩾﹏⩽)");
        return "error";
    }

    @ExceptionHandler(value = BadRequestException.class)
    public String badRequest(Model model,HttpServletRequest request){
        header.header(model,request);
        model.addAttribute("url","/assets/images/bad_request.jpeg");
        model.addAttribute("fault"," Bad Request ಠ_ಠ");
        return "error";
    }

    @ExceptionHandler(value = FileSizeLimitExceededException.class)
    public String fileSize(Model model, HttpServletRequest request){
        header.header(model,request);
        model.addAttribute("url","/assets/images/fileSize.jpeg");
        model.addAttribute("fault"," The file you sent us is too big, try sending a smaller one.");
        return "error";
    }
}
