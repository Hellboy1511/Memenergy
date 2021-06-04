package Memenergy.database.services.relations;

import Memenergy.data.relations.Follow;
import Memenergy.database.repositories.relations.FollowRepository;
import Memenergy.database.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FollowService {

    @Autowired
    private FollowRepository repository;
    @Autowired
    EntityManager entityManager;

    @Autowired
    UserService userService;

    //get a Follow given the username of the users followers and followed
    public Follow get(String userFollowed, String userFollower){
        try{
        return entityManager.createQuery("select f from Follow f where f.followId.usernameFollowed.username=:userFollowed and f.followId.usernameFollower.username=:userFollower", Follow.class).setParameter("userFollowed", userFollowed).setParameter("userFollower",userFollower ).getSingleResult().clone();
        }
        catch(NoResultException e){
            return null;
        }
    }

    //create a new Follow
    public Follow create(Follow follow) {
        return repository.saveAndFlush(follow);
    }

    //delete a Follow given the username of the users followers and followed
    @Transactional
    public Follow delete(String userFollowed, String userFollower) {
        Follow deletedFollow = this.get(userFollowed, userFollower);
        if (deletedFollow != null) {
            entityManager.createQuery("delete from Follow f where f.followId.usernameFollowed.username=:userFollowed and f.followId.usernameFollower.username=:userFollower").setParameter("userFollowed", userFollowed).setParameter("userFollower",userFollower ).executeUpdate();
            return deletedFollow;
        } else return null;
    }

    // return 10 followers from a specific user given its username and page, may return a null element or less than 10 follows
    public Collection<Follow> followers(String userFollowed, long page) {
        Stream<Follow> stream;
        List<Follow> listFinal = new LinkedList<>();
        page--;
        List<Follow> aux = entityManager.createQuery("select f from Follow f where f.followId.usernameFollowed.username =: userFollowed", Follow.class).setParameter("userFollowed", userFollowed).getResultList();
        stream = aux.parallelStream().sorted().sequential();
        long size = aux.parallelStream().count();
        if (page * 10 + 1 > size) {
            return null;
        } else {
            long n = 10;
            if (size - page * 10 < 10) {
                n = size - page * 10;
            }
            aux = stream.skip(page * 10).collect(Collectors.toList());
            for (int i = 0; i < n; ++i) {
                listFinal.add(aux.get(i));
            }
            return listFinal;
        }
    }


    // return 10 followed from a specific user given its username and page, may return a null element or less than 10 follows
    public Collection<Follow> followed(String userFollower, long page) {
        Stream<Follow> stream;
        List<Follow> listFinal = new LinkedList<>();
        page--;
        List<Follow> aux = entityManager.createQuery("select f from Follow f where f.followId.usernameFollower.username =: userFollower", Follow.class).setParameter("userFollower", userFollower).getResultList();
        stream = aux.parallelStream().sorted().sequential();
        long size = aux.parallelStream().count();
        if (page * 10 + 1 > size) {
            return null;
        } else {
            long n = 10;
            if (size - page * 10 < 10) {
                n = size - page * 10;
            }
            aux = stream.skip(page * 10).collect(Collectors.toList());
            for (int i = 0; i < n; ++i) {
                listFinal.add(aux.get(i));
            }
            return listFinal;
        }

    }
}
