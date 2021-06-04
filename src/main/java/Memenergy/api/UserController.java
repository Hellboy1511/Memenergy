package Memenergy.api;

import Memenergy.data.User;
import Memenergy.database.services.UserService;
import Memenergy.exceptions.exceptions.api.*;
import Memenergy.security.LoggedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

@RestController
public class UserController {

    @Autowired
    LoggedUser loggedUser;
    @Autowired
    UserService userService;

    //get a user given its username
    @GetMapping("/api/user/{username}")
    public ResponseEntity<User> user(@PathVariable String username) {
        User user = this.userService.get(username);
        if (user == null) {throw new NotFoundException();}
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    //create a new user
    @PostMapping("/api/user")
    public ResponseEntity<User> newUser(@RequestBody User user) {

        if (user.getUsername() == null || user.getPassword() == null || user.getEmail() == null) throw new BadRequestException();
        if (user.getUsername().isEmpty()||user.getUsername().length()>255)throw new BadRequestException();
        if (user.getPassword().isEmpty())throw new BadRequestException();
        if (user.getEmail().isEmpty()||user.getEmail().length()>255)throw new BadRequestException();
        if (user.getDescription()!=null&&(user.getDescription().isEmpty()||user.getDescription().length()>1024))throw new BadRequestException();
        if (user.getCountry()!=null&&user.getCountry().length()>255)throw new BadRequestException();
        if (this.userService.get(user.getUsername()) != null || this.userService.getByEmail(user.getEmail()) != null) throw new ConflictException();

            user.setPrivileges(0);
            user.setProfileImage(false);
            return new ResponseEntity<>(this.userService.create(user),HttpStatus.CREATED);

    }

    // edit User given username
    // the only attributes that are not modifiable are username and privileges are not modifiable (may be modified internally by the application or directly on the DB), email is modifiable with restrictions
    @PutMapping("/api/user/{username}")
    public ResponseEntity<User> updateUser(@PathVariable String username, @RequestBody User updatedUser) {
        if (!(this.loggedUser.getLoggedUser().getUsername().equalsIgnoreCase(username)||this.loggedUser.isAdmin())) throw new UnauthorizedException();
        User user = userService.get(username);
        if (user == null) {throw new NotFoundException();}


        if(this.loggedUser.getLoggedUser().equals(user)) {
            if (updatedUser.getEmail() == null || updatedUser.getPassword() == null || (user.getPrivileges()!=updatedUser.getPrivileges()&&user.getPrivileges()!=0)) throw new BadRequestException();
            if (updatedUser.getPrivileges()!=user.getPrivileges())throw new ForbiddenException();
            if (updatedUser.getPassword().isEmpty())throw new BadRequestException();
            if (updatedUser.getEmail().isEmpty()||updatedUser.getEmail().length()>255)throw new BadRequestException();
            if (user.getDescription()!=null&&user.getDescription().length()>1024)throw new BadRequestException();
            if (user.getCountry()!=null&&user.getCountry().length()>255)throw new BadRequestException();

            updatedUser.setUsername(user.getUsername());
            if (this.userService.getByEmail(updatedUser.getEmail()) == null || user.equals(this.userService.getByEmail(updatedUser.getEmail()))) {
                updatedUser.setUsername(this.loggedUser.getLoggedUser().getUsername());
                userService.update(username, updatedUser);
                return new ResponseEntity<>(updatedUser, HttpStatus.OK);
            } else {
                throw new ConflictException();
            }

        } else if (this.loggedUser.isAdmin()){
            if(updatedUser.getPrivileges()>-1){
                user.setPrivileges(updatedUser.getPrivileges());
                return new ResponseEntity<>(this.userService.update(username,user),HttpStatus.OK);
            } else {
                throw new BadRequestException();
            }
        } else {
            throw new UnauthorizedException();
        }
    }

    // delete User given its username
    @DeleteMapping("/api/user/{username}")
    public ResponseEntity<User> deleteUser(@PathVariable String username, HttpServletRequest request) {
        User user = userService.get(username);
        if (user == null) {throw new NotFoundException();}
        if(!(request.getUserPrincipal().equals(user)||(request.isUserInRole("admin")&& !user.getRoles().contains("ROLE_admin")))){throw new UnauthorizedException();}
        return new ResponseEntity<>(this.userService.delete(username), HttpStatus.OK);
    }

    // patch a User given its username
    // the only attributes that are not modifiable are username and privileges are not modifiable (may be modified internally by the application or directly on the DB), email is modifiable with restrictions
    // patching privilegies is only allowed to change its value to 1 as it is impossible to differentiate it from its null value
    @PatchMapping("/api/user/{username}")
    public ResponseEntity<User> patchUser(@PathVariable String username,@RequestBody User updatedUser) {
        if (!(this.loggedUser.getLoggedUser().getUsername().equalsIgnoreCase(username)||this.loggedUser.isAdmin())) throw new UnauthorizedException();
        User user = userService.get(username);
        if (user == null) {throw new NotFoundException();}
        if(this.loggedUser.getLoggedUser().equals(user)){
            if (updatedUser.getBirthDate() != null) {
                user.setBirthDate(updatedUser.getBirthDate());
                userService.update(username, user);
                return new ResponseEntity<>(user, HttpStatus.OK);
            } else if (updatedUser.getCountry() != null) {
                if (updatedUser.getCountry().length()>255)throw new BadRequestException();
                user.setCountry(updatedUser.getCountry());
                userService.update(username, user);
                return new ResponseEntity<>(user, HttpStatus.OK);
            } else if (updatedUser.getDescription() != null) {
                if (updatedUser.getDescription().length()>1024)throw new BadRequestException();
                user.setDescription(updatedUser.getDescription());
                userService.update(username, user);
                return new ResponseEntity<>(user, HttpStatus.OK);
            } else if (updatedUser.getPassword() != null) {
                if(updatedUser.getPassword().isEmpty())throw new BadRequestException();
                user.setPassword(updatedUser.getPassword());
                userService.update(username, user);
                return new ResponseEntity<>(user, HttpStatus.OK);
            } else if (updatedUser.getEmail() != null) {
                if (userService.getByEmail(updatedUser.getEmail()) != null) {
                    throw new ConflictException();
                } else {
                    if (updatedUser.getEmail().isEmpty()||updatedUser.getEmail().length()>255)throw new BadRequestException();
                    user.setEmail(updatedUser.getEmail());
                    userService.update(username, user);
                    return new ResponseEntity<>(user, HttpStatus.OK);
                }
            } else if (updatedUser.getPrivileges()>0){
                throw new ForbiddenException();
            } else {
                throw new BadRequestException();
            }

        }else if(this.loggedUser.isAdmin()){
            if(updatedUser.getPrivileges()>-1){
                user.setPrivileges(updatedUser.getPrivileges());
                return new ResponseEntity<>(this.userService.update(username,user),HttpStatus.OK);
            } else throw new BadRequestException();
        } else throw new UnauthorizedException();
    }

    // return 10 first Users, may return a null element or less than 10 users
    @GetMapping("/api/users")
    @ResponseStatus(HttpStatus.OK)
    public Collection<User> users() {
        return userService.get(1);
    }

    // return 10 Users given page, may return a null element or less than 10 users
    @GetMapping("/api/users/{page}")
    @ResponseStatus(HttpStatus.OK)
    public Collection<User> users(@PathVariable long page) {
        return userService.get(page);
    }

    @GetMapping("/api/users/filtered")
    public ResponseEntity<Collection<User>> filtered(@RequestParam String title, @RequestParam long page) {
        Collection<User> aux = this.userService.filtered(title, page);
        if (aux == null || aux.isEmpty()) {
            if (page == 1) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT); //as a redirection is not needed
            }
        } else {
            return new ResponseEntity<>(aux, HttpStatus.OK);
        }
    }
}
