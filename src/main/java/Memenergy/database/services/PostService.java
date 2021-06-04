package Memenergy.database.services;

import Memenergy.data.Post;
import Memenergy.database.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PostService {
    @Autowired
    private PostRepository repository;
    @Autowired
    UserService userService;
    @Autowired
    EntityManager entityManager;

    //a map is the most efficient structure for posts

    //get a post given its id
    public Post get(long id) {
        try {
        return this.entityManager.createQuery("select p from Post p where p.id=:id",Post.class).setParameter("id",id).getSingleResult().clone();
        } catch (NoResultException e){
            return null;
        }
    } //if Optional == null, return null, otherwise returns Optional.get()

    //create a new post
    public Post create(Post post) {
        return repository.saveAndFlush(post);
    }

    //update an existing post
    @Transactional
    public Post update(long id, Post updatedPost) {
        Post actualPost = this.get(id);
        if(actualPost!=null){
            boolean change = false;
            boolean likeCounterChanged = false;
            boolean reportCounterChanged = false;
            boolean descriptionChanged = false;
            boolean dateChanged = false;
            boolean visibilityChanged = false;
            boolean titleChanged = false;
            boolean userChanged = false;
            boolean moderatorChanged = false;

            StringBuilder sb = new StringBuilder();
            sb.append("update Post p set ");

            if(!Objects.equals(actualPost.getLikeCounter(),updatedPost.getLikeCounter())){
                change = true;
                sb.append("p.likeCounter=:likeCounter");
                actualPost.setLikeCounter(updatedPost.getLikeCounter());
                likeCounterChanged = true;
            }
            if(!Objects.equals(actualPost.getReportCounter(),updatedPost.getReportCounter())){
                if(change){
                    sb.append(", ");
                }
                change = true;
                sb.append("p.reportCounter=:reportCounter");
                actualPost.setReportCounter(updatedPost.getReportCounter());
                reportCounterChanged = true;
            }
            if(!Objects.equals(actualPost.getDescription(),updatedPost.getDescription())){
                if(change){
                    sb.append(", ");
                }
                change = true;
                sb.append("p.description=:description");
                actualPost.setDescription(updatedPost.getDescription());
                descriptionChanged = true;
            }
            if(!(updatedPost.getDate()==null || Objects.equals(actualPost.getDate(),updatedPost.getDate()))){
                if(change){
                    sb.append(", ");
                }
                change = true;
                sb.append("p.date=:date");
                actualPost.setDate(updatedPost.getDate());
                dateChanged = true;
            }
            if(!Objects.equals(actualPost.getForcedVisibility(),updatedPost.getForcedVisibility())){
                if(change){
                    sb.append(", ");
                }
                change = true;
                sb.append("p.forcedVisibility=:visibility");
                actualPost.setForcedVisibility(updatedPost.getForcedVisibility());
                visibilityChanged = true;
            }
            if(!(updatedPost.getTitle()==null || Objects.equals(actualPost.getTitle(),updatedPost.getTitle()))){
                if(change){
                    sb.append(", ");
                }
                change = true;
                sb.append("p.title=:title");
                actualPost.setTitle(updatedPost.getTitle());
                titleChanged = true;
            }
            if(!(updatedPost.getUserCreator()==null || Objects.equals(actualPost.getUserCreator(),updatedPost.getUserCreator()))){
                if(change){
                    sb.append(", ");
                }
                change = true;
                sb.append("p.userCreator=:userCreator");
                actualPost.setUserCreator(updatedPost.getUserCreator());
                userChanged = true;
            }
            if(!Objects.equals(actualPost.getModeratedBy(),updatedPost.getModeratedBy())){
                if(change){
                    sb.append(", ");
                }
                change = true;
                sb.append("p.moderatedBy=:moderatedBy");
                actualPost.setModeratedBy(updatedPost.getModeratedBy());
                moderatorChanged = true;
            }

            if (change){
                sb.append(" where p.id=:id");
                Query query = this.entityManager.createQuery(sb.toString()).setParameter("id",id);
                if(likeCounterChanged){
                    query.setParameter("likeCounter",updatedPost.getLikeCounter());
                }
                if(reportCounterChanged){
                    query.setParameter("reportCounter",updatedPost.getReportCounter());
                }
                if(descriptionChanged){
                    query.setParameter("description",updatedPost.getDescription());
                }
                if(dateChanged){
                    query.setParameter("date",updatedPost.getDate());
                }
                if(visibilityChanged){
                    query.setParameter("visibility",updatedPost.getForcedVisibility());
                }
                if(titleChanged){
                    query.setParameter("title",updatedPost.getTitle());
                }
                if(userChanged){
                    query.setParameter("userCreator",updatedPost.getUserCreator());
                }
                if(moderatorChanged){
                    query.setParameter("moderatedBy",updatedPost.getModeratedBy());
                }

                if(query.executeUpdate()>0){
                    return actualPost;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    //delete a post given its id
    @Transactional
    public Post delete(long id) {
        Post deletedPost = this.get(id);
        if(deletedPost !=null) {
            if (this.entityManager.createQuery("delete from Post p where p.id=:id").setParameter("id", id).executeUpdate() > 0) {
                return deletedPost;
            } else {
                return null;
            }
        }else {
            return null;
        }
    }

    //get 10 posts given a page, may return null or less than 10 posts
    public Collection<Post> byPage(long page) {
        Stream<Post> stream;
        List<Post> listFinal = new LinkedList<>();
        page--;
        List<Post> aux = this.entityManager.createQuery("select p from Post p where p.forcedVisibility = 1 or (p.forcedVisibility = 0 and p.reportCounter < 10)",Post.class).getResultList();
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

    //get 10 posts given a user username and a page, may return null or less than 10 posts
    public Collection<Post> byUsername(String username, long page) {
        Stream<Post> stream;
        List<Post> listFinal = new LinkedList<>();
        page--;
        List<Post> aux = this.entityManager.createQuery("select p from Post p where p.userCreator.username=:user and (p.forcedVisibility = 1 or (p.forcedVisibility = 0 and p.reportCounter < 10))",Post.class).setParameter("user",username).getResultList();
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

    //get 10 reported posts given a page, may return null or less than 10 posts
    public Collection<Post> reported(long page) {
        Stream<Post> stream;
        List<Post> listFinal = new LinkedList<>();
        page--;
        List<Post> aux = this.entityManager.createQuery("select p from Post p where p.reportCounter > 0", Post.class).getResultList();
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

    //get 10 reported posts given a page and their forcedVisibility, may return null or less than 10 posts
    public Collection<Post> reported(long page, int forcedVisibility) {
        Stream<Post> stream;
        List<Post> listFinal = new LinkedList<>();
        page--;
        List<Post> aux = this.entityManager.createQuery("select p from Post p where p.forcedVisibility=:forcedVisibility",Post.class).setParameter("forcedVisibility",forcedVisibility).getResultList();
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

    //report a post given its id
    @Transactional
    public Post report(long id) {
        Post aux = this.get(id);
        if (aux != null) {
            aux.setReportCounter(aux.getReportCounter() + 1);
            if(this.entityManager.createQuery("UPDATE Post p SET p.reportCounter=:c where p.id=:id").setParameter("id",id).setParameter("c",aux.getReportCounter()).executeUpdate()>0) {
                return aux;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    //like a post given its id
    @Transactional
    public Post like(long id) {
        Post aux = this.get(id);
        if (aux != null) {
            aux.setLikeCounter(aux.getLikeCounter() + 1);
            if(this.entityManager.createQuery("UPDATE Post p SET p.likeCounter=:c where p.id=:id").setParameter("id",id).setParameter("c",aux.getLikeCounter()).executeUpdate()>0) {
                return aux;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    //dislike a post given its id
    @Transactional
    public Post dislike(long id) {
        Post aux = this.get(id);
        if (aux != null) {
            aux.setLikeCounter(aux.getLikeCounter() - 1);
            if(this.entityManager.createQuery("UPDATE Post p SET p.likeCounter=:c where p.id=:id").setParameter("id",id).setParameter("c",aux.getLikeCounter()).executeUpdate()>0) {
                return aux;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public Collection<Post> filtered(String title, long minLikes, long page) {
        List<Post> aux;
        page--;

        if (!(title.isEmpty() || title.isBlank())) {
            if (minLikes > 0) {
                aux = this.entityManager.createQuery("SELECT p from Post p where p.title like :title and p.likeCounter > :c",Post.class).setParameter("title","%"+title+"%").setParameter("c",minLikes).getResultList();
            } else {
                aux = this.entityManager.createQuery("SELECT p from Post p where p.title like :title",Post.class).setParameter("title","%"+title+"%").getResultList();
            }
        } else {
            if (minLikes > 0) {
                aux = this.entityManager.createQuery("SELECT p from Post p where p.likeCounter > :c",Post.class).setParameter("c",minLikes).getResultList();
            } else {
                aux = new LinkedList<>();
            }
        }

        Stream<Post> stream;
        List<Post> listFinal = new LinkedList<>();
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
