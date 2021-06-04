package Memenergy.web;

import Memenergy.data.User;
import Memenergy.database.services.UserService;
import Memenergy.security.LoggedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;

@Component
public class Header {

    @Autowired
    static UserService userService;
    @Autowired
    LoggedUser loggedUser;

    public Header() {}

    public User header(Model model, HttpServletRequest request){
        model.addAttribute("isLogged", this.loggedUser.isLogged());
        model.addAttribute("isNotLogged", !this.loggedUser.isLogged());
        model.addAttribute("isAdmin", this.loggedUser.isAdmin());
        model.addAttribute("isNotAdmin", !this.loggedUser.isAdmin());
        model.addAttribute("isExceptionHandler", this.loggedUser.isExceptionHandler());
        model.addAttribute("isNotExceptionHandler", !this.loggedUser.isExceptionHandler());
        if(this.loggedUser.isLogged()) {
            model.addAttribute("loggedUser", this.loggedUser.getLoggedUser().getUsername());
        }
        CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
        try {
            model.addAttribute("token",token.getToken());
        }catch (NullPointerException e){ //por si la api quiere devolver un error que no de una excepcion
            model.addAttribute("token","");
        }
        return this.loggedUser.getLoggedUser();
    }
}
