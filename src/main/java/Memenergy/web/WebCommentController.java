package Memenergy.web;

import Memenergy.data.Comment;
import Memenergy.data.User;
import Memenergy.database.services.CommentService;
import Memenergy.database.services.PostService;
import Memenergy.database.services.UserService;
import Memenergy.exceptions.exceptions.web.BadRequestException;
import Memenergy.exceptions.exceptions.web.NotFoundException;
import org.owasp.html.PolicyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedList;

@Controller
public class WebCommentController {
    //All PathVariable parameters are filled via Mustache

    @Autowired
    PolicyFactory policyFactory;
    @Autowired
    Header header;

    @Autowired
    CommentService commentService;
    @Autowired
    PostService postService;
    @Autowired
    UserService userService;

    //Display a comment given its id
    @GetMapping("/comment/{id}")
    public String comment(Model model, @PathVariable long id, HttpServletRequest request) {
        //Check if visitor is logged to change page display accordingly
        User loggedUser = header.header(model,request);
        Comment comment = this.commentService.get(id);
        if (comment == null) {throw new NotFoundException();} //if comment exists
            model.addAttribute("isUserOwner",loggedUser!=null&&loggedUser.equals(comment.getUserCreator()));
            model.addAttribute("userCreator", comment.getUserCreator());
            model.addAttribute("date", comment.getDate());
            model.addAttribute("relatedPost", comment.getRelatedPost());
            model.addAttribute("reportCounter", comment.getReportCounter());
            model.addAttribute("text", comment.getText());
            //Check comment visibility
            if (comment.getForcedVisibility() == -1) {
                model.addAttribute("forcedVisibility", "Hidden");
            } else if (comment.getForcedVisibility() == 0) {
                model.addAttribute("forcedVisibility", "Default");
            } else {
                model.addAttribute("forcedVisibility", "Visible");
            }
            //Check if visitor is admin to change page display accordingly
            model.addAttribute("hasUserCreator",comment.hasUserCreator());
            model.addAttribute("notHasUserCreator",comment.notHasUserCreator());

            model.addAttribute("id", comment.getId());
            return "comment";

    }

    //Create a comment given id of the related Post
    @PostMapping("/post/{id}")
    public String newComment(Model model, @PathVariable long id,
                             @RequestParam String text, HttpServletRequest request) {
        //Check if visitor is logged to change page display accordingly
        text=policyFactory.sanitize(text);
        if(text.isEmpty()||text.length()>1024)throw new BadRequestException();
        User loggedUser = header.header(model,request);
        Comment comment = new Comment(text,loggedUser, this.postService.get(id));
        this.commentService.create(comment);
        model.addAttribute("url", "/post/" + id);
        return "redirect";
    }

    //Change a comment visibility given its id
    @PostMapping("/comment/{id}/forceVisibility")
    public String forceVisibility(Model model, @PathVariable long id, @RequestParam int reportAction, HttpServletRequest request) {
        //Check if visitor is logged to change page display accordingly
        if(!(reportAction==-1||reportAction==0||reportAction==1))throw new BadRequestException();
        User loggedUser = header.header(model,request);
        Comment aux = this.commentService.get(id);
        if (aux == null) {throw new NotFoundException();}
            aux.setForcedVisibility(reportAction);
            aux.setModeratedBy(loggedUser);
            this.commentService.update(id, aux);
            model.addAttribute("url", "/comment/" + id);
            return "redirect";

    }

    //Edit a comment given its id
    // forcedVisibility is not modifiable to 0, as it would be impossible to difference it from its null value (use "/comment/{id}/forceVisibility")
    // Other attributes are only modifiable by specific buttons or directly on the DB
    @PostMapping("/comment/{id}/edit")
    public String editComment(Model model, @PathVariable long id, @RequestParam String text, HttpServletRequest request) {
        //Check if visitor is logged to change page display accordingly
        text=policyFactory.sanitize(text);
        if(text.isEmpty()||text.length()>1024)throw new BadRequestException();
        header.header(model,request);
        Comment aux = this.commentService.get(id);
        if (aux == null) {throw new NotFoundException();}
        aux.setText(text);
        this.commentService.update(id,aux);
        model.addAttribute("url", "/comment/" + id);
        return "redirect";

    }

    // Return 10 last reported comments, may return a not found page, an empty page or less than 10 comments
    @GetMapping("/comments/reported")
    public String reported(Model model, HttpServletRequest request) {
        //Check if visitor is logged to change page display accordingly
        header.header(model,request);
        model.addAttribute("comments", this.commentService.reported(1) == null ? new LinkedList<Comment>() : this.commentService.reported(1));
        model.addAttribute("page", 1);
        model.addAttribute("previousPage", "/comments/reported/" + 0);
        model.addAttribute("nextPage", "/comments/reported/" + 2);
        model.addAttribute("lastPage", this.commentService.reported(2) != null);
        model.addAttribute("firstPage", true);
        return "reported_comments";
    }

    // Return 10 reported comments given page, may return a not found page, an empty page or less than 10 comments
    @GetMapping("/comments/reported/{page}")
    public String reported(Model model, @PathVariable long page, HttpServletRequest request) {
        //Check if visitor is logged to change page display accordingly
        header.header(model,request);
        model.addAttribute("comments", this.commentService.reported(page) == null ? new LinkedList<Comment>() : this.commentService.reported(page));
        model.addAttribute("page", page);
        model.addAttribute("previousPage", "/comments/reported/" + (page - 1));
        model.addAttribute("nextPage", "/comments/reported/" + (page + 1));
        model.addAttribute("lastPage", this.commentService.reported(page + 1) != null);
        model.addAttribute("firstPage", page != 1);
        return "reported_comments";
    }
}