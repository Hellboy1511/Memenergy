package Memenergy.web;

import Memenergy.data.relations.Likes;
import Memenergy.database.services.PostService;
import Memenergy.database.services.UserService;
import Memenergy.database.services.relations.LikesService;
import Memenergy.exceptions.exceptions.web.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.stream.Collectors;

@Controller
public class WebLikesController {
    //All PathVariable parameters are filled via Mustache
    @Autowired
    Header header;

    @Autowired
    UserService userService;
    @Autowired
    PostService postService;
    @Autowired
    LikesService likesService;

    //Get all post which have been liked by a certain user given the user's username, may return an empty page
    @GetMapping("/user/{username}/likes")
    public String likes(Model model, @PathVariable String username, HttpServletRequest request) {
        //Check if visitor is logged to change page display accordingly
        header.header(model,request);

        if (this.userService.get(username) == null) {throw new NotFoundException();}
            Collection<Likes> aux = this.likesService.get(username);
            //Check if visitor is Admin to change page display accordingly

            model.addAttribute("post", aux.parallelStream().map(Likes::getId).sorted().collect(Collectors.toList()));
            model.addAttribute("notLikes", false);
            return "index";

    }

    ////Get all user who have liked a certain post given the post's id, may return an empty page
    @GetMapping("/post/{id}/likes")
    public String likes(Model model, @PathVariable long id, HttpServletRequest request) {
        //Check if visitor is logged to change page display accordingly
        header.header(model,request);

        if (this.postService.get(id) == null) {throw new NotFoundException();}
            Collection<Likes> aux = this.likesService.get(id);
            model.addAttribute("followers", false);
            model.addAttribute("following", false);
            model.addAttribute("userList", true);
            model.addAttribute("users", aux.parallelStream().map(Likes::getUsername).sorted().collect(Collectors.toList()));
            model.addAttribute("notLikes", false);
            return "users";

    }

}
