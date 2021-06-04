package Memenergy.database.services.relations;

import Memenergy.data.relations.Likes;
import Memenergy.database.repositories.relations.LikesRepository;
import Memenergy.database.services.PostService;
import Memenergy.database.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.Collection;

@Service
public class LikesService {

    @Autowired
    private LikesRepository repository;
    @Autowired
    EntityManager entityManager;

    @Autowired
    PostService postService;
    @Autowired
    UserService userService;

    //get a Like given a post's id and a user's username
    public Likes get(String username, long id) {
        try {
            return entityManager.createQuery("select l from Likes l where l.likesId.username.username=:username and l.likesId.id.id=:id", Likes.class).setParameter("username", username).setParameter("id",id).getSingleResult().clone();
        } catch (NoResultException e) {
            return null;
        }
    }

    //create a new Like
    public Likes create(Likes likes) {
        this.postService.like(likes.getId().getId());
        return repository.saveAndFlush(likes);
    }

    //delete a Like given a post id and a user username
    @Transactional
    public Likes delete(String username, long id) {
        Likes likes = this.get(username, id);
        if (likes != null) {
            this.postService.dislike(likes.getId().getId());
            this.entityManager.createQuery("delete from Likes l where l.likesId.username.username=:username and l.likesId.id.id=:id").setParameter("username", username).setParameter("id",id).executeUpdate();
            return likes;
        } else {
            return null;
        }
    }

    //get all likes given a user's username
    public Collection<Likes> get(String username) {
        return this.entityManager.createQuery("select l from Likes l where l.likesId.username.username=:username", Likes.class).setParameter("username", username).getResultList();

    }

    //get all likes given a post's id
    public Collection<Likes> get(long id) {
        return this.entityManager.createQuery("select l from Likes l where l.likesId.id.id=:id", Likes.class).setParameter("id",id).getResultList();
    }
}
