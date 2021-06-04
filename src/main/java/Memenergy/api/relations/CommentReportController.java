package Memenergy.api.relations;

import Memenergy.data.relations.CommentReport;
import Memenergy.database.services.CommentService;
import Memenergy.database.services.UserService;
import Memenergy.database.services.relations.CommentReportService;
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
public class CommentReportController {

    @Autowired
    LoggedUser loggedUser;
    @Autowired
    CommentReportService commentReportService;
    @Autowired
    CommentService commentService;
    @Autowired
    UserService userService;

    // get CommentReport given Comment id and username of the reporter
    @GetMapping("/api/comment/{id}/report/{username}")
    public ResponseEntity<CommentReport> commentReport(@PathVariable long id, @PathVariable String username) {
        if(!loggedUser.isAdmin()) throw new UnauthorizedException();
        CommentReport aux = this.commentReportService.get(username, id);
        if (aux == null) {throw new NotFoundException();}
            return new ResponseEntity<>(aux, HttpStatus.OK);

    }

    // create new CommentReport
    @PostMapping("/api/comment/{id}/report")
    public ResponseEntity<CommentReport> newCommentReport(@PathVariable long id, @RequestBody CommentReport commentReport) {
        if (commentReport.getReason() == null || commentReport.getReason().isBlank()||commentReport.getReason().length()>1024) throw new BadRequestException();
        if (this.commentService.get(id) == null) {throw new NotFoundException();}
        commentReport.setUsername(loggedUser.getLoggedUser());
        commentReport.setId(this.commentService.get(id));
        CommentReport aux = this.commentReportService.create(commentReport);
        if (aux != null) {
            return new ResponseEntity<>(aux, HttpStatus.CREATED);
        } else {
            throw new ConflictException();
        }
    }

    // edit CommentReport given Comment id and username of the reporter
    // only the reason is modifiable
    @PutMapping("/api/comment/{id}/report")
    public ResponseEntity<CommentReport> updateCommentReport(@PathVariable long id, @RequestBody CommentReport updatedCommentReport) {
        CommentReport commentReport = commentReportService.get(loggedUser.getLoggedUser().getUsername(), id);
        if (commentReport == null) {throw new NotFoundException();}
        if (updatedCommentReport.getReason()==null||updatedCommentReport.getReason().isBlank()||updatedCommentReport.getReason().length()>1024)throw new BadRequestException();
        commentReport.setReason(updatedCommentReport.getReason());
        return new ResponseEntity<>(commentReportService.update(loggedUser.getLoggedUser().getUsername(), id, commentReport), HttpStatus.OK);
    }

    // delete CommentReport given Comment id and username of the reporter
    @DeleteMapping("/api/comment/{id}/report")
    public ResponseEntity<CommentReport> deleteCommentReport(@PathVariable long id) {
        CommentReport commentReport = this.commentReportService.get(loggedUser.getLoggedUser().getUsername(), id);
        if (commentReport == null) {throw new NotFoundException();}
        return new ResponseEntity<>(this.commentReportService.delete(loggedUser.getLoggedUser().getUsername(), id), HttpStatus.OK);
    }

    // patch a CommentReport given Comment id and username of the reporter
    // only the reason is modifiable
    @PatchMapping("/api/comment/{id}/report")
    public ResponseEntity<CommentReport> patchCommentReport(@PathVariable long id, @RequestBody CommentReport updatedCommentReport) {
        CommentReport commentReport = this.commentReportService.get(loggedUser.getLoggedUser().getUsername(), id);
        if (commentReport == null) {throw new NotFoundException();}
        if (updatedCommentReport.getReason() != null) {
            if (updatedCommentReport.getReason().isBlank()||updatedCommentReport.getReason().length()>1024) throw new BadRequestException();
            commentReport.setReason(updatedCommentReport.getReason());
            return new ResponseEntity<>(this.commentReportService.update(loggedUser.getLoggedUser().getUsername(), id, commentReport), HttpStatus.OK);
        } else {
            throw new BadRequestException();
        }
    }

    // return first 10 CommentReports from a specific comment, may return a null element or less than 10 comment reports
    @GetMapping("/api/comment/{id}/reports")
    @ResponseStatus(HttpStatus.OK)
    public Collection<CommentReport> commentReports(@PathVariable long id) {
        if(!loggedUser.isAdmin())throw new UnauthorizedException();
        return this.commentReportService.byComment(id, 1);
    }

    // return 10 CommentReports from a specific comment given page, may return a null element or less than 10 comment reports
    @GetMapping("/api/comment/{id}/reports/{page}")
    @ResponseStatus(HttpStatus.OK)
    public Collection<CommentReport> commentReports1(@PathVariable long id, @PathVariable long page) {
        if(!loggedUser.isAdmin())throw new UnauthorizedException();
        return this.commentReportService.byComment(id, page);
    }

    // return first 10 CommentReports, may return a null element or less than 10 comment reports
    @GetMapping("/api/comments/reports")
    @ResponseStatus(HttpStatus.OK)
    public Collection<CommentReport> commentsReports() {
        if(!loggedUser.isAdmin())throw new UnauthorizedException();
        return this.commentReportService.byPage(1);
    }

    // return 10 CommentReports given page, may return a null element or less than 10 comment reports
    @GetMapping("/api/comments/reports/{page}")
    @ResponseStatus(HttpStatus.OK)
    public Collection<CommentReport> commentsReports(@PathVariable long page) {
        if(!loggedUser.isAdmin())throw new UnauthorizedException();
        return this.commentReportService.byPage(page);
    }
}
