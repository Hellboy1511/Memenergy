package Memenergy.web;

import Memenergy.data.relations.Follow;
import Memenergy.database.services.UserService;
import Memenergy.database.services.relations.FollowService;
import Memenergy.exceptions.exceptions.web.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Collectors;

@Controller
public class WebFollowController {
    //All PathVariable params are filled via Mustache
    @Autowired
    Header header;

    @Autowired
    FollowService followService;
    @Autowired
    UserService userService;

    //Get 10 last followers of a certain User given username of the followed, may return a not found page, an empty page or less than 10 follows
    @GetMapping("/user/{username}/followers")
    public String followers(Model model, @PathVariable String username, HttpServletRequest request) {
        //Check if visitor is logged to change page display accordingly
        header.header(model,request);
        if (this.userService.get(username) == null) {throw new NotFoundException();}
        long page = 1;
        Collection<Follow> lista = this.followService.followers(username, page);
        model.addAttribute("user", username);
        model.addAttribute("followers", true);
        model.addAttribute("following", false);
        model.addAttribute("userList", false);
        model.addAttribute("users", lista == null ? new LinkedList<Follow>() : lista.parallelStream()
                .map(Follow::getUsernameFollower).collect(Collectors.toList()));
        model.addAttribute("page", page);
        model.addAttribute("previousPage", page - 1);
        model.addAttribute("nextPage", page + 1);
        model.addAttribute("firstPage", page != 1);
        model.addAttribute("lastPage", this.followService.followers(username, page + 1) != null);
        model.addAttribute("notLikes", true);
        return "users";

    }

    // Return 10 followers of a certain User given username of the followed and page, may return a not found page, an empty page or less than 10 follows
    @GetMapping("/user/{username}/followers/{page}")
    public String followers(Model model, @PathVariable String username, @PathVariable long page, HttpServletRequest request) {
        //Check if visitor is logged to change page display accordingly
        header.header(model,request);
        if (this.userService.get(username) == null) {throw new NotFoundException();}
        Collection<Follow> lista = this.followService.followers(username, page);
        model.addAttribute("user", username);
        model.addAttribute("followers", true);
        model.addAttribute("following", false);
        model.addAttribute("userList", false);
        model.addAttribute("users", lista == null ? new LinkedList<Follow>() : lista.parallelStream()
                .map(Follow::getUsernameFollower).collect(Collectors.toList()));
        model.addAttribute("page", page);
        model.addAttribute("previousPage", page - 1);
        model.addAttribute("nextPage", page + 1);
        model.addAttribute("firstPage", page != 1);
        model.addAttribute("lastPage", this.followService.followers(username, page + 1) != null);
        model.addAttribute("notLikes", true);
        return "users";

    }

    //Get 10 last followed users by a certain User given username of the follower, may return a not found page, an empty page or less than 10 follows
    @GetMapping("/user/{username}/following")
    public String following(Model model, @PathVariable String username, HttpServletRequest request) {
        //Check if visitor is logged to change page display accordingly
        header.header(model,request);
        if (this.userService.get(username) == null) {throw new NotFoundException();}
        long page = 1;
        Collection<Follow> lista = this.followService.followed(username, page);
        model.addAttribute("user", username);
        model.addAttribute("followers", false);
        model.addAttribute("following", true);
        model.addAttribute("userList", false);
        model.addAttribute("users", lista == null ? new LinkedList<Follow>() : lista.parallelStream()
                .map(Follow::getUsernameFollowed).collect(Collectors.toList()));
        model.addAttribute("page", page);
        model.addAttribute("previousPage", "/user/" + username + "/following/" + (page - 1));
        model.addAttribute("nextPage", "/user/" + username + "/following/" + (page + 1));
        model.addAttribute("firstPage", page != 1);
        model.addAttribute("lastPage", this.followService.followed(username, page + 1) != null);
        model.addAttribute("notLikes", true);
        return "users";

    }

    // Return 10 followed users by a certain User given username of the follower and page, may return a not found page, an empty page or less than 10 follows
    @GetMapping("/user/{username}/following/{page}")
    public String following(Model model, @PathVariable String username, @PathVariable long page, HttpServletRequest request) {
        //Check if visitor is logged to change page display accordingly
        header.header(model,request);
        if (this.userService.get(username) == null) {throw new NotFoundException();}
        Collection<Follow> lista = this.followService.followed(username, page);
        model.addAttribute("user", username);
        model.addAttribute("followers", false);
        model.addAttribute("following", true);
        model.addAttribute("userList", false);
        model.addAttribute("users", lista == null ? new LinkedList<Follow>() : lista.parallelStream()
                .map(Follow::getUsernameFollowed).collect(Collectors.toList()));
        model.addAttribute("page", page);
        model.addAttribute("previousPage", "/user/" + username + "/following/" + (page - 1));
        model.addAttribute("nextPage", "/user/" + username + "/following/" + (page + 1));
        model.addAttribute("firstPage", page != 1);
        model.addAttribute("lastPage", this.followService.followed(username, page + 1) != null);
        model.addAttribute("notLikes", true);
        return "users";

    }
}
