package Memenergy.security;

import Memenergy.data.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
public class LoginController {

    @Autowired
    LoggedUser loggedUser;

    @GetMapping("/api/login")
    public ResponseEntity<User> login(){
        if (!loggedUser.isLogged()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else {
            return new ResponseEntity<>(loggedUser.getLoggedUser(), HttpStatus.OK);
        }
    }

    @GetMapping("/api/logout")
    public ResponseEntity<Boolean> logout(HttpSession session){
        if (!loggedUser.isLogged()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else {
            session.invalidate();
            return new ResponseEntity<>(true, HttpStatus.OK);
        }
    }

}
