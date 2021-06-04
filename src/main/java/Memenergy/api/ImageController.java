package Memenergy.api;

import Memenergy.data.Post;
import Memenergy.data.User;
import Memenergy.database.services.ImageService;
import Memenergy.database.services.PostService;
import Memenergy.database.services.UserService;
import Memenergy.exceptions.exceptions.api.BadRequestException;
import Memenergy.exceptions.exceptions.api.NotFoundException;
import Memenergy.exceptions.exceptions.api.UnauthorizedException;
import Memenergy.security.LoggedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

//all file formats accepted as image by html are accepted in this algorithm
@RestController
public class ImageController {
    @Autowired
    LoggedUser loggedUser;

    @Autowired
    ImageService imageService;
    @Autowired
    UserService userService;
    @Autowired
    PostService postService;


    //returns a user's profile image given its username
    @GetMapping("/api/user/{username}/image")
    public ResponseEntity<Object> getImage(@PathVariable String username) {
        User aux = this.userService.get(username);
        if (aux == null) {throw new NotFoundException();}
        try {
            return this.imageService.createResponseFromImage(username);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    //creates or updates a user's profile image given its username
    @PostMapping("/api/user/{username}/image")
    public ResponseEntity<Object> newImage(@PathVariable String username, @RequestParam MultipartFile imageFile) throws IOException {
        User user = this.userService.get(username);
        if(!this.loggedUser.getLoggedUser().getUsername().equalsIgnoreCase(username)) throw new UnauthorizedException();
        if (user == null) {throw new NotFoundException();}
        if(imageFile.isEmpty() || imageFile.getBytes().length > 60000000||!imageFile.getContentType().split("/")[0].equals("image")) throw new BadRequestException();
            this.imageService.saveImage(username, imageFile);
            return this.imageService.createResponseFromImage(username);
    }

    //returns a post's image given its username
    @GetMapping("/api/post/{id}/image")
    public ResponseEntity<Object> getImage(@PathVariable long id) {
        Post aux = this.postService.get(id);
        if (aux == null) {throw new NotFoundException();}
        try {
            return this.imageService.createResponseFromImage(id);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //creates a user's profile image given its username
    //updates to posts' images are not permitted
    @PostMapping("/api/post/{id}/image")
    public ResponseEntity<Object> newImage(@PathVariable long id, @RequestParam MultipartFile imageFile) throws IOException {
        Post post = this.postService.get(id);
        if (post == null) {throw new NotFoundException();}
        if(!this.loggedUser.getLoggedUser().equals(post.getUserCreator())) throw new UnauthorizedException();
        if(imageFile!=null && (imageFile.isEmpty() || imageFile.getBytes().length > 60000000||!imageFile.getContentType().split("/")[0].equals("image"))) throw new BadRequestException();
            this.postService.update(id, post);
            this.imageService.saveImage(id, imageFile);
            return this.imageService.createResponseFromImage(id);

    }
}
