package Memenergy.api;

import Memenergy.data.Comment;
import Memenergy.data.Post;
import Memenergy.database.services.CommentService;
import Memenergy.database.services.PostService;
import Memenergy.database.services.UserService;
import Memenergy.exceptions.exceptions.api.BadRequestException;
import Memenergy.exceptions.exceptions.api.ForbiddenException;
import Memenergy.exceptions.exceptions.api.NotFoundException;
import Memenergy.exceptions.exceptions.api.UnauthorizedException;
import Memenergy.security.LoggedUser;
import org.owasp.html.PolicyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
public class CommentController {

    @Autowired
    LoggedUser loggedUser;
    @Autowired
    PolicyFactory policyFactory;

    @Autowired
    CommentService commentService;
    @Autowired
    PostService postService;
    @Autowired
    UserService userService;

    // return Comment given id
    @GetMapping("/api/comment/{id}")
    public ResponseEntity<Comment> comment(@PathVariable long id) {
        Comment aux = commentService.get(id);
        if (aux == null) {throw new NotFoundException();}
        return new ResponseEntity<>(aux, HttpStatus.OK);
    }

    // create a new Comment
    @PostMapping("/api/post/{id}")
    public ResponseEntity<Comment> newComment(@PathVariable long id, @RequestBody Comment comment) {
        Post post =this.postService.get(id);
        if (post == null) {throw new NotFoundException();}
        if (comment.getText() == null || comment.getText().length()>1024 || comment.getText().isBlank()) throw new BadRequestException();
        if(comment.getForcedVisibility()!=0)throw new BadRequestException();
        comment.setText(this.policyFactory.sanitize(comment.getText()));
        comment.setUserCreator(loggedUser.getLoggedUser());
        comment.setRelatedPost(post);
        return new ResponseEntity<>(this.commentService.create(comment), HttpStatus.CREATED);
    }

    // edit Comment given id
    // the only attributes modifiable via API are: text and forcedVisibility, the rest are only modifiable by the application internally or directly on the DB
    @PutMapping("/api/comment/{id}")
    public ResponseEntity<Comment> updateComment(@PathVariable long id, @RequestBody Comment updatedComment) {
        Comment comment = commentService.get(id);

        if (comment == null) {throw new NotFoundException();}

        if(loggedUser.getLoggedUser().equals(comment.getUserCreator())) {
            if (comment.getText() == null || comment.getText().length()>1024 || comment.getText().isBlank()) throw new BadRequestException();
            if (comment.getForcedVisibility()!=updatedComment.getForcedVisibility()) throw new ForbiddenException();
            updatedComment.setUserCreator(comment.getUserCreator());
            updatedComment.setId(id);
            updatedComment.setDate(comment.getDate());
            updatedComment.setModeratedBy(comment.getModeratedBy());
            updatedComment.setText(policyFactory.sanitize(updatedComment.getText()));
            commentService.update(id, updatedComment);
            return new ResponseEntity<>(updatedComment, HttpStatus.OK);
        } else if(loggedUser.isAdmin()){
            int aux = updatedComment.getForcedVisibility();
            if(aux == 0||aux==1||aux ==-1)
            {
                comment.setForcedVisibility(aux);
                return new ResponseEntity<>(this.commentService.update(id, comment),HttpStatus.OK);
            } else {
                throw new BadRequestException();
            }
        } else throw new UnauthorizedException();
    }

    // delete Comment given id
    @DeleteMapping("/api/comment/{id}")
    public ResponseEntity<Comment> deleteComment(@PathVariable long id) {
        Comment comment = commentService.get(id);
        if (comment == null) {throw new NotFoundException();}
        if(!loggedUser.getLoggedUser().equals(comment.getUserCreator()))throw new UnauthorizedException();
        return new ResponseEntity<>(this.commentService.delete(id), HttpStatus.OK);
    }

    // Patch a comment given id
    // forcedVisibility is not modifiable to 0, as it would be impossible to difference it from its null value
    // the only attributes modifiable via API are: text and forcedVisibility, the rest are only modifiable by the application internally or directly on the DB
    @PatchMapping("/api/comment/{id}")
    public ResponseEntity<Comment> patchComment(@PathVariable long id, @RequestBody Comment updatedComment) {
        Comment comment = commentService.get(id);

        if (comment == null) {throw new NotFoundException();}

        if (loggedUser.getLoggedUser().equals(comment.getUserCreator())){
            if (updatedComment.getText() != null) {
                if (comment.getText().length()>1024 || comment.getText().isBlank()) throw new BadRequestException();
                comment.setText(policyFactory.sanitize(updatedComment.getText()));
                commentService.update(id, comment);
                return new ResponseEntity<>(comment, HttpStatus.OK);
            } else if(updatedComment.getForcedVisibility()==0){
                throw new BadRequestException();
            } else {
                throw new ForbiddenException();
            }
        } else if (loggedUser.isAdmin()){
            if( (updatedComment.getForcedVisibility() == 1 || updatedComment.getForcedVisibility() == -1 || updatedComment.getForcedVisibility() ==0)) {
                comment.setForcedVisibility(updatedComment.getForcedVisibility());
                return new ResponseEntity<>(commentService.update(id, comment), HttpStatus.OK);
            } else {
                throw new BadRequestException();
            }
        } else {
            throw new UnauthorizedException();
        }
    }

    // Return 10 last comments given postId, may return a null element or less than 10 comments
    @GetMapping("/api/comments/{postId}")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Comment> comments(@PathVariable long postId) {
        return commentService.get(1, postId);
    }

    // Return 10 comments given postId and position, may return a null element or less than 10 comments
    @GetMapping("/api/comments/{postId}/{page}")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Comment> comments(@PathVariable long postId, @PathVariable long page) {
        return commentService.get(page, postId);
    }

    // return first 10 reported Comments, may return a null element or less than 10 comments
    @GetMapping("/api/comments/reported")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Comment> commentsReported() {
        if(!loggedUser.isAdmin())throw new UnauthorizedException();
        return this.commentService.reported(1);
    }

    // return 10 CommentReports given page, may return a null element or less than 10 comments
    @GetMapping("/api/comments/reported/{page}")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Comment> commentsReported(@PathVariable long page) {
        if(!loggedUser.isAdmin())throw new UnauthorizedException();
        return this.commentService.reported(page);
    }
}
