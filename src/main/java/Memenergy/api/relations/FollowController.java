package Memenergy.api.relations;

import Memenergy.data.User;
import Memenergy.data.relations.Follow;
import Memenergy.database.services.UserService;
import Memenergy.database.services.relations.FollowService;
import Memenergy.exceptions.exceptions.api.ConflictException;
import Memenergy.exceptions.exceptions.api.NotFoundException;
import Memenergy.security.LoggedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
public class FollowController {

    @Autowired
    LoggedUser loggedUser;
    @Autowired
    FollowService followService;
    @Autowired
    UserService userService;

    //get a Follow given the username of the follower and the followed users
    @GetMapping("/api/user/{username}/follow/{follower}")
    public ResponseEntity<Follow> follow(@PathVariable String username, @PathVariable String follower) {
        Follow follow = this.followService.get(username, follower);
        if (follow == null) {throw new NotFoundException();}
        return new ResponseEntity<>(follow, HttpStatus.OK);
    }

    //create a new Follow
    @PostMapping("/api/user/{username}/follow")
    public ResponseEntity<Follow> newFollow(@PathVariable String username, @RequestBody(required = false) Follow follow) {
        if(this.followService.get(username,this.loggedUser.getLoggedUser().getUsername())!=null)throw new ConflictException();
        User user = this.userService.get(username);
        if(user == null) throw new NotFoundException();
        if(follow==null)follow = new Follow();
        follow.setUsernameFollowed(user);
        follow.setUsernameFollower(this.loggedUser.getLoggedUser());
        return new ResponseEntity<>(this.followService.create(follow), HttpStatus.CREATED);
    }

    //Delete a Follow given the username of the follower and the followed users
    @DeleteMapping("/api/user/{username}/follow")
    public ResponseEntity<Follow> deleteFollow(@PathVariable String username) {
        Follow follow = followService.delete(username, this.loggedUser.getLoggedUser().getUsername());
        if (follow == null) {throw new NotFoundException();}
        return new ResponseEntity<>(follow, HttpStatus.OK);
    }

    //get first 10 followers of a user given its username, may return a null element or less than 10 Follows
    @GetMapping("/api/user/{username}/followers")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Follow> followers(@PathVariable String username) {
        return followService.followers(username, 1);
    }

    //get first 10 followed users of a user given its username, may return a null element or less than 10 Follows
    @GetMapping("/api/user/{username}/followed")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Follow> followed(@PathVariable String username) {
        return followService.followed(username, 1);
    }

    //get 10 followers of a user given its username, may return a null element or less than 10 Follows
    @GetMapping("/api/user/{username}/followers/{page}")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Follow> followers(@PathVariable String username, @PathVariable long page) {
        return followService.followers(username, page);
    }

    //get 10 followed users of a user given its username, may return a null element or less than 10 Follows
    @GetMapping("/api/user/{username}/followed/{page}")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Follow> followed(@PathVariable String username, @PathVariable long page) {
        return followService.followed(username, page);
    }
}
