package Memenergy.database.services.relations;

import Memenergy.data.composedId.CommentReportId;
import Memenergy.data.relations.CommentReport;
import Memenergy.database.repositories.relations.CommentReportRepository;
import Memenergy.database.services.CommentService;
import Memenergy.database.services.UserService;
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
public class CommentReportService {

    @Autowired
    private CommentReportRepository repository;
    @Autowired
    EntityManager entityManager;

    @Autowired
    CommentService commentService;
    @Autowired
    UserService userService;


    //get a comment report given the username of the reporter and the comment's id
    public CommentReport get(String username, long id) {
        try {
            return this.entityManager.createQuery("select cr from CommentReport cr where cr.commentReportId.id.id=:id and cr.commentReportId.username.username=:username", CommentReport.class).setParameter("id", id).setParameter("username",username).getSingleResult().clone();
        } catch (NoResultException e) {
            return null;
        }
    }

    //create a new comment report
    public CommentReport create(CommentReport commentReport) {
        if (repository.findById(new CommentReportId(commentReport.getUsername(), commentReport.getId())).isPresent()) {
            return null;
        } else {
            if (this.commentService.get(commentReport.getId().getId()) != null) {
                this.commentService.report(commentReport.getId().getId());
                return repository.saveAndFlush(commentReport);
            } else {
                return null;
            }
        }
    }

    //update a comment report given the username of the reporter and the comment id
    @Transactional
    public CommentReport update(String username, long id, CommentReport updatedCommentReport) {
        CommentReport actualCommentReport = this.get(username,id);
        if(actualCommentReport!=null){
            boolean change = false;
            boolean reasonChanged = false;
            boolean dateChanged = false;

            StringBuilder sb = new StringBuilder();
            sb.append("update CommentReport cr set ");

            if(!(updatedCommentReport.getReason()==null ||Objects.equals(actualCommentReport.getReason(),updatedCommentReport.getReason()))){
                change = true;
                sb.append("cr.reason=:reason");
                actualCommentReport.setReason(updatedCommentReport.getReason());
                reasonChanged = true;
            }
            if (!(updatedCommentReport.getDate()==null ||Objects.equals(actualCommentReport.getDate(),updatedCommentReport.getDate()))){
                if(change){
                    sb.append(", ");
                }
                change = true;
                sb.append("cr.date=:date");
                actualCommentReport.setDate(updatedCommentReport.getDate());
                dateChanged = true;
            }

            if(change){
                sb.append(" where cr.commentReportId.id.id=:id and cr.commentReportId.username.username=:username");
                Query query = this.entityManager.createQuery(sb.toString()).setParameter("id",id).setParameter("username",username);
                if(reasonChanged){
                    query.setParameter("reason",updatedCommentReport.getReason());
                }
                if(dateChanged){
                    query.setParameter("date",updatedCommentReport.getDate());
                }

                if(query.executeUpdate()>0){
                    return actualCommentReport;
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

    //delete a comment report given the username of the reporter and the comment id
    @Transactional
    public CommentReport delete(String username, long id) {
        CommentReportId composedId = new CommentReportId(this.userService.get(username), this.commentService.get(id));
        CommentReport commentReportDeleted = this.get(username,id);
        if (commentReportDeleted != null) {
            entityManager.createQuery("delete from CommentReport cr where cr.commentReportId.username=:username and cr.commentReportId.id.id=:id").setParameter("id", id).setParameter("username",username).executeUpdate();
            return commentReportDeleted;
        } else {
            return null;
        }
    }

    // return 10 CommentReports from a specific comment given its id and page, may return a null element or less than 10 comment reports
    public Collection<CommentReport> byComment(long id, long page) {
        Stream<CommentReport> stream;
        List<CommentReport> listFinal = new LinkedList<>();
        page--;
        List<CommentReport> aux = entityManager.createQuery("select cr from CommentReport cr where cr.commentReportId.id.id =:id ", CommentReport.class).setParameter("id", id).getResultList();
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

    // return 10 CommentReports given page, may return a null element or less than 10 comment reports
    public Collection<CommentReport> byPage(long page) {
        Stream<CommentReport> stream;
        List<CommentReport> listFinal = new LinkedList<>();
        page--;
        List<CommentReport> aux = entityManager.createQuery("select cr from CommentReport cr", CommentReport.class).getResultList();
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
