package Memenergy.api.relations;

import Memenergy.data.relations.PostReport;
import Memenergy.database.services.PostService;
import Memenergy.database.services.UserService;
import Memenergy.database.services.relations.PostReportService;
import Memenergy.exceptions.exceptions.api.BadRequestException;
import Memenergy.exceptions.exceptions.api.ConflictException;
import Memenergy.exceptions.exceptions.api.NotFoundException;
import Memenergy.exceptions.exceptions.api.UnauthorizedException;
import Memenergy.security.LoggedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
public class PostReportController {

    @Autowired
    LoggedUser loggedUser;
    @Autowired
    PostReportService postReportService;
    @Autowired
    PostService postService;
    @Autowired
    UserService userService;

    // get PostReport given post id and username of the reporter
    @GetMapping("/api/post/{id}/report/{username}")
    public ResponseEntity<PostReport> postReport(@PathVariable long id, @PathVariable String username) {
        if(!this.loggedUser.isAdmin()) throw new UnauthorizedException();
        PostReport postReport = this.postReportService.get(username, id);
        if (postReport == null) {throw new NotFoundException();}
            return new ResponseEntity<>(postReport, HttpStatus.OK);

    }

    // create new PostReport
    @PostMapping("/api/post/{id}/report")
    public ResponseEntity<PostReport> newPostReport(@PathVariable long id, @RequestBody PostReport postReport) {
        if (postReport.getReason() == null || postReport.getReason().isBlank() || postReport.getReason().length() > 1024) throw new BadRequestException();

        if (this.postService.get(id) == null) { throw new NotFoundException();}
        postReport.setUsername(this.loggedUser.getLoggedUser());
        postReport.setId(this.postService.get(id));
        PostReport aux = this.postReportService.create(postReport);
        if (aux != null) {
            return new ResponseEntity<>(aux, HttpStatus.CREATED);
        } else {
            throw new ConflictException();
        }

    }

    // edit PostReport given post id and username of the reporter
    // the only attribute modifiable is reason
    @PutMapping("/api/post/{id}/report/")
    public ResponseEntity<PostReport> updatePostReport(@PathVariable long id, @RequestBody PostReport updatedPostReport) {
        PostReport postReport = postReportService.get(this.loggedUser.getLoggedUser().getUsername(), id);
        if (postReport == null) {throw new NotFoundException();}
        if (updatedPostReport.getReason()==null||updatedPostReport.getReason().length() > 1024 || updatedPostReport.getReason().isBlank())throw new BadRequestException();
        postReport.setReason(updatedPostReport.getReason());
        return new ResponseEntity<>(postReportService.update(this.loggedUser.getLoggedUser().getUsername(), id, postReport), HttpStatus.OK);
    }

    // delete PostReport given post id and username of the reporter
    @DeleteMapping("/api/post/{id}/report/")
    public ResponseEntity<PostReport> deletePostReport(@PathVariable long id) {
        PostReport postReport = postReportService.get(this.loggedUser.getLoggedUser().getUsername(), id);
        if (postReport == null) {throw new NotFoundException();}
        return new ResponseEntity<>(postReportService.delete(this.loggedUser.getLoggedUser().getUsername(), id), HttpStatus.OK);
    }

    // patch a PostReport given post id and username of the reporter
    // the only attribute modifiable is reason
    @PatchMapping("/api/post/{id}/report/")
    public ResponseEntity<PostReport> patchPostReport(@PathVariable long id, @RequestBody PostReport updatedPostReport) {
        PostReport postReport = postReportService.get(this.loggedUser.getLoggedUser().getUsername(), id);
        if (postReport == null) {throw new NotFoundException();}
        if (updatedPostReport.getReason() != null && !updatedPostReport.getReason().isBlank() && updatedPostReport.getReason().length() < 1024) {
            postReport.setReason(updatedPostReport.getReason());
            postReportService.update(this.loggedUser.getLoggedUser().getUsername(), id, postReport);
            return new ResponseEntity<>(postReport, HttpStatus.OK);
        } else {
            throw new BadRequestException();
        }

    }

    // return first 10 PostReports, may return a null element or less than 10 post reports
    @GetMapping("/api/posts/reports")
    @ResponseStatus(HttpStatus.OK)
    public Collection<PostReport> postReports() {
        if(!this.loggedUser.isAdmin())throw new UnauthorizedException();
        return postReportService.byPage(1);
    }

    // return 10 PostReports given page, may return a null element or less than 10 post reports
    @GetMapping("/api/posts/reports/{page}")
    @ResponseStatus(HttpStatus.OK)
    public Collection<PostReport> postReports(@PathVariable long page) {
        if(!this.loggedUser.isAdmin())throw new UnauthorizedException();
        return postReportService.byPage(page);
    }

    // return first 10 PostReports given post, may return a null element or less than 10 posts
    @GetMapping("/api/post/{id}/reports")
    @ResponseStatus(HttpStatus.OK)
    public Collection<PostReport> postReportsId(@PathVariable long id) {
        if(!this.loggedUser.isAdmin())throw new UnauthorizedException();
        return postReportService.byPost(id, 1);
    }

    // return 10 PostReports given page given post, may return a null element or less than 10 posts
    @GetMapping("/api/post/{id}/reports/{page}")
    @ResponseStatus(HttpStatus.OK)
    public Collection<PostReport> postReportsId(@PathVariable long id, @PathVariable long page) {
        if(!this.loggedUser.isAdmin())throw new UnauthorizedException();
        return postReportService.byPost(id, page);
    }
}
