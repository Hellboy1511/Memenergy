package Memenergy.web;

import Memenergy.data.Calendario;
import Memenergy.data.User;
import Memenergy.database.services.ImageService;
import Memenergy.database.services.UserService;
import Memenergy.database.services.relations.FollowService;
import Memenergy.exceptions.exceptions.web.BadRequestException;
import Memenergy.exceptions.exceptions.web.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;

@Controller
public class WebUserController {
    //All PathVariable parameters are filled via Mustache
    @Autowired
    Header header;

    @Autowired
    UserService userService;
    @Autowired
    ImageService imageService;
    @Autowired
    FollowService followService;

    //Get a user given their username
    @GetMapping("/user/{username}")
    public String user(Model model, @PathVariable String username, HttpServletRequest request) {
        User user = header.header(model,request);
        if(user != null && username.equalsIgnoreCase(user.getUsername())) {
            model.addAttribute("url","/myprofile");
            return "redirect";
        } else {
            User aux = this.userService.get(username);
            //Check if visitor is logged to change page display accordingly

            //Check if it's the visitor's profile to change page display accordingly
            model.addAttribute("isMyProfile", false);
            model.addAttribute("isNotMyProfile", true);
            if (aux == null) {
                throw new NotFoundException();
            }
            model.addAttribute("username", aux.getUsername());
            model.addAttribute("email", aux.getEmail());
            model.addAttribute("date", aux.getBirthDate());
            model.addAttribute("country", aux.getCountry() == null ? "  " : aux.getCountry());
            model.addAttribute("description", aux.getDescription() == null ? "  " : aux.getDescription());
            if (user != null)
                model.addAttribute("exists", this.followService.get(username, user.getUsername()) == null ? "notExists" : "exists");

            return "profile";
        }
    }

    //Check if visitor is logged to change register page display accordingly
    @GetMapping("/register")
    public String userForm(Model model,HttpServletRequest request) {
        header.header(model,request);
        //Error control
        model.addAttribute("emailTaken", false);
        model.addAttribute("usernameTaken", false);
        return "register";
    }

    //Check if visitor is logged to change register error page display accordingly
    @GetMapping("/register/error/email")
    public String errorMail(Model model,HttpServletRequest request) {
        header.header(model,request);
        model.addAttribute("emailTaken", true); //this page appears when the email given to /register is already taken
        model.addAttribute("usernameTaken", false);
        return "register";
    }

    //Check if visitor is logged to change register error page display accordingly
    @GetMapping("/register/error/username")
    public String errorUsername(Model model,HttpServletRequest request) {
        header.header(model,request);
        model.addAttribute("emailTaken", false);
        model.addAttribute("usernameTaken", true); //this page appears when the username given to /register is already taken
        return "register";
    }

    //Check if visitor is logged to change register error page display accordingly
    @GetMapping("/register/error/all")
    public String errorAll(Model model,HttpServletRequest request) {
        header.header(model,request);
        //this page appears when both the email and the username given to /register are already taken
        model.addAttribute("emailTaken", true);
        model.addAttribute("usernameTaken", true);
        return "register";
    }

    //Create a new user
    @PostMapping("/user")
    public String register(Model model, @RequestParam String email, @RequestParam String username,
                           @RequestParam String password,HttpServletRequest request) {
        //Check if visitor is logged to change page display accordingly
        if (email.isEmpty()||username.isEmpty()||password.isEmpty()) throw new BadRequestException();
        if (email.isBlank()||username.isBlank()||password.isBlank()) throw new BadRequestException();
        if(email.length()>255||username.length()>255)throw new BadRequestException();
        header.header(model,request);
        if (this.userService.getByEmail(email) != null) {
            if (this.userService.get(username) != null) {
                model.addAttribute("url", "/register/error/all");
            } else {
                model.addAttribute("url", "/register/error/email");
            }
        } else if (this.userService.get(username) != null) {
            model.addAttribute("url", "/register/error/username");
        } else {
            User aux = new User(username, email, password);
            this.userService.create(aux);
            model.addAttribute("url", "/login");
        }
        return "redirect";
    }

    //Get the visitor's profile page (can only be accessed when visitor is logged)
    @GetMapping("/myprofile") // not implemented until session control
    public String myprofile(Model model, HttpServletRequest request) {
        //Check if visitor is logged to change page display accordingly
        model.addAttribute("isMyProfile",true);
        model.addAttribute("isNotMyProfile", false );
        User user = header.header(model,request);

        model.addAttribute("email",user.getEmail());
        model.addAttribute("date",user.getBirthDate());
        model.addAttribute("country", user.getCountry() == null ? "  " : user.getCountry());
        model.addAttribute("description", user.getDescription() == null ? "  " : user.getDescription());
        model.addAttribute("username",user.getUsername());


        header.header(model,request);
        return "profile";
    }

    //Edit the visitor's user (can only be accessed when visitor is logged)
    @PostMapping("/myprofile/edit") // not implemented completely until session control
    public String profileEdit(Model model, @RequestParam(required = false) String email,
                              @RequestParam(required = false) MultipartFile image, @RequestParam(required = false) String description, @RequestParam(required = false) String country,
                              @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date birthDate,@RequestParam(required = false) String password, HttpServletRequest request) throws IOException {
        //Check if visitor is logged to change page display accordingly
        User user = header.header(model,request);

        if (user == null) {throw new NotFoundException();}

        if (email!=null && (email.isEmpty() || email.length()>255)) throw new BadRequestException();
        if (image!=null && (image.getBytes().length > 60000000||(!image.isEmpty()&&!image.getContentType().split("/")[0].equals("image")))) throw new BadRequestException();
        if (description!=null && (description.length()>1024)) throw new BadRequestException();
        if (country!=null && (country.length()>255)) throw new BadRequestException();


        if (image != null && (!image.isEmpty()) && image.getContentType().split("/")[0].equals("image")) {
            user.setProfileImage(true);
            this.imageService.saveImage(user.getUsername(), image);
        }
        if (email != null && !email.isBlank()) {
            if (this.userService.getByEmail(email) == null) {
                user.setEmail(email);
            }
        }
        if(description==null ||description.length()==0) {
            user.setDescription("  ");
        } else {
            user.setDescription(description);
        }
        user.setCountry(country);
        user.setBirthDate(Calendario.fromHtlmDate(birthDate));
        if(password != null && !password.isBlank()){
            user.setPassword(password);
        }

        this.userService.update(user.getUsername(), user);
        model.addAttribute("url", "/myprofile");
        return "redirect";
    }

    //Check if visitor is logged to change profile edit page display accordingly
    @GetMapping("/myprofile/edit")
    public String editForm(Model model, HttpServletRequest request) {
        User user = header.header(model,request);
        model.addAttribute("email",user.getEmail());
        model.addAttribute("date",user.getBirthDate());
        model.addAttribute("country", user.getCountry() == null ? "  " : user.getCountry());
        model.addAttribute("description", user.getDescription() == null ? "  " : user.getDescription());
        model.addAttribute("username",user.getUsername());
        return "profile_edit";
    }

    //Get the 10 last users created
    @GetMapping("/users")
    public String users(Model model, HttpServletRequest request) {
        long page = 1;
        //Check if visitor is logged to change page display accordingly
        header.header(model,request);
        model.addAttribute("followers", false);
        model.addAttribute("following", false);
        model.addAttribute("userList", true);
        model.addAttribute("users", this.userService.get(page));
        model.addAttribute("page", page);
        model.addAttribute("previousPage", "/users/" + (page - 1));
        model.addAttribute("nextPage", "/users/" + (page + 1));
        model.addAttribute("firstPage", page != 1);
        model.addAttribute("lastPage", this.userService.get(page + 1) != null);
        model.addAttribute("notLikes", true);
        return "users";
    }

    //Get 10 users created given page
    @GetMapping("/users/{page}")
    public String users(Model model, @PathVariable long page, HttpServletRequest request) {
        //Check if visitor is logged to change page display accordingly
        header.header(model,request);
        model.addAttribute("followers", false);
        model.addAttribute("following", false);
        model.addAttribute("userList", true);
        model.addAttribute("users", this.userService.get(page));
        model.addAttribute("page", page);
        model.addAttribute("previousPage", "/users/" + (page - 1));
        model.addAttribute("nextPage", "/users/" + (page + 1));
        model.addAttribute("firstPage", page != 1);
        model.addAttribute("lastPage", this.userService.get(page + 1) != null);
        model.addAttribute("notLikes", true);
        return "users";
    }


    //Check if visitor is logged to change login page display accordingly
    @GetMapping("/login")
    public String loginForm(Model model,HttpServletRequest request) {
        header.header(model,request);
        return "login";

    }

    @GetMapping("/loginError")
    public String loginError(Model model,HttpServletRequest request){
        header.header(model,request);
        return "login_error";
    }

    @GetMapping("/users/filtered")
    public String filtered(Model model, HttpServletRequest request) {

        header.header(model,request);
        model.addAttribute("type", "user");
        model.addAttribute("post", false);
        model.addAttribute("user", true);
        return "filtered";
    }
}
