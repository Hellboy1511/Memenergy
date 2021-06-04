package Memenergy.api;

import Memenergy.data.Post;
import Memenergy.database.services.PostService;
import Memenergy.database.services.UserService;
import Memenergy.exceptions.exceptions.api.BadRequestException;
import Memenergy.exceptions.exceptions.api.ForbiddenException;
import Memenergy.exceptions.exceptions.api.NotFoundException;
import Memenergy.exceptions.exceptions.api.UnauthorizedException;
import Memenergy.security.LoggedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
public class PostController {
    @Autowired
    LoggedUser loggedUser;
    @Autowired
    PostService postService;
    @Autowired
    UserService userService;

    // return Post given id
    @GetMapping("/api/post/{id}")
    public ResponseEntity<Post> post(@PathVariable long id) {
        Post post = this.postService.get(id);
        if (post == null) {throw new NotFoundException();}
        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    // create a new Post
    @PostMapping("/api/post")
    public ResponseEntity<Post> newPost(@RequestBody Post post) {
        if (post.getTitle() == null || post.getTitle().isBlank()|| post.getTitle().length()>255) throw new BadRequestException();
        if (post.getDescription() != null && post.getDescription().length()>1024) throw new BadRequestException();
        if (post.getForcedVisibility()!=0) throw new BadRequestException();
        post.setUserCreator(this.loggedUser.getLoggedUser());
        return new ResponseEntity<>(this.postService.create(post), HttpStatus.CREATED);
    }

    // edit Post given id
    // the only attributes modifiable via API are: title, description and forcedVisibility, the rest are only modifiable by the application internally or directly on the DB
    @PutMapping("/api/post/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable long id, @RequestBody Post updatedPost) {
        Post post = postService.get(id);
        if (post == null) {throw new NotFoundException();}

        if(this.loggedUser.getLoggedUser().equals(post.getUserCreator())) {
            if (updatedPost.getTitle()==null||updatedPost.getTitle().length()>255)throw new BadRequestException();
            if(updatedPost.getDescription()!=null&&updatedPost.getDescription().length()>255)throw new BadRequestException();
            if (updatedPost.getForcedVisibility()!=post.getForcedVisibility())throw new ForbiddenException();

            updatedPost.setReportCounter(post.getReportCounter());
            updatedPost.setLikeCounter(post.getLikeCounter());
            updatedPost.setModeratedBy(post.getModeratedBy());
            updatedPost.setId(id);
            updatedPost.setDate(post.getDate());
            postService.update(id, updatedPost);
            return new ResponseEntity<>(updatedPost, HttpStatus.OK);

        } else if(this.loggedUser.isAdmin()){
            int aux = updatedPost.getForcedVisibility();
            if(aux==0||aux==1||aux==-1){
                post.setForcedVisibility(updatedPost.getForcedVisibility());
                return new ResponseEntity<>(this.postService.update(id,post),HttpStatus.OK);
            }else {
                throw new BadRequestException();
            }
        } else throw new UnauthorizedException();

    }

    // delete Post given id
    @DeleteMapping("/api/post/{id}")
    public ResponseEntity<Post> deletePost(@PathVariable long id) {
        Post post = postService.get(id);
        if (post == null) {throw new NotFoundException();}
        if(!this.loggedUser.getLoggedUser().equals(post.getUserCreator())) throw new UnauthorizedException();
        return new ResponseEntity<>(this.postService.delete(id), HttpStatus.OK);
    }

    // patch a Post given id
    // the only attributes modifiable via API are: title, description and forcedVisibility, the rest are only modifiable by the application internally or directly on the DB
    @PatchMapping("/api/post/{id}")
    public ResponseEntity<Post> patchPost(@PathVariable long id, @RequestBody Post updatedPost) {
        Post post = postService.get(id);
        if (post == null) {throw new NotFoundException();}
        if(this.loggedUser.getLoggedUser().equals(post.getUserCreator())) {
            if (updatedPost.getTitle() != null) {
                if(updatedPost.getTitle().length()>255 || updatedPost.getTitle().isBlank())throw new BadRequestException();
                post.setTitle(updatedPost.getTitle());
                postService.update(id, post);
                return new ResponseEntity<>(post, HttpStatus.OK);
            } else if (updatedPost.getDescription() != null) {
                if (updatedPost.getDescription().length()>1024)throw new BadRequestException();
                post.setDescription(updatedPost.getDescription());
                postService.update(id, post);
                return new ResponseEntity<>(post, HttpStatus.OK);
            } else if(updatedPost.getForcedVisibility()!=0){
                throw new ForbiddenException();
            } else {
                throw new BadRequestException();
            }
        } else if(this.loggedUser.isAdmin()){
            if (updatedPost.getForcedVisibility() == -1 || updatedPost.getForcedVisibility()==0 || updatedPost.getForcedVisibility() == 1) {
                post.setForcedVisibility(updatedPost.getForcedVisibility());
                postService.update(id, post);
                return new ResponseEntity<>(post, HttpStatus.OK);
            } else {
                throw new BadRequestException();
            }
        } else throw new UnauthorizedException();
    }

    // return last 10 Posts, may return a null element or less than 10 posts
    @GetMapping("/api/posts")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Post> posts() {
        return postService.byPage(1);
    }

    // return 10 Posts given page, may return a null element or less than 10 posts
    @GetMapping("/api/posts/{page}")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Post> posts(@PathVariable long page) {
        return postService.byPage(page);
    }

    // return last 10 Posts, may return a null element or less than 10 posts (sorted by the date of creation of the post)
    @GetMapping("/api/posts/reported")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Post> reported() {
        if(!this.loggedUser.isAdmin())throw new UnauthorizedException();
        return postService.reported(1);
    }

    // return 10 Posts given page, may return a null element or less than 10 posts (sorted by the date of creation of the post)
    @GetMapping("/api/posts/reported/{page}")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Post> reported(@PathVariable long page) {
        if(!this.loggedUser.isAdmin())throw new UnauthorizedException();
        return postService.reported(page);
    }

    @GetMapping("/api/posts/filtered")
    public ResponseEntity<Collection<Post>> filtered(@RequestParam String title, @RequestParam long minLikes, @RequestParam long page) {
        Collection<Post> aux = postService.filtered(title, minLikes, page);
        if (aux == null || aux.isEmpty()) {
            if (page == 1) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT); //as a redirection is not needed
            }
        } else {
            return new ResponseEntity<>(aux, HttpStatus.OK);
        }
    }
}
