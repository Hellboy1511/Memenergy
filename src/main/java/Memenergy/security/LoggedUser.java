package Memenergy.security;

import Memenergy.data.User;
import Memenergy.database.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope
public class LoggedUser {
    @Autowired
    UserService userService;

    private User loggedUser;


    public LoggedUser() {
        this.loggedUser = null;
    }

    public User getLoggedUser() {
        return loggedUser;
    }

    public void setLoggedUser(User loggedUser) {
        this.loggedUser = loggedUser;
    }

    public void setLoggedUserByUsername(String username) {
        this.setLoggedUser(this.userService.get(username));
    }

    public boolean isAdmin(){
        try {
            return this.getLoggedUser().getRoles().contains("ROLE_admin");
        }catch (NullPointerException e){
            return false;
        }
    }

    public boolean isExceptionHandler(){
        try {
            return this.getLoggedUser().getRoles().contains("ROLE_exceptionHandler");
        }catch (NullPointerException e){
            return false;
        }
    }

    public boolean isLogged(){
        return this.getLoggedUser()!=null;
    }
}
