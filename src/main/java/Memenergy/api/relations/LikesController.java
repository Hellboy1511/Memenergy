package Memenergy.api.relations;

import Memenergy.data.Post;
import Memenergy.data.relations.Likes;
import Memenergy.database.services.PostService;
import Memenergy.database.services.UserService;
import Memenergy.database.services.relations.LikesService;
import Memenergy.exceptions.exceptions.api.ConflictException;
import Memenergy.exceptions.exceptions.api.NotFoundException;
import Memenergy.security.LoggedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
public class LikesController {

    @Autowired
    LoggedUser loggedUser;
    @Autowired
    LikesService likesService;
    @Autowired
    PostService postService;
    @Autowired
    UserService userService;

    //Get a like given the liked post id and the user's username
    @GetMapping("/api/post/{id}/like/{username}")
    public ResponseEntity<Likes> like(@PathVariable long id, @PathVariable String username) {
        Likes likes = this.likesService.get(username, id);
        if (likes == null) {throw new NotFoundException();}
        return new ResponseEntity<>(likes, HttpStatus.OK);
    }

    //create a new Like
    @PostMapping("/api/post/{id}/like")
    public ResponseEntity<Likes> newLike(@PathVariable long id, @RequestBody(required = false) Likes likes) {
        Post post = this.postService.get(id);
        if (post == null) {throw new NotFoundException();}
        if(this.likesService.get(this.loggedUser.getLoggedUser().getUsername(),id)!=null) throw new ConflictException();
        if (likes == null) likes = new Likes();
        likes.setId(post);
        likes.setUsername(this.userService.get(this.loggedUser.getLoggedUser().getUsername()));
        return new ResponseEntity<>(this.likesService.create(likes), HttpStatus.OK);
    }

    //Delete a like given the liked post id and the user's username
    @DeleteMapping("/api/post/{id}/like")
    public ResponseEntity<Likes> deleteLike(@PathVariable long id) {
        Likes likes = this.likesService.delete(this.loggedUser.getLoggedUser().getUsername(), id);
        if (likes == null) {throw new NotFoundException();}
        return new ResponseEntity<>(likes, HttpStatus.OK);
    }

    //get all likes of a post given its id, may return a null element or less than 10 Likes
    @GetMapping("/api/post/{id}/likes")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Likes> byId(@PathVariable long id) {
        return this.likesService.get(id);
    }

    //get all likes of a user given its username, may return a null element or less than 10 Likes
    @GetMapping("/api/user/{username}/likes")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Likes> byUsername(@PathVariable String username) {
        return this.likesService.get(username);
    }
}
