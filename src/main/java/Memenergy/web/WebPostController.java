package Memenergy.web;

import Memenergy.data.Post;
import Memenergy.data.User;
import Memenergy.database.services.CommentService;
import Memenergy.database.services.ImageService;
import Memenergy.database.services.PostService;
import Memenergy.database.services.UserService;
import Memenergy.database.services.relations.LikesService;
import Memenergy.exceptions.exceptions.web.BadRequestException;
import Memenergy.exceptions.exceptions.web.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Controller
public class WebPostController {
    //All PathVariable parameters are filled via Mustache
    @Autowired
    Header header;

    @Autowired
    PostService postService;
    @Autowired
    CommentService commentService;
    @Autowired
    LikesService likesService;
    @Autowired
    UserService userService;
    @Autowired
    ImageService imageService;

    //Get a post and its 10 last comments (may appear less than 10 comments) given its id
    @GetMapping("/post/{id}")
    public String post(Model model, @PathVariable long id, HttpServletRequest request) {
        //Check if visitor is logged to change page display accordingly
        User loggedUser = header.header(model,request);
        Post aux = this.postService.get(id);

        if (aux == null) {throw new NotFoundException();}

        model.addAttribute("id", aux.getId());
        model.addAttribute("isUserOwner",request.isUserInRole("admin")||(loggedUser != null && loggedUser.equals(aux.getUserCreator())));

        if (loggedUser!=null &&this.likesService.get(loggedUser.getUsername(), id) != null) {
            model.addAttribute("like", "/assets/ico/liked.svg");
        } else {
            model.addAttribute("like", "/assets/ico/like.svg");
        }
        model.addAttribute("likes", aux.getLikeCounter());
        model.addAttribute("title", aux.getTitle());
        model.addAttribute("date", aux.getDate());
        model.addAttribute("user", aux.getUserCreator());
        model.addAttribute("reports", aux.getReportCounter());
        model.addAttribute("description", aux.getDescription());
        //Check if visitor is Admin to change page display accordingly
        //model.addAttribute("isNotAdmin",true); ?

        model.addAttribute("page", 1);
        model.addAttribute("previousPage", "/post/" + id + "/" + 0);
        model.addAttribute("nextPage", "/post/" + id + "/" + 2);
        model.addAttribute("firstPage", false);
        model.addAttribute("lastPage", this.commentService.get(2, id) != null);
        model.addAttribute("comments", this.commentService.get(1, id));
        model.addAttribute("hasUserCreator",aux.hasUserCreator());
        model.addAttribute("notHasUserCreator",aux.notHasUserCreator());

        return "post";
    }

    //Get a post and 10 of its comments (may appear less than 10 comments) given its id and page
    @GetMapping("/post/{id}/{page}")
    public String post(Model model, @PathVariable long id, @PathVariable long page, HttpServletRequest request) {
        //Check if visitor is logged to change page display accordingly
        User loggedUser = header.header(model,request);
        Post aux = this.postService.get(id);
        if (aux == null) {throw new NotFoundException();}

        model.addAttribute("id", aux.getId());
        model.addAttribute("isUserOwner",request.isUserInRole("admin")||(loggedUser != null && loggedUser.equals(aux.getUserCreator())));

        if (loggedUser!=null &&this.likesService.get(loggedUser.getUsername(), id) != null) {
            model.addAttribute("like", "/assets/ico/liked.svg");
        } else {
            model.addAttribute("like", "/assets/ico/like.svg");
        }

        model.addAttribute("likes", aux.getLikeCounter());
        model.addAttribute("title", aux.getTitle());
        model.addAttribute("date", aux.getDate());
        model.addAttribute("user", aux.getUserCreator());
        model.addAttribute("reports", aux.getReportCounter());
        model.addAttribute("description", aux.getDescription());
        //Check if visitor is Admin to change page display accordingly
        //model.addAttribute("isNotAdmin",true); ?

        model.addAttribute("page", page);
        model.addAttribute("previousPage", "/post/" + id + "/" + (page - 1));
        model.addAttribute("nextPage", "/post/" + id + "/" + (page + 1));
        model.addAttribute("firstPage", page != 1);
        model.addAttribute("lastPage", this.commentService.get(page + 1, id) != null);
        model.addAttribute("comments", this.commentService.get(page, id));
        model.addAttribute("hasUserCreator",aux.hasUserCreator());
        model.addAttribute("notHasUserCreator",aux.notHasUserCreator());

        return "post";
    }

    //Check if visitor is logged to change post upload page display accordingly
    @GetMapping("/post")
    public String postForm(Model model, HttpServletRequest request) {
        header.header(model,request);
        return "upload";
    }

    // Post a new post
    @PostMapping("/post")
    public String newPost(Model model, @RequestParam String title, @RequestParam MultipartFile file,
                          @RequestParam(required = false) String description, HttpServletRequest request) throws IOException {

        if(title.isEmpty()||title.length()>255) throw new BadRequestException();
        if(description!=null&&description.length()>1024) throw new BadRequestException();
        if(file.getBytes().length>60000000||!file.getContentType().split("/")[0].equals("image")) throw new BadRequestException();

        //Check if visitor is logged to change page display accordingly
        header.header(model,request);
        Post aux = new Post(title, description==null?"  ":description, this.userService.get(request.getUserPrincipal().getName()));
        aux = this.postService.create(aux);
        this.imageService.saveImage(aux.getId(), file);
        model.addAttribute("url", "/post/" + aux.getId());
        return "redirect";
    }

    //Get last 10 posts to fill index page, may appear less than 10 posts
    @GetMapping("/")
    public String root(Model model, HttpServletRequest request) {
        long page = 1;
        //Check if visitor is logged to change page display accordingly
        header.header(model,request);
        model.addAttribute("post", this.postService.byPage(page));
        model.addAttribute("page", page);
        model.addAttribute("previousPage", "/posts/" + (page - 1));
        model.addAttribute("nextPage", "/posts/" + (page + 1));
        model.addAttribute("firstPage", page != 1);
        model.addAttribute("lastPage", this.postService.byPage(page + 1) != null);
        //Check if visitor is Admin to change page display accordingly
        //model.addAttribute("isNotAdmin",true); ?

        model.addAttribute("notLikes", true);
        return "index";
    }

    //Get last 10 posts to fill index page, may appear less than 10 posts
    @GetMapping("/posts")
    public String posts(Model model, HttpServletRequest request) {
        long page = 1;
        //Check if visitor is logged to change page display accordingly
        header.header(model,request);
        model.addAttribute("post", this.postService.byPage(page));
        model.addAttribute("page", page);
        model.addAttribute("previousPage", "/posts/" + (page - 1));
        model.addAttribute("nextPage", "/posts/" + (page + 1));
        model.addAttribute("firstPage", page != 1);
        model.addAttribute("lastPage", this.postService.byPage(page + 1) != null);
        //Check if visitor is Admin to change page display accordingly
        //model.addAttribute("isNotAdmin",true); ?

        model.addAttribute("notLikes", true);
        return "index";
    }

    //Get 10 posts given page, may appear less than 10 posts
    @GetMapping("/posts/{page}")
    public String posts(Model model, @PathVariable long page, HttpServletRequest request) {
        //Check if visitor is logged to change page display accordingly
        header.header(model,request);
        model.addAttribute("post", this.postService.byPage(page));
        model.addAttribute("page", page);
        model.addAttribute("previousPage", "/posts/" + (page - 1));
        model.addAttribute("nextPage", "/posts/" + (page + 1));
        model.addAttribute("firstPage", page != 1);
        model.addAttribute("lastPage", this.postService.byPage(page + 1) != null);
        //Check if visitor is Admin to change page display accordingly
        //model.addAttribute("isNotAdmin",true); ?

        model.addAttribute("notLikes", true);
        return "index";
    }

    //Get 10 last reported posts, may appear less than 10 posts
    @GetMapping("/posts/reported")
    public String reported(Model model, HttpServletRequest request) {
        long page = 1;
        //Check if visitor is logged to change page display accordingly
        header.header(model,request);
        model.addAttribute("post", this.postService.reported(page));
        model.addAttribute("page", page);
        model.addAttribute("previousPage", "/posts/reported/" + (page - 1));
        model.addAttribute("nextPage", "/posts/reported/" + (page + 1));
        model.addAttribute("firstPage", page != 1);
        model.addAttribute("lastPage", this.postService.reported(page + 1) != null);
        //Check if visitor is Admin to change page display accordingly
        //model.addAttribute("isNotAdmin",true); ?

        model.addAttribute("notLikes", true);
        return "index";
    }

    //Get 10 last reported posts given page, may appear less than 10 posts
    @GetMapping("/posts/reported/{page}")
    public String reported(Model model, @PathVariable long page, HttpServletRequest request) {
        //Check if visitor is logged to change page display accordingly
        header.header(model,request);
        model.addAttribute("post", this.postService.reported(page));
        model.addAttribute("page", page);
        model.addAttribute("previousPage", "/posts/reported/" + (page - 1));
        model.addAttribute("nextPage", "/posts/reported/" + (page + 1));
        model.addAttribute("firstPage", page != 1);
        model.addAttribute("lastPage", this.postService.reported(page + 1) != null);
        //Check if visitor is Admin to change page display accordingly
        //model.addAttribute("isNotAdmin",true); ?

        model.addAttribute("notLikes", true);
        return "index";
    }

    //Get 10 last posts posted by a certain user given the creator's username, may appear less than 10 posts
    @GetMapping("/user/{username}/posts")
    public String userPosts(Model model, @PathVariable String username, HttpServletRequest request) {
        long page = 1;
        //Check if visitor is logged to change page display accordingly
        header.header(model,request);
        if (this.userService.get(username) == null) {throw new NotFoundException();}
        model.addAttribute("post", this.postService.byUsername(username, page));
        model.addAttribute("page", page);
        model.addAttribute("previousPage", "/user/" + username + "/posts/" + (page - 1));
        model.addAttribute("nextPage", "/user/" + username + "/posts/" + (page + 1));
        model.addAttribute("firstPage", page != 1);
        model.addAttribute("lastPage", this.postService.byUsername(username, page) != null);
        //Check if visitor is Admin to change page display accordingly
        //model.addAttribute("isNotAdmin",true); ?

        model.addAttribute("notLikes", true);
        return "index";

    }

    //Get 10 posts posted by a certain user given the creator's username and page, may appear less than 10 posts
    @GetMapping("/user/{username}/posts/{page}")
    public String userPosts(Model model, @PathVariable String username, @PathVariable long page, HttpServletRequest request) {
        //Check if visitor is logged to change page display accordingly
        header.header(model,request);
        if (this.userService.get(username) == null) {throw new NotFoundException();}
        model.addAttribute("post", this.postService.byUsername(username, page));
        model.addAttribute("page", page);
        model.addAttribute("previousPage", "/user/" + username + "/posts/" + (page - 1));
        model.addAttribute("nextPage", "/user/" + username + "/posts/" + (page + 1));
        model.addAttribute("firstPage", page != 1);
        model.addAttribute("lastPage", this.postService.byUsername(username, page) != null);
        //Check if visitor is Admin to change page display accordingly
        //model.addAttribute("isNotAdmin",true); ?

        model.addAttribute("notLikes", true);
        return "index";

    }

    //Change a post visibility
    @PostMapping("/post/{id}/forceVisibility")
    public String forceVisibility(Model model, @PathVariable long id, @RequestParam int reportAction, HttpServletRequest request) {
        //Check if visitor is logged to change page display accordingly
        if (!(reportAction==-1||reportAction==0||reportAction==1))throw new BadRequestException();
        User loggedUser = header.header(model,request);
        Post aux = this.postService.get(id);
        if (aux == null) {throw new NotFoundException();}
        aux.setForcedVisibility(reportAction);
        aux.setModeratedBy(loggedUser);
        this.postService.update(id, aux);
        model.addAttribute("url", "/post/" + id);
        return "redirect";

    }

    //Auxiliar of post edit page
    @GetMapping("/post/{id}/edit")
    public String edit(Model model, @PathVariable long id, HttpServletRequest request) {
        //Check if visitor is logged to change page display accordingly
        User loggedUser = header.header(model,request);
        Post post = this.postService.get(id);
        if (post == null) {throw new NotFoundException();}
        //Check if visitor is Admin to change page display accordingly
        model.addAttribute("isUserOwner",loggedUser!=null&&loggedUser.equals(post.getUserCreator()));
        model.addAttribute("id", id);
        model.addAttribute("date", post.getDate());
        model.addAttribute("title", post.getTitle());
        model.addAttribute("userCreator", post.getUserCreator());
        model.addAttribute("forcedVisibility", post.getForcedVisibility());
        model.addAttribute("description", post.getDescription());
        model.addAttribute("reportCounter", post.getReportCounter());
        model.addAttribute("hasUserCreator",post.hasUserCreator());
        model.addAttribute("notHasUserCreator",post.notHasUserCreator());
        return "post_edit";

    }

    //Edit a post given its id
    @PostMapping("/post/{id}/edit")
    public String edit(Model model, @PathVariable long id, @RequestParam String title,  @RequestParam(required = false) String description, HttpServletRequest request) {
        //Check if visitor is logged to change page display accordingly
        if (title.isEmpty()||title.length()>255) throw new BadRequestException();
        if (description!=null&&description.length()>1024)throw new BadRequestException();
        header.header(model,request);
        Post post = this.postService.get(id);
        if (post == null) {throw new NotFoundException();}
        post.setTitle(title);

            if (description == null || description.length() != 0) {
                post.setDescription(description);
            }
            this.postService.update(id, post);
            model.addAttribute("url", "/post/" + id);
            return "redirect";

    }

    @GetMapping("/posts/filtered")
    public String filtered(Model model, HttpServletRequest request) {
        header.header(model,request);
        model.addAttribute("type", "post");
        model.addAttribute("post", true);
        model.addAttribute("user", false);
        return "filtered";
    }

}
