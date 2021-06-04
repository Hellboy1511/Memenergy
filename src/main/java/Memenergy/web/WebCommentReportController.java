package Memenergy.web;

import Memenergy.data.Comment;
import Memenergy.data.User;
import Memenergy.data.relations.CommentReport;
import Memenergy.database.services.CommentService;
import Memenergy.database.services.UserService;
import Memenergy.database.services.relations.CommentReportService;
import Memenergy.exceptions.exceptions.web.BadRequestException;
import Memenergy.exceptions.exceptions.web.NotFoundException;
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
public class WebCommentReportController {
    //All PathVariable parameters are filled via Mustache
    @Autowired
    Header header;

    @Autowired
    CommentReportService commentReportService;
    @Autowired
    CommentService commentService;
    @Autowired
    UserService userService;

    //Get the last 10 reports of a certain Comment given its id, may return a not found page, an empty page or less than 10 comment reports
    @GetMapping("/comment/{id}/reports")
    public String reports(Model model, @PathVariable long id, HttpServletRequest request) {
        //Check if visitor is logged to change page display accordingly
        header.header(model,request);
        if (this.commentService.get(id) == null) {throw new NotFoundException();}
        long page = 1;
        model.addAttribute("reports", this.commentReportService.byComment(id, page) == null ? new LinkedList<CommentReport>() : this.commentReportService.byComment(id, page));
        model.addAttribute("tipo", "comment");
        model.addAttribute("page", page);
        model.addAttribute("previousPage", "/comment/" + id + "/reports/" + (page - 1));
        model.addAttribute("nextPage", "/comment/" + id + "/reports/" + (page + 1));
        model.addAttribute("firstPage", page != 1);
        model.addAttribute("lastPage", this.commentReportService.byComment(id, page + 1) != null);
        return "reports";

    }

    // Return 10 reports of a certain Comment given page and the comment's id, may return a not found page, an empty page or less than 10 comment reports
    @GetMapping("/comment/{id}/reports/{page}")
    public String reports(Model model, @PathVariable long id, @PathVariable long page, HttpServletRequest request) {
        //Check if visitor is logged to change page display accordingly
        header.header(model,request);
        if (this.commentService.get(id) == null) {throw new NotFoundException();}
        model.addAttribute("reports", this.commentReportService.byComment(id, page) == null ? new LinkedList<CommentReport>() : this.commentReportService.byComment(id, page));
        model.addAttribute("tipo", "comment");
        model.addAttribute("page", page);
        model.addAttribute("previousPage", "/comment/" + id + "/reports/" + (page - 1));
        model.addAttribute("nextPage", "/comment/" + id + "/reports/" + (page + 1));
        model.addAttribute("firstPage", page != 1);
        model.addAttribute("lastPage", this.commentReportService.byComment(id, page + 1) != null);
        return "reports";

    }

    //Create a new report for a certain Comment given its id
    @PostMapping("/comment/{id}/report")
    public String newReport(Model model, @PathVariable long id, @RequestParam String reason, HttpServletRequest request) {
        //Check if visitor is logged to change page display accordingly
        if (reason.isEmpty()||reason.length()>1024)throw new BadRequestException();
        User loggedUser = header.header(model,request);
        Comment comment = this.commentService.get(id);
        if (comment == null) {throw new NotFoundException();}
            if (this.commentReportService.get(loggedUser.getUsername(), id) != null) { //no hacemos nada si el commentReport ya existia en la DB
                model.addAttribute("url", "/comment/" + id);
                return "redirect";
            } else {
                CommentReport aux = new CommentReport(loggedUser, comment, reason);
                this.commentReportService.create(aux);
                model.addAttribute("url", "/comment/" + id);
                return "redirect";
            }

    }

    //get report form
    @GetMapping("/comment/{id}/report")
    public String reportForm(Model model, @PathVariable long id, HttpServletRequest request) {
        //Check if visitor is logged to change page display accordingly
        header.header(model,request);
        if (this.commentService.get(id) == null) {throw new NotFoundException();}
            model.addAttribute("tipo", "comment");
            model.addAttribute("id", id);
            return "report";

    }

}
