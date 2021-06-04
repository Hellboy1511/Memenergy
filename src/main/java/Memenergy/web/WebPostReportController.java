package Memenergy.web;

import Memenergy.data.Post;
import Memenergy.data.User;
import Memenergy.data.relations.PostReport;
import Memenergy.database.services.PostService;
import Memenergy.database.services.UserService;
import Memenergy.database.services.relations.PostReportService;
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

@Controller
public class WebPostReportController {
    //All PathVariable parameters are filled via Mustache
    @Autowired
    Header header;

    @Autowired
    PostReportService postReportService;
    @Autowired
    PostService postService;
    @Autowired
    UserService userService;

    //Get last 10 reports of a certain post given its id, may appear less than 10 reports
    @GetMapping("/post/{id}/reports")
    public String reports(Model model, @PathVariable long id, HttpServletRequest request) {
        //Check if visitor is logged to change page display accordingly
        header.header(model,request);
        if (this.postService.get(id) == null) {throw new NotFoundException();}
        long page = 1;
        model.addAttribute("reports", this.postReportService.byPost(id, page));
        model.addAttribute("tipo", "post");
        model.addAttribute("page", page);
        model.addAttribute("previousPage", "/post/" + id + "/reports/" + (page - 1));
        model.addAttribute("nextPage", "/post/" + id + "/reports/" + (page + 1));
        model.addAttribute("firstPage", page != 1);
        model.addAttribute("lastPage", this.postReportService.byPost(id, page + 1) != null);
        return "reports";

    }

    //Get 10 reports of a certain post given its id and page, may appear less than 10 reports
    @GetMapping("/post/{id}/reports/{page}")
    public String reports(Model model, @PathVariable long id, @PathVariable long page, HttpServletRequest request) {
        //Check if visitor is logged to change page display accordingly
        header.header(model,request);
        if (this.postService.get(id) == null) {throw new NotFoundException();}
        model.addAttribute("reports", this.postReportService.byPost(id, page));
        model.addAttribute("tipo", "post");
        model.addAttribute("page", page);
        model.addAttribute("previousPage", "/post/" + id + "/reports/" + (page - 1));
        model.addAttribute("nextPage", "/post/" + id + "/reports/" + (page + 1));
        model.addAttribute("firstPage", page != 1);
        model.addAttribute("lastPage", this.postReportService.byPost(id, page + 1) != null);
        return "reports";

    }

    //Report a certain post given its id
    @PostMapping("/post/{id}/report")
    public String newReport(Model model, @PathVariable long id, @RequestParam String reason, HttpServletRequest request) {
        //Check if visitor is logged to change page display accordingly
        if (reason.isEmpty()||reason.length()>1024) throw new BadRequestException();
        User loggedUser = header.header(model,request);
        Post aux = this.postService.get(id);
        if (aux == null) {throw new NotFoundException();}
            if (this.postReportService.get(loggedUser.getUsername(), id) != null) {
                model.addAttribute("url", "/post/" + id);
                return "redirect";
            } else {
                PostReport pr = new PostReport(loggedUser, aux, reason);
                this.postReportService.create(pr);
                model.addAttribute("url", "/post/" + id);
                return "redirect";
            }


    }

    //get report form
    @GetMapping("/post/{id}/report")
    public String reportForm(Model model, @PathVariable long id, HttpServletRequest request) {
        //Check if visitor is logged to change page display accordingly
        header.header(model,request);
        if (this.postService.get(id) == null) {throw new NotFoundException();}
        model.addAttribute("tipo", "post");
        model.addAttribute("id", id);
        return "report";

    }

}
