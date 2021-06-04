package Memenergy.database.services;

import Memenergy.data.User;
import Memenergy.data.images.PostImage;
import Memenergy.data.images.UserImage;
import Memenergy.database.repositories.image.PostImageRepository;
import Memenergy.database.repositories.image.UserImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Paths;

@Service
@Configuration
//all file formats accepted as image by html are accepted in this algorithm
public class ImageService {

    @Autowired
    UserService userService;
    @Autowired
    PostService postService;
    @Autowired
    PostImageRepository postImageRepository;
    @Autowired
    UserImageRepository userImageRepository;
    @Autowired
    EntityManager entityManager;

    public void saveImage(long id, MultipartFile image) throws IOException {
        PostImage postImage = new PostImage(this.postService.get(id), image.getBytes(), image.getContentType());
        this.postImageRepository.saveAndFlush(postImage);
    }

    public void saveImage(String username, MultipartFile image) throws IOException {
        User aux = this.userService.get(username);
        UserImage userImage = new UserImage(aux, image.getBytes(), image.getContentType());
        aux.setProfileImage(true);
        this.userService.update(username,aux);
        this.userImageRepository.saveAndFlush(userImage);
    }

    public void saveImage(long id, byte[] image,String extension) {
        PostImage postImage = new PostImage(this.postService.get(id), image, extension);
        this.postImageRepository.saveAndFlush(postImage);
    }

    public void saveImage(String username, byte[] image,String extension) {
        User aux = this.userService.get(username);
        UserImage userImage = new UserImage(aux, image, extension);
        aux.setProfileImage(true);
        this.userService.update(username,aux);
        this.userImageRepository.saveAndFlush(userImage);
    }


    public ResponseEntity<Object> createResponseFromImage(long id) {
        PostImage postImage = this.entityManager.createQuery("select i from PostImage i where i.id=:id",PostImage.class).setParameter("id",id).getSingleResult();
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, postImage.getExtension()).body(postImage.getImage());
    }

    public ResponseEntity<Object> createResponseFromImage(String username) throws MalformedURLException {
        if(this.userService.get(username).hasProfileImage()) {
            UserImage userImage = this.entityManager.createQuery("select i from UserImage i where i.username=:username",UserImage.class).setParameter("username",username).getSingleResult();
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, userImage.getExtension()).body(userImage.getImage());
        } else {
            Resource file = new UrlResource(Paths.get(System.getProperty("user.dir")).resolve("src/main/resources/static/assets/ico/default_avatar.jpg").toUri());
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/jpeg").body(file);
        }
    }
}
