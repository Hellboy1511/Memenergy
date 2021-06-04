package Memenergy.database.services;

import Memenergy.data.Comment;
import Memenergy.database.repositories.CommentRepository;
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
public class CommentService {

    @Autowired
    private CommentRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    PostService postService;

    //get a comment given its id
    public Comment get(long id) {
        try {
            return this.entityManager.createQuery("SELECT c from Comment c where c.id=:id",Comment.class).setParameter("id",id).getSingleResult().clone();
        } catch (NoResultException e){
            return null;
        }
    }

    //create a new comment
    public Comment create(Comment comment) {

        return this.repository.saveAndFlush(comment);
    }

    //update an existing comment
    @Transactional
    public Comment update(long id, Comment updatedComment) {
        Comment actualComment = this.get(id);
        if (actualComment != null) {
            boolean change = false;
            boolean textChanged = false;
            boolean dateChanged = false;
            boolean moderatedByChanged = false;
            boolean relatedPostChange = false;
            boolean userCreatorChanged = false;
            boolean forceVisibilityChanged = false;
            boolean reportCounterChanged = false;
            StringBuilder sb = new StringBuilder();
            sb.append("update Comment c set ");
            if(!Objects.equals(updatedComment.getReportCounter(),actualComment.getReportCounter())){
                actualComment.setReportCounter(updatedComment.getReportCounter());
                sb.append("c.reportCounter=:reportCounter");
                change = true;
                reportCounterChanged = true;
            }
            if(!(updatedComment.getText()==null || Objects.equals(updatedComment.getText(),actualComment.getText()))){
                actualComment.setText(updatedComment.getText());
                if(change){
                    sb.append(", ");
                }
                sb.append("c.text=:text");
                textChanged = true;
                change=true;
            }
            if(!Objects.equals(updatedComment.getForcedVisibility(),actualComment.getForcedVisibility())){
                actualComment.setForcedVisibility(updatedComment.getForcedVisibility());
                if(change){
                    sb.append(", ");
                }
                sb.append("c.forcedVisibility=:forcedVisibility");
                change = true;
                forceVisibilityChanged = true;
            }
            if(!(updatedComment.getDate()==null || Objects.equals(updatedComment.getDate(),actualComment.getDate()))){
                actualComment.setDate(updatedComment.getDate());
                if(change){
                    sb.append(", ");
                }
                change = true;
                dateChanged=true;
                sb.append("c.date=:date");
            }
            if(!Objects.equals(updatedComment.getModeratedBy(),actualComment.getModeratedBy())){
                actualComment.setModeratedBy(updatedComment.getModeratedBy());
                if(change){
                    sb.append(", ");
                }
                sb.append("c.moderatedBy.username=:moderatedBy");
                change = true;
                moderatedByChanged=true;
            }
            if(!(updatedComment.getRelatedPost()==null || Objects.equals(updatedComment.getRelatedPost(),actualComment.getRelatedPost()))){
                actualComment.setRelatedPost(updatedComment.getRelatedPost());
                if(change){
                    sb.append(", ");
                }
                sb.append("c.relatedPost.id=:relatedPost");
                change = true;
                relatedPostChange =true;
            }
            if(!(updatedComment.getUserCreator()==null || Objects.equals(updatedComment.getUserCreator(),updatedComment.getUserCreator()))){
                actualComment.setUserCreator(updatedComment.getUserCreator());
                if(change){
                    sb.append(", ");
                }
                sb.append("c.userCreator.username=:userCreator");
                change = true;
                userCreatorChanged =true;
            }


            if(change) {
                sb.append(" where c.id=:id");
                Query query = this.entityManager.createQuery(sb.toString()).setParameter("id", id);
                if (reportCounterChanged) {
                    query.setParameter("reportCounter", updatedComment.getReportCounter());
                }
                if (textChanged) {
                    query.setParameter("text", updatedComment.getText());
                }
                if (forceVisibilityChanged) {
                    query.setParameter("forcedVisibility", updatedComment.getForcedVisibility());
                }
                if(dateChanged){
                    query.setParameter("date",updatedComment.getDate());
                }
                if(moderatedByChanged){
                    query.setParameter("moderatedBy",updatedComment.getModeratedBy().getUsername());
                }
                if(relatedPostChange){
                    query.setParameter("relatedPost",updatedComment.getRelatedPost().getId());
                }
                if(userCreatorChanged){
                    query.setParameter("userCreator",updatedComment.getUserCreator().getUsername());
                }


                if (query.executeUpdate() > 0) {
                    return actualComment;
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

    //delete a comment given its id
    @Transactional
    public Comment delete(long id) {
        Comment deletedComment = this.get(id);
        if (deletedComment!=null) {
            if(this.entityManager.createQuery("DELETE FROM Comment c WHERE c.id = :id").setParameter("id", id).executeUpdate() >0){
                return deletedComment;
            }
            else{
                return null;
            }
        } else {
            return null;
        }
    }

    //get 10 comments given a post id and a page, may return null or less than 10 comments
    public Collection<Comment> get(long page, long postId) {
        Stream<Comment> stream;
        List<Comment> listFinal = new LinkedList<>();
        page--;
        List<Comment> aux = this.entityManager.createQuery("SELECT c from Comment c where c.relatedPost.id =:relatedPost and (c.forcedVisibility = 1 or (c.forcedVisibility=0 and c.reportCounter < 10))",Comment.class).setParameter("relatedPost",postId).getResultList();
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

    //get 10 reported comments given a page, may return null or less than 10 comments
    public Collection<Comment> reported(long page) {
        Stream<Comment> stream;
        List<Comment> listFinal = new LinkedList<>();
        page--;
        List<Comment> aux = this.entityManager.createQuery("SELECT c from Comment c where c.reportCounter > 0",Comment.class).getResultList();
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

    //get 10 reported comments given a page and their forcedVisibility, may return null or less than 10 comments
    public Collection<Comment> reported(long page, int forcedVisibility) {
        Stream<Comment> stream;
        List<Comment> listFinal = new LinkedList<>();
        page--;
        List<Comment> aux = this.entityManager.createQuery("SELECT c from Comment c where c.reportCounter > 0 and c.forcedVisibility=:forcedVisibility",Comment.class).setParameter("forcedVisibility",forcedVisibility).getResultList();
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

    //allows to report a comment
    @Transactional
    public Comment report(long id) {
        Comment aux = this.get(id);
        if (aux != null) {
            aux.setReportCounter(aux.getReportCounter()+1);
            if(this.entityManager.createQuery("UPDATE Comment c SET c.reportCounter=:reportCounter WHERE c.id=:id").setParameter("reportCounter", aux.getReportCounter()).setParameter("id", id).executeUpdate() >0){
                return aux;
            }
            else{
                return null;
            }
        } else {
            return null;
        }
    }

}
